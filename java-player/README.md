# Red vs Blue

This is the tutorial for creating a Java based Spring Boot application for the Red vs Blue workshop.

# Java Player

We will be playing for team RED for this tutorial.
The Python player will be playing in team Blue.

## Initialize the project

Go to start.spring.io and generate a new Spring Boot Project.
You can leave the defaults filled in, or provide your own custom information.

You can chose `Maven` or `Gradle` as your dependency management tool.
In this tutorial, we will be using `Java` as our programming language.
As you can see, `Kotlin` and `Groovy` are also supported out-of-the-box.

Be sure to select the following dependencies:

- **Spring Boot DevTools**: enables all kinds of functionality for development
- **Cloud Stream**: Enables the Spring Cloud Stream dependency
- **Spring for RabbitMQ**: Enables the Rabbit binder for Spring Cloud Stream

Click `Explore the project` to see how your zip will look like.
When you're happy with the result, click `Generate the project`.
It will start the download of a zip file.

## Setup the project

Unzip the contents of the zip file in the current `java-player` folder and open the folder in your favourite IDE.
Eclipse, Spring Tool Suite, Netbeans and IntelliJ are fully supported.

You can even use [Visual Studio Code](https://code.visualstudio.com/docs/java/java-spring-boot) if you want.

> For IntelliJ, don't use the `New Project from existing sources...` option, just use the `Open...` option and select the java-player folder

When the project is setup correctly, go to the `pom.xml` file and add a line to the `<build>` section:

```xml
<build>
    <finalName>red-player</finalName><!-- will generate an artifact red-player.jar without version -->
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

This will make sure everyone in the red team has a jar called `red-player.jar` without any versions.

Also, the referee application should be running locally on port 8080, so we want to run our player application on another one.
Add the following line to the `application.properties` file:

```bash
server.port=8081
```

Or in `application.yml` in YAML format:

```yaml
server:
  port: 8081
```

## Run the project

### Using Maven

Open a new terminal window and go to the java-player folder.
You should be able to run the application using:

```bash
./mvnw spring-boot:run
```

### As a JAR

Open a new terminal window and go to the java-player folder.
If you rather package and run the application as a jar:

```bash
./mvnw clean
./mvnw package
java -jar target/red-player.jar
```

### As a Docker container

Make sure Docker is running:

```bash
docker ps -a
```

Then add the following to your pom.xml in the `build/plugins` section:

```xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>1.4.0</version>
    <configuration>
        <to>
            <image>${project.build.finalName}</image>
        </to>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>dockerBuild</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

This plugin will include the [Jib Maven Plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin) to your project and do a Docker build whenever you package your application.
Now you can package your application as a Docker image by executing:

```bash
./mvnw clean package
``` 

You can then run the application as a Docker container:

```bash
docker run red-player -P8081:8081
```

This will run the Docker container on your local Docker installation and expose container port 8081 on your machine's local port 8081.
Use the `-d` option if you want to run this command in "detached mode" (container runs in the background).
Check the logs by running `docker logs <CONTAINER_ID>`.

### Success

In all cases, you should see the application starting up.
It should start up normally, but it won't find any connection to a RabbitMQ instance.
You can find a log line about that like this:

```bash
2019-08-14 14:26:28.634  INFO 1 --- [  restartedMain] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:5672]
```

### Setup RabbitMQ

To have our application connect to Rabbit, we will spin up a RabbitMQ instance through Docker.
Make sure to have Docker installed and running on your machine.
Then run the following command in a new terminal window:

```bash
docker run -d --name some-rabbit -p 5672:5672 -p 15672:15672 -p 25672:25672 rabbitmq:3-management
```

Go back to your application logs.
You should see the application connecting to the RabbitMQ broker now.
Your Spring Boot application seamlessly recovered after a failed connection and is ready to rock.
That's the power of a resilient cloud-native application!

## Implement the Business Logic

We would like to throw some balls at players on the blue team.
First we need to create a player on the team RED.

### Create a player

#### Manually

In the `referee` folder, there's an example of a red player in JSON format:

```json
{
  "name": "Dieter Hubau",
  "team": "RED",
  "score": 5
}
```

Edit the name and save the file.
Then use Postman or HTTPIE or Curl to create a red player in the referee application:

```bash
http POST :8080/player < red-player.json
```

#### Programatically

We could send an HTTP POST request to the Referee API in a programmatic way, when the application starts.
In Spring Boot, there's a utility class for that, called a `CommandLineRunner`.

Create a new class that implements a `CommandLineRunner` like this:

```java
@Component
public class PlayerCreator implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        
    }
}
```

We want need to create a REST call to the Referee REST API, which means we need a `RestTemplate` Bean instance in our class.
Add a field of type `RestTemplate` to the class and inject it in the constructor:

```java
@Component
public class PlayerCreator implements CommandLineRunner {

    private final RestTemplate restTemplate;

    public PlayerCreator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
```

When the Spring application starts up, it will try to look for a Bean of this type and inject it into your class.
But right now, we haven't defined this `RestTemplate` Bean yet.
We need to define it in our main application class:

```java
@SpringBootApplication
public class RedPlayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedPlayerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

This way, Spring will create this singleton Bean at startup to be used everywhere in our application.

Now let's make that call to the Referee REST API:

```java
@Override
public void run(String... args) throws Exception {
    Player red = new Player(null, "MY PLAYERNAME", 0L, Team.RED);

    try {
        ResponseEntity<Player> response = this.restTemplate.postForEntity("http://localhost:8080/player", red, Player.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            LOGGER.info("Player [{}] with ID [{}] created successfully", response.getBody().getName(), response.getBody().getId());
        }
    } catch (RestClientException ex) {
        LOGGER.warn("Could not create player [" + red.getName() + "]");
    }
}
```

### Messaging

#### Define Channels

If you look back at the architecture diagram, you'll see that we need to send messages to RabbitMQ in order to throw balls.
The referee application will verify the `thrower` and the `target` and verify whether it was a hit!

Using Spring Cloud Stream, it's quite easy to send a message to any message broker.
We only need to declare one or more output channels:

```java
public interface OutputChannels {

    @Output("balls")
    MessageChannel output();
}
```

The exchange where the referee application is subscribed to is the `balls` exchange.
We need to enable the binding of this channel in our main application class:

```java
@EnableBinding(OutputChannels.class)
public class RedPlayerApplication {
   // ...
}
```

From now on, the Spring application will create a proxy of this interface and make it available as a Bean.
So we can autowire it in one of our new classes:

```java
@Component
public class BallShooter {

    private final OutputChannels outputChannels;

    public BallShooter(OutputChannels outputChannels) {
        this.outputChannels = outputChannels;
    }
}
```

The `OutputChannels` Bean will be injected into the `BallShooter` class at startup.
This is the core of dependency injection (DI) and is one of the core features of the Spring framework.
It makes your code more clean, modular, cheaper to maintain and easier to test.

#### Send a message

Now it's time to send a message to the message broker, in our case RabbitMQ.
Let's send a message every 5 seconds by scheduling a job.
Using some built-in utilities, this is extremely straight forward in Spring Boot.

Enable scheduling in the main application class:

```java
@EnableScheduling
@SpringBootApplication
@EnableBinding(OutputChannels.class)
public class RedPlayerApplication {

    // ...
}

```

Then use the `@Scheduled` annotation to schedule a simple job.
Add this method in the `BallShooter` class:

```java
@Scheduled(fixedRate = 5000)
public void shoot() {
    Player shooter = new Player();
    shooter.setId(1);
    Player target = new Player();
    target.setId(6);
    
    this.outputChannels.output().send(new GenericMessage<>(new Ball(shooter, target)));
}
```

where the `Ball` is just an aggregate class of two players: a shooter and a target:

```java
public class Ball {

    private Player thrower;
    private Player target;

    public Ball(Player thrower, Player target) {
        this.thrower = thrower;
        this.target = target;
    }
}
```

> Be sure to set the `shooter`s ID to the player you just created if you want to score points!

When you run this code, every 5 seconds a new message should be sent to the message broker.
The referee application should pick these up and process them.
Check the referee logs, they should look like this eventually:

```bash
2019-08-14 18:10:27.780  INFO 32577 --- [s6GykvMe7OMkw-1] i.p.w.r.game.scoring.ScoringService      : Checking Ball thrown from [Dieter Hubau] to [Lars Rosenquist]...
2019-08-14 18:10:27.780  INFO 32577 --- [s6GykvMe7OMkw-1] i.p.w.r.game.scoring.ScoringService      : HIT!
```

## Deploy to Pivotal Platform

### Create a manifest

Once you are comfortable with your application, you should deploy this application to the Pivotal Platform.
It's best to have a manifest file where you can define some characteristics of your application.
Create a `manifest.yml` file in the root of the java-player folder:

[manifest.yml]
```yaml

---
applications:
  - name: red-team-<YOUR_NAME>
    path: target/red-player.jar
    buildpacks:
      - java_buildpack_offline
    instances: 1
    memory: 1G
    services:
      - rabbit
```

Be sure to fill in <YOUR_NAME> so the different applications won't clash with each other.
Also, automatically there is a route being created for you in the form of `red-team-<YOUR_NAME>.apps.pcfone.io` so if that route has been taken already, the deployment will fail.
Every player will be deploying its own application.

### Package your application

Make sure to package your application in a Jar.

> make jar, not war!

```bash
./mvnw clean package
```

### CF PUSH

This is the easy part:

```bash
cf push
```