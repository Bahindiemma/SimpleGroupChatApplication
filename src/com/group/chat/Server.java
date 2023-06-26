package com.group.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
	private ArrayList<ConnectionHandler> connections;
	private ServerSocket server;
	private boolean done;
	private ExecutorService pool;
	private Map<String, String> userCredentials;

	public Server() {
		connections = new ArrayList<>();
		done = false;
		userCredentials = new HashMap<>();
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(9999);
			pool = Executors.newCachedThreadPool();
			while (!done) {
				Socket client = server.accept();
				ConnectionHandler handler = new ConnectionHandler(client);
				connections.add(handler);
				pool.execute(handler);
			}
		} catch (Exception e) {
			// TODO: handle
			shutdown();
		}
	}

	public void broadcast(String message) {
		for (ConnectionHandler ch : connections) {
			if (ch != null) {
				ch.sendMessage(message);
			}
		}
	}

	public void removeConnection(ConnectionHandler connection) {
		connections.remove(connection);
	}

	public void shutdown() {
		try {
			done = true;
			if (!server.isClosed()) {
				server.close();
			}
			for (ConnectionHandler ch : connections) {
				ch.shutdown();
			}
		} catch (IOException e) {
			// Ignore
		}
	}

	class ConnectionHandler implements Runnable {
		private Socket client;
		private BufferedReader in;
		private PrintWriter out;
		private String nickname;

		public ConnectionHandler(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				boolean loggedIn = false;
				while (!loggedIn) {
					out.println("Choose an option:\n1. Register\n2. Login");
					String option = in.readLine();
					if (option.equals("1")) {
						if (registerUser()) {
							out.println("Registration successful! Please login.");
						} else {
							out.println("Registration failed! Please try again.");
						}
					} else if (option.equals("2")) {
						if (loginUser()) {
							loggedIn = true;
							out.println("LOGIN_SUCCESS");
							System.out.println(nickname + " connected!");
							broadcast(nickname + " joined the chat!");
							String message;
							while ((message = in.readLine()) != null) {
								if (message.startsWith("/nick ")) {
									// TODO: handle nickname
									String[] messageSplit = message.split(" ", 2);
									if (messageSplit.length == 2) {
										broadcast(nickname + " renamed themselves to " + messageSplit[1]);
										System.out.println(nickname + " renamed themselves to " + messageSplit[1]);
										nickname = messageSplit[1];
										out.println("Successfully changed nickname to " + nickname);
									} else {
										out.println("No nickname was provided!");
									}
								} else if (message.startsWith("quit")) {
									broadcast(nickname + " left the chat!");
									removeConnection(this);
									shutdown();
									break;
								} else {
									broadcast(nickname + ": " + message);
								}
							}
							if (!done) {
								removeConnection(this);
							}
						} else {
							out.println("LOGIN_FAILURE");
							System.out.println("Authentication failed for user: " + nickname);
						}
					}
				}
			} catch (IOException e) {
				if (!done) {
					removeConnection(this);
					shutdown();
				}
			}
		}

		private boolean registerUser() throws IOException {
			out.println("Enter a username: ");
			String username = in.readLine();
			out.println("Enter a password: ");
			String password = in.readLine();

			// Check if the username is already taken
			if (userCredentials.containsKey(username)) {
				out.println("Username already exists. Please choose a different username.");
				return false;
			}

			// Register the user
			userCredentials.put(username, password);
			nickname = username;
			return true;
		}

		private boolean loginUser() throws IOException {
			out.println("Enter your username: ");
			String username = in.readLine();
			out.println("Enter your password: ");
			String password = in.readLine();

			// Check if the username exists and the password matches
			String storedPassword = userCredentials.get(username);
			if (storedPassword != null && storedPassword.equals(password)) {
				nickname = username;
				return true;
			}

			return false;
		}

		public void sendMessage(String message) {
			out.println(message);
		}

		public void shutdown() {
			try {
				in.close();
				out.close();
				if (!client.isClosed()) {
					client.close();
				}
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
}
