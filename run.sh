# script to start all brevity processes

KAFKA_DIR=$((grep -w "KAFKA_DIR" | cut -d= -f2) < properties)

# Make log directory if not exists
mkdir -p log

echo "starting zookeeper..."
$KAFKA_DIR/bin/zookeeper-server-start.sh $KAFKA_DIR/config/zookeeper.properties > ./log/zk.log &
sleep 2

echo "starting kafka..."
$KAFKA_DIR/bin/kafka-server-start.sh $KAFKA_DIR/config/server.properties > ./log/kafka.log &

#start backend
cd backend/
echo "building backend..."
./mvnw package > ../log/backend.log
sleep 2

echo "starting backend..."
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8080" > ../log/backend.log &

# start nlp workers
echo "starting nlp..."
pip3 install -r requirements.txt -U
python3 nlp/src/extraction.py > ../log/nlp.log &

# sleep for 10s while backend starts .... 
# volatile - if stuff is failing on your machine maybe increase sleep time
sleep 10

cd ../frontend/
echo "installing npm dependencies"
npm install > ../log/npm.log
echo "updating npm dependencies"
npm update >> ../log/npm.log
echo "starting frontend..."
npm start >> ../log/npm.log

