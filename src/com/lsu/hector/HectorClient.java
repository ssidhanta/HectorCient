package com.lsu.hector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.lsu.jmx.JMXClient;
import com.lsu.ml.Learner;

import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import weka.core.*;
import weka.classifiers.*;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.*;
import weka.gui.visualize.*;



public class HectorClient {

	/**
	 * @param args call the procedure for classifying and generating ROC for test data
	 */
	public static void main(String[] args) {
		String[] cLevel = null;
		File f_test = new File("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//test_data.arff");
		
		File f = new File("C://Users//ssidha1//Dropbox//CodeBase Log ingestion//training_data_final.arff");
		cLevel = Learner.predict_Final(f_test);
	}
	
	
	//Draw ROC curve for the classifier
	public static void drawROC(Instances data, Classifier cl)
	{
		
	// train classifier
	Evaluation eval;
	try {
		eval = new Evaluation(data);
	
		eval.crossValidateModel(cl, data, 10, new Random(1));
		
		// generate curve
		ThresholdCurve tc = new ThresholdCurve();
		
		int classIndex = 0;
		Instances result = tc.getCurve(eval.predictions(), classIndex);
		System.out.println(eval.toMatrixString());
		// plot curve
		ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
		vmc.setROCString("(Area under ROC = " + 
		Utils.doubleToString(tc.getROCArea(result), 4) + ")");
		vmc.setName(result.relationName()); 	
		PlotData2D tempd = new PlotData2D(result);
		tempd.setPlotName(result.relationName());
		tempd.addInstanceNumberAttribute();
		// specify which points are connected
		boolean[] cp = new boolean[result.numInstances()];
		for (int n = 1; n < cp.length; n++)
		cp[n] = true;
		tempd.setConnectPoints(cp);
		// add plot
		vmc.addPlot(tempd);
		
		// display curve
		String plotName = vmc.getName(); 
		final javax.swing.JFrame jf = 
		new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
		jf.setSize(500,400);
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(vmc, BorderLayout.CENTER);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
		public void windowClosing(java.awt.event.WindowEvent e) {
		jf.dispose();
		}
		});
		jf.setVisible(true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
