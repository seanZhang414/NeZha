package cn.com.duiba.nezha.compute.biz.utils.hbase;


import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.conf.ConfigFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;


public class HbaseUtil {

    private static Connection connection = null;
    private Admin admin = null;
    private static String zk = null;

    private static Object tablelock = new Object();

    private static Map<String, Table> tableMap = new HashMap<>();

    static {
        try {
            zk = ConfigFactory.getInstance()
                    .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.ZK_LIST);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static HbaseUtil instance = null;

    public static HbaseUtil getInstance() throws IOException {
        if (instance == null) {
            synchronized (HbaseUtil.class) {
                if (instance == null) {
                    instance = new HbaseUtil();
                }
            }
        }
        return instance;
    }

    private HbaseUtil() throws IOException {
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", zk);
        connection = ConnectionFactory.createConnection(conf);
        admin = connection.getAdmin();

    }

    public void close() throws IOException {
        connection.close();
    }

    public void createTable(String tablename, String[] cfs) {
        try {

            if (admin.tableExists(TableName.valueOf(tablename))) {
            } else {
                HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(tablename));
                if (cfs != null) {
                    for (String cf : cfs) {
                        descriptor.addFamily(new HColumnDescriptor(cf.getBytes()));
                    }
                }
                admin.createTable(descriptor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建好表后，添加列簇
     */
    public void addColumnFamily(String tableName, String[] cfs) {

        try {
            HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(tableName));
            if (cfs != null) {
                for (String cf : cfs) {
                    descriptor.addFamily(new HColumnDescriptor(cf.getBytes()));
                }
            }
            admin.disableTable(TableName.valueOf(tableName));
            admin.modifyTable(TableName.valueOf(tableName), descriptor);
            admin.enableTable(TableName.valueOf(tableName));
            admin.createTable(descriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void deleteTable(String tablename) {
        try {

            if (admin.tableExists(TableName.valueOf(tablename))) {
                admin.disableTable(TableName.valueOf(tablename));
                admin.deleteTable(TableName.valueOf(tablename));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建好表后，删除列簇
     */
    public void deleteColumn(String tableName, String[] cfs) {

        try {
            HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(tableName));
            if (cfs != null) {
                for (String cf : cfs) {
                    descriptor.addFamily(new HColumnDescriptor(cf.getBytes()));
                    admin.deleteColumn(TableName.valueOf(tableName), cf.getBytes());
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Table getHTable(String tableName) {

        Table tableRet = null;
        try {
            Table tableTmp = tableMap.get(tableName);
            // || !admin.isTableAvailable(TableName.valueOf(tableName))
//            System.out.println("admin"+admin.tableExists(TableName.valueOf(tableName)));
            if (tableTmp == null) {
                setHTable(tableName, false, null);
            }

            tableRet = tableMap.get(tableName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableRet;

    }

    private void setHTable(String tableName, boolean isCreate, String[] cfs) {
        synchronized (tablelock) {
            try {
                if (isCreate) {
                    createTable(tableName, cfs);
                }
                if (admin.tableExists(TableName.valueOf(tableName))) {
                    Table tableTmp = connection.getTable(TableName.valueOf(tableName));
                    tableMap.put(tableName, tableTmp);
                    System.out.println("setHTable by name = " + tableName);

                } else {
                    System.out.println("setHTable not exists by name = " + tableName);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void insert(String tableName, String rowKey, String family,
                       String quailifer, String value) {
        try {
            Put put = new Put(rowKey.getBytes());
            put.addColumn(family.getBytes(), quailifer.getBytes(), value.getBytes());
            getHTable(tableName).put(put);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insert(String tableName, String rowKey, String family, Map<String, String> rValues) {
        try {
            Put p = new Put(Bytes.toBytes(rowKey));
            for (String qkey : rValues.keySet()) {
                String value = rValues.get(qkey);
                if (value != null) {
                    p.addColumn(family.getBytes(), qkey.getBytes(), value.getBytes());
                }
            }
            getHTable(tableName).put(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insert(String tableName, String rowKey, Map<String, Map<String, String>> rValues) {
        try {
            Put p = new Put(Bytes.toBytes(rowKey));
            for (Entry<String, Map<String, String>> entry : rValues.entrySet()) {
                String family = entry.getKey();
                Map<String, String> fValues = entry.getValue();
                for (String qkey : fValues.keySet()) {
                    String value = fValues.get(qkey);
                    if (value != null) {
                        p.addColumn(family.getBytes(), qkey.getBytes(), value.getBytes());
                    }

                }
            }
            getHTable(tableName).put(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insert(String tableName, Map<String, Map<String, Map<String, String>>> kvMap) {

        try {
            List<Put> puts = new ArrayList<Put>();

            for (String rowKey : kvMap.keySet()) {
                Map<String, Map<String, String>> rValues = kvMap.get(rowKey);
                Put p = new Put(Bytes.toBytes(rowKey));
                for (Entry<String, Map<String, String>> entry : rValues.entrySet()) {
                    String family = entry.getKey();
                    Map<String, String> fValues = entry.getValue();
                    for (String qkey : fValues.keySet()) {
                        String value = fValues.get(qkey);
                        p.addColumn(family.getBytes(), qkey.getBytes(), value.getBytes());
                    }
                }
                puts.add(p);
            }
            getHTable(tableName).put(puts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void incrementColumeValue(String tableName, String rowKey, String family, String quailifer, Long value) {

        try {
            if (rowKey != null) {
                getHTable(tableName).incrementColumnValue(rowKey.getBytes(), family.getBytes(), quailifer.getBytes(), value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param tableName
     * @param rowKey
     * @param fcvMap
     */
    public void incrementColumeValues(String tableName, String rowKey, Map<String, Map<String, Long>> fcvMap) {

        try {

            if (rowKey != null && fcvMap != null) {
                Increment increment = new Increment(rowKey.getBytes());

                for (String familyName : fcvMap.keySet()) {
                    Map<String, Long> colValMap = fcvMap.get(familyName);
                    for (String col : colValMap.keySet()) {
                        Long val = colValMap.get(col);
                        increment.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(col), val);
                    }
                }
                if (!increment.isEmpty()) {
                    getHTable(tableName).increment(increment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void incrementColumeValues(String tableName, String rowKey, String family[], String[] quailifer, Long value[]) {
        try {
            if (quailifer != null &&
                    value != null &&
                    family != null &&
                    family.length == quailifer.length &&
                    quailifer.length == value.length) {
                Increment increment = new Increment(rowKey.getBytes());

                for (int i = 0; i < quailifer.length; i++) {
                    increment.addColumn(family[i].getBytes(), quailifer[i].getBytes(), value[i]);
                }
                getHTable(tableName).increment(increment);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getOneRow(String tableName, String rowKey, QueryCallback callback) throws Exception {

        List<Result> list = new ArrayList<Result>();
        Result rsResult = null;
        try {
            Get get = new Get(rowKey.getBytes());
            rsResult = getHTable(tableName).get(get);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.add(rsResult);
        callback.process(list);
    }

    public void getOneRow(String tableName, String rowKey, String familyName, Set<String> cSet, QueryCallback callback) throws Exception {

        List<Result> list = new ArrayList<Result>();
        Result rsResult = null;
        try {
            Get get = new Get(rowKey.getBytes());
            if (familyName != null && AssertUtil.isNotEmpty(cSet)) {
                for (String columnName : cSet) {
                    get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
                }
            }
            rsResult = getHTable(tableName).get(get);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.add(rsResult);
        callback.process(list);

    }

    public void getOneRow(String tableName, String rowKey, String familyName, String columnName, QueryCallback callback) throws Exception {

        List<Result> list = new ArrayList<Result>();
        Result rsResult = null;
        try {
            Get get = new Get(rowKey.getBytes());
            get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName)); // 获取指定列族和列修饰符对应的列
            rsResult = getHTable(tableName).get(get);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.add(rsResult);
        callback.process(list);
    }

    public void getOneRow(String tableName, String rowKey, Map<String, Set<String>> fcMap, QueryCallback callback) throws Exception {

        List<Result> list = new ArrayList<Result>();
        Result rsResult = null;
        try {
            Get get = new Get(rowKey.getBytes());
            if (fcMap != null) {
                for (String familyName : fcMap.keySet()) {
                    Set<String> columnNameSet = fcMap.get(familyName);
                    for (String columnName : columnNameSet) {
                        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
                    }
                }
            }
            rsResult = getHTable(tableName).get(get);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.add(rsResult);
        callback.process(list);

    }


    public void getRows(String tableName, String rowKeyLike, QueryCallback callback) throws Exception {

        List<Result> list = new ArrayList<>();
        try {
            Scan scan = new Scan();
            if (rowKeyLike != null) {
                PrefixFilter filter = new PrefixFilter(rowKeyLike.getBytes());
                scan.setFilter(filter);
            }
            ResultScanner scanner = getHTable(tableName).getScanner(scan);
            for (Result rs : scanner) {
                list.add(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        callback.process(list);
    }

    public void getRows(String tableName, String rowKeyLike, String famls[], String cols[], QueryCallback callback) throws Exception {

        List<Result> list = new ArrayList<Result>();
        try {
            PrefixFilter filter = new PrefixFilter(rowKeyLike.getBytes());
            Scan scan = new Scan();
            for (String faml : famls) {
                for (int i = 0; i < cols.length; i++) {
                    scan.addColumn(faml.getBytes(), cols[i].getBytes());
                }
            }
            scan.setFilter(filter);
            ResultScanner scanner = getHTable(tableName).getScanner(scan);
            for (Result rs : scanner) {
                list.add(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        callback.process(list);
    }

    public void deleteRows(String tableName, String[] rows) {

        if (rows != null && rows.length > 0) {
            List<Delete> list = new ArrayList<Delete>();

            for (String row : rows) {
                Delete delete = new Delete(row.getBytes());
                list.add(delete);
            }

            try {
                getHTable(tableName).delete(list);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 查询表中的某一列
     *
     * @param tableName
     * @param rowKey
     * @param familyName
     * @param columnName
     * @throws IOException
     */
    public Result getResultByColumn(String tableName, String rowKey,
                                    String familyName, String columnName) throws IOException {

        Result result = null;
        try {
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName)); // 获取指定列族和列修饰符对应的列
            result = getHTable(tableName).get(get);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static interface QueryCallback {

        void process(List<Result> retList) throws Exception;

    }
}
