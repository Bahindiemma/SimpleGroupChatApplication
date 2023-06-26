# Simple Group Chat Application

This is a simple chat application implemented in Java. It consists of a server and a client component that allow multiple clients to connect to the server and communicate with each other in a chat room.

## Server

The server component is responsible for accepting incoming client connections, managing client communication, and broadcasting messages to all connected clients. Here's an overview of the Server class:

### Class: Server

- `connections`: An ArrayList that stores instances of `ConnectionHandler` for each connected client.
- `server`: A ServerSocket object that listens for incoming client connections.
- `done`: A boolean flag indicating whether the server is done or still running.
- `pool`: An ExecutorService that manages the thread pool for handling client connections.

#### Method: Server()

- Initializes the `connections` ArrayList.
- Sets `done` flag to false.

#### Method: run()

- Starts the server by creating a ServerSocket on port 9999.
- Creates a thread pool using `Executors.newCachedThreadPool()`.
- Accepts incoming client connections in a loop and creates a new `ConnectionHandler` for each client.
- Adds the `ConnectionHandler` to the `connections` ArrayList.
- Submits the `ConnectionHandler` to the thread pool for execution.

#### Method: broadcast(message)

- Sends the given `message` to all connected clients by calling the `sendMessage()` method of each `ConnectionHandler` in the `connections` ArrayList.

#### Method: shutdown()

- Sets the `done` flag to true.
- Closes the server socket if it's still open.
- Calls the `shutdown()` method of each `ConnectionHandler` to close client connections.

### Class: ConnectionHandler (inner class)

- This inner class is responsible for handling individual client connections.

#### Method: ConnectionHandler(client)

- Initializes the `client` socket.

#### Method: run()

- Sets up input and output streams for the client.
- Prompts the client to enter a nickname.
- Notifies all clients that a new client has joined.
- Listens for messages from the client and processes them accordingly.
- Handles commands such as changing the nickname and quitting the chat.
- Broadcasts client messages or status updates to all connected clients.
- Closes the connection when the client disconnects.

#### Method: sendMessage(message)

- Sends the given `message` to the client through the output stream.

#### Method: shutdown()

- Closes the input and output streams.
- Closes the client socket if it's still open.

#### Main method

- Creates an instance of the `Server` class.
- Calls the `run()` method to start the server.

## Client

The client component allows a user to connect to the chat server and send and receive messages. Here's an overview of the Client class:

### Class: Client

- `client`: A Socket object representing the client connection.
- `in`: A BufferedReader for reading incoming messages from the server.
- `out`: A PrintWriter for sending messages to the server.
- `done`: A boolean flag indicating whether the client is done or still running.

#### Method: run()

- Connects to the server at IP address "127.0.0.1" and port 9999.
- Sets up input and output streams for communication with the server.
- Creates a separate thread for handling user input.
- Listens for messages from the server and prints them to the console.
- Closes the connection if an IOException occurs.

#### Method: shutdown()

- Sets the `done` flag to true.
- Closes the input and output streams.
- Closes the client socket if it's still open.

### Class: InputHandler (inner class)

- This inner class is responsible for handling user input.

#### Method: run()

- Reads

 user input from the console.
- Sends the input as a message to the server.
- Closes the input stream and calls the `shutdown()` method if the input is "/quit".

#### Main method

- Creates an instance of the `Client` class.
- Calls the `run()` method to start the client.

## Usage

1. Start the server by running the `Server` class.
2. Start the client(s) by running the `Client` class.
3. Enter a nickname when prompted by the client.
4. Start chatting by typing messages in the client's console.
5. To quit, type "/quit" in the client's console.

Note: The server and client components should be run on separate machines or on the same machine with different terminals to simulate multiple clients. Make sure the server is running before starting the clients.

Enjoy chatting!
