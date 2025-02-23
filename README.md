# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Server Design Diagram URL
https://sequencediagram.org/index.html#initialData=actor%20Client%0Aparticipant%20Server%0Aparticipant%20Handler%0Aparticipant%20Service%0Aparticipant%20DataAccess%0Adatabase%20db%0A%0Aentryspacing%200.9%0Agroup%20%23navy%20Registration%20%23white%0AClient%20-%3E%20Server%3A%20%5BPOST%5D%20%2Fuser%5Cn%7B%22username%22%3A%22%20%22%2C%20%22password%22%3A%22%20%22%2C%20%22email%22%3A%22%20%22%7D%0AServer%20-%3E%20Handler%3A%20%7B%22username%22%3A%22%20%22%2C%20%22password%22%3A%22%20%22%2C%20%22email%22%3A%22%20%22%7D%0AHandler%20-%3E%20Service%3A%20register(RegisterRequest)%0AService%20-%3E%20DataAccess%3A%20getUser(username)%0ADataAccess%20-%3E%20db%3AFind%20UserData%20by%20username%0ADataAccess%20--%3E%20Service%3A%20null%0AService%20-%3E%20DataAccess%3AcreateUser(userData)%0ADataAccess%20-%3E%20db%3AAdd%20UserData%0AService%20-%3E%20DataAccess%3AcreateAuth(authData)%0ADataAccess%20-%3E%20db%3AAdd%20AuthData%0AService%20--%3E%20Handler%3A%20RegisterResult%0AHandler%20--%3E%20Server%3A%20%7B%22username%22%20%3A%20%22%20%22%2C%20%22authToken%22%20%3A%20%22%20%22%7D%0AServer%20--%3E%20Client%3A%20200%5Cn%7B%22username%22%20%3A%20%22%20%22%2C%20%22authToken%22%20%3A%20%22%20%22%7D%0AServer%20--%3E%20Client%3A%20400%5Cn%7B%22message%22%3A%20%22Error%3A%20bad%20request%22%7D%0AServer%20--%3E%20Client%3A%20403%5Cn%7B%22message%22%3A%20%22Error%3A%20already%20taken%22%7D%0AServer%20--%3E%20Client%3A%20500%5Cn%7B%22message%22%3A%20%22Error%3A%20(description%20of%20error)%22%7D%0Aend%0A%0Agroup%20%23orange%20Login%20%23white%0AClient%20-%3E%20Server%3A%20%5BPOST%5D%20%2Fsession%5Cn%7Busername%2C%20password%7D%0AServer%20-%3E%20Handler%3A%20%7B%22username%22%3A%22%20%22%2C%20%22password%22%3A%22%20%22%7D%0AHandler%20-%3E%20Service%3A%20login(LoginRequest)%0AService%20-%3E%20DataAccess%3A%20getUser(username)%0ADataAccess%20-%3E%20db%3AFind%20UserData%20by%20username%0ADataAccess%20--%3E%20Service%3A%20null%5Cnusername%20not%20found%0ADataAccess%20--%3E%20Service%3A%20UserData%20found%0AService%20--%3E%20Handler%3A%20LoginResult%0AHandler%20--%3E%20Server%3A%20%7B%22username%22%20%3A%20%22%20%22%2C%20%22authToken%22%20%3A%20%22%20%22%7D%0AServer%20--%3E%20Client%3A%20200%5Cn%7B%22username%22%20%3A%20%22%20%22%2C%20%22authToken%22%20%3A%20%22%20%22%7D%0AServer%20--%3E%20Client%3A%20401%5Cn%7B%22message%22%3A%20%22message%22%3A%20%22Error%3A%20unauthorized%22%7D%0AServer%20--%3E%20Client%3A%20500%5Cn%7B%22message%22%3A%20%22Error%3A%20(description%20of%20error)%22%7D%0Aend%0A%0Agroup%20%23green%20Logout%20%23white%0AClient%20-%3E%20Server%3A%20%5BDELETE%5D%20%2Fsession%5CnauthToken%0AServer%20-%3E%20Handler%3A%20%7B%7D%0AHandler%20-%3E%20Service%3A%20logout(LogoutRequest)%0AService%20-%3E%20DataAccess%3A%20if%20joined%20a%20game%5CnupdateGame(gameData)%0ADataAccess%20-%3E%20db%3A%20Update%20GameData%20by%20gameID%0AService%20--%3E%20Handler%3A%20LogoutResult%0AHandler%20--%3E%20Server%3A%20%7B%7D%0AServer%20--%3E%20Client%3A%20200%5Cn%7B%7D%0AServer%20--%3E%20Client%3A%20401%5Cn%7B%22message%22%3A%20%22message%22%3A%20%22Error%3A%20unauthorized%22%7D%0AServer%20--%3E%20Client%3A%20500%5Cn%7B%22message%22%3A%20%22Error%3A%20(description%20of%20error)%22%7D%0Aend%0A%0Agroup%20%23red%20List%20Games%20%23white%0AClient%20-%3E%20Server%3A%20%5BGET%5D%20%2Fgame%5CnauthToken%0AServer%20-%3E%20Handler%3A%20%7B%7D%0AHandler%20-%3E%20Service%3A%20list(ListRequest)%0AService%20-%3E%20DataAccess%3A%20listGames()%0ADataAccess%20-%3E%20db%3A%20List%20all%20games%20in%20GameData%0ADataAccess%20--%3E%20Service%3A%20listOfGames%0AService%20--%3E%20Handler%3A%20ListResult%0AHandler%20--%3E%20Server%3A%20%7B%22games%22%3A%20%5B%7B%22gameID%22%3A%201234%2C%20%22whiteUsername%22%3A%22%22%2C%20%22blackUsername%22%3A%22%22%2C%20%22gameName%3A%22%22%7D%20%5D%7D%0AServer%20--%3E%20Client%3A%20200%5Cn%7B%22games%22%3A%20%5B%7B%22gameID%22%3A%201234%2C%20%22whiteUsername%22%3A%22%22%2C%20%22blackUsername%22%3A%22%22%2C%20%22gameName%3A%22%22%7D%20%5D%7D%0AServer%20--%3E%20Client%3A%20401%5Cn%7B%22message%22%3A%20%22message%22%3A%20%22Error%3A%20unauthorized%22%7D%0AServer%20--%3E%20Client%3A%20500%5Cn%7B%22message%22%3A%20%22Error%3A%20(description%20of%20error)%22%7D%0Aend%0A%0Agroup%20%23purple%20Create%20Game%20%23white%0AClient%20-%3E%20Server%3A%20%5BPOST%5D%20%2Fgame%5CnauthToken%5Cn%7BgameName%7D%0AServer%20-%3E%20Handler%3A%20%7B%22gameName%22%3A%22%22%7D%0AHandler%20-%3E%20Service%3A%20create(CreateRequest)%0AService%20-%3E%20DataAccess%3A%20CreateGame(gameData)%0ADataAccess%20-%3E%20db%3A%20Create%20a%20new%20game%20with%20GameData%0ADataAccess%20--%3E%20Service%3A%20gameID%0AService%20--%3E%20Handler%3A%20CreateResult%0AHandler%20--%3E%20Server%3A%20%7B%22gameID%22%3A%201234%7D%0AServer%20--%3E%20Client%3A%20200%5Cn%7B%22gameID%22%3A%201234%7D%0AServer%20--%3E%20Client%3A%20400%5Cn%7B%22message%22%3A%20%22Error%3A%20bad%20request%22%7D%0AServer%20--%3E%20Client%3A%20401%5Cn%7B%22message%22%3A%20%22message%22%3A%20%22Error%3A%20unauthorized%22%7D%0AServer%20--%3E%20Client%3A%20500%5Cn%7B%22message%22%3A%20%22Error%3A%20(description%20of%20error)%22%7D%0Aend%0A%0Agroup%20%23yellow%20Join%20Game%20%23black%0AClient%20-%3E%20Server%3A%20%5BPUT%5D%20%2Fgame%5CnauthToken%5Cn%7BplayerColor%2C%20gameID%7D%0AServer%20-%3E%20Handler%3A%20%7B%22playerColor%22%3A%22WHITE%2FBLACK%22%2C%20%22gameID%22%3A%201234%7D%0AHandler%20-%3E%20Service%3A%20join(JoinRequest)%0AService%20-%3E%20DataAccess%3A%20joinGame(userData%2C%20gameData)%0ADataAccess%20-%3E%20db%3A%20Find%20game%20by%20gameID%0ADataAccess%20--%3E%20Service%3A%20game%0AService%20--%3E%20Handler%3A%20JoinResult%0AHandler%20--%3E%20Server%3A%20%7B%7D%0AServer%20--%3E%20Client%3A%20200%5Cn%7B%7D%0AServer%20--%3E%20Client%3A%20400%5Cn%7B%22message%22%3A%20%22Error%3A%20bad%20request%22%7D%0AServer%20--%3E%20Client%3A%20401%5Cn%7B%22message%22%3A%20%22message%22%3A%20%22Error%3A%20unauthorized%22%7D%0AServer%20--%3E%20Client%3A%20403%5Cn%7B%22message%22%3A%20%22Error%3A%20already%20taken%22%7D%0AServer%20--%3E%20Client%3A%20500%5Cn%7B%22message%22%3A%20%22Error%3A%20(description%20of%20error)%22%7D%0Aend%0A%0Agroup%20%23gray%20Clear%20application%20%23white%0AClient%20-%3E%20Server%3A%20%5BDELETE%5D%20%2Fdb%0AServer%20-%3E%20Handler%3A%20%7B%7D%0AHandler%20-%3E%20Service%3A%20clear(ClearRequest)%0AService%20-%3E%20DataAccess%3A%20clearUsers()%0ADataAccess%20-%3E%20db%3A%20Delete%20all%20UserData%0AService%20-%3E%20DataAccess%3A%20clearAuthTokens()%0ADataAccess%20-%3E%20db%3A%20Delete%20all%20AuthData%0AService%20-%3E%20DataAccess%3A%20clearGames()%0ADataAccess%20-%3E%20db%3A%20Delete%20all%20GameData%0AService%20--%3E%20Handler%3A%20ClearResult%0AHandler%20--%3E%20Server%3A%20%7B%7D%0AServer%20--%3E%20Client%3A%20200%5Cn%7B%7D%0AServer%20--%3E%20Client%3A%20500%5Cn%7B%22message%22%3A%20%22Error%3A%20(description%20of%20error)%22%7D%0Aend%0A

