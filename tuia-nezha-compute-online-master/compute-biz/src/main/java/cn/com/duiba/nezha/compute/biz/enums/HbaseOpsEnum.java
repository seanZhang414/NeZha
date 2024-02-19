package cn.com.duiba.nezha.compute.biz.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum HbaseOpsEnum {

    SEARCH(0, "读取"),
    INSERT_AND_UPDATE(1, "插入 或 更新"),
    DELETE(2, "删除"),
    INCREMENT(3, "自增"),

    ;

    private int index;

    private String desc;

    HbaseOpsEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public int  getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }
}
