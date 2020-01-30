KAFKA_DIR=$((grep -w "KAFKA_DIR" | cut -d= -f2) < properties)

cd $KAFKA_DIR

./bin/kafka-server-stop.sh
./bin/zookeeper-server-stop.sh

for pid in $(ps aux | grep '[b]revity/backend' | awk {'print $2'}); do kill -9 $pid; done

for pid in $(ps aux | grep '[k]afka' | grep brevity | awk '{print $2}'); do kill -9 $pid; done

for pid in $(ps aux | grep '[p]ython' | grep nlp | awk '{print $2}'); do kill -9 $pid; done
