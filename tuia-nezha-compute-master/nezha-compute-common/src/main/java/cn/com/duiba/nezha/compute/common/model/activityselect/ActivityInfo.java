package cn.com.duiba.nezha.compute.common.model.activityselect;

import java.io.Serializable;
/**
 * Created by jiali on 2017/10/17.
 */
public class ActivityInfo implements Serializable{
    long activityId;
    long slotId;
    long source;
    double alpha = 1.5;
    double beta = 2;
    double reward = 0;
    double count = 0;
    boolean valid = true;

    Val request;  //活动请求uv
    Val click;    //券点击
    Val send;     //发券
    Val cost;     //消耗
    Val directRequest;  //直投活动请求uv
    Val directClick;
    Val directSend;
    Val directCost;
    Val hisRequest;
    Val hisClick;
    Val hisSend;
    Val hisCost;
    Val lastRequest;
    Val lastClick;
    Val lastSend;
    Val lastCost;
    boolean isUpdate = false;
    long createTime = 0;  //微秒
    long updateTime = 0; //微秒

    public class Val implements Serializable{
        public double appVal = 0;
        public double slotVal = 0;
        public double globalVal = 0;

        public double getAppVal() {
            return appVal;
        }

        public double getGlobalVal() {
            return globalVal;
        }

        public double getSlotVal() {
            return slotVal;
        }

        public void setAppVal(double appVal) {
            this.appVal = appVal;
        }

        public void setGlobalVal(double globalVal) {
            this.globalVal = globalVal;
        }

        public void setSlotVal(double slotVal) {
            this.slotVal = slotVal;
        }
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public double getBeta() {
        return beta;
    }

    public long getSlotId() {
        return slotId;
    }

    public long getActivityId() {
        return activityId;
    }

    public boolean getIsUpdate() {
        return isUpdate;
    }

    public double getReward() {
        return reward;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getCount() {
        return count;
    }

    public boolean getValid() {
        return valid;
    }


    public long getSource() {
        return source;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public Val getClick() {
        return click;
    }

    public Val getHisClick() {
        return hisClick;
    }

    public Val getHisRequest() {
        return hisRequest;
    }

    public Val getHisSend() {
        return hisSend;
    }

    public Val getLastClick() {
        return lastClick;
    }

    public Val getLastRequest() {
        return lastRequest;
    }

    public Val getLastSend() {
        return lastSend;
    }

    public Val getRequest() {
        return request;
    }

    public Val getSend() {
        return send;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setSlotId(long slotId) {
        this.slotId = slotId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public void setClick(Val click) {
        this.click = click;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public void setHisClick(Val hisClick) {
        this.hisClick = hisClick;
    }

    public void setHisRequest(Val hisRequest) {
        this.hisRequest = hisRequest;
    }

    public void setHisSend(Val hisSend) {
        this.hisSend = hisSend;
    }

    public void setLastClick(Val lastClick) {
        this.lastClick = lastClick;
    }

    public void setLastRequest(Val lastRequest) {
        this.lastRequest = lastRequest;
    }

    public void setLastSend(Val lastSend) {
        this.lastSend = lastSend;
    }

    public void setRequest(Val request) {
        this.request = request;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public void setSend(Val send) {
        this.send = send;
    }

    public void setSource(long source) {
        this.source = source;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public void setIsUpdate(boolean update) {
        this.isUpdate = update;
    }

    public Val getDirectClick() {
        return directClick;
    }

    public Val getDirectRequest() {
        return directRequest;
    }

    public Val getDirectSend() {
        return directSend;
    }

    public void setDirectClick(Val directClick) {
        this.directClick = directClick;
    }

    public void setDirectRequest(Val directRequest) {
        this.directRequest = directRequest;
    }

    public void setDirectSend(Val directSend) {
        this.directSend = directSend;
    }

    public Val getVal()
    {
        return new Val();
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Val getCost() {
        return cost;
    }

    public Val getDirectCost() {
        return directCost;
    }

    public Val getHisCost() {
        return hisCost;
    }

    public Val getLastCost() {
        return lastCost;
    }

    public void setCost(Val cost) {
        this.cost = cost;
    }

    public void setDirectCost(Val directCost) {
        this.directCost = directCost;
    }

    public void setHisCost(Val hisCost) {
        this.hisCost = hisCost;
    }

    public void setLastCost(Val lastCost) {
        this.lastCost = lastCost;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}



