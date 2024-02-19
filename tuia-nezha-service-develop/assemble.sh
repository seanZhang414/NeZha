if [ $# == 0 ]
then
  echo "usage:  develop | aliyun_test | aliyun_pre | aliyun | baiduyun | 115_test"
  exit 1;
fi

if [ $1 != "develop" ] && [ $1 != "aliyun_test" ] && [ $1 != "aliyun_pre" ] && [ $1 != "aliyun" ] && [ $1 != "baiduyun" ] && [ $1 != "115_test" ]
then
  echo "env not found"
  exit 1;
fi

echo "package env:$1 ..."
git pull

gradle clean

sed -i "s/disconf.env=.*/disconf.env=$1/g" stock-service-deploy/src/main/resources/disconf.properties
if [ $? != 0 ] ;then
  echo "env: set error"
  exit 1
fi

gradle build -x test

git clean -df
git reset --hard