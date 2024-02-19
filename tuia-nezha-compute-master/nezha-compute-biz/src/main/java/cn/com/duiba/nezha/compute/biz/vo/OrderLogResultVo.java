package cn.com.duiba.nezha.compute.biz.vo;


import java.io.Serializable;
import java.util.List;

/**
 * Created by pc on 2016/11/16.
 */
public class OrderLogResultVo implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;
    private List<OrderFeatureSyncVo> orderFeatureSyncVoList = null;
    private AdvertStatVo advertStatVo = null;


    public List<OrderFeatureSyncVo> getOrderFeatureSyncVoList() {return orderFeatureSyncVoList;}

    public void setOrderFeatureSyncVoList(List<OrderFeatureSyncVo> orderFeatureSyncVoList) {this.orderFeatureSyncVoList = orderFeatureSyncVoList;}

    public AdvertStatVo getAdvertStatVo() {return advertStatVo;}

    public void setAdvertStatVo(AdvertStatVo advertStatVo) {this.advertStatVo = advertStatVo;}

}
