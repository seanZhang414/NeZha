# 创建TensorFlow Serving的namespace
export NAMESPACE=default

# 指定Kubeflow的版本
VERSION=v0.2.0-rc.0
APP_NAME=tf-serving-deepfm-cvr-001

# 初始化Kubeflow应用，并且将其namespace设置为default环境
ks init ${APP_NAME} --api-spec=version:v1.9.3
cd ${APP_NAME}
ks env add ack
ks env set ack --namespace ${NAMESPACE}

# 安装 Kubeflow 模块
ks registry add kubeflow github.com/kubeflow/kubeflow/tree/${VERSION}/kubeflow
ks pkg install kubeflow/tf-serving@${VERSION}

# 指定配置TensorFlow Serving所需环境变量
MODEL_COMPONENT=tf-serving-deepfm-cvr-001
MODEL_NAME=deepfm-cvr-001
MODEL_PATH=/mnt/serving-model/deepfm-cvr-001
MODEL_STORAGE_TYPE=nfs
SERVING_PVC_NAME=pvc-deepfm-cvr-001
MODEL_SERVER_IMAGE=registry.aliyuncs.com/kubeflow-images-public/tensorflow-serving-1.7:v20180604-0da89b8a


# 创建TensorFlow Serving的模板
ks generate tf-serving ${MODEL_COMPONENT} --name=${MODEL_NAME}
ks param set ${MODEL_COMPONENT} modelPath ${MODEL_PATH}
ks param set ${MODEL_COMPONENT} modelStorageType ${MODEL_STORAGE_TYPE}
ks param set ${MODEL_COMPONENT} nfsPVC ${SERVING_PVC_NAME}
ks param set ${MODEL_COMPONENT} modelServerImage $MODEL_SERVER_IMAGE

# 设置tf-serving
ks param set ${MODEL_COMPONENT} cloud ack

# 如果需要暴露对外部系统的服务
ks param set ${MODEL_COMPONENT} serviceType LoadBalancer

# 如果使用GPU, 请使用以下配置
# NUMGPUS=1
# ks param set ${MODEL_COMPONENT} numGpus ${NUMGPUS}
# MODEL_GPU_SERVER_IMAGE=registry.aliyuncs.com/kubeflow-images-public/tensorflow-serving-1.6gpu:v20180604-0da89b8a
# ks param set ${MODEL_COMPONENT} modelServerImage $MODEL_SERVER_IMAGE

ks apply ack -c tf-serving-deepfm-cvr-001