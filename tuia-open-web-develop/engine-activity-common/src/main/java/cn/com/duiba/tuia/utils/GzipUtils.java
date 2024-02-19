package cn.com.duiba.tuia.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GzipUtils
 */
public class GzipUtils {

    private GzipUtils(){
    }

    /**
     * zip
     * 
     * @param value
     * @return byte[]
     */
    public static byte[] zip(byte[] value) throws IOException {
        if (value == null || value.length == 0) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut) ){
            gzipOut.write(value);
            gzipOut.finish();
            return byteOut.toByteArray();
        }
    }

    /**
     * unzip
     * 
     * @param value
     * @return byte[]
     */
    public static byte[] unzip(byte[] value) throws IOException {
        if (value == null || value.length == 0) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(value))){
            byte[] buffer = new byte[256];
            int n;
            while ((n = gzipIn.read(buffer)) >= 0) {
                byteOut.write(buffer, 0, n);
            }
            return byteOut.toByteArray();
        }
    }
}
