package cn.com.duiba.nezha.engine.biz.domain.advert;

import cn.com.duiba.nezha.engine.api.dto.AdvertNewDto;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class Material {

    private Long id;

    // 素材氛围
    private String atmosphere;

    // 背景颜色
    private String backgroundColour;

    // 是否流行
    private Boolean prevalent;

    // 文案
    private String interception;

    // 卡通类型
    private String carton;

    // 主体类型
    private String bodyElement;

    // 素材标签
    private Set<String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(String atmosphere) {
        this.atmosphere = atmosphere;
    }

    public String getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundColour(String backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    public Boolean getPrevalent() {
        return prevalent;
    }

    public void setPrevalent(Boolean prevalent) {
        this.prevalent = prevalent;
    }

    public String getInterception() {
        return interception;
    }

    public void setInterception(String interception) {
        this.interception = interception;
    }

    public String getCarton() {
        return carton;
    }

    public void setCarton(String carton) {
        this.carton = carton;
    }

    public String getBodyElement() {
        return bodyElement;
    }

    public void setBodyElement(String bodyElement) {
        this.bodyElement = bodyElement;
    }

    public Set<String> getTags() {
        return Optional.ofNullable(tags).orElseGet(HashSet::new);
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public static Set<Material> convert(AdvertNewDto advertDto) {
        Map<Long, Set<String>> materialMapNew = advertDto.getMaterialMapNew();
        return advertDto.getMaterials()
                .stream()
                .map(materialDto -> {
                    Material material = new Material();
                    Long id = materialDto.getId();
                    material.setId(id);
                    material.setAtmosphere(materialDto.getAtmosphere());
                    material.setBackgroundColour(materialDto.getBackgroundColour());
                    material.setPrevalent(materialDto.getPrevalent());
                    material.setInterception(materialDto.getInterception());
                    material.setCarton(materialDto.getCarton());
                    material.setBodyElement(materialDto.getBodyElement());
                    material.setTags(materialMapNew.getOrDefault(id, new HashSet<>()));
                    return material;
                })
                .collect(toSet());
    }
}
