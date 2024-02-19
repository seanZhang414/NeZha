package cn.com.duiba.nezha.engine.biz.entity.nezha.advert;

import java.util.Date;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: CpaFactorEntity.java , v 0.1 2017/7/26 上午10:28 ZhouFeng Exp $
 */
public class CpaFactorEntity {

    private String id;
    private Long advertId;
    private Long packageId;
    private Long cvr;
    private Long fee;
    private Long click;
    private Double factor;
    private Date gmtCreate;
    private Date gmtModified;
    private Date expireAt;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getCvr() {
        return cvr;
    }

    public void setCvr(Long cvr) {
        this.cvr = cvr;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Long getClick() {
        return click;
    }

    public void setClick(Long click) {
        this.click = click;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }
}
