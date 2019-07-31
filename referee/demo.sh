#!/usr/bin/env bash

. demo-magic.sh

clear

pe "cf login -a api.run.pcfone.io --sso"

p "cf create-space red-vs-blue"
pe "cf target -o pivot-dhubau -s red-vs-blue"

p "cf create-service p.mysql db-small player-db"
p "cf create-service p-rabbitmq standard rabbit"
pe "cf services"

pe "mvn clean package"
pe "cf push"
pe "cf app red-vs-blue"
pe "cf logs red-vs-blue --recent"

pe "cf scale red-vs-blue -i 2"
pe "cf app red-vs-blue"
pe "cf logs red-vs-blue --recent"
