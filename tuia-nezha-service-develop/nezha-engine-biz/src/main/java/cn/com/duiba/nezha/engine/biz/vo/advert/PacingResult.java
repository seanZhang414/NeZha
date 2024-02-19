package cn.com.duiba.nezha.engine.biz.vo.advert;

public class PacingResult {

    public static final PacingResult DEFAULT = PacingResult.newBuilder()
            .tag(0L)
            .giveUp(false)
            .build();

    private Boolean giveUp;
    private Long tag;

    private PacingResult(Builder builder) {
        setGiveUp(builder.giveUp);
        setTag(builder.tag);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public Boolean getGiveUp() {
        return giveUp;
    }

    public void setGiveUp(Boolean giveUp) {
        this.giveUp = giveUp;
    }

    public Long getTag() {
        return tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
    }


    public static final class Builder {
        private Boolean giveUp;
        private Long tag;

        private Builder() {
        }

        public Builder giveUp(Boolean val) {
            giveUp = val;
            return this;
        }

        public Builder tag(Long val) {
            tag = val;
            return this;
        }

        public PacingResult build() {
            return new PacingResult(this);
        }
    }
}
