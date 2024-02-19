package cn.com.duiba.nezha.compute.biz.ps;

import cn.com.duiba.nezha.compute.biz.bo.PsBo;
import cn.com.duiba.nezha.compute.biz.dto.PsModelBaseInfo;
import cn.com.duiba.nezha.compute.biz.enums.HbaseOpsEnum;

import cn.com.duiba.nezha.compute.core.enums.DateStyle;
import cn.com.duiba.nezha.compute.core.util.DateUtil;
import cn.com.duiba.nezha.compute.core.model.local.LocalModel;
import cn.com.duiba.nezha.compute.core.model.ps.PsModel;
import com.alibaba.fastjson.JSON;


public class PsAgent {

    public static DateStyle dateStyle = DateStyle.YYYY_MM_DD_HH_MM_SS;
    public String modelId;
    public int parSize;
    public int dim;
    public PsModelBaseInfo baseInfo;
    public LocalModel localModel;


    public void setModelId(String modelId) {
        this.modelId = modelId;
    }


    public String getModelId() {
        return this.modelId;
    }

    public void setParSize(int parSize) {
        this.parSize = parSize;
    }

    public int getParSize() {
        return this.parSize;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public int getDim() {
        return this.dim;
    }


    private void setBaseInfo(PsModelBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public PsModelBaseInfo getBaseInfo() {

        return this.baseInfo;
    }


    public void setLocalModel(LocalModel localModel) {
        this.localModel = localModel;
    }

    public LocalModel getLocalModel() {
        return this.localModel;
    }


    /**
     * 拉取模型基本信息
     */
    public int getPsDim() {

        int ret = 0;
        try {
            if (getDim() == 0) {
                pullBaseInfo();
            }
            ret = getBaseInfo().getDim();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 拉取模型基本信息
     */
    public int getPsParSize() {

        int ret = 0;
        try {
            if (getDim() == 0) {
                pullBaseInfo();
            }
            ret = getBaseInfo().getParSize();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * 拉取模型基本信息
     */
    public boolean pullBaseInfo() {

        boolean ret = true;
        try {
            if (getModelId() != null) {
                PsModelBaseInfo baseInfo = PsBo.getPsBaseInfo(getModelId());

                if (baseInfo != null) {
                    ret = false;
                    setBaseInfo(baseInfo);
                } else {

                    PsModelBaseInfo baseInfo2 = new PsModelBaseInfo();
                    baseInfo2.setModelId(getModelId());
                    baseInfo2.setParSize(getParSize());
                    baseInfo2.setVersion(0);
                    baseInfo2.setDim(getDim());
                    baseInfo2.setCreateTime(DateUtil.getCurrentTime(dateStyle));

                    setBaseInfo(baseInfo2);
                }

            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return ret;
    }


    public void pushBaseInfo() {
        try {

            if (getModelId() != null && getBaseInfo() != null) {

                PsBo.updatePsBaseInfo(getModelId(), getBaseInfo());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 拉取远程模型
     *
     * @param searchLocalModel
     * @param isAll
     */
    public boolean pull(LocalModel searchLocalModel, boolean isAll) {
        boolean status = true;// 状态，是否在模型初始阶段
        try {
            if (searchLocalModel != null) {
                // 获取模型信息
                pullBaseInfo();

                if (getBaseInfo().getUpdateTime() != null) {
                    System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + " [INFO] ps pull model is exists ,init from ps");
                    // 检索数据分区
                    PsModel psModel = searchLocalModel.toPsModel(getBaseInfo().getParSize());

                    // 获取远程模型
                    PsModel ret = PsBo.searchPsModel(psModel, getBaseInfo(), isAll);
                    if (ret != null) {
                        setLocalModel(ret.toLocalModel());
                    } else {
                        setLocalModel(LocalModel.getInstance());
                    }
                    //更新初始状态
                    int iM = DateUtil.getIntervalMinutes(DateUtil.getCurrentTime(), getBaseInfo().getUpdateTime(), dateStyle);
                    if (Math.abs(iM) > 60) {
                        status = false;
                    }

                } else {
                    // 不存在更新时间，初始化模型
                    System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "[ INFO] ps pull model is not exists ,init with null");
                    setLocalModel(LocalModel.getInstance());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    public void push(LocalModel deltaLocalModel,double ratio) throws Exception {
        try {
            if (deltaLocalModel != null) {


                // 获取模型信息
                pullBaseInfo();

                // 本地模型分区
                PsModel psModel = deltaLocalModel.toPsModel(getBaseInfo().getParSize());

                //远程模型更新
                PsBo.updatePsModel(psModel, getBaseInfo(), HbaseOpsEnum.INCREMENT);

                //模型信息更新
                getBaseInfo().setUpdateTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
                pushBaseInfo();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 模型存储重构
     */
    public void rebuild(LocalModel searchLocalModel, int dim) {
        try {
            pull(searchLocalModel, true);

            PsModelBaseInfo oldBaseInfo = getBaseInfo();

            PsModelBaseInfo newBaseInfo = new PsModelBaseInfo();
            newBaseInfo.setParSize(getParSize());
            newBaseInfo.setVersion(oldBaseInfo.getVersion() + 1);
            newBaseInfo.setModelId(getModelId());
            newBaseInfo.setCreateTime(oldBaseInfo.getCreateTime());
            newBaseInfo.setUpdateTime(DateUtil.getCurrentTime(dateStyle));

            setBaseInfo(newBaseInfo);

            // 保存新模型
            push(getLocalModel(),1);

            // 删除旧模型
            PsModel psModel = getLocalModel().toPsModel(oldBaseInfo.getParSize());
            PsBo.updatePsModel(psModel, oldBaseInfo, HbaseOpsEnum.DELETE);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /**
     * 模型存储重构
     */
    public void delete(LocalModel searchLocalModel) {
        try {
            PsModelBaseInfo oldBaseInfo = getBaseInfo();

            // 删除旧模型
            PsModel psModel = searchLocalModel.toPsModel(oldBaseInfo.getParSize());
            PsBo.updatePsModel(psModel, oldBaseInfo, HbaseOpsEnum.DELETE);
            PsBo.deletePsBaseInfo(oldBaseInfo.getModelId());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /**
     * 模型存储重构
     */
    public void delete() {
        try {
            PsModelBaseInfo oldBaseInfo = getBaseInfo();
            System.out.println("oldBaseInfo"+JSON.toJSONString(oldBaseInfo));

            // 删除旧模型
            PsModel psModel = getLocalModel().toPsModel(oldBaseInfo.getParSize());

            PsBo.updatePsModel(psModel, oldBaseInfo, HbaseOpsEnum.DELETE);

            PsBo.deletePsBaseInfo(oldBaseInfo.getModelId());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
