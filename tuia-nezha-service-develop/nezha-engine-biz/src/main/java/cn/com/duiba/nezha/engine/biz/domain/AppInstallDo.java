package cn.com.duiba.nezha.engine.biz.domain;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AppInstallDo.java , v 0.1 2018/1/11 下午3:58 ZhouFeng Exp $
 */
@SuppressWarnings({"squid:S00116", "squid:S00100", "squid:S00117"})
public class AppInstallDo {

    /**
     * 是否为游戏 1：是 0：否
     */
    private String isgame;

    /**
     * 一级分类
     */
    private String category1_id;

    /**
     * 二级分类 多个数值按，分隔
     */
    private String category2_id;

    /**
     * app名称
     */
    private String id;

    private String class_id;

    private String is_important;


    public String getIsgame() {
        return isgame;
    }

    public void setIsgame(String isgame) {
        this.isgame = isgame;
    }

    public String getCategory1_id() {
        return category1_id;
    }

    public void setCategory1_id(String category1_id) {
        this.category1_id = category1_id;
    }

    public String getCategory2_id() {
        return category2_id;
    }

    public void setCategory2_id(String category2_id) {
        this.category2_id = category2_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getIs_important() {
        return is_important;
    }

    public void setIs_important(String is_important) {
        this.is_important = is_important;
    }

    public Boolean isImportant() {
        return "1".equals(is_important);
    }
    public Boolean isGame() {
        return "1".equals(isgame);
    }
}