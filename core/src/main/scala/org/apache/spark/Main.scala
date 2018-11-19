package org.apache.spark

import org.apache.spark.scheduler.SchedulingMode

object Main {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("core").setMaster("spark://master:7077").set("spark.scheduler.mode", SchedulingMode.FIFO.toString)
    //    conf.setJars(Array("/home/dongyun/Downloads/spark-2.4.0/spark-core/out/artifacts/spark_core_2_11_jar/spark-core_2.11.jar"))
    val sc = new SparkContext(conf)
    val pre_data = sc.textFile("hdfs://master:9000/dongyun/raindata/raindata")
    val fields = pre_data.map(line => line.trim().replace("  ", " ").replace("   ", " ").split(" ")).filter(_.length == 15).filter(_ (13) != "9").filter(_ (11) != "999.9")
    val pre_ann = fields.map(x => (x(1), x(11).toDouble)).groupByKey().map(x => (x._1, x._2.reduce(_ + _)))
    val pre_sort = pre_ann.map(x => (x._2 * 365, x._1)).sortByKey(false).map(x => (x._2, x._1))
    pre_sort.saveAsTextFile("hdfs://master:9000/dongyun/output")
  }

}
