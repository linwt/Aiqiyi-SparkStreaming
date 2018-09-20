# AiqiyiSparkStreamingProject
SparkStreaming爱奇艺实时流统计项目——实战笔记

## 一、使用步骤
1. 将项目克隆到本地
2. 将Aiqiyi_SparkStreaming和Aiqiyi_Web项目分别导入IDEA
3. 正确设置Aiqiyi_Data里文件的路径
4. 阅读代码，运行项目

## 二、软件版本
1. hadoop-2.6.4
2. zookeeper-3.4.5
3. kafka_2.12-0.11.0.2
4. apache-flume-1.6.0-bin
5. hbase-0.99.2-bin
6. spark-2.1.0-bin-hadoop2.6

## 三、项目需求
1. 统计爱奇艺每个视频类别的访问量
2. 统计从搜索引擎引流过来的类别的访问量

## 四、项目思路
1. 编写python脚本模拟产生日志
2. flume采集日志传送到kafka
3. StreamingApp主程序从kafka获取日志并进行清洗
4. 由清洗日志统计每个类别访问量，并保存到hbase数据库
5. 由清洗日志统计从搜索引擎引流过来的类别的访问量，并保存到hbase数据库
6. 通过读取hbase数据库的数据，进行数据可视化展示

## 五、操作步骤

### 1. 启动hadoop
> [hadoop@mini1 hadoop]$ sbin/start-all.sh

### 2. 启动zookeeper（三台机器）
> [hadoop@mini1 zookeeper]$ bin/zkServer.sh start

### 3. kafka
- 启动kafka（三台机器）
> [hadoop@mini1 kafka]$ bin/kafka-server-start.sh  config/server.properties &
- 创建topic
> [hadoop@mini1 kafka]$ bin/kafka-topics.sh \
--create \
--zookeeper mini1:2181 \
--replication-factor 1 \
--partitions 1 \
--topic flumeTopic
- 启动consumer
> [hadoop@mini1 kafka]$ bin/kafka-console-consumer.sh \
--zookeeper mini1:2181 \
--topic flumeTopic \
--from-beginning

### 4. flume
- 增加配置文件a1.conf
- 启动flume
> [hadoop@mini1 flume]$ bin/flume-ng agent \
-c conf \
-f conf/a1.conf \
-n a1 \
-Dflume.root.logger=INFO,console

### 5. hbase
- 启动hbase
> [hadoop@mini1 hbase]$ bin/start-hbase.sh
- 启动hbase shell
> [hadoop@mini1 hbase]$ bin/hbase shell
- 创建hbase表
> hbase(main):001:0> create ‘type’,’info’ \
hbase(main):001:1> create ‘search,’info’

### 6. 运行程序
> 运行StreamingApp程序，准备接收、处理、保存数据

### 7. 执行python脚本
> [hadoop@mini1 aiqiyi_logs]$ ./log_generator.sh

### 8. 查看hbase表情况
> hbase(main):007:2> scan 'type' \
hbase(main):007:3> scan 'search'

## 六、Spark on YARN
### 1. 打包并上传jar包
> - File → Project Structure → Artifacts → “+” → JAR → From modules with dependencies → Main Class:StreamingApp → OK
> - Build → Build Artifacts → Build
### 2. 提交作业
> [hadoop@mini1 spark]$ bin/spark-submit \
--master yarn \
--class main.StreamingApp \
/home/hadoop/aiqiyi_logs/Aiqiyi_SparkStreaming.jar
### 3. 执行python脚本
### 4. 查看hbase表情况

## 七、其他情况
### 1. 查看进程
[hadoop@mini1 ~]$ jps \
8458 Main		&emsp;// hbase shell \
7426 Hmaster		\
4325 NameNode \
4470 SecondaryNameNode \
2076 QuorumPeerMain  \
2941 Kafka \
4605 ResourceManager \
3517 Application		&emsp;// flume \
7662 Jps \
3230 ConsoleConsumer 

[hadoop@mini2 ~]$ jps \
2194 Kafka \
1556 QuorumPeerMain \
2582 NodeManager \
2508 DataNode \
4334 Jps  \
4063 HRegionServer 

[hadoop@mini3 ~]$ jps \
2497 DataNode \
3749 Jps \
3607 HRegionServer \
2184 Kafka \
2588 NodeManager \
1647 QuorumPeerMain 

### 2. hbase表结果
![](https://github.com/linwt/AiqiyiSparkStreamingProject/blob/master/Aiqiyi_Picture/hbase.png)

### 3. 相关文件
![](https://github.com/linwt/AiqiyiSparkStreamingProject/blob/master/Aiqiyi_Picture/aiqiyi_logs.png)

## 八、可视化展示
### 1. 项目运行在本地
- 读取hbase表数据 <br><br>
![](https://github.com/linwt/AiqiyiSparkStreamingProject/blob/master/Aiqiyi_Picture/type.png)
- 浏览器访问
> localhost:8080/count
- 展示效果 <br><br>
![](https://github.com/linwt/AiqiyiSparkStreamingProject/blob/master/Aiqiyi_Picture/visual.png)

### 2. 项目运行在linux
- 打包并上传jar包
> Maven Projects -> Lifecycle -> package
- 运行作业
> [hadoop@mini1 aiqiyi_logs]$ java –jar aiqiyiweb-0.0.1-SNAPSHOT.jar
- 浏览器访问
> mini1:8080/count


