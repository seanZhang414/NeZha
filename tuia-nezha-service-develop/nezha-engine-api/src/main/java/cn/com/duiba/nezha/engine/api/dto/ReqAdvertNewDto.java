package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lwj on 16/8/2. <br/>
 * 广告结果集
 */
public class ReqAdvertNewDto implements Serializable {

    private static final long serialVersionUID = 523827427472072954L;

    /**
     * 用户参数
     */
    private ConsumerDto consumerDto;

    /**
     * 广告列表
     */
    private List<AdvertNewDto> advertList;

    /**
     * 请求参数
     */
    private RequestDto requestDto;

    /**
     * app参数
     */
    private AppDto appDto;

    /**
     * 活动参数
     */
    private AdvertActivityDto advertActivityDto;

    public ReqAdvertNewDto() {
        consumerDto = new ConsumerDto();
        requestDto = new RequestDto();
        appDto = new AppDto();
        advertActivityDto = new AdvertActivityDto();
    }


    public ConsumerDto getConsumerDto() {
        return consumerDto;
    }

    public ReqAdvertNewDto setConsumerDto(ConsumerDto consumerDto) {
        this.consumerDto = consumerDto;
        return this;
    }

    public List<AdvertNewDto> getAdvertList() {
        return advertList;
    }

    public ReqAdvertNewDto setAdvertList(List<AdvertNewDto> advertList) {
        this.advertList = advertList;
        return this;
    }

    public RequestDto getRequestDto() {
        return requestDto;
    }

    public ReqAdvertNewDto setRequestDto(RequestDto requestDto) {
        this.requestDto = requestDto;
        return this;
    }

    public AppDto getAppDto() {
        return appDto;
    }

    public ReqAdvertNewDto setAppDto(AppDto appDto) {
        this.appDto = appDto;
        return this;
    }

    public AdvertActivityDto getAdvertActivityDto() {
        return advertActivityDto;
    }

    public ReqAdvertNewDto setAdvertActivityDto(AdvertActivityDto advertActivityDto) {
        this.advertActivityDto = advertActivityDto;
        return this;
    }

}
