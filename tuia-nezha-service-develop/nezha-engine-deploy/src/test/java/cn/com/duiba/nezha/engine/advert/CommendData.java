package cn.com.duiba.nezha.engine.advert;

import cn.com.duiba.nezha.engine.api.dto.AdvertNewDto;
import cn.com.duiba.nezha.engine.api.dto.MaterialDto;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.domain.advert.Material;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class CommendData {

    static Collection<Advert> getAdvert() {
        Advert advert1 = new Advert();
        advert1.setId(1L);
        OrientationPackage orientationPackage1 = new OrientationPackage();
        orientationPackage1.setAdvertId(1L);
        orientationPackage1.setId(11L);
        orientationPackage1.setChargeType(1);


        Material material111 = new Material();
        material111.setId(111L);

        Material material112 = new Material();
        material112.setId(112L);

        orientationPackage1.setMaterials(Sets.newHashSet(material111, material112));
        advert1.setOrientationPackages(Sets.newHashSet(orientationPackage1));


        Advert advert2 = new Advert();
        advert2.setId(2L);

        OrientationPackage orientationPackage2 = new OrientationPackage();
        orientationPackage2.setAdvertId(2L);
        orientationPackage2.setId(22L);
        orientationPackage2.setChargeType(1);


        OrientationPackage orientationPackage3 = new OrientationPackage();
        orientationPackage3.setAdvertId(2L);
        orientationPackage3.setId(23L);
        orientationPackage3.setChargeType(2);

        Material material21 = new Material();
        material21.setId(21L);
        Material material22 = new Material();
        material22.setId(22L);
        Material material23 = new Material();
        material23.setId(23L);
        orientationPackage2.setMaterials(Sets.newHashSet(material21, material23));
        orientationPackage3.setMaterials(Sets.newHashSet(material22, material23));
        advert2.setOrientationPackages(Sets.newHashSet(orientationPackage2, orientationPackage3));


        Advert advert3 = new Advert();
        advert3.setId(3L);

        OrientationPackage orientationPackage31 = new OrientationPackage();
        orientationPackage31.setId(31L);
        orientationPackage31.setAdvertId(3L);
        orientationPackage31.setChargeType(1);

        advert3.setOrientationPackages(Sets.newHashSet(orientationPackage31));


        return Sets.newHashSet(advert1, advert2, advert3);
    }

    public static List<AdvertNewDto> advertList() {

        Long launchCount = 1L;
        Long advertId = 1L;
        String accountId = Long.toString(1234);

        // 免费广告
        AdvertNewDto freePackage = AdvertNewDto.newBuilder()
                .advertId(advertId)
                .packageId(0L)
                .fee(0L)
                .chargeType(1)
                .accountId(accountId)
                .launchCountToUser(launchCount)
                .spreadTags(Sets.newHashSet("1", "2"))
                .matchTagNums("1,2")
                .discountRate(new Random().nextDouble())
                .build();

        // 普通广告(人工)
        AdvertNewDto normalPackage = AdvertNewDto.newBuilder()
                .advertId(advertId)
                .packageId(1L)
                .fee(30L)
                .chargeType(1)//cpc
                .accountId(accountId)
                .launchCountToUser(launchCount)
                .spreadTags(Sets.newHashSet("1", "2"))
                .matchTagNums("1,2")
                .packageBudget(200L)
                .discountRate(new Random().nextDouble())
                .build();

        // 智能采买包(cpc)
        AdvertNewDto smartPackageCPC = AdvertNewDto.newBuilder()
                .advertId(advertId)
                .packageId(2L)
                .fee(80L)
                .chargeType(1)//cpc
                .accountId(accountId)
                .convertCost(4000L)
                .launchCountToUser(launchCount)
                .spreadTags(Sets.newHashSet("1", "2"))
                .matchTagNums("1,2")
                .packageBudget(400L)
                .discountRate(new Random().nextDouble())
                .build();

        // 智能采买包(cpa)
        AdvertNewDto smartPackageCPA = AdvertNewDto.newBuilder()
                .advertId(advertId)
                .packageId(3L)
                .fee(120L)
                .chargeType(2)//cpc
                .accountId(accountId)
                .convertCost(3600L)
                .launchCountToUser(launchCount)
                .spreadTags(Sets.newHashSet("1", "2"))
                .matchTagNums("1,2")
                .discountRate(new Random().nextDouble())
                .build();

        // 自动托管包(cpa)
        AdvertNewDto trusteeshipPackageCPA = AdvertNewDto.newBuilder()
                .advertId(advertId)
                .packageId(4L)
                .fee(120L)
                .chargeType(2)//cpc
                .accountId(accountId)
                .trusteeship(true)
                .trusteeshipConvertCost(2400L)
                .launchCountToUser(launchCount)
                .spreadTags(Sets.newHashSet("1", "2"))
                .matchTagNums("1,2")
                .discountRate(new Random().nextDouble())
                .build();

        // 自动托管包(cpc)
        AdvertNewDto trusteeshipPackageCPC = AdvertNewDto.newBuilder()
                .advertId(advertId)
                .packageId(5L)
                .fee(78L)
                .chargeType(1)//cpc
                .accountId(accountId)
                .trusteeship(true)
                .trusteeshipConvertCost(4900L)
                .launchCountToUser(launchCount)
                .spreadTags(Sets.newHashSet("1", "2"))
                .matchTagNums("1,2")
                .discountRate(new Random().nextDouble())
                .build();


        MaterialDto materialDto1 = new MaterialDto();
        materialDto1.setTags(Sets.newHashSet());
        materialDto1.setInterception("1");
        materialDto1.setAtmosphere("1");
        materialDto1.setBackgroundColour("1");
        materialDto1.setPrevalent(false);
        materialDto1.setCarton("1");
        materialDto1.setBodyElement("1");
        materialDto1.setId(1L);

        MaterialDto materialDto2 = new MaterialDto();
        materialDto2.setTags(Sets.newHashSet());
        materialDto2.setInterception("2");
        materialDto2.setAtmosphere("2");
        materialDto2.setBackgroundColour("2");
        materialDto2.setPrevalent(false);
        materialDto2.setCarton("2");
        materialDto2.setBodyElement("2");
        materialDto2.setId(2L);

        MaterialDto materialDto3 = new MaterialDto();
        materialDto3.setTags(Sets.newHashSet());
        materialDto3.setInterception("3");
        materialDto3.setAtmosphere("3");
        materialDto3.setBackgroundColour("3");
        materialDto3.setPrevalent(false);
        materialDto3.setCarton("3");
        materialDto3.setBodyElement("3");
        materialDto3.setId(3L);
        HashSet<MaterialDto> materialDtos1 = Sets.newHashSet(materialDto1, materialDto2);
        HashSet<MaterialDto> materialDtos2 = Sets.newHashSet(materialDto1, materialDto2, materialDto3);

        smartPackageCPA.setMaterials(materialDtos1);
        trusteeshipPackageCPA.setMaterials(materialDtos2);
        smartPackageCPC.setMaterials(materialDtos2);


        return Lists.newArrayList(freePackage, normalPackage, smartPackageCPA, smartPackageCPC, trusteeshipPackageCPA, trusteeshipPackageCPC);

    }
}
