package cn.com.duiba.nezha.compute.common.model;
import java.util.List;
public class MaterialInfo{
    long materialId;
    long appId;
    double alpha = 2;
    double beta = 2;
    double reward = 0;
    double count = 0;
    List<Long> click;      //最近十分钟的点击
    List<Long> exposure;   //最近十分钟的曝光
    List<Long> globalClick;
    List<Long> globalExposure;
    double lastClick = 0;
    double lastExposure = 1;
    double globalLastClick = 0;
    double globalLastExposure = 1;
    int rank;

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setAppId(long appId) {
            this.appId = appId;
        }

    public void setBeta(double beta) {
            this.beta = beta;
        }

    public void setClick(List<Long> click) {
            this.click = click;
        }

    public void setCount(double count) {
            this.count = count;
        }

    public void setExposure(List<Long> exposure) {
            this.exposure = exposure;
        }

    public void setLastClick(double lastClick) {
            this.lastClick = lastClick;
        }

    public void setLastExposure(double lastExposure) {
            this.lastExposure = lastExposure;
        }

    public void setMaterialId(long materialId) {
            this.materialId = materialId;
        }

    public void setReward(double reward) {
            this.reward = reward;
        }

    public List<Long> getClick() {
            return click;
        }

    public List<Long> getExposure() {
            return exposure;
        }

    public double getAlpha() {
            return alpha;
        }

    public double getBeta() {
            return beta;
        }

    public double getCount() {
            return count;
        }

    public double getReward() {
            return reward;
        }

    public long getAppId() {
            return appId;
        }

    public double getLastClick() {
            return lastClick;
        }

    public double getLastExposure() {
            return lastExposure;
        }

    public long getMaterialId() {
            return materialId;
        }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<Long> getGlobalClick() {
        return globalClick;
    }

    public List<Long> getGlobalExposure() {
        return globalExposure;
    }

    public void setGlobalClick(List<Long> globalClick) {
        this.globalClick = globalClick;
    }

    public void setGlobalExposure(List<Long> globalExposure) {
        this.globalExposure = globalExposure;
    }

    public double getGlobalLastClick() {
        return globalLastClick;
    }

    public double getGlobalLastExposure() {
        return globalLastExposure;
    }

    public void setGlobalLastClick(double globalLastClick) {
        this.globalLastClick = globalLastClick;
    }

    public void setGlobalLastExposure(double globalLastExposure) {
        this.globalLastExposure = globalLastExposure;
    }
}