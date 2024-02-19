package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class FeatureIdxDto implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;
    private String  f101001;  // MyStringUtil2.Long2String(cf.getAdvertId()))
    private String  f106001;  // MyStringUtil2.Long2String(cf.getAccountId()));
    private String  f108001;  // MyStringUtil2.Long2String(cf.getSlotId()));
    private String  f109001;  // MyStringUtil2.Long2String(cf.getSlotType()));

    private String  f201001;  // MyStringUtil2.Long2String(cf.getAppId()));
    private String  f202001;  // cf.getAppCategory());

    private String  f301001;  // MyStringUtil2.Long2String(cf.getOperatingActivityId()));
    private String  f302001;  // MyStringUtil2.Long2String(cf.getDuibaActivityId()));
    private String  f303001;  // MyStringUtil2.Long2String(cf.getDuibaActivityType()));

    private String  f501001;  // cf.getUa());
    private String  f502001;  // MyStringUtil2.Integer2String(hour));

    private String  f502002;  // MyStringUtil2.Integer2String(weekDay));
    private String  f503001;  // MyStringUtil2.Long2String(cf.getCityId()));

    // rank 转level
    private String  f601001;  // MyStringUtil2.Long2String(getDayRankLevel(cf.getDayOrderRank())));
    private String  f602001;  // MyStringUtil2.Long2String(getRankLevel(cf.getOrderRank())));
    private String  f603001;  // MyStringUtil2.Long2String(getDayRankLevel(cf.getDayActivityOrderRank())));
    private String  f604001;  // MyStringUtil2.Long2String(getRankLevel(cf.getActivityOrderRank())));

    private String  f605001;  // MyStringUtil2.Long2String(orderGmtIntervelLevel));
    private String  f606001;  // MyStringUtil2.Long2String(activityOrderGmtIntervelLevel));
    private String  f607001;  // MyStringUtil2.Long2String(activityLastChargeStatus));
    private String  f608001;  // MyStringUtil2.Long2String(lastChargeStatus));
    private String  f609001;  // MyStringUtil2.Long2String(activityChangeStatus));

    // 交叉
    private String  cf101201; // app_id+advert_id
    // 交叉
    private String  cf101301; // duiba_activity_id+advert_id


    public String getF101001(){return f101001;}
    public void setF101001(String f101001){this.f101001 = f101001;}

    public String getF106001(){return f106001;}
    public void setF106001(String f106001){this.f106001 = f106001;}

    public String getF108001(){return f108001;}
    public void setF108001(String f108001){this.f108001 = f108001;}

    public String getF109001(){return f109001;}
    public void setF109001(String f109001){this.f109001 = f109001;}

    public String getF201001(){return f201001;}
    public void setF201001(String f201001){this.f201001 = f201001;}

    public String getF202001(){return f202001;}
    public void setF202001(String f202001){this.f202001 = f202001;}

    public String getF301001(){return f301001;}
    public void setF301001(String f301001){this.f301001 = f301001;}


    public String getF302001(){return f302001;}
    public void setF302001(String f302001){this.f302001 = f302001;}

    public String getF303001(){return f303001;}
    public void setF303001(String f303001){this.f303001 = f303001;}


    public String getF501001(){return f501001;}
    public void setF501001(String f501001){this.f501001 = f501001;}

    public String getF502001(){return f502001;}
    public void setF502001(String f502001){this.f502001 = f502001;}

    public String getF502002(){return f502002;}
    public void setF502002(String f502002){this.f502002 = f502002;}

    public String getF503001(){return f503001;}
    public void setF503001(String f503001){this.f503001 = f503001;}



    public String getF601001(){return f601001;}
    public void setF601001(String f601001){this.f601001 = f601001;}

    public String getF602001(){return f602001;}
    public void setF602001(String f602001){this.f602001 = f602001;}

    public String getF603001(){return f603001;}
    public void setF603001(String f603001){this.f603001 = f603001;}

    public String getF604001(){return f604001;}
    public void setF604001(String f604001){this.f604001 = f604001;}

    public String getF605001(){return f605001;}
    public void setF605001(String f605001){this.f605001 = f605001;}

    public String getF606001(){return f606001;}
    public void setF606001(String f606001){this.f606001 = f606001;}

    public String getF607001(){return f607001;}
    public void setF607001(String f607001){this.f607001 = f607001;}

    public String getF608001(){return f608001;}
    public void setF608001(String f608001){this.f608001 = f608001;}

    public String getF609001(){return f609001;}
    public void setF609001(String f609001){this.f609001 = f609001;}

    // 交叉
    public String getCf101201(){return cf101201;}
    public void setCf101201(String cf101201){this.cf101201 = cf101201;}

    // 交叉
    public String getCf101301(){return cf101301;}
    public void setCf101301(String cf101301){this.cf101301 = cf101301;}

}
