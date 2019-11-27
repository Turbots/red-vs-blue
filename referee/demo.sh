#!/usr/bin/env bash

. demo-magic.sh

clear

API_URL=$1
ORG=$2
SPACE=$3

pe "cf login -a $API_URL --skip-ssl-validation"

pe "cf target -o $ORG"
p "cf create-space $SPACE"
pe "cf target -o $ORG -s $SPACE"

pe "cf marketplace"
p "cf create-service p.mysql db-small player-db"
p "cf create-service p-rabbitmq standard rabbit"
pe "cf services"

pe "mvn clean package"
pe "cat manifest.yml"
pe "cf push"
pe "cf app referee"
pe "cf logs referee --recent"

pe "cf scale referee -i 2"
pe "cf app referee"
pe "cf logs referee"
