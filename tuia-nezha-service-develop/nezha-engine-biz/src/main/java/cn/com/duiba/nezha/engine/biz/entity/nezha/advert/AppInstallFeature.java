package cn.com.duiba.nezha.engine.biz.entity.nezha.advert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AppInstallFeature.java , v 0.1 2018/1/10 下午7:58 ZhouFeng Exp $
 */
public class AppInstallFeature {

    public static final AppInstallFeature DEFAULT = new AppInstallFeature.Builder()
            .firstCategory(Collections.emptyList())
            .secondCategory(Collections.emptyList())
            .clusterIdList(Collections.emptyList())
            .importantAppList(Collections.emptyList())
            .hasGame(false)
            .build();

    /**
     * 一级类别
     */
    private List<String> firstCategory;

    /**
     * 二级类别
     */
    private List<String> secondCategory;

    /**
     * app类别列表
     */
    private List<String> clusterIdList;

    /**
     * 重要app列表
     */
    private List<String> importantAppList;

    /**
     * 是否有游戏
     */
    private Boolean hasGame;

    /**
     * 一级类目出现的个数
     */
    private Map<String, Long> firstCategoryCount;

    /**
     * 二级类目出现的个数
     */
    private Map<String, Long> secondCategoryCount;


    private AppInstallFeature(Builder builder) {
        firstCategory = builder.firstCategory;
        secondCategory = builder.secondCategory;
        clusterIdList = builder.clusterIdList;
        importantAppList = builder.importantAppList;
        hasGame = builder.hasGame;
        firstCategoryCount = builder.firstCategoryCount;
        secondCategoryCount = builder.secondCategoryCount;
    }


    public List<String> getFirstCategory() {
        return firstCategory;
    }

    public List<String> getSecondCategory() {
        return secondCategory;
    }

    public List<String> getClusterIdList() {
        return clusterIdList;
    }

    public List<String> getImportantAppList() {
        return importantAppList;
    }

    public Boolean getHasGame() {
        return hasGame;
    }

    public Map<String, Long> getFirstCategoryCount() {
        return firstCategoryCount;
    }

    public Map<String, Long> getSecondCategoryCount() {
        return secondCategoryCount;
    }

    public static final class Builder {
        private List<String> firstCategory;
        private List<String> secondCategory;
        private List<String> clusterIdList;
        private List<String> importantAppList;
        private Boolean hasGame;
        private Map<String, Long> firstCategoryCount;
        private Map<String, Long> secondCategoryCount;

        public Builder() {
            //builder
        }

        public Builder firstCategory(List<String> val) {
            firstCategory = val;
            return this;
        }

        public Builder secondCategory(List<String> val) {
            secondCategory = val;
            return this;
        }

        public Builder clusterIdList(List<String> val) {
            clusterIdList = val;
            return this;
        }

        public Builder importantAppList(List<String> val) {
            importantAppList = val;
            return this;
        }

        public Builder hasGame(Boolean val) {
            hasGame = val;
            return this;
        }

        public Builder firstCategoryCount(Map<String, Long> val) {
            firstCategoryCount = val;
            return this;
        }

        public Builder secondCategoryCount(Map<String, Long> val) {
            secondCategoryCount = val;
            return this;
        }

        public AppInstallFeature build() {
            return new AppInstallFeature(this);
        }
    }
}
