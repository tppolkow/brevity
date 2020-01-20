#!/bin/sh

echo "Retrieving Heroku Config Variables"

exists() (
    command -v "$1" >/dev/null 2>&1
)

if exists heroku; then
    echo "Heroku CLI detected"
else
    echo "Heroku CLI not installed. Please install it first"
    exit 1
fi

PREFIX=$(heroku config:get KAFKA_PREFIX -s)
URL=$(heroku config:get KAFKA_URL -s)
TRUSTED_CERT=$(heroku config:get KAFKA_TRUSTED_CERT -s)
CLIENT_KEY=$(heroku config:get KAFKA_CLIENT_CERT_KEY -s)
CLIENT_CERT=$(heroku config:get KAFKA_CLIENT_CERT -s)

echo "Writing to .env file"

if test -a ".env"; then 
    echo "File already exists"
else
    echo $PREFIX >> .env
    echo $URL >> .env
    echo $TRUSTED_CERT >> .env
    echo $CLIENT_KEY >> .env
    echo $CLIENT_CERT >> .env
fi
