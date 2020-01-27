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
1. Install Heroku CLI (source: https://devcenter.heroku.com/articles/heroku-cli)
2. Login to Heroku using the credentials
3. Run `heroku config` and export all the config vars as environment variables on local machine
e.g. `export CONFIG_VAR="CONFIG_VAR_VAL"`
4. Build backend with `./mvnw package`
5. Start the local environment using `heroku local -f Procfile.dev` which enables debugging for Spring boot