package cn.com.duiba.nezha.compute.common.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pc on 2016/11/16.
 */
public class SerializerStringListVo implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;


    private List<String>  list;


    public List<String>getList() {return list;}

    public void setList( List<String> list) {this.list = list;}


}
