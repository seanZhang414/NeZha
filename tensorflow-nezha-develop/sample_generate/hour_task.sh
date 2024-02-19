#
source  ~/.bashrc

echo -e "`date +"%Y-%m-%d %H:%M:%S"`  === === task hour,start === ==="

echo -e "`date +"%Y-%m-%d %H:%M:%S"`  === ctr001,start ==="
bash /home/db_sa/bin/ctr001
#spark-submit
echo -e "`date +"%Y-%m-%d %H:%M:%S"`  === ctr001,end ==="

echo -e "`date +"%Y-%m-%d %H:%M:%S"`  === cvr001,start ==="
bash /home/db_sa/bin/cvr001
echo -e "`date +"%Y-%m-%d %H:%M:%S"`  === cvr001,end ==="
echo -e "`date +"%Y-%m-%d %H:%M:%S"`  === === task hour,end === ===\n"