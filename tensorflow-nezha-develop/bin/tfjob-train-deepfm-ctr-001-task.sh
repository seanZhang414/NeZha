MODEL_NAME=deepfm-ctr-001

tfjob_train_file=/home/db_sa/tfjob-train/tfjob-train-${MODEL_NAME}.yaml

kubectl delete tfjob tfjob-train-${MODEL_NAME}

if [ -f ${tfjob_train_file} ];then

kubectl create -f ${tfjob_train_file}

fi

