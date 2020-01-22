# script to start all brevity processes

KAFKA_DIR=$((grep -w "KAFKA_DIR" | cut -d= -f2) < properties)

cd $KAFKA_DIR

echo "starting zookeeper..."
./bin/zookeeper-server-start.sh config/zookeeper.properties > ../brevity/log/zk.log &
sleep 2

echo "starting kafka..."
./bin/kafka-server-start.sh config/server.properties > ../brevity/log/kafka.log &

cd ../brevity

#start backend
cd backend/
echo "building backend..."
./mvnw package > ../log/backend.log
sleep 2

echo "starting backend..."
./mvnw spring-boot:run > ../log/backend.log &

# start nlp workers
cd ..
echo "starting nlp..."
pip3 install -r nlp/requirements.txt -U
python3 nlp/src/extraction.py > log/nlp.log &

# sleep for 10s while backend starts .... 
# volatile - if stuff is failing on your machine maybe increase sleep time
sleep 10

cd frontend/
echo "installing npm dependencies"
npm install > ../log/npm.log
echo "updating npm dependencies"
npm update >> ../log/npm.log
echo "starting frontend..."
npm start >> ../log/npm.log

