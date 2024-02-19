package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class MaterialDto implements Serializable {
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

    private Set<String> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialDto that = (MaterialDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public Set<String> getTags() {
        return Optional.ofNullable(tags).orElseGet(HashSet::new);
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getInterception() {
        return interception;
    }

    public void setInterception(String interception) {
        this.interception = interception;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
