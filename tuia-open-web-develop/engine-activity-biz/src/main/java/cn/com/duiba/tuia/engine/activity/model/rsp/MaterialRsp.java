package cn.com.duiba.tuia.engine.activity.model.rsp;

/**
 * MaterialRsp
 */
public class MaterialRsp {

    private Long    ms_item_id;  // NOSONAR
    private Integer item_type;   // NOSONAR

    private String  image_url;   // NOSONAR
    private Integer image_width; // NOSONAR
    private Integer image_height;// NOSONAR

    private String  text;

    public String getImage_url() {//NOSONAR
        return image_url;
    }

    public void setImage_url(String image_url) {//NOSONAR
        this.image_url = image_url;
    }

    public Integer getImage_width() {//NOSONAR
        return image_width;
    }

    public void setImage_width(Integer image_width) {//NOSONAR
        this.image_width = image_width;
    }

    public Integer getImage_height() {//NOSONAR
        return image_height;
    }

    public void setImage_height(Integer image_height) {//NOSONAR
        this.image_height = image_height;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getItem_type() {//NOSONAR
        return item_type;
    }

    public void setItem_type(Integer item_type) {//NOSONAR
        this.item_type = item_type;
    }

    public Long getMs_item_id() {//NOSONAR
        return ms_item_id;
    }

    public void setMs_item_id(Long ms_item_id) {//NOSONAR
        this.ms_item_id = ms_item_id;
    }
}
