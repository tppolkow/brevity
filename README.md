# Brevity

## Setup
Download kafka/zookeeper https://www.apache.org/dyn/closer.cgi?path=/kafka/2.4.0/kafka_2.12-2.4.0.tgz
To use default properties, extract the package one directory up from this git repo. (i.e brevity repo and your kafka package should be on same level)
If you want to extract it somewhere else, you can point to it in the properties file.

## Running
Run `./run.sh`

## Logs
Logs are output in log directory where you can tail them. To see all logs when starting application simply `tail -f log/*.log`

## Errors
If something goes wrong kafka/zookeeper may have to be manually closed. To do this just run `./close.sh`
If this doesn't work you can try manually killing the process: `for pid in $(ps aux | grep kafka | grep brevity | awk '{print $2}'); do kill -9 $pid; done`

## Docker
For anyone looking to run brevity inside the container: 
1. Download docker on your PC
2. Clone our github repo and cd into it
3. Run `docker image build -t brevity .`
4. Run `docker run --rm -d --network host --name brevity brevity`
5. Run `docker exec -it brevity bash`
6. cd into ~/brevity/ and execute ./run.sh

## Database
Database tables should be automatically set up with migrations. Postgresql database needs to be running on your local on port 5432 (the default postgresql port). User and pw should be postgres.

Sample commands to set up on ubuntu:
`sudo apt-get install postgresql` installs postgresql

`sudo su - postgres` starts up postgres

For the first time you run, you will have to set the password to be postgres. To do this, after running the above command run the following:
1. `psql`
2. `\password postgres`
3. Enter new password ( for now just type `postgres` )

Also, you will need to setup the brevity database for first-time so run the following command in the same `psql` session
```
CREATE DATABASE brevity
```
