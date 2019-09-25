"""
A simple Python app for the Pivotal Platform workshop.

Author: Dieter Hubau

"""
# !/usr/bin/env python
import json
import requests
import os
import random
from datetime import timedelta

import pika
from timeloop import Timeloop

tl = Timeloop()

try:
    rabbit = dict(hostname='localhost', port=5672, username='guest', password='guest')
    if 'VCAP_SERVICES' in os.environ:
        services = json.loads(os.getenv('VCAP_SERVICES'))
        rabbit = services['rabbit'][0]['credentials']

    connection = pika.BlockingConnection(pika.ConnectionParameters(
        host=rabbit['hostname'],
        port=rabbit['port'],
        credentials=pika.credentials.PlainCredentials(username=rabbit['username'], password=rabbit['password'])))

    channel = connection.channel()
except RuntimeError:
    print("Could not connect to RabbitMQ")
    rabbit = None


@tl.job(interval=timedelta(milliseconds=1000))
def send_message():
    if rabbit:
        refereeBaseUrl = os.getenv('BLUE_REFEREE_URL', 'http://localhost:8080')

        players = requests.get(url = '%s/score/ranking/' %refereeBaseUrl).json() 
        teamRed = filter(lambda player: player['team'] == 'RED', players)
        teamBlue = filter(lambda player: player['team'] == 'BLUE', players)

        if len(teamRed) == 0 or len(teamBlue) == 0:
            return 'Can\'t throw a ball when there are no players...'
        else:
            for player in teamBlue:

                message = '{"thrower":{"id":%d},"target":{"id":%d}}' %(player["id"], random.choice(teamRed)["id"])

                channel.basic_publish(exchange='balls', routing_key='', body=message)

                print(" [x] Sent %r" % message)

            return 'Sent %d messages' % len(teamBlue)
    else:
        return 'No Redis connection available!'


if __name__ == '__main__':
    tl.start(block=True)