package cn.com.duiba.tuia.constant;

/**
 * Created by shenjunlin on 2017/8/21.
 */
public class ActivityConstant {

    /**
     * 活动来源
     */
    public enum Source {
        DUI_BA(0),
        TUI_A(1),
        GUIDE_PAGE(2);

        private int value;

        Source(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
