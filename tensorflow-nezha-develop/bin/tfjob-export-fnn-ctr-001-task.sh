MODEL_NAME=fnn-ctr-001

kubectl delete tfjob tfjob-train-${MODEL_NAME}

kubectl delete tfjob tfjob-export-${MODEL_NAME}

tfjob_export_file=/home/db_sa/tfjob-export/tfjob-export-${MODEL_NAME}.yaml

if [ -f ${tfjob_export_file} ];then
kubectl create -f ${tfjob_export_file}
fi
sleep 3m
kubectl delete tfjob tfjob-export-${MODEL_NAME}



