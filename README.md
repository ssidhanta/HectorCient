# HectorCient

This is a java client based on the Hector API that:
  1) Predicts the optimum consistency level for a test data given a subSLA row (with threshold values for the parameters latency, 
  throughput, packet retransmission rate, and staleness, from a model.
  2) Generates the ROC curve for a test data given a training model.

The HectorCLient.jar can be called as java -jar HectorClient.jar. The output consistency level can be applied to Apache Cassandra.
We have integrated this jar with YCSB - see our YCSB_patch_predict_consistency project in github.
