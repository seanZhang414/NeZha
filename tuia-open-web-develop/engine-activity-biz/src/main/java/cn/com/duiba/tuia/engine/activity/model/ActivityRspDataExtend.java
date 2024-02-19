package cn.com.duiba.tuia.engine.activity.model;

public class ActivityRspDataExtend {

	private Long sckId;//素材库id
	private Long materialId;//素材id
	private String clickUrl;//点击URL

	public Long getSckId() {
		return sckId;
	}

	public void setSckId(Long sckId) {
		this.sckId = sckId;
	}

	public Long getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Long materialId) {
		this.materialId = materialId;
	}

	public String getClickUrl() {
		return clickUrl;
	}

	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}
}
