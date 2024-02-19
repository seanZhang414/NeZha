package cn.com.duiba.nezha.compute.biz.replay;

import cn.com.duiba.nezha.compute.api.enums.ModelKeyEnum;
import junit.framework.TestCase;

/**
 * Created by pc on 2017/8/8.
 */
public class BaseReplayer$Test extends TestCase {

    public void testRepaly() throws Exception {

        BaseReplayer.replay(ModelKeyEnum.LR_CTR_MODEL_v004.getIndex());
    }
}