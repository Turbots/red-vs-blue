"""
A simple Python app for the Pivotal Platform workshop.

Author: Dieter Hubau

"""
# !/usr/bin/env python
import json
import os
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
        message = '{"thrower":{"id":10},"target":{"id":2}}'

        channel.basic_publish(exchange='balls', routing_key='', body=message)

        print(" [x] Sent %r" % message)

        return 'Sent %r' % message
    else:
        return 'No Redis connection available!'


if __name__ == '__main__':
    tl.start(block=True)
