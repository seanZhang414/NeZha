package cn.com.duiba.nezha.compute.common.model;


import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class RoiPidControllerTest {

    @Test
    public void testAllFactorCase1()
    {
        RoiPidController PID = new RoiPidController();

        StatInfo s1 = new StatInfo();
        mock(s1,"11789_0_DEFAULT",true,30,1,0.8,30,3);

        List<StatInfo> infos = new ArrayList<>();
        infos.add(s1);

        simulate(infos, 100, 5, 0.2);

    }

    private void mock(StatInfo info, String id, boolean isStart, int click, double factor, double parent, int fee, int conv)
    {
        if(isStart)
        {
            info.lastSumConv = 0;
            info.lastSumFee = 0;
        }
        info.sumClick = click;
        info.factor =factor;
        info.sumFee = fee * 100;
        info.sumConv = conv;
        info.id = id;
        info.parentFactor = parent;
    }

    private void simulate(List<StatInfo> infos, int times, int target, double startBid)
    {
        double bid = startBid * 100;
        double maxConv = 100;
        double targetCpa = 1000;
        double actualCpa;
        double budget = 30000;

        for (int i = 0; i < 200; i++){

            if (i == 60)
                targetCpa = 3000;

            double factor = 1;

        }
    }
}
