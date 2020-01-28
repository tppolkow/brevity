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

## Setup Heroku Local and running
1. Install Heroku CLI (see: https://devcenter.heroku.com/articles/heroku-cli)
2. See https://devcenter.heroku.com/articles/collab for collaboration
3. Run `heroku config` and export all the config vars as environment variables on local machine
e.g. `export CONFIG_VAR="CONFIG_VAR_VAL"`
4. Run `prepareEnv.sh` at root directory e.g. brevity/
4. Build backend with `./mvnw package`
5. Start the local environment using `heroku local -f Procfile.dev` which enables debugging for Spring boot

## Deploy to Heroku (Currently not available yet)
1. There should be two remote, one remote that points to the upstream of the repo, and another for deployed heroku app
2. Deploy backend using `git subtree push --prefix backend/ heroku master`. Please use this command at all times when deploying so the app only contains necessary backend code

## Docker
For anyone looking to run brevity inside the container: 
1. Download docker on your PC
2. Clone our github repo and cd into it
3. Run `docker image build -t brevity .`
4. Run `docker run --rm -d --network host --name brevity brevity`
5. Run `docker exec -it brevity bash`
6. cd into ~/brevity/ and execute ./run.sh

