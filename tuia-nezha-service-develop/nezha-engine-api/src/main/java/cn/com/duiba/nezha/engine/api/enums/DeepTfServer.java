package cn.com.duiba.nezha.engine.api.enums;

public enum DeepTfServer {

    FNN_CTR_001("fnn-ctr-001", "10.50.201.51", 9000),
    FNN_CVR_001("fnn-cvr-001", "10.50.201.52", 9000),

    FNN_CTR_002("fnn-ctr-002", "10.50.201.70", 9000),
    FNN_CVR_002("fnn-cvr-002", "10.50.201.71", 9000),

    DEEP_FM_CTR_001("deepfm-ctr-001", "10.50.201.49", 9000),
    DEEP_FM_CVR_001("deepfm-cvr-001", "10.50.201.50", 9000),

    DEEP_FM_CTR_002("deepfm-ctr-002", "10.50.201.68", 9000),
    DEEP_FM_CVR_002("deepfm-cvr-002", "10.50.201.69", 9000),

    XDEEP_FM_CTR_002("xdeepfm-ctr-002", "10.50.201.81", 9000),
    XDEEP_FM_CVR_002("xdeepfm-cvr-002", "10.50.201.83", 9000),

    XDEEP_FM_CTR_003("xdeepfm-ctr-003", "10.50.201.82", 9000),
    XDEEP_FM_CVR_003("xdeepfm-cvr-003", "10.50.201.84", 9000),

    OPNN_CTR_001("opnn-ctr-001", "10.50.201.56", 9000),
    OPNN_CVR_001("opnn-cvr-001", "10.50.201.57", 9000),


    DCN_CTR_001("dcn-ctr-001", "10.50.201.66", 9000),
    DCN_CVR_001("dcn-cvr-001", "10.50.201.67", 9000),

    DCN_CTR_002("dcn-ctr-002", "10.50.201.72", 9000),
    DCN_CVR_002("dcn-cvr-002", "10.50.201.73", 9000),

    DCN_CTR_003("dcn-ctr-003", "10.50.201.79", 9000),
    DCN_CVR_003("dcn-cvr-003", "10.50.201.80", 9000),

    ;

    private String host;
    private Integer port;
    private String modelKey;

    DeepTfServer(String modelKey, String host, Integer port) {
        this.host = host;
        this.port = port;
        this.modelKey = modelKey;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getModelKey() {
        return modelKey;
    }
}
