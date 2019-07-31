"""
A first simple Cloud Foundry Flask app

Author: Ian Huston
License: See LICENSE.txt

"""
# !/usr/bin/env python
import json
import os

import pika
from flask import Flask

app = Flask(__name__)

# Get port from environment variable or choose 9099 as local default
port = int(os.getenv("PORT", 8081))

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


@app.route('/')
def keys():
    if rabbit:
        message = '{"thrower":{"id":2},"target":{"id":10}}'

        channel.basic_publish(exchange='balls', routing_key='', body=message)

        print(" [x] Sent %r" % message)

        return 'Sent %r' % message
    else:
        return 'No Redis connection available!'


if __name__ == '__main__':
    # Run the app, listening on all IPs with our chosen port number
    app.run(host='0.0.0.0', port=port)
