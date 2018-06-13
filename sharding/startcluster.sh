pkill -9 mongod
pkill -9 mongos

/bin/rm -rf /data/configdb
mkdir /data/configdb
mongod --replSet configrepl --configsvr --port 27019 --logpath "/data/configdb/configsvr.log" --fork  
mongo --port 27019 --eval "rs.initiate()"
mongo --port 27019 --eval "rs.status()"

for s in 27021 27022 27023
do
/bin/rm -rf /data/shard-$s
mkdir /data/shard-$s
mongod --wiredTigerCacheSizeGB 2 --shardsvr --port $s --dbpath "/data/shard-$s" --logpath "/data/shard-$s/mongod.log" --fork  
mongo --port $s --eval "db.version()"
done



mongos --port 27017 --logpath /data/mongos.log --configdb configrepl/localhost:27019 --fork

for s in 27021 27022 27023
do
	mongo --eval "sh.addShard(\"localhost:$s\")"
done

mongo --eval "sh.status()"

