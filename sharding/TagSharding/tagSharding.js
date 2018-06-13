sh.enableSharding("houses");
sh.shardCollection("houses.stark", {dire_wolves_owned:1});
sh.shardCollection("houses.lannister", {debt_owed:1});
sh.shardCollection("houses.targaryen", {followers:1});
show dbs;
//use houses;
//use houses;
var bulk = db.stark.initializeUnorderedBulkOp();
for (var i=0; i < 10000; i++) { bulk.insert({dire_wolves_owned: Math.random()}); }
bulk.execute();
bulk = db.lannister.initializeUnorderedBulkOp();
for (var i=0; i < 10000; i++) { bulk.insert({debt_owed: Math.random()}); }
bulk.execute();
bulk = db.targaryen.initializeUnorderedBulkOp();
for (var i=0; i < 10000; i++) { bulk.insert({followers: Math.random()}); }
bulk.execute();
sh.addShardTag("shard0000", "sta");
sh.addShardTag("shard0001", "lan");
sh.addShardTag("shard0002", "tar");
sh.addTagRange("houses.stark", {dire_wolves_owned:MinKey}, {dire_wolves_owned:MaxKey}, "sta");
sh.addTagRange("houses.lannister", {debt_owed:MinKey}, {debt_owed:MaxKey}, "lan");
sh.addTagRange("houses.targaryen", {followers:MinKey}, {followers:MaxKey}, "tar");
