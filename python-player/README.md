# Red vs Blue

This is the tutorial for creating a Python based application for the Red vs Blue workshop.

# Python Player

We will be playing for team BLUE for this tutorial.
The Java player will be playing in team RED.

## Initialize the project



## Setup the project



## Run the project

### As a simple Python executable



### As a Docker container

Make sure Docker is running:

```bash
docker ps -a
```

### Success

RabbitMQ Failure

### Setup RabbitMQ

To have our application connect to Rabbit, we will spin up a RabbitMQ instance through Docker.
Make sure to have Docker installed and running on your machine.
Then run the following command in a new terminal window:

```bash
docker run -d --name some-rabbit -p 5672:5672 -p 15672:15672 -p 25672:25672 rabbitmq:3-management
```

## Implement the Business Logic

We would like to throw some balls at players on the red team.
First we need to create a player on team BLUE.

### Create a player

#### Manually

In the `referee` folder, there's an example of a blue player in JSON format:

```json
{
  "name": "Andreas Evers",
  "team": "BLUE",
  "score": 4
}
```

Edit the name and save the file.
Then use Postman or HTTPIE or Curl to create a red player in the referee application:

```bash
http POST :8080/player < blue-player.json
```

#### Programmatically

Don't.

### Messaging

Messaging setup.

#### Send a message

Now it's time to send a message to the message broker, in our case RabbitMQ.
Let's send a message every 5 seconds by scheduling a job.

> Be sure to set the `shooter`s ID to the player you just created if you want to score points!

When you run this code, every 5 seconds a new message should be sent to the message broker.
The referee application should pick these up and process them.
Check the referee logs, they should look like this eventually:

```bash
2019-08-14 18:10:27.780  INFO 32577 --- [s6GykvMe7OMkw-1] i.p.w.r.game.scoring.ScoringService      : Checking Ball thrown from [Andreas Evers] to [Dieter Hubau]...
2019-08-14 18:10:27.780  INFO 32577 --- [s6GykvMe7OMkw-1] i.p.w.r.game.scoring.ScoringService      : HIT!
```

## Deploy to Pivotal Platform

### Create a manifest

Once you are comfortable with your application, you should deploy this application to the Pivotal Platform.
It's best to have a manifest file where you can define some characteristics of your application.
Create a `manifest.yml` file in the root of the python-player folder:

[manifest.yml]
```yaml

---
applications:
  - name: blue-team-<YOUR_NAME>
    instances: 1
    memory: 128MB
    buildpacks:
      - python_buildpack
    command: python blue-team.py
    services:
      - rabbit
```

Be sure to fill in <YOUR_NAME> so the different applications won't clash with each other.
Also, automatically there is a route being created for you in the form of `blue-team-<YOUR_NAME>.apps.pcfone.io` so if that route has been taken already, the deployment will fail.
Every player will be deploying its own application.

### CF PUSH

This is the easy part:

```bash
cf push
```