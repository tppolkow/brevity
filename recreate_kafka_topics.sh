#script to recreate kafka topics if you want to change the settings (like number of partitions)
#or to delete all existing data in the topic


KAFKA_DIR=$((grep -w "KAFKA_DIR" | cut -d= -f2) < properties)

echo "Make sure your $KAFKA_DIR/config/server.properties has delete.topic.enable=true"
echo "starting zookeeper..."
$KAFKA_DIR/bin/zookeeper-server-start.sh $KAFKA_DIR/config/zookeeper.properties > ./log/zk.log &
sleep 2

echo "starting kafka..."
$KAFKA_DIR/bin/kafka-server-start.sh $KAFKA_DIR/config/server.properties > ./log/kafka.log &

echo "deleting kafka topics..."
$KAFKA_DIR/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic brevity_requests

$KAFKA_DIR/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic brevity_responses

# wait for topics to be deleted
sleep 7

echo "creating kafka topics..."
$KAFKA_DIR/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 8 --topic brevity_requests
$KAFKA_DIR/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 8 --topic brevity_responses

echo "done"
echo "if you got InvalidReplicationFactorException try rerunning this script"
