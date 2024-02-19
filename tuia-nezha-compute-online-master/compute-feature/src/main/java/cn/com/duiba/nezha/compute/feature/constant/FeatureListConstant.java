package cn.com.duiba.nezha.compute.feature.constant;

import cn.com.duiba.nezha.compute.feature.enums.FeatureEnumC1;

import java.util.ArrayList;
import java.util.List;

public class FeatureListConstant {

    public static List<FeatureEnumC1> FLC1_BASE = null;//基础特征
    public static List<FeatureEnumC1> FLC1_BASE_CTR = null;//基础特征+CVR交叉
    public static List<FeatureEnumC1> FLC1_BASE_CVR = null;//基础特征+CTR交叉
    public static List<FeatureEnumC1> FLC1_BASE_ALL = null;//基础特征+CTR交叉+CVR交叉

    public static List<FeatureEnumC1> FLC1_BASE_U_I_P = null;//基础特征+用户商业兴趣偏好
    public static List<FeatureEnumC1> FLC1_BASE_U_I_P_2 = null;//基础特征+用户商业兴趣偏好
    public static List<FeatureEnumC1> FLC1_BASE_CTR_U_I_P = null;//基础特征+用户商业兴趣偏好
    public static List<FeatureEnumC1> FLC1_BASE_CVR_U_I_P = null;//基础特征+用户商业兴趣偏好

    public static List<FeatureEnumC1> FLC1_BASE_ALL_U_I_P = null;//基础特征+CTR+CVR+用户商业兴趣偏好
    public static List<FeatureEnumC1> FLC1_BASE_ALL_U_I_P_2 = null;//基础特征+CTR+CVR+用户商业兴趣偏好



    static {
        FLC1_BASE = getFLC1000();
        FLC1_BASE_CTR = getFLC1001();
        FLC1_BASE_CVR = getFLC1002();
        FLC1_BASE_ALL = getFLC1003();

        FLC1_BASE_U_I_P = getFLC1004();
        FLC1_BASE_U_I_P_2 = getFLC10041();

        FLC1_BASE_CTR_U_I_P = getFLC1005();
        FLC1_BASE_CVR_U_I_P = getFLC1006();

        FLC1_BASE_ALL_U_I_P = getFLC1007();
        FLC1_BASE_ALL_U_I_P_2 = getFLC1008();
    }

    /**
     * 基础版本
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC1000() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);

        list.add(FeatureEnumC1.F301001);
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601001);
        list.add(FeatureEnumC1.F602001);
        list.add(FeatureEnumC1.F603001);
        list.add(FeatureEnumC1.F604001);
        list.add(FeatureEnumC1.F605001);
        list.add(FeatureEnumC1.F606001);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);


        return list;
    }


    /**
     * 基础版本+CTR实时统计特征
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC1001() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);

        list.add(FeatureEnumC1.F301001);
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601001);
        list.add(FeatureEnumC1.F602001);
        list.add(FeatureEnumC1.F603001);
        list.add(FeatureEnumC1.F604001);
        list.add(FeatureEnumC1.F605001);
        list.add(FeatureEnumC1.F606001);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);

        list.add(FeatureEnumC1.F804001);
        list.add(FeatureEnumC1.F805001);
        list.add(FeatureEnumC1.F806001);
        list.add(FeatureEnumC1.F807001);
//        list.add(FeatureEnumC1.F804002);
//        list.add(FeatureEnumC1.F805002);
//        list.add(FeatureEnumC1.F806002);
//        list.add(FeatureEnumC1.F807002);


        return list;
    }

    /**
     * 基础版本+CVR实时统计特征
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC1002() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);

        list.add(FeatureEnumC1.F301001);
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601001);
        list.add(FeatureEnumC1.F602001);
        list.add(FeatureEnumC1.F603001);
        list.add(FeatureEnumC1.F604001);
        list.add(FeatureEnumC1.F605001);
        list.add(FeatureEnumC1.F606001);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);

//        list.add(FeatureEnumC1.F804001);
//        list.add(FeatureEnumC1.F805001);
//        list.add(FeatureEnumC1.F806001);
//        list.add(FeatureEnumC1.F807001);
        list.add(FeatureEnumC1.F804002);
        list.add(FeatureEnumC1.F805002);
        list.add(FeatureEnumC1.F806002);
        list.add(FeatureEnumC1.F807002);


        return list;
    }

    /**
     * 基础版本+CTR实时统计特征+CVR实时统计特征
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC1003() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);

        list.add(FeatureEnumC1.F301001);
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601001);
        list.add(FeatureEnumC1.F602001);
        list.add(FeatureEnumC1.F603001);
        list.add(FeatureEnumC1.F604001);
        list.add(FeatureEnumC1.F605001);
        list.add(FeatureEnumC1.F606001);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);

        list.add(FeatureEnumC1.F804001);
        list.add(FeatureEnumC1.F805001);
        list.add(FeatureEnumC1.F806001);
        list.add(FeatureEnumC1.F807001);

        list.add(FeatureEnumC1.F804002);
        list.add(FeatureEnumC1.F805002);
        list.add(FeatureEnumC1.F806002);
        list.add(FeatureEnumC1.F807002);


        return list;
    }

    /**
     * 基础版本+用户行为偏好
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC1004() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);

        list.add(FeatureEnumC1.F301001);
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601002);
        list.add(FeatureEnumC1.F602002);
        list.add(FeatureEnumC1.F603002);
        list.add(FeatureEnumC1.F604002);
        list.add(FeatureEnumC1.F605002);
        list.add(FeatureEnumC1.F606002);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);

        list.add(FeatureEnumC1.F808001);
        list.add(FeatureEnumC1.F809001);
        list.add(FeatureEnumC1.F810001);

        list.add(FeatureEnumC1.F808002);
        list.add(FeatureEnumC1.F809002);
        list.add(FeatureEnumC1.F810002);

        list.add(FeatureEnumC1.F9916);
        list.add(FeatureEnumC1.F9917);
        list.add(FeatureEnumC1.F9918);

        return list;
    }

    /**
     * 基础版本+用户行为偏好
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC10041() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);

        list.add(FeatureEnumC1.F301001);
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601002);
        list.add(FeatureEnumC1.F602002);
        list.add(FeatureEnumC1.F603002);
        list.add(FeatureEnumC1.F604002);
        list.add(FeatureEnumC1.F605002);
        list.add(FeatureEnumC1.F606002);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);

        list.add(FeatureEnumC1.F808001);
        list.add(FeatureEnumC1.F809001);
        list.add(FeatureEnumC1.F810001);
        list.add(FeatureEnumC1.F811001);

        list.add(FeatureEnumC1.F808002);
        list.add(FeatureEnumC1.F809002);
        list.add(FeatureEnumC1.F810002);
        list.add(FeatureEnumC1.F811002);

        list.add(FeatureEnumC1.F9916);
        list.add(FeatureEnumC1.F9917);
        list.add(FeatureEnumC1.F9918);

        return list;
    }

    /**
     * 基础版本+用户行为偏好+CTR交叉统计
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC1005() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);

        list.add(FeatureEnumC1.F301001);
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601002);
        list.add(FeatureEnumC1.F602002);
        list.add(FeatureEnumC1.F603002);
        list.add(FeatureEnumC1.F604002);
        list.add(FeatureEnumC1.F605002);
        list.add(FeatureEnumC1.F606002);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);

        list.add(FeatureEnumC1.F804003);
        list.add(FeatureEnumC1.F805003);
        list.add(FeatureEnumC1.F806003);
        list.add(FeatureEnumC1.F807003);


        list.add(FeatureEnumC1.F808001);
        list.add(FeatureEnumC1.F809001);
        list.add(FeatureEnumC1.F810001);
        list.add(FeatureEnumC1.F811001);

        list.add(FeatureEnumC1.F808002);
        list.add(FeatureEnumC1.F809002);
        list.add(FeatureEnumC1.F810002);
        list.add(FeatureEnumC1.F811002);

        list.add(FeatureEnumC1.F9916);
        list.add(FeatureEnumC1.F9917);
        list.add(FeatureEnumC1.F9918);
        return list;
    }

    /**
     * 基础版本+用户行为偏好+CVR交叉统计
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC1006() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);

        list.add(FeatureEnumC1.F301001);
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601002);
        list.add(FeatureEnumC1.F602002);
        list.add(FeatureEnumC1.F603002);
        list.add(FeatureEnumC1.F604002);
        list.add(FeatureEnumC1.F605002);
        list.add(FeatureEnumC1.F606002);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);

        list.add(FeatureEnumC1.F804004);
        list.add(FeatureEnumC1.F805004);
        list.add(FeatureEnumC1.F806004);
        list.add(FeatureEnumC1.F807004);


        list.add(FeatureEnumC1.F808001);
        list.add(FeatureEnumC1.F809001);
        list.add(FeatureEnumC1.F810001);
        list.add(FeatureEnumC1.F811001);

        list.add(FeatureEnumC1.F808002);
        list.add(FeatureEnumC1.F809002);
        list.add(FeatureEnumC1.F810002);
        list.add(FeatureEnumC1.F811002);


        list.add(FeatureEnumC1.F9916);
        list.add(FeatureEnumC1.F9917);
        list.add(FeatureEnumC1.F9918);
        return list;
    }


    /**
     * 基础版本+用户行为偏好+CTR交叉统计
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC1007() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);//5
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);//1

        list.add(FeatureEnumC1.F301001);//2
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);//5
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601002);//11
        list.add(FeatureEnumC1.F602002);
        list.add(FeatureEnumC1.F603002);
        list.add(FeatureEnumC1.F604002);
        list.add(FeatureEnumC1.F605002);
        list.add(FeatureEnumC1.F606002);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);

        list.add(FeatureEnumC1.F804003);//4
        list.add(FeatureEnumC1.F805003);
        list.add(FeatureEnumC1.F806003);
        list.add(FeatureEnumC1.F807003);

        list.add(FeatureEnumC1.F804004);//4
        list.add(FeatureEnumC1.F805004);
        list.add(FeatureEnumC1.F806004);
        list.add(FeatureEnumC1.F807004);

        list.add(FeatureEnumC1.F808001);//4
        list.add(FeatureEnumC1.F809001);
        list.add(FeatureEnumC1.F810001);
        list.add(FeatureEnumC1.F811001);

        list.add(FeatureEnumC1.F808002);//4
        list.add(FeatureEnumC1.F809002);
        list.add(FeatureEnumC1.F810002);
        list.add(FeatureEnumC1.F811002);

        list.add(FeatureEnumC1.F9916);//3
        list.add(FeatureEnumC1.F9917);
        list.add(FeatureEnumC1.F9918);
        return list;
    }


    /**
     * 基础版本+用户行为偏好+CTR交叉统计
     *
     * @return
     */
    public static List<FeatureEnumC1> getFLC1008() {
        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F101001);//5
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F106001);
        list.add(FeatureEnumC1.F108001);
        list.add(FeatureEnumC1.F110001);

        list.add(FeatureEnumC1.F201001);//1

        list.add(FeatureEnumC1.F301001);//2
        list.add(FeatureEnumC1.F306001);

        list.add(FeatureEnumC1.F501001);//5
        list.add(FeatureEnumC1.F502001);
        list.add(FeatureEnumC1.F502002);
        list.add(FeatureEnumC1.F503001);
        list.add(FeatureEnumC1.F505001);

        list.add(FeatureEnumC1.F601002);//11
        list.add(FeatureEnumC1.F602002);
        list.add(FeatureEnumC1.F603002);
        list.add(FeatureEnumC1.F604002);
        list.add(FeatureEnumC1.F605002);
        list.add(FeatureEnumC1.F606002);
        list.add(FeatureEnumC1.F607001);
        list.add(FeatureEnumC1.F608001);
        list.add(FeatureEnumC1.F609001);
        list.add(FeatureEnumC1.F610001);
        list.add(FeatureEnumC1.F611001);

        list.add(FeatureEnumC1.F804003);//4
        list.add(FeatureEnumC1.F805003);
        list.add(FeatureEnumC1.F806003);
        list.add(FeatureEnumC1.F807003);

        list.add(FeatureEnumC1.F804004);//4
        list.add(FeatureEnumC1.F805004);
        list.add(FeatureEnumC1.F806004);
        list.add(FeatureEnumC1.F807004);

        list.add(FeatureEnumC1.F808001);//3
        list.add(FeatureEnumC1.F809001);
        list.add(FeatureEnumC1.F810001);


        list.add(FeatureEnumC1.F808002);//3
        list.add(FeatureEnumC1.F809002);
        list.add(FeatureEnumC1.F810002);


        list.add(FeatureEnumC1.F9916);//3
        list.add(FeatureEnumC1.F9917);
        list.add(FeatureEnumC1.F9918);
        return list;
    }

}
