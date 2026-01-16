import java.io.*;
import java.net.*;

/**
 * KnockKnockClient - A simple TCP client that plays knock-knock jokes with a server.
 * 
 * This educational example demonstrates:
 * - Connecting to a remote server using a Socket
 * - Creating input/output streams for network communication
 * - Handling user input from the console
 * - Communicating with a server in a request-response pattern
 * - Proper error handling for network operations
 * - Command-line argument parsing
 * 
 * Usage:
 *   java KnockKnockClient                        # Connect to localhost:4444 (default)
 *   java KnockKnockClient 192.168.1.100          # Connect to specific host (port 4444)
 *   java KnockKnockClient 192.168.1.100 5555     # Connect to specific host and port
 */
public class KnockKnockClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 4444;

    public static void main(String[] args) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        BufferedReader userInput = null;

        // Parse command-line arguments for host and port
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
                if (port < 1 || port > 65535) {
                    System.err.println("Error: Port must be between 1 and 65535. Using default port " + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid port number '" + args[1] + "'. Using default port " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        try {
            // Step 1: Create a socket and connect to the server
            System.out.println("Connecting to server at " + host + ":" + port + "...");
            socket = new Socket(host, port);
            System.out.println("Connected to server!");

            // Step 2: Set up I/O streams for communication
            // PrintWriter: for sending messages to the server
            // BufferedReader: for reading messages from the server
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Step 3: Create a reader for console input from the user
            userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("\n--- Knock Knock Joke Session Started ---\n");

            // Step 4: Main conversation loop
            String messageFromServer;
            String messageFromUser;

            while ((messageFromServer = in.readLine()) != null) {
                // Display the server's message
                System.out.println("Server: " + messageFromServer);

                // Check if the conversation is over
                if (messageFromServer.equals("Bye.")) {
                    System.out.println("\n--- Session Ended ---");
                    break;
                }

                // Prompt the user for input
                System.out.print("You: ");
                messageFromUser = userInput.readLine();

                // Send user's message to the server
                if (messageFromUser != null) {
                    out.println(messageFromUser);
                } else {
                    // Handle EOF from console (Ctrl+D on Unix or Ctrl+Z on Windows)
                    System.out.println("\nEnd of input reached.");
                    break;
                }
            }

        } catch (UnknownHostException e) {
            // Thrown when the host name cannot be resolved
            System.err.println("Connection error: Unknown host '" + host + "'");
            System.err.println("Make sure the server is running and the hostname is correct.");
            e.printStackTrace();
        } catch (IOException e) {
            // Thrown for connection refused, I/O errors, etc.
            System.err.println("Connection error: " + e.getMessage());
            System.err.println("Make sure the server is running on " + host + ":" + port);
            e.printStackTrace();
        } finally {
            // Step 5: Always close resources to prevent memory leaks
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (userInput != null) userInput.close();
                if (socket != null) socket.close();
                System.out.println("\nConnection closed.");
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }    }
}