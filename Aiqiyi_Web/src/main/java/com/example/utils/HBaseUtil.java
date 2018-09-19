package com.example.utils;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HBaseUtil {

    HBaseAdmin admin = null;
    Configuration configration = null;

    private HBaseUtil(){
        configration = new Configuration();
        configration.set("hbase.zookeeper.quorum", "mini1:2181");
        configration.set("hbase.rootdir", "hdfs://mini1:9000/hbase");
        try {
            admin = new HBaseAdmin(configration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HBaseUtil instance = null;

    public static synchronized HBaseUtil getInstance(){
        if(null == instance){
            instance = new HBaseUtil();
        }
        return instance;
    }

    public HTable getTable(String tableName){
        HTable table = null;
        try {
            table = new HTable(configration,tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    public void put(String tableName,String rowkey,String cf,String column,String value){
        HTable table = getTable(tableName);
        Put put = new Put(Bytes.toBytes(rowkey));
        put.add(Bytes.toBytes(cf),Bytes.toBytes(column),Bytes.toBytes(value));
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 根据表名输入条件获取 HBase 的记录数
    public Map<String, Long> query(String tableName, String condition) throws IOException {
        Map<String, Long> map = new HashMap<>();
        HTable table = getTable(tableName);
        String cf = "info";
        String column = "typeCount";
        Scan scan = new Scan();
        Filter filter = new PrefixFilter(Bytes.toBytes(condition));
        scan.setFilter(filter);
        ResultScanner rs = table.getScanner(scan);
        for (Result result : rs) {
            String row = Bytes.toString(result.getRow());
            long count = Bytes.toLong(result.getValue(cf.getBytes(), column.getBytes()));
            map.put(row, count);
        }
        return map;
    }

    public static void main(String[] args) throws IOException {
        Map<String, Long> map = HBaseUtil.getInstance().query("type", "20180918");
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            // 20180918_1 : 8
        }
    }
}