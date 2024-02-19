package cn.com.duiba.nezha.compute.feature.vo;

import java.io.Serializable;

public class FeatureCodeInfo implements Serializable{

    public String name;
    public int codeType;//10: sub,11:subs    20:hash,21:hashs    30:dict,31:dicts
    public int subLen;//
    public int hashNums;//
    public String value;


    public FeatureCodeInfo(String name, int codeType, int subLen, int hashNums, String value) {
        this.name = name;
        this.codeType = codeType;
        this.hashNums = hashNums;
        this.subLen = subLen;
        this.value = value;
    }


    public String getName() {
        return this.name;
    }

    public int getCodeType() {
        return this.codeType;
    }

    public int getSubLen() {
        return this.subLen;
    }

    public int getHashNums() {
        return this.hashNums;
    }

    public String getValue() {
        return this.value;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    public void setSubLen(int subLen) {
        this.subLen = subLen;
    }

    public void setHashNums(int hashNums) {
        this.hashNums = hashNums;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
