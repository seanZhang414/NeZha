package cn.com.duiba.nezha.engine.biz.service.advert.rerank;

import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.support.advert.ComparatorAdvertResortVo;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertResortVo;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pc on 2016/10/18.
 */
@Service
public class AdvertReRankService {

    public List<AdvertResortVo> reRank(List<AdvertResortVo> advertResortVoList) {
        try {
            DBTimeProfile.enter("reRank");
            List<AdvertResortVo> retVoList = new ArrayList<>();

            if (advertResortVoList.isEmpty()) {
                return retVoList;
            }

            //排序
            Comparator<AdvertResortVo> tagCmp = new ComparatorAdvertResortVo();
            advertResortVoList.sort(tagCmp);
            for (int r = 0; r < advertResortVoList.size(); r++) {
                advertResortVoList.get(r).setRank((long) r);
            }
            return advertResortVoList;
        } catch (Exception e) {
            throw new RecommendEngineException("reRank happen error", e);
        } finally {
            DBTimeProfile.release();
        }


    }
}
