package cn.com.duiba.nezha.compute.biz.utils.hbase;

import cn.com.duiba.nezha.compute.biz.constant.PsConstant;
import com.google.common.collect.Sets;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class HbaseUtilTest {

    @Test
    public void createTableBySplitKeys() {

        try {

            String[] keys = new String[] { "10|", "20|", "30|", "40|", "50|",
                    "60|", "70|", "80|", "90|" };

            String tableName = PsConstant.TABLE_NAME_MODEL;
            List<String> famlys = Arrays.asList(PsConstant.FAMILY_MODEL);
            HbaseUtil.getInstance().createTableBySplitKeys(tableName,famlys,keys);
        }catch (Exception e){

        }

    }
    @Test
    public void createTable() {

        try {

            String tableName = PsConstant.TABLE_NAME_BASE_INFO;
            List<String> famlys = Arrays.asList(PsConstant.FAMILY_BASE_INFO);
            HbaseUtil.getInstance().createTable(tableName,famlys);
        }catch (Exception e){

        }

    }

    @Test
    public void get1() {

        try {
            final Set<String> fieldSet = Sets.newHashSet();
            fieldSet.add("3");
            fieldSet.add("15");
            final String[] cols = new String[2];
            cols[0]="15";
            cols[1]="3";


            String rowKey ="55340192608102";
            String family="cf";

//            HbaseUtil.getInstance().incrementColumeValue(PsConstant.TABLE_NAME_MODEL,rowKey,family,"3",10L);

            // 数据读取
            HbaseUtil.getInstance().getOneRow(PsConstant.TABLE_NAME_ORDER_LIST, rowKey, family, fieldSet, new HbaseUtil.QueryCallback() {
                @Override
                public void process(List<Result> retList) throws Exception {
                    if (retList != null) {
                        Result ret = retList.get(0);
                        for (int i = 0; i < 2; i++) {
                            byte[] fieldValueB = ret.getValue(
                                    Bytes.toBytes("model"),
                                    Bytes.toBytes(cols[i]));

                            if(fieldValueB!=null){
                            }

                            System.out.println("rowk="+rowKey+",fam="+family+",col="+cols[i]+",ret="+fieldValueB);

                        }
                    }
                }

            });

        }catch (Exception e){

        }

    }

}