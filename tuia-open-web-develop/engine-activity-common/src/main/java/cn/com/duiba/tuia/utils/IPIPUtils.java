package cn.com.duiba.tuia.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPIPUtils {
    
    private static final Logger logger      = LoggerFactory.getLogger(IPIPUtils.class);

    private static String       downloadUrl = "http://user.ipip.net/download.php?type=datx&token=888cbe802dc8e3232e946617ba40930604e55c4f";

    private static String       ipFilePath  = "/home/admin/data/tuia-engine-activity/ipdata.datx";  //NOSONAR

    private int                 offset;
    private int[]               index       = new int[65536];
    private ByteBuffer          dataBuffer;
    private ByteBuffer          indexBuffer;
    private File                ipFile;
    private ReentrantLock       lock        = new ReentrantLock();

    public void load(String filename) {
        ipFile = new File(filename);
        load();
    }

    public String[] find(String ip) {
        String[] ips = ip.split("\\.");
        int prefixValue = (Integer.valueOf(ips[0]) * 256 + Integer.valueOf(ips[1]));
        long ip2longValue = ip2long(ip);
        int start = index[prefixValue];
        int maxCompLen = offset - 262144 - 4;
        long tmpInt;
        long indexOffset = -1;
        int indexLength = -1;
        byte b = 0;
        for (start = start * 9 + 262144; start < maxCompLen; start += 9) {
            tmpInt = int2long(indexBuffer.getInt(start));
            if (tmpInt >= ip2longValue) {
                indexOffset = bytesToLong(b, indexBuffer.get(start + 6), indexBuffer.get(start + 5), indexBuffer.get(start + 4));
                indexLength = (0xFF & indexBuffer.get(start + 7) << 8) + (0xFF & indexBuffer.get(start + 8));
                break;
            }
        }

        byte[] areaBytes;

        lock.lock();
        try {
            dataBuffer.position(offset + (int) indexOffset - 262144);
            areaBytes = new byte[indexLength];
            dataBuffer.get(areaBytes, 0, indexLength);
        } finally {
            lock.unlock();
        }

        return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
    }

    private void load() {
        lock.lock();
        try {
            dataBuffer = ByteBuffer.wrap(getBytesByFile(ipFile));
            dataBuffer.position(0);
            offset = dataBuffer.getInt(); // indexLength
            byte[] indexBytes = new byte[offset];
            dataBuffer.get(indexBytes, 0, offset - 4);
            indexBuffer = ByteBuffer.wrap(indexBytes);
            indexBuffer.order(ByteOrder.LITTLE_ENDIAN);

            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 256; j++) {
                    index[i * 256 + j] = indexBuffer.getInt();
                }
            }
            indexBuffer.order(ByteOrder.BIG_ENDIAN);
        } finally {
            lock.unlock();
        }
    }

    private byte[] getBytesByFile(File file) {

        byte[] bs = new byte[Long.valueOf(file.length()).intValue()];   //NOSONAR
        try(FileInputStream fin = new FileInputStream(file)) {
            int readBytesLength = 0;
            int i;
            while ((i = fin.available()) > 0) {
                fin.read(bs, readBytesLength, i);
                readBytesLength += i;
            }
        } catch (IOException ioe) {
            logger.warn("getBytesByFile error:{}", ioe);
        }
        return bs;
    }

    private long bytesToLong(byte a, byte b, byte c, byte d) {
        return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
    }

    private int str2Ip(String ip)  {
        String[] ss = ip.split("\\.");
        int a;
        int b;
        int c;
        int d;
        a = Integer.parseInt(ss[0]);
        b = Integer.parseInt(ss[1]);
        c = Integer.parseInt(ss[2]);
        d = Integer.parseInt(ss[3]);
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    private long ip2long(String ip)  {
        return int2long(str2Ip(ip));
    }

    private long int2long(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }

    private boolean download() throws IOException {
        File tmpFile = new File(ipFilePath + ".tmp");
        if (tmpFile.exists()) {
            tmpFile.delete();   //NOSONAR
        }

        long time = System.currentTimeMillis();
        logger.info("Download ip data file start...");
        URL url = new URL(downloadUrl);
        FileUtils.copyURLToFile(url, tmpFile, 10 * 1000, 60 * 1000 * 2);
        if (tmpFile.length() > 5 * 1024 * 1024) {
            logger.info("Download ip data file success, file size= {}, , cost time={}" , tmpFile.length(), (System.currentTimeMillis() - time) / 1000);
            File file = new File(ipFilePath);
            if (file.exists() && file.delete()) {
                logger.info("Remove ip data local file success");
            }
            if (tmpFile.renameTo(file)) {
                logger.info("Replace ip data local file success");

                load(ipFilePath);
                logger.info("Reload ip data local file success");
                return true;
            }
        }
        return false;
    }

    private static IPIPUtils ipUtilHolder;

    static {
        scheduleDownload();
    }

    private static void scheduleDownload() {

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate( () -> {
                try {
                    IPIPUtils ipUtil = new IPIPUtils();
                    File file = new File(ipFilePath);
                    if (file.exists() && ipUtilHolder == null) {
                        ipUtil.load(ipFilePath);
                        ipUtilHolder = ipUtil;
                        logger.info("Load exist ip data file.");
                    } else {
                        boolean result = ipUtil.download();
                        if (result) {
                            ipUtilHolder = ipUtil;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Download ip data error.", e);
                }
        }, 0L, 1000L * 3600 * 24 , TimeUnit.MILLISECONDS);
    }

    public static String[] findIp(String ip) {
        if (ipUtilHolder != null) {
            return ipUtilHolder.find(ip);
        }
        return new String[0];
    }
}
