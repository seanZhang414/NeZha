package cn.com.duiba.tuia.engine.activity.remoteservice;

import cn.com.duiba.tuia.ssp.center.api.dto.ShieldDomainDto;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteShieldDomainSlotService;
import cn.com.duiba.wolf.dubbo.DubboResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DomainShieldService {

    @Autowired
    private RemoteShieldDomainSlotService remoteShieldDomainSlotService;


    public Set<String> selectShieldDomainBySlotId(Long slotId){
        DubboResult<List<ShieldDomainDto>> dubboResult =  remoteShieldDomainSlotService.selectShieldDomainBySlotId(slotId);
        if(!dubboResult.isSuccess()) {
            return Collections.emptySet();
        }

        List<ShieldDomainDto> domainDtoList = dubboResult.getResult();
        Set<String> shieldDomainSet = new HashSet<>();
        for(ShieldDomainDto dto:domainDtoList){
            String[] sheildDomain = dto.getDomain().split(",");
            Collections.addAll(shieldDomainSet, sheildDomain);
        }
        return shieldDomainSet;
    }

}
