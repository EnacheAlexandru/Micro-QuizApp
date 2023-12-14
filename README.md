# Micro-QuizApp
### *Application using microservices and module federation*

## Overview

Micro-QuizApp is an application using microservices and module federation where users can compete with other users by answering correctly as many questions as possible with the goal of climbing the leaderboard.

Using an authentication system, users can register and log in to immediately begin answering questions created by other users. They can also view a list of previously answered questions and even add their own questions for others to answer. 

Each correct answer brings users closer to the top of the leaderboard. If a new high score is achieved, every logged user will receive a notification revealing the new leader and the number of points acquired.

The users can interact with the application through their web browser.

## Architecture

The application consists of four main components, as shown in Figure 1:

- **Single-Page Application (Web Application)** - also known as the front-end, is the component with which the user interacts. He can view and navigate through the pages offered by the web application and perform various operations that will be forwarded to the next main component for processing
- **API Service** - also known as the back-end, is the component where all the requests sent by the web application is processed and sent back as responses.
- **Database** - used by the API Service to store all the necessary business objects.
- **Pub/Sub System** - used by the API Service to allow asynchronous communication internally

Figure 1

![containers](https://github.com/EnacheAlexandru/quiz-microservices/assets/63500798/1b909732-281a-4c72-83ee-f124156cb90e)

In the next section, we will describe each component in more details

## Front-end

The web application is built using **React** framework and **Tailwind CSS** for styling the components within the pages. The main highlight of the web application is that there are two applications (modules) running that share code and resources. This is made possible by the **webpack** framework, which allows for the use of module federation architecture.

The user will interact with only the main module, that will also contain all the pages. 

The **login page** is the first page that greets the user when enters the domain, where he can register or login. After the successful authentication, he will be redirected to one of the pages and allowed to interact with the rest of the functionalities. Furthermore, the module will also establish a WebSocket connection to the API Service in order to inform the user of various events.

The user cannot interact with the second module, only the main module can. This module functions as a global store. When a user authenticates, they receive a JSON Web Token from the API Service that they can use in all subsequent requests. This token is stored in the second module and accessed by the main module as needed. Another entry stored is the current selected page in the main module.

In Figure 2, we can see all the pages available and how they interact with each other, either by automatically redirecting or voluntarily navigating to a certain page.

Figure 2

![pages](https://github.com/EnacheAlexandru/quiz-microservices/assets/63500798/1787c6ea-42ae-4c3d-af19-71e08bee42ec)

## Back-end

The API Service is developed in **Java** utilizing the **Spring** framework ecosystem, incorporating some of the vital Spring components:

- **Spring Boot** includes an embedded web server (Tomcat) that simplifies dependency management and building production-ready applications
- **Spring Security** is used for authentication using JSON Web Tokens
- **Spring Cloud** is used for deploying the microservice-based architecture. It addresses common challenges in distributed systems, such as service discovery (using Eureka) or client-side load balancing (using Netflix Ribbon) 

As Figure 3 illustrates, the API Service is a microservice-based architecture composed of five distinct microservices, each serving a unique purpose.

- **Gateway** - all the requests sent by the front-end will be received here and redirected forward to other microservices to gather the necessary resource. This microservice is responsible for authentication, either by validating the JSON Web Token found in the header of a request sent by a logged user or by generating a new JSON Web Token if the previous token expired
- **Question** - handles most of the business logic related to questions
- **Leaderboard** - Manages the leaderboard
- **Notification** - Responsible of sending various notifications to the clients - the notifications are sent using WebSocket
- **Discovery** - using Eureka framework, this microservice keeps track of all the instances that are running - every time an instance of a microservice is booted, it will attempt to register to the discovery microservice. The discovery microservice will help the other microservices (especially the gateway) balance the traffic across the instances of a microservice using (by default) Robin-Hood algorithm

Figure 3

![components](https://github.com/EnacheAlexandru/quiz-microservices/assets/63500798/b8e24c6f-fac3-4c06-8d30-4c27fb92a6e3)

Almost each microservice is connected to its own **MySQL** database. For example, the gateway microservice has a dedicated database for storing user credentials, while the question microservice houses all information regarding user questions and corresponding answers.

Along with synchronous communication within microservices, there is also asynchronous communication that is used in the cases when the microservice don't really need to wait for a response. The API Service uses two pub/sub queue systems:

#### RabbitMQ

**RabbitMQ** is a message broker that is used for fire-and-forget messaging when it is not crucial to persist the data sent, in case one of the microservices is unavailable. 

In our example, RabbitMQ is used between the leaderboard and notification microservices - when the leaderboard is updated and it has noticed that a new high score is set, then the leaderboard microservice will send a message to the notification microservice. This notification is not that important and will not compromise the consistency of the system even if it fails to persist or go unnoticed by the user.

#### Apache Kafka

**Apache Kafka** is an event streaming platform that is used for messaging where, this time, it is crucial to persist the data sent. Kafka persists messages to disk, ensuring that messages are not lost even if a microservice goes down or it is temporarily unavailable; when it comes back online, the microservice can catch up on the missed messages and resume processing them. 

In our case, Kafka is used between the question and leaderboard microservices - when a user answers a question, a message will be sent to the leaderboard microservice in order to update the leaderboard. The question microservice is not interested in a response, but it is crucial for the leaderboard microservice to ensure that the received message is processed and stored to maintain the consistency of the system.

### Inside a microservice

Each microservice is following the Controller-Service-Repository pattern which is a common

- The **Controller** contains the endpoints and it is responsible for receiving user requests and delegates tasks to the appropriate Service for processing
- The **Service** contains the business logic and orchestrates the execution of tasks - it uses the **Repository** to interact with the data store for persistence and retrieval of data.
- The **Repository** abstracts the data access logic, in our case an external database and provides a clean and standardized interface for the **Service** to interact with the underlying data store.

Each microservice includes a **Security Component** that intercepts requests before they reach the controller. If the request passes through the security chain filters successfully, it will be redirected back to the controller. The primary objective is to validate the JSON Web Token located in the request header of a logged user. The validation involves verifying the signature and checking the expiration date by using a specialized **JWT Service**.

The only endpoints that are not secured are login and register. When a user wishes to login, a new JSON Web Token is generated. In Figure 4, we can see the main Spring Beans components inside the API Gateway Microservice - similar architecture is found on the other microservices as well.

Figure 4

![service](https://github.com/EnacheAlexandru/quiz-microservices/assets/63500798/eda5783e-e6df-405b-a336-5fb7062c0248)

## Functionalities and endpoints

### Endpoints that do not require authentication

- `POST /login` - logs in the user. After logging in, the user will receive a JSON Web Token, which will be included in the header of their subsequent requests until its expiration. They will need to log in again to obtain a new JSON Web Token
- `POST /register` - registers a user

![login](https://github.com/EnacheAlexandru/quiz-microservices/assets/63500798/356f3693-96a0-4d29-a2f7-40ee0312fe5f)

### Endpoints that require authentication

- `GET /question/other/answer` - returns all the questions added by other users that has been answered by the logged user
- `GET /question/other` - returns all the questions added by other users that the logged user still has to answer
- `GET /question/user` - returns all the questions added by the logged user
- `POST /question/add` - adds a question
- `POST /question/delete` - deletes a question added by the logged user with the specified `id` in the request body. The question is not actually deleted, but its status changes to `REMOVED`. This is to prevent inconsistencies if a question was answered by some user and then is deleted in the future. 
- `POST /question/answer` - answers a question added by other user with the specified `id` in the request body. The logged user can only answer a question once
- `GET /leaderboard/list?page=:number` - returns the specified page from the leaderboard. If `page` key is missing or `:number` value is invalid, it returns the first page
- `WS /notification/record` subscription on `/topic/records` topic - sends a notification when a new high score is set by a user. Since a WebSocket is bidirectional and not necessary in this circumstance, the back-end will discard any client-sent messages

![leaderboard_page](https://github.com/EnacheAlexandru/quiz-microservices/assets/63500798/e37697df-4e53-4719-a4ef-8ec1aceab321)

![notification](https://github.com/EnacheAlexandru/quiz-microservices/assets/63500798/4ef8da47-3d3a-4fa1-aa90-e28138fa13ff)

![my_questions_page](https://github.com/EnacheAlexandru/quiz-microservices/assets/63500798/703ad828-9de6-4146-951e-9a6ba480f523)

## Docker

The front-end and back-end, as well as databases and pub/sub queues are dockerized inside `docker-compose.yml` file. The whole application can be run inside Docker on the same network using the following command:
```
docker-compose up
```
By default, `docker-compose.yml` opens two instances of the question service in order to test the load balancing mechanism.
