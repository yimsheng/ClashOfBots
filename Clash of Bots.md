# Clash of Bots

## Project Overview

This website is an interactive online game battle platform. Users can be matched in pair and assign one of their game programs to play the snake game(or play the game yourself).



## Frontend Overview

Frontend is implemented with Vue.js and used AJAX for communication between frontend and backend.

### Login and Register

![image-20220826115752163](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115752163.png)

![image-20220826115802533](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115802533.png)

### User information page

![image-20220826115330948](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115330948.png)

### Full CRUD of Bots

![image-20220826115229073](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115229073.png)

![image-20220826115316938](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115316938.png)

### Matching page

![image-20220826115029563](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115029563.png)

![image-20220826115535707](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115535707.png)

### Game page


![image-20220826115416829](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115416829.png)

![image-20220826115431297](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115431297.png)

### Game recording

![image-20220826115116392](/Users/yimsheng/Library/Application Support/typora-user-images/image-20220826115116392.png)



### Backend Overview

Backend is implemented with SpringBoot. The whole structure is consist of one main backend service and two microservices. Backend servers use restTemplate to inter-communicate.

### Backend Application

This server is responsible for most of the critical services.

-   Game battle
    -   Receive matching result from MatchingSystem
    -   Start the game thread for these two matched player
    -   Receive move instruction from BotRunningSystem
    -   End the game if one of the player lose
-   User
    -   Log in, register and get info services
    -   CRUD service of user's bots
-   Ranklist
    -   Get ranklist of all players
-   Recording
    -   Get previous game recordings

### MatchingSystem

This microservice is responsible for:

-   Add the player into the matchpool
    -   Match all the players in the matchpool according to customized rules
    -   Send the matching result back to the backend server
-   Remove the player from the matchpool

### BotRunningSystem

This microservice is responsible for:

-   Add the bot into the botpool
    -   Consume the bot by dynamically compile the code it provides
    -   Get the direction and send it back to the backend server

### Database

This project utlized MySQL as the database. It stores:

-   User information
-   Bots information
-   Game Records

