package cn.com.duiba.nezha.compute.feature.enums;

/**
 * Created by pc on 2017/1/10.
 */
public enum FeatureEnumC1 {
    F101001(101001, "f101001", 10, 1000000, 1, "advert_id"),
    F102001(102001, "f102001", 21, 1000000,  3, "match_tag_nums"),
//    F103001(103001, "f103001", 10, 100000, 1, "tag_num_category"),
    F104001(104001, "f104001", 10, 1000000, 1, "material_id"),
//    F105001(105001, "f105001", 10, 100000, 1, "advert_name"),
    F106001(106001, "f106001", 10, 100000, 1, "account_id"),
//    F107001(107001, "f107001", 10, 100000, 1, "coupon_name"),
    F108001(108001, "f108001", 10, 100000, 1, "slot_id"),
//    F109001(109001, "f109001", 10, 100000, 1, "slot_type"),
    F110001(110001, "f110001", 10, 10000, 1, "times"),
    F111001(111001, "f111001", 20, 10000, 3, "promote_url"),
    F112001(112001, "f112001", 21, 10000, 3, "material_tags"),
    F113001(113001, "f113001", 21, 100000, 3, "advert_tags"),
    F114001(114001, "f114001", 10, 1000, 1, "slot_industry_tag_pid"),
    F114002(114002, "f114002", 10, 100000, 1, "slot_industry_tag_id"),
    F115001(115001, "f115001", 10, 100, 1, "backend_type"),


    F201001(201001, "f201001", 10, 1000000, 1, "app_id"),
    F202001(202001, "f202001", 10, 100000, 1, "app_category1"),
//    F203001(203001, "f203001", 10, 100000, 1, "app_category2"),
//    F204001(204001, "f204001", 10, 100000, 1, "developer_id"),
    F205001(205001, "f205001", 10, 10000, 1, "app_industry_tag_pid"),
    F205002(205002, "f205002", 10, 10000, 1, "app_industry_tag_id"),
    F206001(206001, "f206001", 10, 10000, 1, "traffic_tag_pid"),
    F206002(206002, "f206002", 10, 10000, 1, "traffic_tag_id"),

    F301001(301001, "f301001", 10, 10000000, 1, "activity_id"),
    F302001(302001, "f302001", 10, 100000, 1, "duiba_activity_id"),
    F303001(303001, "f303001", 10, 10000, 1, "duiba_activity_type"),
//    F304001(304001, "f304001", 10, 100000, 1, "activity_tag_num"),
    F305001(305001, "f305001", 20, 100000, 3, "activity_sub_type"),
    F306001(306001, "f306001", 10, 10, 1, "activity_use_type"),
    F307001(307001, "f307001", 10, 10000, 1, "source_id"),
    F308001(308001, "f308001", 10, 100000, 1, "source_type"),
//    F309001(309001, "f309001", 10, 100000, 1, "appBannerId"),
//    F310001(310001, "f310001", 10, 100000, 1, "activity_tag"),

//    F401001(401001, "f401001", 10, 100000, 1, "order_cycle_level"),
//    F402001(402001, "f402001", 10, 100000, 1, "consumer_id"),
//    F403001(403001, "f403001", 10, 100000, 1, "member_id"),
//    F403002(403002, "f403002", 10, 100000, 1, " province"),
//    F403003(403003, "f403003", 10, 100000, 1, " city"),
//    F403004(403004, "f403004", 10, 100000, 1, "is_old"),
//    F403005(403005, "f403005", 10, 100000, 1, " mobile"),

    F501001(501001, "f501001", 30, 10, 1, "ua"),
    F502001(502001, "f502001", 10, 25, 1, "gmt_create_hour"),
    F502002(502002, "f502002", 10, 10, 1, "gmt_create_weekday"),
    F502003(502003, "f502003", 10, 10, 1, "is_holiday"),
    F503001(503001, "f503001", 10, 100000, 1, "city_id"),
//    F503002(503002, "f503002", 10, 100000, 1, "city_code"),
//    F503003(503003, "f503003", 10, 100000, 1, "province_code"),
    F504001(504001, "f504001", 20, 100000, 3, "model"),
    F505001(505001, "f505001", 30, 10, 1, "price_section"),

    F506001(506001, "f506001", 10, 100000, 1, "connection_type"),
    F507001(507001, "f507001", 10, 100000, 1, "operator_type"),
    F508001(508001, "f508001", 20, 100000, 3, "phoneBrand"),
    F508002(508002, "f508002", 20, 100000, 3, "phoneModelNum"),

    F601001(601001, "f601001", 10, 100, 1, "day_order_rank_level"),
    F601002(601002, "f601002", 10, 100, 1, "day_order_rank_level_2"),

    F602001(602001, "f602001", 10, 100, 1, "order_rank_level"),
    F602002(602002, "f602002", 10, 100, 1, "order_rank_level_2"),

    F603001(603001, "f603001", 10, 100, 1, "day_activity_order_rank_level"),
    F603002(603002, "f603002", 10, 100, 1, "day_activity_order_rank_level_2"),

    F604001(604001, "f604001", 10, 100, 1, "activity_order_rank_level"),
    F604002(604002, "f604002", 10, 100, 1, "activity_order_rank_level_2"),

    F605001(605001, "f605001", 10, 100, 1, "order_gmt_intervel_level"),
    F605002(605002, "f605002", 10, 100, 1, "order_gmt_intervel_level_2"),

    F606001(606001, "f606001", 10, 100, 1, "activity_order_gmt_intervel_level"),
    F606002(606002, "f606002", 10, 100, 1, "activity_order_gmt_intervel_level_2"),

    F607001(607001, "f607001", 10, 100, 1, "activity_last_charge_status"),
    F608001(608001, "f608001", 10, 100, 1, "last_charge_status"),
    F609001(609001, "f609001", 10, 100, 1, "last_activity_equal_status"),
    F610001(610001, "f610001", 10, 100, 1, "day_last_match_tag_nums_equal_status"),
    F611001(611001, "f611001", 10, 1000, 1, "put_index"),

//    F801001(801001, "f801001", 10, 100000, 1, "goodsid"),
//    F802001(802001, "f802001", 10, 100000, 1, "catid"),
//    F802002(802002, "f802002", 10, 100000, 1, "brandid"),
//    F803001(803001, "f803001", 10, 100000, 1, "cost"),
//    F803002(803002, "f803002", 10, 100000, 1, "price"),
//    F803003(803003, "f803003", 10, 100000, 1, "viewcnt"),
//    F803004(803004, "f803004", 10, 100000, 1, "buycnt"),

    F804001(804001, "f804001", 10, 100, 1, "ad_sctr_level"),
    F804002(804002, "f804002", 10, 100, 1, "ad_scvr_level"),
    F804003(804003, "f804003", 10, 100, 1, "ad_sctr_level_v2"),
    F804004(804004, "f804004", 10, 100, 1, "ad_scvr_level_v2"),

    F805001(805001, "f805001", 10, 100, 1, "ad_app_sctr_level"),
    F805002(805002, "f805002", 10, 100, 1, "ad_app_scvr_level"),
    F805003(805003, "f805003", 10, 100, 1, "ad_app_sctr_level_v2"),
    F805004(805004, "f805004", 10, 100, 1, "ad_app_scvr_level_v2"),

    F806001(806001, "f806001", 10, 100, 1, "ad_slot_sctr_level"),
    F806002(806002, "f806002", 10, 100, 1, "ad_slot_scvr_level"),
    F806003(806003, "f806003", 10, 100, 1, "ad_slot_sctr_level_v2"),
    F806004(806004, "f806004", 10, 100, 1, "ad_slot_scvr_level_v2"),

    F807001(807001, "f807001", 10, 100, 1, "ad_activity_sctr_level"),
    F807002(807002, "f807002", 10, 100, 1, "ad_activity_scvr_level"),
    F807003(807003, "f807003", 10, 100, 1, "ad_activity_sctr_level_v2"),
    F807004(807004, "f807004", 10, 100, 1, "ad_activity_scvr_level_v2"),



    F808001(808001, "f808001", 21, 1000000, 3, "user_ctr_bp"),
    F808002(808002, "f808002", 21, 1000000, 3, "user_cvr_bp"),

    F809001(809001, "f809001", 20, 1000000, 3, "user_ctr_tbp"),
    F809002(809002, "f809002", 20, 1000000, 3, "user_cvr_tbp"),

    F810001(810001, "f810001", 20, 1000000, 3, "user_ctr_cbp"),
    F810002(810002, "f810002", 20, 1000000, 3, "user_cvr_cbp"),

    F811001(811001, "f811001", 11, 1000000, 3, "user_ctr_sbp"),
    F811002(811002, "f811002", 11, 1000000, 3, "user_cvr_sbp"),


    F9902(9902, "f9902", 21, 100000, 5, "appList2"),// f9902
    F9906(9906, "f9906", 21, 100000, 5, "categoryIdList1"),// f9906  app一级分类ID列表
    F9907(9907, "f9907", 21, 100000, 5, "categoryIdList2"),// f9907  app二级分类ID列表
    F9908(9908, "f9908", 10, 100, 1, "isGame"),// f9908 是否游戏
    F9912(9912, "f9912", 21, 100000, 3, "tradeId"),// f9912 广告行业标签
    F9913(9913, "f9913", 21, 100000, 3, "tradeId2"),//f99 新  行业ID
    F9914(9914, "f9914", 21, 1000000, 3, "impordant_app"),//重要app
    F9915(9915, "f9915", 21, 1000000, 3, "cluster_id"),//app聚类特征

    F9916(9916, "f9916", 21, 1000000, 3, "launch_pv"),//曝光
    F9917(9917, "f9917", 21, 1000000, 3, "click_pv"),//点击
    F9918(9918, "f9918", 21, 1000000, 3, "effect_pv"),//转化
    F9919(9919, "f9919", 21, 1000000, 3, "score"),//得分

    F9921(9921, "f9921", 21, 1000000, 3, "ctr_intersred"),//用户ctr感兴趣的行业id
    F9922(9922, "f9922", 21, 1000000, 3, "cvr_intersred"),//用户cvr感兴趣的行业id
    F9923(9923, "f9923", 21, 1000000, 3, "ctr_unintersred"),//用户ctr不感兴趣的行业id
    F9924(9924, "f9924", 21, 1000000, 3, "cvr_unintersred"),//用户cvr不感兴趣的行业id

    F9925(9925, "f9925", 21, 10000000, 3, "category1_id_cnt_list"),//app安装列表一级类目及对应的类目下安装应用个数
    F9926(9926, "f9926", 21, 10000000, 3, "category2_id_cnt_list"),//app安装列表二级类目及对应的类目下安装应用个数

    ;

    private int index;
    private String name;

    private int codeType;//10: sub,11:subs    20:hash,21:hashs    30:dict,31:dicts
    private int subLen;//
    private int hashNums;//
    private String desc;

    FeatureEnumC1(int index, String name, int codeType, int subLen, int hashNums, String desc) {
        this.index = index;
        this.name = name;
        this.codeType = codeType;
        this.hashNums = hashNums;
        this.subLen = subLen;
        this.desc = desc;
    }

    public int getIndex() {
        return this.index;
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

    public String getDesc() {
        return this.desc;
    }
}