package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum SerializerEnum {

    JAVA_ORIGINAL(0, "Java原生序列化"),
    KRYO(1, "kryo序列化"),

    ;
    private int index;

    private String desc;

    SerializerEnum(int index, String desc) {
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
