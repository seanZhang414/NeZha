package cn.com.duiba.tuia.engine.activity.remoteservice;

import cn.com.duiba.tuia.ssp.center.api.dto.DomainConfigDto;
import cn.com.duiba.tuia.ssp.center.api.dto.DomainInfoDto;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteDomainConfigService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteDomainInfoService;
import cn.com.duiba.wolf.dubbo.DubboResult;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.duiba.tuia.ssp.center.api.dto.SystemConfigDto;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteSystemConfigService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class SystemConfigService implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(SystemConfigService.class);

    @Autowired
    RemoteSystemConfigService remoteSystemConfigService;

    @Autowired
    private RemoteDomainInfoService remoteDomainInfoService;

    @Autowired
    private RemoteDomainConfigService remoteDomainConfigService;

    /** 系统配置缓存 */
    private LoadingCache<String,Optional<SystemConfigDto>> sysConfigCache;


    @Override
    public void afterPropertiesSet() throws Exception {
        sysConfigCache = CacheBuilder
                .newBuilder()
                .maximumSize(100)
                .refreshAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Optional<SystemConfigDto>>() {

                    @Override
                    public Optional<SystemConfigDto> load(String key) throws Exception {
                        return Optional.ofNullable(getGlobalHost(key));
                    }
                });
    }


    public SystemConfigDto getSystemConfig(String key) {
        try {
            Optional<SystemConfigDto> s = this.sysConfigCache.get(key);
            return s.orElse(null);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }

    }


    public String getTuiaHost(String key){
        DubboResult<String> result = remoteSystemConfigService.selectTuiaValueByKey(key);
        if (!result.isSuccess()){
            log.error("getTuiaHost error,tuia key=[{}],msg=[{}],", key, result.getMsg());
        }
        return result.getResult();
    }

    public SystemConfigDto getGlobalHost(String key){
        DubboResult<SystemConfigDto> result = remoteSystemConfigService.selectByKey(key);
        if (!result.isSuccess()){
            log.error("getGlobalHost error,tuia key=[{}],msg=[{}],", key, result.getMsg());
            return null;
        }
        return result.getResult();
    }

    public String selectActDomainBySlotId(Long slotId){
        String actUrl  = "";
        DubboResult<DomainInfoDto> dubboResult = remoteDomainInfoService.selectDomainBySlotId(slotId);
        if (!dubboResult.isSuccess()){
            log.error("remoteDomainInfoService selectDomainBySlotId error, msg=[{}],", dubboResult.getMsg());
            return actUrl;
        }
        DomainInfoDto domainInfoDto = dubboResult.getResult();
        if(domainInfoDto!=null){
            actUrl = JSONObject.toJSONString(domainInfoDto);
        }
        return actUrl;
    }

    public DomainConfigDto selectDomainConfigByAppId(Long appId) {
        DomainConfigDto domainConfigDto = remoteDomainConfigService.selectByAppId(appId);
        if (domainConfigDto == null || StringUtils.isBlank(domainConfigDto.getActUrl())) {
            domainConfigDto = new DomainConfigDto();
            SystemConfigDto systemConfigDto = this.getGlobalHost("tuia-activity-url");
            domainConfigDto.setActUrl(systemConfigDto.getTuiaValue());
            domainConfigDto.setProtocolHeader(DomainConfigDto.PROTOCOL_HTTPS);
        }
        return domainConfigDto;
    }

}
