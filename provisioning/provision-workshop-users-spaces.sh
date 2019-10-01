#!/bin/bash

USERSFILE=users.txt
DEFAULT_PASSWORD=`cat password.txt`

WORKSHOP_ORG=dnp

cf target -o ${WORKSHOP_ORG}

for user in `cat ${USERSFILE}`
do
    SPACENAME=`echo ${user} | awk -F"@" '{print $1}'`
    echo "Creating user: ${user}"
    cf create-user ${user} ${DEFAULT_PASSWORD}
    echo "Creating space: ${SPACENAME}"
    cf create-space ${SPACENAME}
    cf set-space-role ${user} ${WORKSHOP_ORG} ${SPACENAME} SpaceDeveloper
    cf set-space-role ${user} ${WORKSHOP_ORG} ${SPACENAME} SpaceManager
    cf set-space-role ${user} ${WORKSHOP_ORG} workshop SpaceDeveloper
done
