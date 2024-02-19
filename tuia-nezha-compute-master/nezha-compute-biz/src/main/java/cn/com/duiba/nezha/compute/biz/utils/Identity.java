package cn.com.duiba.nezha.compute.biz.utils;


import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;

/**
 * Created by pc on 2017/2/27.
 */
public class Identity {
    //    位权值数组
    private static byte[] Wi = new byte[17];
    //    身份证前部分字符数
    private static final byte fPart = 6;
    //    身份证算法求模关键值
    private static final byte fMod = 11;
    //    旧身份证长度
    private static final byte oldIDLen = 15;
    //    新身份证长度
    private static final byte newIDLen = 18;
    //    新身份证年份标志
    private static final String yearFlag = "19";
    //    校验码串
    private static final String CheckCode = "10X98765432";
    //    最小的行政区划码
    private static final int minCode = 150000;
    //    最大的行政区划码
    private static final int maxCode = 700000;
//    旧身份证号码
//    private String oldIDCard="";
//    新身份证号码
//    private String newIDCard="";
//    地区及编码


    //private String Area[][2] =
    private static void setWiBuffer() {
        for (int i = 0; i < Wi.length; i++) {
            int k = (int) Math.pow(2, (Wi.length - i));
            Wi[i] = (byte) (k % fMod);
        }
    }

    //获取新身份证的最后一位:检验位
    private static String getCheckFlag(String idCard) {
        int sum = 0;
        //进行加权求和
        for (int i = 0; i < 17; i++) {
            sum += Integer.parseInt(idCard.substring(i, i + 1)) * Wi[i];
        }
        //取模运算，得到模值
        byte iCode = (byte) (sum % fMod);
        return CheckCode.substring(iCode, iCode + 1);
    }

    //获取新身份证的最后一位:检验位
    private static Boolean checkNewFlag(String idCard) {
        Boolean ret = false;
        if (idCard != null && idCard.length() == oldIDLen) {
            ret = false;
        }
        if (idCard != null && idCard.length() == newIDLen) {
            ret = true;
        }
        return ret;
    }


    //判断串长度的合法性
    private static boolean checkLength(final String idCard) {
        boolean right = (idCard.length() == oldIDLen) || (idCard.length() == newIDLen);
        return right;
    }


    //获取时间串
    private static String getIDDate(final String idCard, boolean newIDFlag) {
        String dateStr = null;
        if (newIDFlag)
            dateStr = idCard.substring(fPart, fPart + 8);
        else
            dateStr = yearFlag + idCard.substring(fPart, fPart + 6);
        return dateStr;
    }

    //判断时间
    private static Date getDate(final String dateSource) {
        String dateStr = dateSource.substring(0, 4) + "-" + dateSource.substring(4, 6) + "-" + dateSource.substring(6, 8);
        DateFormat df = DateFormat.getDateInstance();
        df.setLenient(false);
        try {
            Date date = df.parse(dateStr);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }


    //获取性别
    public static Long getAge(final String idCard) {
        Long age = null;
        if (checkIDCard(idCard)) {
            String dateStr = getIDDate(idCard, checkNewFlag(idCard));
            Date ageDate = getDate(dateStr);
            Integer age2 = DateUtil.getIntervalYears(DateUtil.getCurrentDate(), ageDate);
            if (age2 != null) {
                age = age2.longValue();
            }


        }
        return age;
    }


    //获取地址串
    public static String getAddressCode(final String idCard) {
        String dateStr = null;
        if (idCard != null) {
            dateStr = idCard.substring(0, fPart);
        }
        return dateStr;
    }


    //获取性别
    private static Long getIdSex(final String idCard, boolean newIDFlag) {
        Long sex = null;
        String idOrderStr = getIDOrder(idCard, newIDFlag);

        if (AssertUtil.isNotEmpty(idOrderStr)) {
            Long idOrder = Long.valueOf(idOrderStr);
            if (idOrder != null) {
                sex = idOrder % 2;
            }

        }

        return sex;
    }

    //获取性别
    public static Long getSex(final String idCard) {
        Long sex = null;
        if (checkIDCard(idCard)) {
            sex = getIdSex(idCard, checkNewFlag(idCard));
        }
        return sex;
    }


    //获取时间串
    public static String getIDOrder(final String idCard, boolean newIDFlag) {
        String idOrderStr = null;
        if (newIDFlag) {
            idOrderStr = idCard.substring(newIDLen - 4, newIDLen-1);
        } else {
            idOrderStr = idCard.substring(oldIDLen - 4, oldIDLen-1);
        }
        return idOrderStr;
    }


    //判断时间合法性
    private static boolean checkDate(final String dateSource) {
        String dateStr = dateSource.substring(0, 4) + "-" + dateSource.substring(4, 6) + "-" + dateSource.substring(6, 8);
        DateFormat df = DateFormat.getDateInstance();
        df.setLenient(false);
        try {
            Date date = df.parse(dateStr);
            return (date != null);
        } catch (ParseException e) {
            return false;
        }
    }


    //旧身份证转换成新身份证号码
    public static String getNewIDCard(final String oldIDCard) {
        //初始化方法
        Identity.setWiBuffer();
        if (!checkIDCard(oldIDCard)) {
            return oldIDCard;
        }
        String newIDCard = oldIDCard.substring(0, fPart);
        newIDCard += yearFlag;
        newIDCard += oldIDCard.substring(fPart, oldIDCard.length());
        String ch = getCheckFlag(newIDCard);
        newIDCard += ch;
        return newIDCard;
    }

    //新身份证转换成旧身份证号码
    public static String getOldIDCard(final String newIDCard) {
        //初始化方法
        Identity.setWiBuffer();
        if (!checkIDCard(newIDCard)) {
            return newIDCard;
        }
        String oldIDCard = newIDCard.substring(0, fPart) +
                newIDCard.substring(fPart + yearFlag.length(), newIDCard.length() - 1);
        return oldIDCard;
    }


    //判断身份证号码的合法性
    public static boolean checkIDCard(final String idCard) {
        //初始化方法
        Identity.setWiBuffer();
        //String message = "";
        if (!checkLength(idCard)) {
            //message = "ID长度异常";
            return false;
        }
        String idDate = getIDDate(idCard, checkNewFlag(idCard));
        if (!checkDate(idDate)) {
            //message = "ID时间异常";
            return false;
        }
        if (checkNewFlag(idCard)) {
            String checkFlag = getCheckFlag(idCard);
            String theFlag = idCard.substring(idCard.length() - 1, idCard.length());
            if (!checkFlag.equals(theFlag)) {
                //message = "新身份证校验位异常";
                return false;
            }
        }
        return true;
    }

    //获取一个随机的"伪"身份证号码
    public static String getRandomIDCard(final boolean idNewID) {
        //初始化方法
        Identity.setWiBuffer();
        Random ran = new Random();
        String idCard = getAddressCode(ran) + getRandomDate(ran, idNewID) + getIDOrder(ran);
        if (idNewID) {
            String ch = getCheckFlag(idCard);
            idCard += ch;
        }
        return idCard;
    }

    //产生随机的地区编码
    private static String getAddressCode(Random ran) {
        if (ran == null) {
            return "";
        } else {
            int addrCode = minCode + ran.nextInt(maxCode - minCode);
            return Integer.toString(addrCode);
        }
    }

    //产生随机的出生日期
    private static String getRandomDate(Random ran, boolean idNewID) {
        if (ran == null) {
            return "";
        }
        int year = 0;
        if (idNewID) {
            year = 1900 + ran.nextInt(2007 - 1900);
        } else {
            year = 1 + ran.nextInt(99);
        }
        int month = 1 + ran.nextInt(12);
        int day = 0;
        if (month == 2) {
            day = 1 + ran.nextInt(28);
        } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            day = 1 + ran.nextInt(31);
        } else {
            day = 1 + ran.nextInt(30);
        }
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setMaximumIntegerDigits(2);
        nf.setMinimumIntegerDigits(2);
        String dateStr = Integer.toString(year) + nf.format(month) + nf.format(day);
        return dateStr;
    }

    //产生随机的序列号
    private static String getIDOrder(Random ran) {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setMaximumIntegerDigits(3);
        nf.setMinimumIntegerDigits(3);
        if (ran == null) {
            return "";
        } else {
            int order = 1 + ran.nextInt(999);
            return nf.format(order);
        }
    }
}
