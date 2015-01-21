package com.lsu.ml;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.lsu.hector.HectorClient;
import com.lsu.jmx.JMXClient;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class Learner {

	
	//Write the test data to intermediate arff format for processing by weka classifier
	public static void writeToTestFile(BufferedWriter fw,String str){
		try {
			fw.append("@relation consistency");
			fw.append("\n@attribute "+JMXClient.attr1+ " NUMERIC" );
			fw.write("\n@attribute "+JMXClient.attr13+ " NUMERIC" );
			fw.append("\n@attribute "+JMXClient.attr14+ " NUMERIC" );
			fw.append("\n@attribute "+JMXClient.attr15+ " NUMERIC" );
			fw.append("\n@attribute class {ANY,ONE,TWO,THREE,LOCAL_QUORUM,EACH_QUORUM,QUORUM,ALL}" );
			
			fw.append("\n@data\n");
			fw.append(str+"\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//appends the string to the file
		 
	}
	
	//Classify the test data using weka j48 decision tree and call the drawROC method
	public static String[] predict_Final(File f_test){
		String[] cLevel = null;
        Instances test = null;
        
        try {
        
         BufferedReader reader_test = new BufferedReader(
                 new FileReader(f_test.getPath()));	
         test = new Instances(reader_test);
         test.setClassIndex(test.numAttributes() - 1);
         // deserialize model
         ObjectInputStream ois = new ObjectInputStream(
                                   new FileInputStream("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//j48.model"));
        
         FilteredClassifier fc = (FilteredClassifier) ois.readObject();
         
         final javax.swing.JFrame jf = 
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
         tv.fitToScreen();
         ois.close();
         cLevel = new String[test.numInstances()];
         for (int i = 0; i < test.numInstances(); i++) {
             double pred = fc.classifyInstance(test.instance(i));
             cLevel[i] = test.classAttribute().value((int) pred);
           }
         HectorClient.drawROC(test,fc);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cLevel;
	}
	
	
}
