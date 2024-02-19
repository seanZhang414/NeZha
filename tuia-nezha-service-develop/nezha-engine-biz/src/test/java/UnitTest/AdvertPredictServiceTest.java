package UnitTest;

import cn.com.duiba.nezha.engine.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.impl.AdvertPredictServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AdvertPredictServiceImpl.class)
@PowerMockIgnore({"javax.management.*", "javax.crypto.*"})
public class AdvertPredictServiceTest {


    @InjectMocks
    private AdvertPredictServiceImpl advertPredictService;

    @Mock
    private MongoTemplate mongoTemplate;


    @Test
    public void a() {

        ModelKeyEnum[] modelKeyEnums = ModelKeyEnum.values();

        for (ModelKeyEnum modelKeyEnum : modelKeyEnums) {

        }

/*

        new FeatureParameter.Builder()
                .advertBackendStatMap()
                .advertList()
                .advertStatFeatureMap()
                .featureDo()
                .build()
        ;

        Map<Long, Map<String, String>> advertFeatureMap = advertPredictService.getFeatureMap();

*/


    }
}
