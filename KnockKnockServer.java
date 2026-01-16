import java.net.*;
import java.io.*;

/**
 * KnockKnockServer - A simple TCP server that plays knock-knock jokes with clients.
 * 
 * This educational example demonstrates:
 * - Server socket creation and listening on a port
 * - Accepting client connections
 * - Reading from and writing to client sockets
 * - Protocol implementation using state machines
 * - Resource management with try-catch-finally patterns
 * - Command-line argument parsing
 * 
 * Usage:
 *   java KnockKnockServer           # Use default port 4444
 *   java KnockKnockServer 5555      # Use custom port 5555
 * 
 * The server listens on the specified port and handles one client at a time.
 */
public class KnockKnockServer {
    private static final int DEFAULT_PORT = 4444;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        // Parse command-line arguments for port
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1 || port > 65535) {
                    System.err.println("Error: Port must be between 1 and 65535. Using default port " + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid port number '" + args[0] + "'. Using default port " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        try {
            // Step 1: Create a ServerSocket to listen for incoming connections
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port + ". Waiting for client connections...");

            // Step 2: Accept a client connection (blocks until a client connects)
            clientSocket = serverSocket.accept();
            System.out.println("Client connected from: " + clientSocket.getInetAddress());

            // Step 3: Set up I/O streams for communication with the client
            // "true" in PrintWriter enables autoFlush for immediate sending
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Step 4: Initialize the protocol handler (manages knock-knock joke state)
            KnockKnockProtocol protocol = new KnockKnockProtocol();

            // Step 5: Send initial greeting and start conversation loop
            String serverResponse = protocol.processInput(null);
            out.println(serverResponse);
            System.out.println("Server: " + serverResponse);

            // Step 6: Read client input and respond until conversation ends
            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                System.out.println("Client: " + clientInput);
                
                serverResponse = protocol.processInput(clientInput);
                out.println(serverResponse);
                System.out.println("Server: " + serverResponse);

                // Exit loop when the conversation is complete
                if (serverResponse.equals("Bye.")) {
                    break;
                }
            }

            System.out.println("Conversation ended. Client disconnected.");

        } catch (IOException e) {
            // Handle I/O exceptions (connection refused, socket creation failed, etc.)
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Step 7: Always close resources to prevent memory leaks
            // Close in the reverse order they were opened
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
                if (serverSocket != null) serverSocket.close();
                System.out.println("All resources closed.");
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
