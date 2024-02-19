package cn.com.duiba.nezha.compute.biz.dto;

public class PsModelBaseInfo {

    private String modelId;
    private int parSize = 10000;
    private int version = 0;
    private int dim = 0;
    private String updateTime;
    private String createTime;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }


    public int getParSize() {
        return parSize;
    }

    public void setParSize(int parSize) {
        this.parSize = parSize;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


}
