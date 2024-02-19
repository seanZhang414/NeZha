package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class NezhaStatDto implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;


    private String id;
    private Long algType;
    private Long advertId;
    private Long appId;
    private Long ctrLaunchCnt;
    private Long cvrLaunchCnt;
    private Double preCtrAcc;
    private Double preCvrAcc;
    private Double preCtrAvg;
    private Double preCvrAvg;

    private Double statCtrAcc;
    private Double statCvrAcc;
    private Double statCtrAvg;
    private Double statCvrAvg;


    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getAlgType() {
        return algType;
    }

    public void setAlgType(Long algType) {
        this.algType = algType;
    }

    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }


    public Long getCtrLaunchCnt() {
        return ctrLaunchCnt;
    }

    public void setCtrLaunchCnt(Long ctrLaunchCnt) {
        this.ctrLaunchCnt = ctrLaunchCnt;
    }


    public Long getCvrLaunchCnt() {
        return cvrLaunchCnt;
    }

    public void setCvrLaunchCnt(Long cvrLaunchCnt) {
        this.cvrLaunchCnt = cvrLaunchCnt;
    }


    public Double getPreCtrAcc() {
        return preCtrAcc;
    }

    public void setPreCtrAcc(Double preCtrAcc) {
        this.preCtrAcc = preCtrAcc;
    }


    public Double getPreCvrAcc() {
        return preCvrAcc;
    }

    public void setPreCvrAcc(Double preCvrAcc) {
        this.preCvrAcc = preCvrAcc;
    }


    public Double getPreCtrAvg() {
        return preCtrAvg;
    }

    public void setPreCtrAvg(Double preCtrAvg) {
        this.preCtrAvg = preCtrAvg;
    }


    public Double getPreCvrAvg() {
        return preCvrAvg;
    }

    public void setPreCvrAvg(Double preCvrAvg) {
        this.preCvrAvg = preCvrAvg;
    }



    public Double getStatCtrAcc() {
        return statCtrAcc;
    }

    public void setStatCtrAcc(Double statCtrAcc) {
        this.statCtrAcc = statCtrAcc;
    }


    public Double getStatCvrAcc() {
        return statCvrAcc;
    }

    public void setStatCvrAcc(Double statCvrAcc) {
        this.statCvrAcc = statCvrAcc;
    }


    public Double getStatCtrAvg() {
        return statCtrAvg;
    }

    public void setStatCtrAvg(Double statCtrAvg) {
        this.statCtrAvg = statCtrAvg;
    }


    public Double getStatCvrAvg() {
        return statCvrAvg;
    }

    public void setStatCvrAvg(Double statCvrAvg) {
        this.statCvrAvg = statCvrAvg;
    }

}
