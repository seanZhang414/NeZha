package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: TagStat.java , v 0.1 2018/3/2 下午6:25 ZhouFeng Exp $
 */
public class TagStat implements Serializable {

    private static final long serialVersionUID = -3671571726638721514L;

    /**
     * 标签ID
     */
    private String tagId;

    /**
     * 得分
     */
    private Double score;

    /**
     * 发券量
     */
    private Long launch;

    /**
     * 点击数
     */
    private Long click;

    /**
     * 转化数
     */
    private Long convert;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Long getLaunch() {
        return launch;
    }

    public void setLaunch(Long launch) {
        this.launch = launch;
    }

    public Long getClick() {
        return click;
    }

    public void setClick(Long click) {
        this.click = click;
    }

    public Long getConvert() {
        return convert;
    }

    public void setConvert(Long convert) {
        this.convert = convert;
    }
}
