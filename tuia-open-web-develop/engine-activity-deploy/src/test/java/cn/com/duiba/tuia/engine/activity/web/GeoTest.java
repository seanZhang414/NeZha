package cn.com.duiba.tuia.engine.activity.web;

import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.ssp.center.api.dto.IpAreaLibraryDto;
import cn.com.duiba.tuia.utils.GeoData;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GeoTest {

    @Autowired
    private LocalCacheService localCacheService;

    @Test
    public void testGeo() {
        GeoData data = null;

        IpAreaLibraryDto byIpLong = localCacheService.findByIpLong(IpAreaLibraryDto.convertIpLong("115.238.95.186"));
        if (byIpLong != null) {
            data = new GeoData();
            data.setCountry(byIpLong.getCountry());
            data.setProvince(byIpLong.getProvince());
            data.setCity(byIpLong.getCity());
            data.setJson(JSON.toJSONString(byIpLong));
        }
        System.out.println(data);
    }
}
