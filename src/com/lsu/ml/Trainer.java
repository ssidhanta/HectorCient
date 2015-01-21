package com.lsu.ml;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.Row;
import org.apache.cassandra.db.RowMutation;
import org.apache.cassandra.db.SystemTable;
import org.apache.cassandra.db.Table;
import org.apache.cassandra.gms.ApplicationState;
import org.apache.cassandra.gms.EndpointState;
import org.apache.cassandra.gms.Gossiper;
import org.apache.cassandra.gms.VersionedValue;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.utils.FBUtilities;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
//import weka.classifiers.functions.SMOreg;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.CqlRows;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.ThriftCfDef;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnIndexType;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;

public class Trainer {

	private static StringSerializer stringSerializer = StringSerializer.get();
	private static final String USER="MyColumnFamily";
	public static void main(String[] args) {
		//File f = new File("/tmp/training_data.arff");
		//File f = new File("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//trainData.arff");
		File f = new File("/tmp/training_data.arff");
		//File f = new File("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//training_data.arff");
		//exhaustiveSearch(f,5,7,2,4,0,0,100,0,0.5);
		//exhaustiveSearchOptimal(f,-1,1,2,1,2,1);
		//exhaustiveSearchOptimalThreshold(f,-1,1,2,1,2,1);
		//parseNet();
		// TODO Auto-generated method stub;
		//double[] coeffs = linearRegression();
		//System.out.println("***coeffs length:"+coeffs.length);
		//optimize(coeffs);
		List<Map<String, Double>> listOfSlas = new ArrayList<Map<String, Double>>();
		//int noSlas = 3;
		HashMap map = new HashMap();
		//for(int i=0;i<noSlas;i++)
		//{
		//public static void exhaustiveSearchOptimal(File f,double wt_latency,double wt_throughput,double wt_staleness,double wt_retransmisson,double wt_load,double wt_packetloss){
		map.put("latency", 120.0);
		map.put("staleness", 5.0);
		map.put("retransmission", 10.0);
		map.put("throughput", 100.0);
		listOfSlas.add(map);
		map = new HashMap();
		map.put("latency", 50.0);
		map.put("staleness", 10.0);
		map.put("retransmission", 10.0);
		map.put("throughput", 50.0);
		listOfSlas.add(map);
		map = new HashMap();
		map.put("latency", 10.0);
		map.put("staleness", 20.0);	
		map.put("retransmission", 10.0);
		map.put("throughput", 10.0);
		listOfSlas.add(map);
		tryOutSlas(f, listOfSlas);
		
		//}
		//Trainer.labellingSla(f, 10000, listOfSlas);
		//Trainer.labelling(f,10000);
		//Trainer.generateModelCloud();
		//double[] coeffs = Trainer.linearRegression();
		//System.out.println("****coeffs:="+coeffs);
		
	}
	
	public static void tryOutSlas(File f, List<Map<String, Double>> listOfSlas){
		boolean failure = false;
		for(int i=0;i<listOfSlas.size();i++)
		{
			if(!failure)
			{
				exhaustiveSearchOptimalThresholdFailure(f, (Map<String, Double>)listOfSlas.get(i));
				Trainer.generateModelFailure();
				String[] cLevel = null;
				//File f_test = new File("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//test_data.arff");
				File f_test = new File("/tmp/test_data.arff");
				//File f = new File("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//training_data_final.arff");
				//File f = new File("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//trainData_final.arff");
				//File f_test = new File("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//testData.arff");
				
				//chooseConsistency(f,5366,200,3637475.387755102,10,0,50);
				cLevel = Learner.predict_Final_Failure(f_test);
				failure = Trainer.applyOperation(cLevel[0]);
				
				}
			//failure = true;
		}
	}
	
	
	public static boolean testFailure(Keyspace cfsKeyspace,String columnFamily,String cLevel,String updateStr){
		int count = 1;
		ByteBuffer key = null;
		List<Row> rows=SystemTable.serializedSchema(columnFamily);
		for (  Row row : rows) {
			key = null;
			key = row.key.key;
		}
		//RowMutation mutation=new RowMutation(Table.SYSTEM_TABLE,row.key.key);
		List<InetAddress> endpoints=StorageService.instance.getLiveNaturalEndpoints((org.apache.cassandra.db.Keyspace) cfsKeyspace, key);
		if(cLevel.contains("ANY") || cLevel.contains("ONE"))
			count = 1;
		else if(cLevel.contains("ALL"))
			count = endpoints.size();
		else if(cLevel.contains("QUORUM"))
			count = endpoints.size()/2;
		else if(cLevel.contains("TWO"))
			count = 2;
		int checkCount = 0;
		//DatabaseDescriptor.getEndpointSnitch().sortByProximity(FBUtilities.getLocalAddress(),endpoints);
		List<String> hosts=new ArrayList<String>(endpoints.size());
		for (  InetAddress endpoint : endpoints) {
			EndpointState es=Gossiper.instance.getEndpointStateForEndpoint(endpoint);
			//hosts.add(endpoint.getHostName());
			VersionedValue state=es.getApplicationState(ApplicationState.DC);
			 String DC=state.value;
			 System.out.println("DC value state:= "+DC);;
			 if(DC.contains(updateStr))
				 checkCount++;
		}
		if(checkCount>=count)
			return true;
		else
			return false;
		
	}
	
	public static boolean applyOperation(String cLevel){
		
		Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9160");
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace("failure");
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition("failure", "MyColumnFamily", ComparatorType.BYTESTYPE);
		if(keyspaceDef == null)
		{
			
			keyspaceDef = HFactory.createKeyspaceDefinition("failure", ThriftKsDef.DEF_STRATEGY_CLASS,  1, Arrays.asList(cfDef));
		
			// Add the schema to the cluster.
			// "true" as the second param means that Hector will block until all nodes see the change.
			cluster.addKeyspace(keyspaceDef, true);
			cluster.addColumnFamily(cfDef);
		}
		
		//Keyspace keyspace = HFactory.createKeyspace("failure", cluster);
		//for(CassandraHost host: cluster.getKnownPoolHosts(true))
		ConfigurableConsistencyLevel configurableConsistencyLevel = new ConfigurableConsistencyLevel();
		Map<String, HConsistencyLevel> clmap = new HashMap<String, HConsistencyLevel>();

		// Define CL.ONE for ColumnFamily "MyColumnFamily"

		//clmap.put("MyColumnFamily", HConsistencyLevel.ONE);
		clmap.put("MyColumnFamily", HConsistencyLevel.valueOf(cLevel));

		// In this we use CL.ONE for read and writes. But you can use different CLs if needed.
		configurableConsistencyLevel.setReadCfConsistencyLevels(clmap);
		configurableConsistencyLevel.setWriteCfConsistencyLevels(clmap);

		// Then let the keyspace know
		Keyspace keyspace = HFactory.createKeyspace("failure", cluster, configurableConsistencyLevel);
		//final StringSerializer se = new StringSerializer();
		//final LongSerializer le = new LongSerializer();
		CqlQuery<String,String,Long> cqlQuery = new CqlQuery<String,String,Long>(keyspace, stringSerializer, stringSerializer, LongSerializer.get());
		/*cqlQuery.setQuery("CREATE TABLE users (userid text PRIMARY KEY, first_name text, last_name text, emails set<text>, top_scores list<int>, todo map<timestamp, text>);");
		cqlQuery.execute();*/
		/*BasicColumnFamilyDefinition columnFamilyDefinition = new BasicColumnFamilyDefinition(cfDef);

        BasicColumnDefinition columnDefinition = new BasicColumnDefinition();
        columnDefinition.setName(StringSerializer.get().toByteBuffer("aKey"));
        columnDefinition.setIndexName("key_idx1");
        columnDefinition.setIndexType(ColumnIndexType.KEYS);
        columnDefinition.setValidationClass(ComparatorType.LONGTYPE.getClassName());
        columnFamilyDefinition.addColumnDefinition(columnDefinition);

        columnDefinition = new BasicColumnDefinition();
        columnDefinition.setName(StringSerializer.get().toByteBuffer("aTestColumn"));    
        columnDefinition.setValidationClass(ComparatorType.LONGTYPE.getClassName());
        columnFamilyDefinition.addColumnDefinition(columnDefinition);    

        cluster.updateColumnFamily(new ThriftCfDef(columnFamilyDefinition));*/
		
		//qlQuery.setQuery("INSERT INTO users (key, column1) VALUES(textAsBlob('frodo'), textAsBlob('Frodo'))");
		//cqlQuery.execute();
		
        Mutator<String> mutator = HFactory.createMutator(keyspace, stringSerializer);
        String id = UUID.randomUUID().toString();
        
        mutator.addInsertion(id, USER, HFactory.createStringColumn("full_name", "John"))
                .addInsertion(id, USER, HFactory.createStringColumn("email", "test"))
                .addInsertion(id, USER, HFactory.createStringColumn("state","yfcy"))
                .addInsertion(id, USER, HFactory.createStringColumn("gender", "male"))
                .addInsertion(id, USER, HFactory.createStringColumn("birth_year", "1990"));
        mutator.execute();
        
		boolean failure = Trainer.testFailure(keyspace,"MyColumnFamily",cLevel,"John");
		return failure;
	}
	
	public static void exhaustiveSearchOptimalThresholdFailure(File f, Map<String, Double> sla){
		String cLevel = "ONE", str = null,cLevelOld,input="",hmapKey = null;
		BufferedReader reader = null;
		String line = "";
		ArrayList<String> rows = new ArrayList<String>();
		Map<String,Map<String,ArrayList<String>>> hmap = null;
		Map<String,ArrayList<String>> hmapInner = new HashMap();
		int ave = 0;
		double maxVal = 0, val =0, valThreadCount = 0, valPacketCount = 0, valReadWriteProportions = 0;
		try {
			/*RandomAccessFile file = new RandomAccessFile(filePath, "r");
			file.seek(position);
			byte[] bytes = new byte[size];
			file.read(bytes);
			file.close();*/
			
			reader = new BufferedReader(
					new FileReader(f.getPath()));
			while ((line = reader.readLine())!= null) {
				if(!"".equalsIgnoreCase(line) && !line.contains("@"))
				{
					str = line.toString();
					valPacketCount = Double.parseDouble(str.split(",")[6]);
					valThreadCount = Double.parseDouble(str.split(",")[7]);
					valReadWriteProportions = Double.parseDouble(str.split(",")[8]);
					//rows.add(str);
					/*val = Double.parseDouble(str.split(",")[1])*wt_latency;
					val = Double.parseDouble(str.split(",")[2])*wt_throughput - val;
					//val = val + Double.parseDouble(line.split(",")[2])*wt_load;
					val = val - Double.parseDouble(str.split(",")[4])*wt_staleness;
					val = val -  Double.parseDouble(str.split(",")[5])*wt_retransmisson;*/
									
					//cLevel = str.split(",")[9].trim();
					//System.out.println("666 cLevel hmap line str:---"+str);
					if(hmap!=null && hmap.containsKey(valPacketCount+","+valThreadCount+","+valReadWriteProportions))
					{
						//System.out.println("882222444444444422222222else cLevel hmap line:---"+line);
						hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
						hmapInner = hmap.get(hmapKey);
						if(hmapInner!=null)
						{
						Iterator<Map.Entry<String, ArrayList<String>>> entriesInner = hmapInner.entrySet().iterator();
					        while (entriesInner.hasNext()) {
								Entry<String, ArrayList<String>> entryInner = entriesInner.next();
								rows = (ArrayList<String>) entryInner.getValue();
								if(entryInner.getKey()!=null && entryInner.getKey().contains(",") && entryInner.getKey().split(",").length>0)
									maxVal = Double.parseDouble((String)entryInner.getKey().split(",")[1]);
								rows.add(str);
								//System.out.println("1 rows add line:---"+line);
					        }
						}
						if(cLevel == null)
						{
							cLevel = str.split(",")[9].trim();
							//System.out.println("2 line:---"+line);
							
							//output = output + str + "\n";;
						}
						else //if(val>maxVal)
						{
							//hmapInner = hmap.get(cLevel+","+maxVal);
							if(hmapInner!=null && rows!=null && cLevel!=null)
							{
								 //rows =  new ArrayList();
								hmapInner.remove(cLevel+","+maxVal);
								hmap.remove(hmapKey);
								//rows.add(str);
								
								if(Double.parseDouble(str.split(",")[1])<=(Double)sla.get("latency") && Double.parseDouble(str.split(",")[5]) <=(Double)sla.get("retransmission") && Double.parseDouble(str.split(",")[2])>(Double)sla.get("throughput") && Double.parseDouble(str.split(",")[4])>(Double)sla.get("staleness"))
								//if(Double.parseDouble(str.split(",")[1])<=(int)sla.get("latency"))
									cLevel = str.split(",")[9].trim();
								//System.out.println("3 line:---"+hmapInner);
								maxVal = maxVal + val; 
								ave = ave + 1;
								//hmapInner.put(cLevel+","+maxVal, rows);
								
							}
							//hmapInner.put(cLevel, rows);
							//hmap.put(packetCount+","+valThreadCount+","+valReadWriteProportions, cLevel);
							
							//output = output + str + "\n";;
						}
						/*else
						{
							//System.out.println("8 line:---"+line);
							
							//output = output + str + "\n";;
						}*/
						
					}
					else if(hmap==null)
					{
						hmap = new HashMap();
						hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
						maxVal =  val;
						hmapInner = new HashMap();
						rows = new ArrayList();
						rows.add(str);
						//System.out.println("4 line:---"+hmapInner);
					}
					else if(hmapKey!=null && !hmapKey.equalsIgnoreCase(valPacketCount+","+valThreadCount+","+valReadWriteProportions))
					{
						
						/*if(hmapKey==null)
						{*/
							
							//if(cLevel == null)
								//cLevel = str.split(",")[9].trim();
							if(hmapInner==null)
								hmapInner = new HashMap();
							else
							{
								hmapInner.put(cLevel+","+maxVal, rows);
								hmap.put(hmapKey, hmapInner);
								hmapInner = new HashMap();
								rows = new ArrayList();
								ave = 0;
							}
							//System.out.println("5 line:---"+hmapInner);
							/*if(maxVal < val)
								maxVal = val;*/
							maxVal = maxVal + val; 
							rows.add(str);
							maxVal = maxVal / ave;
							//input = input + str + "\n";
							hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							//hmapInner = new HashMap();
							
							//System.out.println("3 line:---"+line);
							//output = output + str + "\n";;
							
						/*}
						else
						{
							if(cLevel == null)
								cLevel = str.split(",")[9].trim();
							rows.add(str);
							if(hmapInner==null)
								hmapInner = new HashMap();
							if(hmapInner!=null && cLevel!=null)
								hmapInner.put(cLevel+","+maxVal, rows);
							hmap.put(hmapKey, hmapInner);
							hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							cLevel = str.split(",")[9].trim();
							System.out.println("4 line:---"+line);
							//output = output + str + "\n";;
						}
						hmapInner = new HashMap();
					    rows =  new ArrayList();*/
					    
					}
					else
					{
						hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
						hmapInner = hmap.get(hmapKey);
						if(val>maxVal)
						{
							if(hmapInner!=null)
								hmapInner.remove(cLevel+","+maxVal);
							
							hmap.remove(hmapKey);
							//input = input + str + "\n";
							maxVal = val;
							cLevel = str.split(",")[9].trim();
							/*cLevel = str.split(",")[9].trim();
							//hmap.put(packetCount+","+valThreadCount+","+valReadWriteProportions, cLevel);
							System.out.println("5 line:---"+line);
							
							 rows.add(str);
							 hmapInner.put(cLevel+","+maxVal, rows);
							 hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							 hmap.put(hmapKey, hmapInner);*/
							//output = output + str + "\n";;
						}
						/*else
						{
							//System.out.println("cLevel hmap inner str:---"+str);
							//input = input + str + "\n";
							System.out.println("6 cLevel:---"+str);
							hmapInner = new HashMap();
							rows =  new ArrayList();
							rows.add(str);
							hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							hmap.put(hmapKey, hmapInner);
							//output = output + str + "\n";;
						}*/
						//System.out.println("6 line:---"+hmapInner);
						
						rows.add(str);
						if(hmapInner==null)
							hmapInner = new HashMap();
						 //hmapInner.put(cLevel+","+maxVal, rows);
						 //hmap.put(hmapKey, hmapInner);
					}
					//System.out.println("cLevel hmap inner:---"+cLevel);
					
				}
				else if(!"".equalsIgnoreCase(line))
				{
					input = input + line + "\n";
					//System.out.println("7 line:---"+line);
					//output = output + str + "\n";;
				}
				
			}
			
			
			//System.out.println("Iterator entries hmap size:---"+input);
			Iterator<Map.Entry<String, Map<String, ArrayList<String>>>> entries = hmap.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, Map<String, ArrayList<String>>> entry = entries.next();
		        //System.out.println(entry.getKey() + " = " + entry.getValue());
		        hmapInner = (Map<String, ArrayList<String>>) entry.getValue();
		        if(hmapInner!=null)
		        {
			        Iterator<Map.Entry<String, ArrayList<String>>> entriesInner = hmapInner.entrySet().iterator();
			        while (entriesInner.hasNext()) {
						Entry<String, ArrayList<String>> entryInner = entriesInner.next();
						rows = (ArrayList<String>) entryInner.getValue();
				        for(int i=0;i<rows.size();i++){
				        	str = (String)rows.get(i);
				        	cLevel = (String)entryInner.getKey().split(",")[0];
							cLevelOld = str.split(",")[9];
							//System.out.println("str value:---"+str+" :: cLevel:"+cLevel);
							if(str!=null && !"".equalsIgnoreCase(str) && cLevel!=null)
								str = str.replace(cLevelOld, cLevel);
				        	input = input + str + "\n";
				        }
			        }
		        }
		    }
			
			//File file = new File("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//training_data_final.arff");
			File file = new File("/tmp/training_data_final.arff");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(input);
			bw.close();
			//System.out.println("maxVal:---"+maxVal);
			//System.out.println("cLevel:---"+cLevel);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void labelling(File f,double boundResp){
		double valReqRate=0, valWorkloadType=0, valCost=0;
		String label = null, str = null,labelOld,input="",hmapKey = null;
		BufferedReader reader = null;
		String line = "";
		ArrayList<String> rows = new ArrayList<String>();
		Map<String,Map<String,ArrayList<String>>> hmap = null;
		Map<String,ArrayList<String>> hmapInner = new HashMap();
		//int ave = 0;
		double minVal = 0, minValOld=0;
		try {
			reader = new BufferedReader(
					new FileReader(f.getPath()));
			while ((line = reader.readLine())!= null) {
				if(!"".equalsIgnoreCase(line) && !line.contains("@"))
				{
					str = line.toString();
					valWorkloadType = Double.parseDouble(str.split(",")[0]);
					valReqRate = Double.parseDouble(str.split(",")[1]);
					valCost = Double.parseDouble(str.split(",")[2]);;
					if(hmap!=null && hmap.containsKey(valWorkloadType+","+valReqRate))
					{
						hmapKey = valWorkloadType+","+valReqRate;
						hmapInner = hmap.get(hmapKey);
						if(hmapInner!=null)
						{
							Iterator<Map.Entry<String, ArrayList<String>>> entriesInner = hmapInner.entrySet().iterator();
					        while (entriesInner.hasNext()) {
								Entry<String, ArrayList<String>> entryInner = entriesInner.next();
								rows = (ArrayList<String>) entryInner.getValue();
								if(entryInner.getKey()!=null && entryInner.getKey().contains(",") && entryInner.getKey().split(",").length>0)
									minVal = Double.parseDouble((String)entryInner.getKey().split(",")[1]);
								rows.add(str);
								//System.out.println("1 rows add line:---"+line);
					        }
						}
						if(label == null && Double.parseDouble(str.split(",")[3].trim()) < boundResp)
						{
							label = str.split(",")[4].trim();
							
						}
						else //if(val>maxVal)
						{
							//hmapInner = hmap.get(cLevel+","+maxVal);
							if(hmapInner!=null && rows!=null && label!=null)
							{
								 //rows =  new ArrayList();
								//hmapInner.remove(label+","+minVal);
								//hmap.remove(hmapKey);
								//if(valCost < minVal  && Double.parseDouble(str.split(",")[3].trim()) < boundResp)
								if(Double.parseDouble(str.split(",")[3].trim()) < boundResp)
								{
									label = str.split(",")[4].trim();
									minVal = valCost;
								}	
							}
							
						}
					}
					else if(hmap==null)
					{
						hmap = new HashMap();
						hmapKey = valWorkloadType +","+valReqRate;
						minVal =  valCost;
						//hmapInner = new HashMap();
						//rows = new ArrayList();
						//rows.add(str);
						label = str.split(",")[4].trim();
						//minVal = valCost;
						//System.out.println("4 line:---"+hmapInner);
					}
					else if(hmapKey!=null && !hmapKey.equalsIgnoreCase(valWorkloadType+","+valReqRate))
					{
						
							if(hmapInner==null)
								hmapInner = new HashMap();
							else
							{
								hmapInner.put(label+","+minVal, rows);
								hmap.put(hmapKey, hmapInner);
								hmapInner = new HashMap();
								rows = new ArrayList();
							}
							
							minVal = valCost; 
							rows.add(str);
							label = str.split(",")[4].trim();
							//input = input + str + "\n";
							hmapKey =valWorkloadType +","+valReqRate;
							
					    
					}
					else
					{
						hmapKey =valWorkloadType +","+valReqRate;
						hmapInner = hmap.get(hmapKey);
						if(valCost<minVal  && Double.parseDouble(str.split(",")[3].trim()) < boundResp)
						{
							if(hmapInner!=null)
								hmapInner.remove(label+","+minVal);
							
							hmap.remove(hmapKey);
							//input = input + str + "\n";
							minVal = valCost;
							label = str.split(",")[4].trim();
							
						}
						
						rows.add(str);
						if(hmapInner==null)
							hmapInner = new HashMap();
						 
					}
				}
				else if(!"".equalsIgnoreCase(line))
				{
					input = input + line + "\n";
					
				}
				
			}
			
			//int x =0;
			//System.out.println("Iterator entries hmap size:---"+hmap.size());
			Iterator<Map.Entry<String, Map<String, ArrayList<String>>>> entries = hmap.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, Map<String, ArrayList<String>>> entry = entries.next();
		        //System.out.println(entry.getKey() + " = " + entry.getValue());
		        hmapInner = (Map<String, ArrayList<String>>) entry.getValue();
		        if(hmapInner!=null)
		        {
			        Iterator<Map.Entry<String, ArrayList<String>>> entriesInner = hmapInner.entrySet().iterator();
			        while (entriesInner.hasNext()) {
						Entry<String, ArrayList<String>> entryInner = entriesInner.next();
						rows = (ArrayList<String>) entryInner.getValue();
				        for(int i=0;i<rows.size();i++){
				        	//x++;
				        	str = (String)rows.get(i);
				        	label = (String)entryInner.getKey().split(",")[0];
				        	labelOld = str.split(",")[4];
				        	minVal = Double.parseDouble((String)entryInner.getKey().split(",")[1]);
				        	minValOld = Double.parseDouble(str.split(",")[2]);
							//System.out.println("str value:---"+str+" :: cLevel:"+cLevel);
							if(str!=null && !"".equalsIgnoreCase(str) && label!=null)
							{
								str = str.replace(labelOld, label);
								str = str.replace(String.valueOf(minValOld), String.valueOf(minVal));
							}
				        	input = input + str + "\n";
				        }
			        }
		        }
		    }
			//System.out.println("Iterator entries hmap size:---"+x);
			File file = new File("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//trainData_final.arff");
			 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(input);
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void exhaustiveSearchOptimalThreshold(File f,double wt_latency,double wt_throughput,double wt_staleness,double wt_retransmisson,double wt_load,double wt_packetloss){
		String cLevel = "ONE", str = null,cLevelOld,input="",hmapKey = null;
		BufferedReader reader = null;
		String line = "";
		ArrayList<String> rows = new ArrayList<String>();
		Map<String,Map<String,ArrayList<String>>> hmap = null;
		Map<String,ArrayList<String>> hmapInner = new HashMap();
		int ave = 0;
		double maxVal = 0, val =0, valThreadCount = 0, valPacketCount = 0, valReadWriteProportions = 0;
		try {
			/*RandomAccessFile file = new RandomAccessFile(filePath, "r");
			file.seek(position);
			byte[] bytes = new byte[size];
			file.read(bytes);
			file.close();*/
			
			reader = new BufferedReader(
					new FileReader(f.getPath()));
			while ((line = reader.readLine())!= null) {
				if(!"".equalsIgnoreCase(line) && !line.contains("@"))
				{
					str = line.toString();
					valPacketCount = Double.parseDouble(str.split(",")[6]);
					valThreadCount = Double.parseDouble(str.split(",")[7]);
					valReadWriteProportions = Double.parseDouble(str.split(",")[8]);
					//rows.add(str);
					/*val = Double.parseDouble(str.split(",")[1])*wt_latency;
					val = Double.parseDouble(str.split(",")[2])*wt_throughput - val;
					//val = val + Double.parseDouble(line.split(",")[2])*wt_load;
					val = val - Double.parseDouble(str.split(",")[4])*wt_staleness;
					val = val -  Double.parseDouble(str.split(",")[5])*wt_retransmisson;*/
									
					//cLevel = str.split(",")[9].trim();
					//System.out.println("666 cLevel hmap line str:---"+str);
					if(hmap!=null && hmap.containsKey(valPacketCount+","+valThreadCount+","+valReadWriteProportions))
					{
						//System.out.println("882222444444444422222222else cLevel hmap line:---"+line);
						hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
						hmapInner = hmap.get(hmapKey);
						if(hmapInner!=null)
						{
						Iterator<Map.Entry<String, ArrayList<String>>> entriesInner = hmapInner.entrySet().iterator();
					        while (entriesInner.hasNext()) {
								Entry<String, ArrayList<String>> entryInner = entriesInner.next();
								rows = (ArrayList<String>) entryInner.getValue();
								if(entryInner.getKey()!=null && entryInner.getKey().contains(",") && entryInner.getKey().split(",").length>0)
									maxVal = Double.parseDouble((String)entryInner.getKey().split(",")[1]);
								rows.add(str);
								//System.out.println("1 rows add line:---"+line);
					        }
						}
						if(cLevel == null)
						{
							cLevel = str.split(",")[9].trim();
							//System.out.println("2 line:---"+line);
							
							//output = output + str + "\n";;
						}
						else //if(val>maxVal)
						{
							//hmapInner = hmap.get(cLevel+","+maxVal);
							if(hmapInner!=null && rows!=null && cLevel!=null)
							{
								 //rows =  new ArrayList();
								hmapInner.remove(cLevel+","+maxVal);
								hmap.remove(hmapKey);
								//rows.add(str);
								
								if(Double.parseDouble(str.split(",")[1])<=wt_latency && Double.parseDouble(str.split(",")[5]) <=wt_retransmisson && Double.parseDouble(str.split(",")[2])>wt_throughput && Double.parseDouble(str.split(",")[4])>wt_staleness)
									cLevel = str.split(",")[9].trim();
								//System.out.println("3 line:---"+hmapInner);
								maxVal = maxVal + val; 
								ave = ave + 1;
								//hmapInner.put(cLevel+","+maxVal, rows);
								
							}
							//hmapInner.put(cLevel, rows);
							//hmap.put(packetCount+","+valThreadCount+","+valReadWriteProportions, cLevel);
							
							//output = output + str + "\n";;
						}
						/*else
						{
							//System.out.println("8 line:---"+line);
							
							//output = output + str + "\n";;
						}*/
						
					}
					else if(hmap==null)
					{
						hmap = new HashMap();
						hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
						maxVal =  val;
						hmapInner = new HashMap();
						rows = new ArrayList();
						rows.add(str);
						//System.out.println("4 line:---"+hmapInner);
					}
					else if(hmapKey!=null && !hmapKey.equalsIgnoreCase(valPacketCount+","+valThreadCount+","+valReadWriteProportions))
					{
						
						/*if(hmapKey==null)
						{*/
							
							//if(cLevel == null)
								//cLevel = str.split(",")[9].trim();
							if(hmapInner==null)
								hmapInner = new HashMap();
							else
							{
								hmapInner.put(cLevel+","+maxVal, rows);
								hmap.put(hmapKey, hmapInner);
								hmapInner = new HashMap();
								rows = new ArrayList();
								ave = 0;
							}
							//System.out.println("5 line:---"+hmapInner);
							/*if(maxVal < val)
								maxVal = val;*/
							maxVal = maxVal + val; 
							rows.add(str);
							maxVal = maxVal / ave;
							//input = input + str + "\n";
							hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							//hmapInner = new HashMap();
							
							//System.out.println("3 line:---"+line);
							//output = output + str + "\n";;
							
						/*}
						else
						{
							if(cLevel == null)
								cLevel = str.split(",")[9].trim();
							rows.add(str);
							if(hmapInner==null)
								hmapInner = new HashMap();
							if(hmapInner!=null && cLevel!=null)
								hmapInner.put(cLevel+","+maxVal, rows);
							hmap.put(hmapKey, hmapInner);
							hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							cLevel = str.split(",")[9].trim();
							System.out.println("4 line:---"+line);
							//output = output + str + "\n";;
						}
						hmapInner = new HashMap();
					    rows =  new ArrayList();*/
					    
					}
					else
					{
						hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
						hmapInner = hmap.get(hmapKey);
						if(val>maxVal)
						{
							if(hmapInner!=null)
								hmapInner.remove(cLevel+","+maxVal);
							
							hmap.remove(hmapKey);
							//input = input + str + "\n";
							maxVal = val;
							cLevel = str.split(",")[9].trim();
							/*cLevel = str.split(",")[9].trim();
							//hmap.put(packetCount+","+valThreadCount+","+valReadWriteProportions, cLevel);
							System.out.println("5 line:---"+line);
							
							 rows.add(str);
							 hmapInner.put(cLevel+","+maxVal, rows);
							 hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							 hmap.put(hmapKey, hmapInner);*/
							//output = output + str + "\n";;
						}
						/*else
						{
							//System.out.println("cLevel hmap inner str:---"+str);
							//input = input + str + "\n";
							System.out.println("6 cLevel:---"+str);
							hmapInner = new HashMap();
							rows =  new ArrayList();
							rows.add(str);
							hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							hmap.put(hmapKey, hmapInner);
							//output = output + str + "\n";;
						}*/
						//System.out.println("6 line:---"+hmapInner);
						
						rows.add(str);
						if(hmapInner==null)
							hmapInner = new HashMap();
						 //hmapInner.put(cLevel+","+maxVal, rows);
						 //hmap.put(hmapKey, hmapInner);
					}
					//System.out.println("cLevel hmap inner:---"+cLevel);
					
				}
				else if(!"".equalsIgnoreCase(line))
				{
					input = input + line + "\n";
					//System.out.println("7 line:---"+line);
					//output = output + str + "\n";;
				}
				
			}
			
			
			//System.out.println("Iterator entries hmap size:---"+input);
			Iterator<Map.Entry<String, Map<String, ArrayList<String>>>> entries = hmap.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, Map<String, ArrayList<String>>> entry = entries.next();
		        //System.out.println(entry.getKey() + " = " + entry.getValue());
		        hmapInner = (Map<String, ArrayList<String>>) entry.getValue();
		        if(hmapInner!=null)
		        {
			        Iterator<Map.Entry<String, ArrayList<String>>> entriesInner = hmapInner.entrySet().iterator();
			        while (entriesInner.hasNext()) {
						Entry<String, ArrayList<String>> entryInner = entriesInner.next();
						rows = (ArrayList<String>) entryInner.getValue();
				        for(int i=0;i<rows.size();i++){
				        	str = (String)rows.get(i);
				        	cLevel = (String)entryInner.getKey().split(",")[0];
							cLevelOld = str.split(",")[9];
							//System.out.println("str value:---"+str+" :: cLevel:"+cLevel);
							if(str!=null && !"".equalsIgnoreCase(str) && cLevel!=null)
								str = str.replace(cLevelOld, cLevel);
				        	input = input + str + "\n";
				        }
			        }
		        }
		    }
			
			File file = new File("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//training_data_final.arff");
			 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(input);
			bw.close();
			//System.out.println("maxVal:---"+maxVal);
			//System.out.println("cLevel:---"+cLevel);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void exhaustiveSearchOptimal(File f,double wt_latency,double wt_throughput,double wt_staleness,double wt_retransmisson,double wt_load,double wt_packetloss){
		String cLevel = "ONE", str = null,cLevelOld,input="",hmapKey = null;
		BufferedReader reader = null;
		String line = "";
		ArrayList<String> rows = new ArrayList<String>();
		Map<String,Map<String,ArrayList<String>>> hmap = null;
		Map<String,ArrayList<String>> hmapInner = new HashMap();
		int ave = 0;
		double maxVal = 0, val =0, valThreadCount = 0, valPacketCount = 0, valReadWriteProportions = 0;
		try {
			/*RandomAccessFile file = new RandomAccessFile(filePath, "r");
			file.seek(position);
			byte[] bytes = new byte[size];
			file.read(bytes);
			file.close();*/
			
			reader = new BufferedReader(
					new FileReader(f.getPath()));
			while ((line = reader.readLine())!= null) {
				if(!"".equalsIgnoreCase(line) && !line.contains("@"))
				{
					str = line.toString();
					valPacketCount = Double.parseDouble(str.split(",")[6]);
					valThreadCount = Double.parseDouble(str.split(",")[7]);
					valReadWriteProportions = Double.parseDouble(str.split(",")[8]);
					//rows.add(str);
					val = Double.parseDouble(str.split(",")[1])*wt_latency;
					val = Double.parseDouble(str.split(",")[2])*wt_throughput - val;
					//val = val + Double.parseDouble(line.split(",")[2])*wt_load;
					val = val - Double.parseDouble(str.split(",")[4])*wt_staleness;
					val = val -  Double.parseDouble(str.split(",")[5])*wt_retransmisson;
					//cLevel = str.split(",")[9].trim();
					//System.out.println("666 cLevel hmap line str:---"+str);
					if(hmap!=null && hmap.containsKey(valPacketCount+","+valThreadCount+","+valReadWriteProportions))
					{
						//System.out.println("882222444444444422222222else cLevel hmap line:---"+line);
						hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
						hmapInner = hmap.get(hmapKey);
						if(hmapInner!=null)
						{
						Iterator<Map.Entry<String, ArrayList<String>>> entriesInner = hmapInner.entrySet().iterator();
					        while (entriesInner.hasNext()) {
								Entry<String, ArrayList<String>> entryInner = entriesInner.next();
								rows = (ArrayList<String>) entryInner.getValue();
								if(entryInner.getKey()!=null && entryInner.getKey().contains(",") && entryInner.getKey().split(",").length>0)
									maxVal = Double.parseDouble((String)entryInner.getKey().split(",")[1]);
								rows.add(str);
								//System.out.println("1 rows add line:---"+line);
					        }
						}
						if(cLevel == null)
						{
							cLevel = str.split(",")[9].trim();
							//System.out.println("2 line:---"+line);
							
							//output = output + str + "\n";;
						}
						else //if(val>maxVal)
						{
							//hmapInner = hmap.get(cLevel+","+maxVal);
							if(hmapInner!=null && rows!=null && cLevel!=null)
							{
								 //rows =  new ArrayList();
								hmapInner.remove(cLevel+","+maxVal);
								hmap.remove(hmapKey);
								//rows.add(str);
								cLevel = str.split(",")[9].trim();
								//System.out.println("3 line:---"+hmapInner);
								maxVal = maxVal + val; 
								ave = ave + 1;
								//hmapInner.put(cLevel+","+maxVal, rows);
								
							}
							//hmapInner.put(cLevel, rows);
							//hmap.put(packetCount+","+valThreadCount+","+valReadWriteProportions, cLevel);
							
							//output = output + str + "\n";;
						}
						/*else
						{
							//System.out.println("8 line:---"+line);
							
							//output = output + str + "\n";;
						}*/
						
					}
					else if(hmap==null)
					{
						hmap = new HashMap();
						hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
						maxVal =  val;
						hmapInner = new HashMap();
						rows = new ArrayList();
						rows.add(str);
						//System.out.println("4 line:---"+hmapInner);
					}
					else if(hmapKey!=null && !hmapKey.equalsIgnoreCase(valPacketCount+","+valThreadCount+","+valReadWriteProportions))
					{
						
						/*if(hmapKey==null)
						{*/
							
							//if(cLevel == null)
								//cLevel = str.split(",")[9].trim();
							if(hmapInner==null)
								hmapInner = new HashMap();
							else
							{
								hmapInner.put(cLevel+","+maxVal, rows);
								hmap.put(hmapKey, hmapInner);
								hmapInner = new HashMap();
								rows = new ArrayList();
								ave = 0;
							}
							//System.out.println("5 line:---"+hmapInner);
							/*if(maxVal < val)
								maxVal = val;*/
							maxVal = maxVal + val; 
							rows.add(str);
							maxVal = maxVal / ave;
							//input = input + str + "\n";
							hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							//hmapInner = new HashMap();
							
							//System.out.println("3 line:---"+line);
							//output = output + str + "\n";;
							
						/*}
						else
						{
							if(cLevel == null)
								cLevel = str.split(",")[9].trim();
							rows.add(str);
							if(hmapInner==null)
								hmapInner = new HashMap();
							if(hmapInner!=null && cLevel!=null)
								hmapInner.put(cLevel+","+maxVal, rows);
							hmap.put(hmapKey, hmapInner);
							hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							cLevel = str.split(",")[9].trim();
							System.out.println("4 line:---"+line);
							//output = output + str + "\n";;
						}
						hmapInner = new HashMap();
					    rows =  new ArrayList();*/
					    
					}
					else
					{
						hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
						hmapInner = hmap.get(hmapKey);
						if(val>maxVal)
						{
							if(hmapInner!=null)
								hmapInner.remove(cLevel+","+maxVal);
							
							hmap.remove(hmapKey);
							//input = input + str + "\n";
							maxVal = val;
							cLevel = str.split(",")[9].trim();
							/*cLevel = str.split(",")[9].trim();
							//hmap.put(packetCount+","+valThreadCount+","+valReadWriteProportions, cLevel);
							System.out.println("5 line:---"+line);
							
							 rows.add(str);
							 hmapInner.put(cLevel+","+maxVal, rows);
							 hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							 hmap.put(hmapKey, hmapInner);*/
							//output = output + str + "\n";;
						}
						/*else
						{
							//System.out.println("cLevel hmap inner str:---"+str);
							//input = input + str + "\n";
							System.out.println("6 cLevel:---"+str);
							hmapInner = new HashMap();
							rows =  new ArrayList();
							rows.add(str);
							hmapKey = valPacketCount+","+valThreadCount+","+valReadWriteProportions;
							hmap.put(hmapKey, hmapInner);
							//output = output + str + "\n";;
						}*/
						//System.out.println("6 line:---"+hmapInner);
						
						rows.add(str);
						if(hmapInner==null)
							hmapInner = new HashMap();
						 //hmapInner.put(cLevel+","+maxVal, rows);
						 //hmap.put(hmapKey, hmapInner);
					}
					//System.out.println("cLevel hmap inner:---"+cLevel);
					
				}
				else if(!"".equalsIgnoreCase(line))
				{
					input = input + line + "\n";
					//System.out.println("7 line:---"+line);
					//output = output + str + "\n";;
				}
				
			}
			
			
			//System.out.println("Iterator entries hmap size:---"+input);
			Iterator<Map.Entry<String, Map<String, ArrayList<String>>>> entries = hmap.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, Map<String, ArrayList<String>>> entry = entries.next();
		        //System.out.println(entry.getKey() + " = " + entry.getValue());
		        hmapInner = (Map<String, ArrayList<String>>) entry.getValue();
		        if(hmapInner!=null)
		        {
			        Iterator<Map.Entry<String, ArrayList<String>>> entriesInner = hmapInner.entrySet().iterator();
			        while (entriesInner.hasNext()) {
						Entry<String, ArrayList<String>> entryInner = entriesInner.next();
						rows = (ArrayList<String>) entryInner.getValue();
				        for(int i=0;i<rows.size();i++){
				        	str = (String)rows.get(i);
				        	cLevel = (String)entryInner.getKey().split(",")[0];
							cLevelOld = str.split(",")[9];
							//System.out.println("str value:---"+str+" :: cLevel:"+cLevel);
							if(str!=null && !"".equalsIgnoreCase(str) && cLevel!=null)
								str = str.replace(cLevelOld, cLevel);
				        	input = input + str + "\n";
				        }
			        }
		        }
		    }
			
			File file = new File("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//training_data_final.arff");
			 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(input);
			bw.close();
			//System.out.println("maxVal:---"+maxVal);
			//System.out.println("cLevel:---"+cLevel);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//public static void exhaustiveSearch(File f,double wt_latency,double wt_throughput,double wt_staleness,double wt_retransmisson,double wt_load,double wt_packetloss,double packetCount, double threadCount,double readWriteProprtion){
	public static void exhaustiveSearch(File f,double wt_latency,double wt_throughput,double wt_staleness,double wt_retransmisson,double wt_load,double wt_packetloss){
		double packetCount=0, threadCount=0, readWriteProportions=0;
		String cLevel = "ONE", str,cLevelOld,input="";
		BufferedReader reader = null;
		String line = "";
		ArrayList<String> rows = new ArrayList<String>();
		HashMap hmap = new HashMap();
		double maxVal = 0, val =0, valThreadCount = 0, valPacketCount = 0, valReadWriteProportions = 0;
		try {
			/*RandomAccessFile file = new RandomAccessFile(filePath, "r");
			file.seek(position);
			byte[] bytes = new byte[size];
						hmapInner = new HashMap();
					    rows =  new ArrayList();
					    
			file.read(bytes);
			file.close();*/
			
			reader = new BufferedReader(
					new FileReader(f.getPath()));
			while ((line = reader.readLine())!= null) {
				if(!"".equalsIgnoreCase(line) && !line.contains("@"))
				{
					rows.add(line.toString());
				}
				else if(!"".equalsIgnoreCase(line))
					input = input + line.toString() + "\n";
			}
			for(int i=0;i<rows.size();i++){
				    str = (String)rows.get(i);
					valPacketCount = Double.parseDouble(str.split(",")[6]);
					valThreadCount = Double.parseDouble(str.split(",")[7]);
					valReadWriteProportions = Double.parseDouble(str.split(",")[8]);
					for(int j=0;j<rows.size();j++){
						packetCount = Double.parseDouble(str.split(",")[6]);
						threadCount = Double.parseDouble(str.split(",")[7]);
						readWriteProportions = Double.parseDouble(str.split(",")[8]);
						if((valPacketCount==packetCount) && (valThreadCount==threadCount) && (valReadWriteProportions==readWriteProportions))
						{
							val = Double.parseDouble(str.split(",")[1])*wt_latency;
							val = Double.parseDouble(str.split(",")[2])*wt_throughput - val;
							//val = val + Double.parseDouble(line.split(",")[2])*wt_load;
							val = val - Double.parseDouble(str.split(",")[4])*wt_staleness;
							val = val -  Double.parseDouble(str.split(",")[5])*wt_retransmisson;
							//System.out.println("***line val:---"+val);
							if(val>maxVal)
							{
								maxVal = val;
								cLevel = str.split(",")[9].trim();
								hmap.put(packetCount+","+valThreadCount+","+valReadWriteProportions, cLevel);
								
							}
						}
							
					}

			}
			for(int i=0;i<rows.size();i++){
			    str = (String)rows.get(i);
				valPacketCount = Double.parseDouble(str.split(",")[6]);
				valThreadCount = Double.parseDouble(str.split(",")[7]);
				valReadWriteProportions = Double.parseDouble(str.split(",")[8]);
				cLevel = (String)hmap.get(packetCount+","+valThreadCount+","+valReadWriteProportions);
				cLevelOld = str.split(",")[8];
				System.out.println("str value:---"+str);
				if(str!=null && !"".equalsIgnoreCase(str) && cLevel!=null)
				{
					str.replace(cLevelOld, cLevel);
					rows.set(i,str);
				}
				input = input + str + "\n";
			}
			File file = new File("/tmp/training_data_final.arff");
			 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(input);
			bw.close();
			//System.out.println("maxVal:---"+maxVal);
			//System.out.println("cLevel:---"+cLevel);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getLMS()
	{
		double[][] data = { { 1, 3 }, {2, 5 }, {3, 7 }, {4, 14 }, {5, 11 }};
		SimpleRegression regression = new SimpleRegression();
		regression.addData(data);
		//System.out.println("***regression predict:"+regression.predict(1));
	    // displays predicted y value for x = 1.5
	}
	public static double[] linearRegression()
	{
		NominalToBinary m_NominalToBinary = new NominalToBinary();
		File f_final = new File("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//trainData.arff");
		//File f_final = new File("/tmp/trainData.arff");
		BufferedReader reader;
		double [] lmCoeffs = null;
		try {
			reader = new BufferedReader(
					new FileReader(f_final.getPath()));
		
		Instances data = new Instances(reader);
		m_NominalToBinary.setInputFormat(data);
		Instances reducedInst = Filter.useFilter(data, m_NominalToBinary);
		reducedInst.setClassIndex(reducedInst.numAttributes()-1);
		new LinearRegression();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lmCoeffs;
	}
	public static double optimize(double[] x)
	{
		double minFunction = 0;
		// TODO Auto-generated method stub
		//Optimizable opt = new Optimizable();
		int numVariables = 12;
		//double[] x = {1,2,3,4};
		// Lower and upper bounds: 1st row is lower bounds, 2nd is upper
		int cnstr = 2;
		double[][] constraints = new double[cnstr][numVariables];
		//for(int i=0;i<cnstr;i++)
			for(int j=0;j<numVariables;j++)	
				constraints[0][j] =0.0000000000000000000000000000000000000000000000000000000;
			for(int j=0;j<numVariables;j++)	
				constraints[1][j] =100;
		// Find the minimum, 200 iterations as default
		try {
			//x = opt.findArgmin(x, constraints);
		
		while(x == null){  // 200 iterations are not enough
		    //x = opt.getVarbValues();  // Try another 200 iterations
		    //x = opt.findArgmin(x, constraints);
		}		 
		// The minimal function value
		//minFunction = opt.getMinFunction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("***minFunction: "+Arrays.toString(x));
		return minFunction;
		//System.out.println("***minFunction: "+minFunction);
	}
	public static void generateModelFailure()
	{
		Instances train = null;
		ObjectOutputStream oos = null;
        //File f_final = new File("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//training_data_final.arff");
		File f_final = new File("/tmp/training_data_final.arff");
        BufferedReader reader = null;
		new ByteArrayOutputStream();			
		FilteredClassifier fc = null;
		new StringBuffer();
		try {
			
            reader = new BufferedReader(
						new FileReader(f_final.getPath()));
		         //System.out.println("*******in f exists"+f.getPath());
		         train = new Instances(reader);
		  reader.close();
		 // setting class attribute
         train.setClassIndex(train.numAttributes() - 1);
         Remove rm = new Remove();
         rm.setAttributeIndices("1");  // remove 1st attribute
         // classifier
         J48 j48 = new J48();
        // NaiveBayes  bayes = new NaiveBayes ();
         j48.setUnpruned(true);        // using an unpruned J48
         // meta-classifier
         fc = new FilteredClassifier();
         fc.setFilter(rm);
         //fc.setClassifier(bayes);
         fc.setClassifier(j48); 
         
         // train and make predictions
         
            fc.buildClassifier(train);
            
         // display classifier
            /*final javax.swing.JFrame jf = 
              new javax.swing.JFrame("Weka Classifier Tree Visualizer: J48");
            jf.setSize(500,400);
            jf.getContentPane().setLayout(new BorderLayout());
            TreeVisualizer tv = new TreeVisualizer(null,
                fc.graph(),
                new PlaceNode2());
            jf.getContentPane().add(tv, BorderLayout.CENTER);
            jf.addWindowListener(new java.awt.event.WindowAdapter() {
              public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
              }
            });

            jf.setVisible(true);
            tv.fitToScreen();*/
            
            oos = new ObjectOutputStream(
            		new FileOutputStream("/tmp/j48.model"));
                    //new FileOutputStream("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//j48.model"));
			oos.writeObject(fc);
			oos.flush();
			oos.close();
            
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void generateModelCloud()
	{
		//Instances train = null;
		//Instances train_tmp = null;
        ObjectOutputStream oos = null;
        new ByteArrayOutputStream();			
		new StringBuffer();
		try {
			FileReader trainreader = new FileReader("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//trainData_final.arff"); 
			FileReader testreader = new FileReader("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//testData.arff"); 


			Instances train = new Instances(trainreader); 
			Instances test = new Instances(testreader); 
			train.setClassIndex(train.numAttributes() - 1); 
			test.setClassIndex(test.numAttributes() - 1); 

			MultilayerPerceptron mlp = new MultilayerPerceptron(); 
			//mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 500 -V 40 -S 0 -E 20 -H 35")); 


			mlp.buildClassifier(train); 
			
			/*Evaluation eval = new Evaluation(train); 
			eval.evaluateModel(mlp, test); 
			System.out.println(eval.toSummaryString("\nResults\n======\n", false)); */
			trainreader.close(); 
			testreader.close(); 
            oos = new ObjectOutputStream(
                    new FileOutputStream("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//j48.model"));
			oos.writeObject(mlp);
			oos.flush();
			oos.close();
            
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void generateModel()
	{
		Instances train = null;
		ObjectOutputStream oos = null;
        File f_final = new File("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//trainData_final.arff");
        BufferedReader reader = null;
		new ByteArrayOutputStream();			
		FilteredClassifier fc = null;
		new StringBuffer();
		try {
			
            reader = new BufferedReader(
						new FileReader(f_final.getPath()));
		         //System.out.println("*******in f exists"+f.getPath());
		         train = new Instances(reader);
		  reader.close();
		 // setting class attribute
         train.setClassIndex(train.numAttributes() - 1);
         Remove rm = new Remove();
         //rm.setAttributeIndices("1");  // remove 1st attribute
         // classifier
         //J48 j48 = new J48();
         NaiveBayes  bayes = new NaiveBayes ();
         //j48.setUnpruned(true);        // using an unpruned J48
         // meta-classifier
         fc = new FilteredClassifier();
         fc.setFilter(rm);
         fc.setClassifier(bayes);
         //fc.setClassifier(j48); 
         
         // train and make predictions
         
            fc.buildClassifier(train);
            
         // display classifier
            /*final javax.swing.JFrame jf = 
              new javax.swing.JFrame("Weka Classifier Tree Visualizer: J48");
            jf.setSize(500,400);
            jf.getContentPane().setLayout(new BorderLayout());
            TreeVisualizer tv = new TreeVisualizer(null,
                fc.graph(),
                new PlaceNode2());
            jf.getContentPane().add(tv, BorderLayout.CENTER);
            jf.addWindowListener(new java.awt.event.WindowAdapter() {
              public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
              }
            });

            jf.setVisible(true);
            tv.fitToScreen();*/
            
            oos = new ObjectOutputStream(
                    new FileOutputStream("C://Users//ssidha1//Dropbox//CLoudAnalyticsLatest//data//j48.model"));
			oos.writeObject(fc);
			oos.flush();
			oos.close();
            
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void generateModel_old()
	{
		Instances train = null;
		Instances train_tmp = null;
        ObjectOutputStream oos = null;
        File f_final = new File("/tmp/training_data_final.arff");
        BufferedReader reader = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();			
		byte[] buf = new byte[4096];
		int n;	
		byte[] data = null;
		FileOutputStream out = null;
		FilteredClassifier fc = null;
		Process p;
		String command = null;
		new StringBuffer();
		try {
			if(f_final.exists()) 
				f_final.delete();
			f_final.createNewFile(); //the true will append the new data
			command = "sudo /home/ubuntu/apache-cassandra-2.0.6/bin/nodetool --host=172.31.21.207 --port=9999 status";
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			reader = 
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";		
            int i = 0;
            String[] listHost = new String[30];
			while ((line = reader.readLine())!= null) {
				if(line.toString().split("  ").length>1 && !"Address".contains(line.toString().split("  ")[1]))
				{
					listHost[i] = line.toString().split("  ")[1];
					System.out.println("***nodetool output host: "+listHost[i]);
					i++;
				}
				
			}
			Cluster cluster = HFactory.getOrCreateCluster("Test Cluster","172.31.21.207:9999");
			cluster.describeKeyspace("ycsb");
			//for(CassandraHost host: cluster.getKnownPoolHosts(true))
			int j = 0;
			while(listHost[j]!=null && !"".equalsIgnoreCase(listHost[j]))
			{
				System.out.println("*Cluster Host Name:"+listHost[j]);
				JSch jsch = new JSch();

				Session session = jsch.getSession( "admins", listHost[j], 22 );    
				// non-interactive version. Relies in host key being in known-hosts file
				session.setPassword( "changeme1234567890987654321" );
				//System.out.println("***just before sftp session connect :"+session.getTimeout());
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect(30000);
				//System.out.println("***after sftp session connect :"+session.getTimeout());
				Channel channel = session.openChannel( "sftp" );
				channel.connect();
				//System.out.println("***after channel connect:"+channel.getId());
				ChannelSftp sftpChannel = (ChannelSftp) channel;
				//System.out.println("***after sftp cast from chjannel:"+sftpChannel.getId());
				//sftpChannel.get("remote-file", "local-file" );
				// OR
				InputStream in = sftpChannel.get( "/tmp/training_data_final.arff" );
				  // process inputstream as needed
				//System.out.println("***after sftp channel get file:"+in.available());
				
				while ((n = in.read(buf)) >= 0) 
					os.write(buf, 0, n);
				os.close();			
				data = os.toByteArray();
				out = new FileOutputStream(f_final);
				out.write(data);
				out.flush();
				out.close();
				sftpChannel.exit();
				session.disconnect();
				//System.out.println("***after sftp close:");
				reader = new BufferedReader(
						new FileReader(f_final.getPath()));
		         //System.out.println("*******in f exists"+f.getPath());
		         train_tmp = new Instances(reader);
		         if(train!=null)
		        	 Instances.mergeInstances(train, train_tmp);
		         else
		        	 train = train_tmp;
		         reader.close();
				//System.out.println("**********Known Host: " + host.getHost());
				//nodeList[i] = host.getHost();
		        j++;
			}
		    
		 /*reader = new BufferedReader(
				new FileReader(f_final.getPath()));
         //System.out.println("*******in f exists"+f.getPath());
         train = new Instances(reader);*/
         
         // setting class attribute
         train.setClassIndex(train.numAttributes() - 1);
         Remove rm = new Remove();
         rm.setAttributeIndices("1");  // remove 1st attribute
         // classifier
         J48 j48 = new J48();
         j48.setUnpruned(true);        // using an unpruned J48
         // meta-classifier
         fc = new FilteredClassifier();
         fc.setFilter(rm);
         fc.setClassifier(j48);
         // train and make predictions
         
            fc.buildClassifier(train);
            oos = new ObjectOutputStream(
                    new FileOutputStream("/tmp/j48.model"));
			oos.writeObject(fc);
			oos.flush();
			oos.close();
            //.core.SerializationHelper.write("/tmp/j48.model", fc);
            /*for (int i = 0; i < test.numInstances(); i++) {
                   double pred = fc.classifyInstance(test.instance(i));
                   //System.out.print(" predicted: " + test.classAttribute().value((int) pred));
                 }*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
