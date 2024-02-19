package UnitTest;

import cn.com.duiba.nezha.engine.biz.service.advert.rerank.AdvertReRankService;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertResortVo;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertReRankServiceTest.java , v 0.1 2017/8/17 下午4:45 ZhouFeng Exp $
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AdvertReRankService.class})
public class AdvertReRankServiceTest {

    @InjectMocks
    private AdvertReRankService advertReRankService;

    @Test
    public void when_advert_recommend_then_choose_bigest_score() {


        List<Double> score = Lists.newArrayList(58.12045580413749, 11.87229209492343, 52.93908064037576,
                50.41896148391471, 30.49958952647461, 42.32670892999371, 34.32854941408004, 22.123628868654365,
                56.054556305073135, 65.8508753798429, 54.66617012547344, 54.65785845049914, 11.061182771569522,
                87.20833081180866, 3.44678466134154, 48.36505284536956, 18.22526325825109, 84.77670204517986,
                96.44753436924573, 93.22210874733608, 38.155989383835276, 34.206702454231106, 1.248401324551851,
                34.95935459472543, 42.27841047837618, 74.91314488142754, 7.485569464384967, 76.71927039893018,
                10.937491896041184, 84.13867214452254, 48.86014993349763, 49.37406730170374, 42.12950190489227,
                77.68666787009224, 37.68259484530011, 86.60187791597052, 45.13710982362643, 8.85441201874757,
                89.23345645904186, 41.51219639863385, 47.39976854914944, 64.63124742869965, 1.0333463416896338,
                95.0421568022873, 33.88900102552041, 65.89297538509823, 10.610409757819983, 43.08595620851463,
                59.580587351488745, 4.549668552497854, 87.42725993910486, 30.41316542441973, 12.478500507006075,
                95.8029978820782, 22.97298754262397, 75.67116157126938, 96.81980244982107, 96.7088718858378,
                3.093524471681175, 66.53883694931214, 0.536163103726206, 4.0396665457587355, 94.00521549820454,
                28.200597677853658, 43.00711466701452, 21.967926338943975, 87.98201165665058, 78.91410207049704,
                27.389380212908566, 18.225003369256598, 5.681443399900976, 65.99712025118848, 93.80493333745233,
                35.158297774122516, 96.76074230378676, 44.54335108721686, 15.745705916109953, 6.5316691477421145,
                36.8613894326714, 62.61183281311341, 26.34402538349573, 85.42167695334923, 35.31770680229328,
                49.298357964890215, 75.27608953172304, 15.900955873956823, 96.62614523169937, 13.306593992913873,
                49.025184857267945, 52.20050796202996, 23.150771263167048, 23.83015536018034, 33.24133261925031,
                91.38557744511459, 49.646621459758066, 29.89378376918932, 79.58922908724062, 53.86777658418518,
                47.176905985765416, 12.205547059777066);

        List<AdvertResortVo> advertResortVoList = new ArrayList<>();

        Stream.iterate(1L, sead -> sead + 1).limit(100).forEach(advertId -> {
            Double value = score.get((int) (advertId - 1));

            AdvertResortVo advertResortVo = new AdvertResortVo();
            advertResortVo.setAdvertId(advertId);
            advertResortVo.setRankScore(value);

            advertResortVoList.add(advertResortVo);
        });


        List<AdvertResortVo> advertResortVos = advertReRankService.reRank(advertResortVoList);


        Assert.assertEquals(advertResortVoList.size(), advertResortVos.size());

        AdvertResortVo advertResortVo = advertResortVos.get(0);

        Assert.assertEquals(advertResortVo.getAdvertId().longValue(), 57L);
        Assert.assertEquals(advertResortVo.getRank().longValue(), 0L);
        Assert.assertTrue(advertResortVo.getRankScore() - 96.81980244982107 < 0.0000001);

    }

}
