package cn.com.duiba.nezha.compute.common.util.serialize;

import java.io.*;

/**
 * Created by pc on 2016/12/21.
 */
public class SerializeTool {
    public static String object2String(Object obj) {
        String objBody = null;
        ByteArrayOutputStream baops = null;
        ObjectOutputStream oos = null;

        try {
            baops = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baops);
            oos.writeObject(obj);
            byte[] bytes = baops.toByteArray();
            objBody = getStringFromBytes(bytes);
        } catch (Exception e) {
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (baops != null)
                    baops.close();
            } catch (Exception e) {
            }
        }
        return objBody;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T getObjectFromString(String objBody, Class<T> clazz) {

        ObjectInputStream ois = null;
        T obj = null;
        try {

            byte[] bytes = getBytesFromString(objBody);
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            obj = (T) ois.readObject();
        } catch (Exception e) {
        } finally {

            try {
                if (ois != null)
                    ois.close();
            } catch (Exception e) {
            }
        }

        return obj;
    }

    public static <T extends Serializable> T getObjectFromString(String objBody) {

        T obj = null;
        try {

            obj = (T) getObjectFromStr(objBody);
        } catch (Exception e) {
        }
        return obj;
    }


    public static  Object getObjectFromStr(String objBody) {

        ObjectInputStream ois = null;
        Object obj = null;
        try {

            byte[] bytes = getBytesFromString(objBody);
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            obj = ois.readObject();
        } catch (Exception e) {
        } finally {

            try {
                if (ois != null)
                    ois.close();
            } catch (Exception e) {
            }
        }

        return obj;
    }

    public static byte[] getBytesFromString(String str) throws Exception {
        byte[] bytes = str.getBytes("ISO-8859-1");
        return bytes;
    }

    public static String getStringFromBytes(byte[] bAarry) throws Exception {
        return new String(bAarry, "ISO-8859-1");
    }
}