package cn.com.duiba.nezha.compute.common.model.pacing;

/**
 * Created by jiali on 2018/2/6.
 */
public class StatInfo
{
    Double app1h;
    Double app1d;
    Double app7d;
    Double g1h;
    Double g1d;
    Double g7d;

    public void setApp1d(Double app1d) {
        this.app1d = app1d;
    }

    public void setApp1h(Double app1h) {
        this.app1h = app1h;
    }

    public void setApp7d(Double app7d) {
        this.app7d = app7d;
    }

    public void setG1d(Double g1d) {
        this.g1d = g1d;
    }

    public void setG1h(Double g1h) {
        this.g1h = g1h;
    }

    public void setG7d(Double g7d) {
        this.g7d = g7d;
    }

    public Double getApp1d() {
        return app1d;
    }

    public Double getApp1h() {
        return app1h;
    }

    public Double getApp7d() {
        return app7d;
    }

    public Double getG1d() {
        return g1d;
    }

    public Double getG1h() {
        return g1h;
    }

    public Double getG7d() {
        return g7d;
    }
}
