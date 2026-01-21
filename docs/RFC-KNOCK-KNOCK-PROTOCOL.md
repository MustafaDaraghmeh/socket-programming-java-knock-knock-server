# RFC: Knock-Knock Protocol Specification

**Status:** Educational Implementation  
**Date:** January 2026  
**Author:** Knock-Knock Protocol Working Group  
**Category:** Educational Network Protocol

---

## Abstract

This document specifies the Knock-Knock Protocol (KKP), a simple text-based client-server protocol designed for educational purposes to teach fundamental TCP/IP socket programming concepts. The protocol implements an interactive knock-knock joke exchange pattern using a finite state machine approach.

The protocol is intentionally simple to demonstrate:
- TCP socket programming fundamentals
- Stateful protocol design
- Client-server communication patterns
- Input validation and error handling
- State machine implementation

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Architecture Overview](#2-architecture-overview)
3. [Protocol Specification](#3-protocol-specification)
4. [State Machine Definition](#4-state-machine-definition)
5. [Message Format](#5-message-format)
6. [Connection Lifecycle](#6-connection-lifecycle)
7. [Error Handling](#7-error-handling)
8. [Security Considerations](#8-security-considerations)
9. [Implementation Notes](#9-implementation-notes)
10. [Extensibility](#10-extensibility)
11. [References](#11-references)

---

## 1. Introduction

### 1.1 Purpose

The Knock-Knock Protocol (KKP) is an educational protocol designed to teach network programming concepts through a practical, interactive example. By implementing a familiar human interaction pattern (knock-knock jokes), students can focus on learning networking concepts without complex domain logic.

### 1.2 Scope

This specification defines:
- The communication protocol between KKP client and server
- State transitions and valid state machine operations
- Message formats and expected responses
- Connection establishment and termination procedures
- Error handling mechanisms

### 1.3 Design Goals

1. **Educational Clarity**: Protocol must be simple enough for beginners to understand
2. **Stateful Design**: Demonstrate state machine patterns in protocol design
3. **Error Recovery**: Show proper error handling and validation
4. **Extensibility**: Allow for easy modification and enhancement
5. **Standard Compliance**: Use standard TCP/IP and text-based messaging

### 1.4 Terminology

- **KKP**: Knock-Knock Protocol
- **Server**: The entity that initiates knock-knock jokes
- **Client**: The entity that responds to knock-knock jokes
- **State**: A specific point in the protocol conversation
- **Transition**: Movement from one state to another
- **Joke Tuple**: A pair of (clue, punchline) representing one joke

---

## 2. Architecture Overview

### 2.1 System Components

The KKP implementation consists of three primary components:

```
┌─────────────────────┐         ┌─────────────────────┐
│   KnockKnockClient  │         │  KnockKnockServer   │
│                     │         │                     │
│  - Socket I/O       │◄───────►│  - ServerSocket     │
│  - User Interface   │   TCP   │  - Protocol Handler │
│  - Input Reading    │         │  - Joke Database    │
└─────────────────────┘         └──────────┬──────────┘
                                          │
                                          │ uses
                                          ▼
                                ┌──────────────────────┐
                                │ KnockKnockProtocol   │
                                │                      │
                                │ - State Machine      │
                                │ - Joke Selection     │
                                │ - Input Validation   │
                                └──────────────────────┘
```

### 2.2 Component Responsibilities

#### 2.2.1 KnockKnockServer
- **Network Layer**: Creates ServerSocket and accepts incoming connections
- **I/O Management**: Manages input/output streams for client communication
- **Protocol Delegation**: Delegates protocol logic to KnockKnockProtocol
- **Connection Lifecycle**: Handles connection establishment and termination
- **Configuration**: Parses command-line arguments (port configuration)

#### 2.2.2 KnockKnockClient
- **Connection Management**: Establishes TCP connection to server
- **User Interface**: Displays server messages and prompts user for input
- **Input Collection**: Reads user input from console
- **Message Transmission**: Sends user responses to server
- **Session Management**: Maintains connection until protocol completion

#### 2.2.3 KnockKnockProtocol
- **State Machine**: Implements the protocol's finite state machine
- **Data Store**: Maintains joke database (clues and punchlines)
- **Validation**: Validates client input against expected responses
- **Response Generation**: Generates appropriate server responses based on state and input
- **State Transitions**: Manages state changes according to protocol rules

### 2.3 Communication Model

```
Client                          Server
  │                               │
  │   ─────── TCP SYN ──────►     │  (Connection Establishment)
  │   ◄────── TCP SYN-ACK ───     │
  │   ─────── TCP ACK ──────►     │
  │                               │
  │   ◄──── "Knock! Knock!" ──    │  (Protocol Start)
  │   ──── "Who's there?" ────►   │
  │   ◄────── "Turnip" ───────    │
  │   ──── "Turnip who?" ─────►   │
  │   ◄─── [Punchline + prompt]   │
  │   ────────── "y" ─────────►   │
  │   ◄──── "Knock! Knock!" ──    │
  │          ... (repeat) ...     │
  │   ────────── "n" ─────────►   │
  │   ◄──────── "Bye." ───────    │
  │   ─────── TCP FIN ──────►     │  (Connection Termination)
  │                               │
```

---

## 3. Protocol Specification

### 3.1 Transport Protocol

**Transport Layer**: TCP (Transmission Control Protocol)  
**Default Port**: 4444  
**Port Range**: 1-65535 (configurable)  
**Character Encoding**: UTF-8 (platform default)  
**Line Termination**: Platform-dependent newline (\\n or \\r\\n)

### 3.2 Connection Requirements

1. **Server MUST** create a ServerSocket and bind to a specified port
2. **Server MUST** accept incoming client connections
3. **Client MUST** initiate connection to server's host and port
4. **Both parties MUST** establish reliable TCP connection before protocol exchange
5. **Server MUST** initiate the protocol conversation by sending first message

### 3.3 Message Exchange Rules

1. Messages are line-oriented (terminated by newline character)
2. Server sends first message after connection established
3. Client and server alternate sending messages
4. Server always waits for client response before sending next message
5. Protocol terminates when server sends "Bye." message

### 3.4 Protocol Flow

```
Phase 1: CONNECTION
  Server: Listen on port → Accept client connection

Phase 2: INITIALIZATION
  Server: Send "Knock! Knock!"

Phase 3: JOKE EXCHANGE (Repeatable)
  Server: "Knock! Knock!"
  Client: "Who's there?"
  Server: [CLUE]
  Client: "[CLUE] who?"
  Server: [PUNCHLINE] + " Want another? (y/n)"
  Client: "y" or "n"

Phase 4: CONTINUATION
  If Client == "y": GOTO Phase 3 (next joke)
  If Client == "n": GOTO Phase 5

Phase 5: TERMINATION
  Server: "Bye."
  Server: Close connection
```

---

## 4. State Machine Definition

### 4.1 State Enumeration

The protocol defines four distinct states:

| State ID | State Name       | Description                                    |
|----------|------------------|------------------------------------------------|
| 0        | WAITING          | Initial state, ready to start new joke        |
| 1        | SENTKNOCKKNOCK   | Sent "Knock! Knock!", awaiting "Who's there?" |
| 2        | SENTCLUE         | Sent clue, awaiting "[CLUE] who?"             |
| 3        | ANOTHER          | Sent punchline, awaiting continuation choice  |

### 4.2 State Transition Diagram

```
                    ┌─────────┐
                    │ WAITING │ (State 0)
                    └────┬────┘
                         │
                         │ processInput(null)
                         │ → "Knock! Knock!"
                         ▼
                ┌────────────────────┐
                │  SENTKNOCKKNOCK    │ (State 1)
                └─────────┬──────────┘
                          │
                          │ processInput("Who's there?")
                          │ → [CLUE]
                          ▼
                   ┌─────────────┐
                   │  SENTCLUE   │ (State 2)
                   └──────┬──────┘
                          │
                          │ processInput("[CLUE] who?")
                          │ → [PUNCHLINE] + prompt
                          ▼
                    ┌─────────┐
                    │ ANOTHER │ (State 3)
                    └────┬────┘
                         │
                ┌────────┴────────┐
                │                 │
         input=="y"          input=="n"
                │                 │
                ▼                 ▼
         SENTKNOCKKNOCK      WAITING
         (next joke)         ("Bye.")
```

### 4.3 State Transition Table

| Current State    | Client Input         | Server Response                            | Next State       | Notes                    |
|------------------|----------------------|-------------------------------------------|------------------|--------------------------|
| WAITING          | null (initial)       | "Knock! Knock!"                           | SENTKNOCKKNOCK   | Protocol initialization  |
| SENTKNOCKKNOCK   | "Who's there?"       | [CLUE]                                    | SENTCLUE         | Valid response           |
| SENTKNOCKKNOCK   | *invalid*            | Error message + "Knock! Knock!"           | SENTKNOCKKNOCK   | Re-prompt user           |
| SENTCLUE         | "[CLUE] who?"        | [PUNCHLINE] + " Want another? (y/n)"      | ANOTHER          | Valid response           |
| SENTCLUE         | *invalid*            | Error message + "Knock! Knock!"           | SENTKNOCKKNOCK   | Restart joke             |
| ANOTHER          | "y" (yes)            | "Knock! Knock!"                           | SENTKNOCKKNOCK   | Continue with next joke  |
| ANOTHER          | "n" (no)             | "Bye."                                    | WAITING          | End conversation         |

### 4.4 Input Validation Rules

#### State: SENTKNOCKKNOCK
- **Expected**: "Who's there?" (case-insensitive)
- **Valid**: Exact match (ignoring case)
- **Invalid**: Any other input
- **Recovery**: Re-send "Knock! Knock!" and stay in same state

#### State: SENTCLUE
- **Expected**: "[CLUE] who?" where [CLUE] matches current joke clue (case-insensitive)
- **Valid**: Exact match (ignoring case)
- **Invalid**: Any other input
- **Recovery**: Return to SENTKNOCKKNOCK state and restart joke

#### State: ANOTHER
- **Expected**: "y" for yes, "n" for no (case-insensitive)
- **Valid**: "y" or "n" (any case)
- **Invalid**: Any other input (implementation may choose to exit)
- **Recovery**: Treat as "no" or re-prompt (implementation-specific)

---

## 5. Message Format

### 5.1 General Format

Messages are UTF-8 text strings terminated by newline character(s):

```
[MESSAGE_TEXT]\n
```

or

```
[MESSAGE_TEXT]\r\n
```

### 5.2 Message Categories

#### 5.2.1 Server Messages

**Greeting Message**
```
Knock! Knock!
```
- **Context**: Sent to initiate new joke
- **State**: Transition to SENTKNOCKKNOCK
- **Client Response**: "Who's there?"

**Clue Message**
```
[CLUE_TEXT]
```
- **Context**: Response to "Who's there?"
- **Examples**: "Turnip", "Little Old Lady", "Atch"
- **State**: Transition to SENTCLUE
- **Client Response**: "[CLUE_TEXT] who?"

**Punchline Message**
```
[ANSWER_TEXT] Want another? (y/n)
```
- **Context**: Response to "[CLUE] who?"
- **Examples**: "Turnip the heat, it's cold in here! Want another? (y/n)"
- **State**: Transition to ANOTHER
- **Client Response**: "y" or "n"

**Error/Correction Messages**
```
You're supposed to say "[EXPECTED_INPUT]"! Try again. Knock! Knock!
```
- **Context**: Invalid client input
- **Examples**: 
  - `You're supposed to say "Who's there?"! Try again. Knock! Knock!`
  - `You're supposed to say "Turnip who?"! Try again. Knock! Knock!`
- **State**: Recovery to appropriate state
- **Client Response**: Retry with correct input

**Termination Message**
```
Bye.
```
- **Context**: End of conversation
- **State**: Transition to WAITING (connection closes)
- **Client Response**: None (connection closes)

#### 5.2.2 Client Messages

**Initial Response**
```
Who's there?
```
- **Context**: Response to "Knock! Knock!"
- **Case**: Insensitive
- **Valid**: Exact phrase match

**Follow-up Response**
```
[CLUE] who?
```
- **Context**: Response to clue
- **Case**: Insensitive
- **Format**: Clue text followed by " who?"
- **Example**: "Turnip who?"

**Continuation Response**
```
y
```
or
```
n
```
- **Context**: Response to "Want another? (y/n)"
- **Case**: Insensitive
- **Values**: "y" (yes, continue) or "n" (no, terminate)

### 5.3 Character Set and Encoding

- **Character Set**: Unicode
- **Encoding**: UTF-8 (platform default)
- **Line Terminators**: CR+LF (\\r\\n) or LF (\\n) - both acceptable
- **Whitespace**: Leading/trailing whitespace handling is implementation-specific

---

## 6. Connection Lifecycle

### 6.1 Connection Establishment

```
Step 1: Server Initialization
  - Server creates ServerSocket(port)
  - Server calls accept() → blocks until client connects
  
Step 2: Client Connection
  - Client creates Socket(host, port)
  - TCP three-way handshake completes
  - Server's accept() returns with client Socket
  
Step 3: Stream Setup
  - Server creates PrintWriter(clientSocket.getOutputStream(), autoFlush=true)
  - Server creates BufferedReader(clientSocket.getInputStream())
  - Client creates PrintWriter(socket.getOutputStream(), autoFlush=true)
  - Client creates BufferedReader(socket.getInputStream())
  
Step 4: Protocol Initialization
  - Server creates KnockKnockProtocol instance
  - Server calls protocol.processInput(null)
  - Server sends initial "Knock! Knock!" to client
```

### 6.2 Active Communication

```
Loop:
  1. Client reads line from server
  2. Client displays message to user
  3. Client prompts user for input
  4. Client reads user input
  5. Client sends input to server
  6. Server reads client input
  7. Server calls protocol.processInput(input)
  8. Server sends response to client
  9. IF response == "Bye." THEN exit loop
  10. GOTO Step 1
```

### 6.3 Connection Termination

```
Normal Termination:
  1. Client sends "n" (no more jokes)
  2. Server sends "Bye."
  3. Server closes streams (PrintWriter, BufferedReader)
  4. Server closes clientSocket
  5. Server closes serverSocket
  6. Client closes streams
  7. Client closes socket
  8. TCP connection terminates (FIN/ACK handshake)

Abnormal Termination:
  - Client disconnects unexpectedly → server catches IOException
  - Server crashes → client catches IOException
  - Network failure → both sides catch IOException
  - All resources MUST be closed in finally blocks
```

### 6.4 Resource Management

**Critical Rules:**
1. Always close resources in `finally` blocks
2. Close resources in reverse order of creation
3. Check for null before closing
4. Use try-with-resources when possible (Java 7+)
5. Enable autoFlush on PrintWriter for immediate transmission

**Closure Order:**
```java
finally {
    if (out != null) out.close();           // Close output first
    if (in != null) in.close();             // Then input
    if (clientSocket != null) clientSocket.close();  // Then client socket
    if (serverSocket != null) serverSocket.close();  // Finally server socket
}
```

---

## 7. Error Handling

### 7.1 Network Errors

#### 7.1.1 Connection Refused
- **Cause**: Server not running or wrong host/port
- **Detection**: Client catches `ConnectException`
- **Recovery**: Display error, verify server is running
- **User Action**: Start server, verify host/port, retry

#### 7.1.2 Address Already in Use
- **Cause**: Port already bound by another process
- **Detection**: Server catches `BindException`
- **Recovery**: Display error, suggest different port
- **User Action**: Use different port number

#### 7.1.3 Connection Reset
- **Cause**: Client/server unexpectedly disconnects
- **Detection**: `IOException` during read/write
- **Recovery**: Clean up resources, log error
- **User Action**: Check network connectivity, restart

### 7.2 Protocol Errors

#### 7.2.1 Invalid Input in SENTKNOCKKNOCK State
- **Input**: Anything except "Who's there?"
- **Response**: `You're supposed to say "Who's there?"! Try again. Knock! Knock!`
- **State**: Remain in SENTKNOCKKNOCK
- **Recovery**: User can retry with correct input

#### 7.2.2 Invalid Input in SENTCLUE State
- **Input**: Anything except "[CLUE] who?"
- **Response**: `You're supposed to say "[CLUE] who?"! Try again. Knock! Knock!`
- **State**: Return to SENTKNOCKKNOCK
- **Recovery**: Restart the current joke

#### 7.2.3 Invalid Input in ANOTHER State
- **Input**: Anything except "y" or "n"
- **Response**: Implementation-specific (may treat as "n", may re-prompt)
- **State**: Implementation-specific
- **Recovery**: Implementation-specific

### 7.3 Configuration Errors

#### 7.3.1 Invalid Port Number
- **Cause**: Port argument not a number or out of range (1-65535)
- **Detection**: `NumberFormatException` or range check
- **Recovery**: Display error, use DEFAULT_PORT (4444)
- **User Action**: Provide valid port number

#### 7.3.2 Invalid Host
- **Cause**: Host argument cannot be resolved
- **Detection**: `UnknownHostException`
- **Recovery**: Display error
- **User Action**: Verify hostname or IP address

### 7.4 Error Message Format

Error messages SHOULD include:
1. Error type/category
2. Specific cause
3. Suggested action
4. Default fallback (if applicable)

**Example:**
```
Error: Invalid port number 'abc'. Using default port 4444
Error: Connection refused. Is the server running on localhost:4444?
Error: Port 4444 already in use. Try a different port: java KnockKnockServer 5555
```

---

## 8. Security Considerations

### 8.1 Threat Model

As an **educational protocol**, KKP intentionally omits security features to maintain simplicity. However, implementers SHOULD be aware of security implications:

#### 8.1.1 Authentication
- **Current**: None
- **Risk**: Anyone can connect to server
- **Mitigation**: Deploy only on trusted networks or localhost
- **Extension**: Add password/token authentication

#### 8.1.2 Encryption
- **Current**: Plain text communication
- **Risk**: All messages visible to network sniffers
- **Mitigation**: Use only on trusted networks
- **Extension**: Wrap with TLS/SSL

#### 8.1.3 Input Validation
- **Current**: Basic protocol validation only
- **Risk**: Buffer overflow (unlikely in Java), resource exhaustion
- **Mitigation**: Input length limits, timeout mechanisms
- **Extension**: Rate limiting, input sanitization

#### 8.1.4 Denial of Service
- **Risk**: Single-threaded server can handle only one client
- **Impact**: One client blocks all others
- **Mitigation**: Educational limitation, acceptable for learning
- **Extension**: Multi-threaded or NIO-based server

### 8.2 Deployment Recommendations

For educational use:
- ✅ Run on localhost (127.0.0.1)
- ✅ Run on isolated lab network
- ✅ Use non-privileged ports (1024-65535)

For production use (NOT RECOMMENDED):
- ⚠️ Add authentication mechanism
- ⚠️ Implement TLS encryption
- ⚠️ Add rate limiting
- ⚠️ Use multi-threaded server
- ⚠️ Add comprehensive logging
- ⚠️ Implement input sanitization

### 8.3 Privacy Considerations

- No personal data is collected or transmitted
- No persistent storage of conversation
- Server logs may contain IP addresses and timestamps
- Session data exists only in memory during connection

---

## 9. Implementation Notes

### 9.1 Reference Implementation

The reference implementation consists of three Java classes:

#### 9.1.1 KnockKnockServer.java
- **Language**: Java
- **JDK Version**: Java 7+ (compatible with Java 8+)
- **Dependencies**: java.net.*, java.io.* (standard library only)
- **Concurrency**: Single-threaded (educational simplification)
- **Resource Management**: try-catch-finally pattern

**Key Features:**
- Command-line port configuration
- Comprehensive error handling
- Detailed logging to console
- Resource cleanup in finally blocks

#### 9.1.2 KnockKnockClient.java
- **Language**: Java
- **JDK Version**: Java 7+ (compatible with Java 8+)
- **Dependencies**: java.net.*, java.io.* (standard library only)
- **User Interface**: Console-based (System.in/System.out)
- **Resource Management**: try-catch-finally pattern

**Key Features:**
- Command-line host/port configuration
- Interactive user prompts
- Connection status feedback
- Resource cleanup in finally blocks

#### 9.1.3 KnockKnockProtocol.java
- **Language**: Java
- **JDK Version**: Java 7+ (compatible with Java 8+)
- **Dependencies**: None (pure Java, no imports)
- **Design Pattern**: Finite State Machine
- **Data Structure**: Parallel arrays for jokes

**Key Features:**
- Four-state FSM implementation
- Input validation with case-insensitive matching
- Debug logging for state transitions
- Circular joke rotation

### 9.2 Compilation and Execution

#### Compilation
```bash
javac KnockKnockServer.java
javac KnockKnockClient.java
javac KnockKnockProtocol.java

# Or compile all at once:
javac *.java
```

#### Execution - Server
```bash
# Default port 4444
java KnockKnockServer

# Custom port
java KnockKnockServer 8080
```

#### Execution - Client
```bash
# Default localhost:4444
java KnockKnockClient

# Custom host, default port
java KnockKnockClient example.com

# Custom host and port
java KnockKnockClient 192.168.1.100 8080
```

### 9.3 Testing Strategies

#### Unit Testing
- Test state transitions in isolation
- Verify input validation logic
- Test joke rotation mechanism
- Validate error message formatting

#### Integration Testing
- Test complete joke cycles
- Verify server-client message exchange
- Test connection establishment/termination
- Validate error recovery paths

#### System Testing
- Test on different operating systems
- Verify localhost vs. remote connections
- Test with various port configurations
- Validate resource cleanup under error conditions

#### Manual Testing Scenarios
```
Scenario 1: Happy Path
  - Start server
  - Connect client
  - Complete one joke
  - Request another (y)
  - Complete second joke
  - Decline another (n)
  - Verify clean termination

Scenario 2: Error Recovery
  - Start server
  - Connect client
  - Provide wrong response to "Knock! Knock!"
  - Verify error message and retry
  - Provide correct response
  - Complete joke successfully

Scenario 3: Multi-Joke Cycle
  - Start server
  - Connect client
  - Complete all 5 jokes
  - Verify joke wraps back to first
  - Request termination
```

### 9.4 Performance Characteristics

#### Latency
- **Message Latency**: ~1-5ms on localhost
- **State Transition**: < 1ms (in-memory operation)
- **Network Latency**: Depends on network conditions

#### Throughput
- **Messages per Second**: Limited by user input speed (interactive)
- **Theoretical Max**: ~1000 msgs/sec on localhost (if automated)

#### Resource Usage
- **Memory**: < 10MB heap space
- **CPU**: Minimal (blocking I/O, event-driven)
- **Network**: Text-based, ~50-200 bytes per message
- **File Handles**: 1 ServerSocket + 1 Socket per connection

#### Scalability Limitations
- **Concurrent Clients**: 1 (single-threaded)
- **Port Binding**: 1 port per server instance
- **Memory**: Linear with joke database size (trivial for 5 jokes)

---

## 10. Extensibility

### 10.1 Planned Extensions

The protocol design allows for several educational extensions:

#### 10.1.1 Multi-threaded Server
```java
// Accept multiple clients concurrently
while (true) {
    Socket client = serverSocket.accept();
    new Thread(new ClientHandler(client)).start();
}
```
**Learning Objectives:**
- Thread creation and management
- Synchronization and thread safety
- Resource sharing between threads

#### 10.1.2 Joke Database from File
```java
// Load jokes from external JSON/CSV/text file
List<Joke> jokes = JokeLoader.loadFromFile("jokes.json");
```
**Learning Objectives:**
- File I/O operations
- Data parsing (JSON, CSV, XML)
- Error handling for missing/corrupt files

#### 10.1.3 Authentication
```java
// Simple password authentication
Server: "Password?"
Client: [password]
Server: "Authenticated" or "Access Denied"
```
**Learning Objectives:**
- Security concepts
- Protocol extension
- State machine modification

#### 10.1.4 Logging Framework
```java
// Replace System.out with proper logging
Logger logger = LoggerFactory.getLogger(KnockKnockServer.class);
logger.info("Client connected from: {}", clientAddress);
```
**Learning Objectives:**
- Logging best practices
- Log levels (INFO, DEBUG, ERROR)
- External library integration

### 10.2 Protocol Versioning

If extending the protocol, consider versioning:

```
Server: "KKP/1.0"     (Version announcement)
Client: "KKP/1.0"     (Version confirmation)
Server: "Knock! Knock!" (Protocol continues)
```

**Version Compatibility:**
- **Major version**: Breaking changes (incompatible)
- **Minor version**: Backward-compatible additions

### 10.3 Backward Compatibility

When extending the protocol:
1. MUST NOT break existing state machine
2. SHOULD add new states rather than modify existing
3. MUST maintain message format compatibility
4. MAY add optional features with capability negotiation

### 10.4 Future Enhancements

Suggested enhancements for advanced learners:

| Enhancement | Difficulty | Concepts Taught |
|-------------|-----------|-----------------|
| Multithreading | Medium | Concurrency, thread safety |
| Non-blocking I/O (NIO) | Hard | Async I/O, event loops |
| TLS/SSL encryption | Medium | Security, certificates |
| HTTP REST API | Medium | Web protocols, APIs |
| WebSocket upgrade | Hard | Protocol switching |
| Metrics/monitoring | Easy | Observability |
| Rate limiting | Medium | Resource management |
| Command pattern | Medium | Design patterns |

---

## 11. References

### 11.1 Normative References

- **[RFC 793]** Transmission Control Protocol (TCP)  
  https://tools.ietf.org/html/rfc793

- **[RFC 2119]** Key words for use in RFCs to Indicate Requirement Levels  
  https://tools.ietf.org/html/rfc2119

### 11.2 Informative References

- **[JAVA-SOCKET]** Oracle Java Socket Documentation  
  https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html

- **[JAVA-TCP]** Oracle Java Networking Tutorial  
  https://docs.oracle.com/javase/tutorial/networking/sockets/

- **[STATE-MACHINE]** State Machine Design Pattern  
  https://en.wikipedia.org/wiki/Finite-state_machine

- **[CLIENT-SERVER]** Client-Server Architecture  
  https://en.wikipedia.org/wiki/Client%E2%80%93server_model

### 11.3 Educational Resources

- **README.md** - Project overview and quick start guide
- **docs/GETTING_STARTED.md** - Beginner's detailed guide
- **docs/QUICKSTART.md** - Hands-on learning path
- **docs/COMMANDLINE_ARGS.md** - Command-line configuration guide

### 11.4 Related Protocols

This educational protocol draws inspiration from:
- **SMTP** (Simple Mail Transfer Protocol) - Text-based, stateful
- **FTP** (File Transfer Protocol) - Stateful, command-response
- **ECHO** (RFC 862) - Simple educational protocol
- **DAYTIME** (RFC 867) - Simple educational protocol

---

## Appendix A: Complete State Machine Implementation

### A.1 State Constants
```java
private static final int WAITING = 0;
private static final int SENTKNOCKKNOCK = 1;
private static final int SENTCLUE = 2;
private static final int ANOTHER = 3;
```

### A.2 State Transition Logic
```java
public String processInput(String clientInput) {
    switch (state) {
        case WAITING:
            // Initialize new joke
            return "Knock! Knock!";
            
        case SENTKNOCKKNOCK:
            // Validate "Who's there?"
            if (valid) return clue;
            else return errorMessage;
            
        case SENTCLUE:
            // Validate "[CLUE] who?"
            if (valid) return punchline;
            else return errorMessage;
            
        case ANOTHER:
            // Validate "y" or "n"
            if (continue) return "Knock! Knock!";
            else return "Bye.";
    }
}
```

---

## Appendix B: Example Session Transcript

### B.1 Successful Complete Session
```
[Server starts]
Server: Server started on port 4444. Waiting for client connections...

[Client connects]
Server: Client connected from: /127.0.0.1
Server: Knock! Knock!

Client: Who's there?
Server: Turnip

Client: Turnip who?
Server: Turnip the heat, it's cold in here! Want another? (y/n)

Client: y
Server: Knock! Knock!

Client: Who's there?
Server: Little Old Lady

Client: Little Old Lady who?
Server: I didn't know you could yodel! Want another? (y/n)

Client: n
Server: Bye.

[Connection closes]
Server: Conversation ended. Client disconnected.
Server: All resources closed.
```

### B.2 Session with Error Recovery
```
Server: Knock! Knock!

Client: Hello?
Server: You're supposed to say "Who's there?"! Try again. Knock! Knock!

Client: Who's there?
Server: Turnip

Client: What?
Server: You're supposed to say "Turnip who?"! Try again. Knock! Knock!

Client: Who's there?
Server: Turnip

Client: Turnip who?
Server: Turnip the heat, it's cold in here! Want another? (y/n)

Client: n
Server: Bye.
```

---

## Appendix C: Joke Database Schema

### C.1 Parallel Array Structure
```java
// Index:     0           1                  2          3       4
clues[]   = {"Turnip", "Little Old Lady", "Atch",    "Who",  "Who"};
answers[] = {"Turnip the heat...", "I didn't know...", "Bless you!", ...};
```

### C.2 Joke Rotation Algorithm
```java
// After each joke completion:
if (currentJoke == NUM_JOKES - 1) {
    currentJoke = 0;  // Wrap to first
} else {
    currentJoke++;    // Increment to next
}
```

### C.3 Adding New Jokes
```java
// Step 1: Increment NUM_JOKES
private static final int NUM_JOKES = 6;  // Was 5

// Step 2: Add to clues array
private String[] clues = {
    "Turnip",
    // ... existing jokes ...
    "Boo"  // New joke
};

// Step 3: Add to answers array (same index)
private String[] answers = {
    "Turnip the heat, it's cold in here!",
    // ... existing answers ...
    "Aww, don't cry!"  // New punchline
};
```

---

## Appendix D: Port Configuration Examples

### D.1 Common Port Scenarios

| Scenario | Server Command | Client Command | Notes |
|----------|---------------|----------------|-------|
| Default localhost | `java KnockKnockServer` | `java KnockKnockClient` | Uses port 4444 |
| Custom port | `java KnockKnockServer 8080` | `java KnockKnockClient localhost 8080` | Uses port 8080 |
| Remote server | `java KnockKnockServer` | `java KnockKnockClient 192.168.1.100 4444` | Cross-machine |
| Multiple servers | `java KnockKnockServer 4444` | `java KnockKnockClient localhost 4444` | Different ports per server |
| HTTP alt port | `java KnockKnockServer 8080` | `java KnockKnockClient localhost 8080` | Common web dev port |

### D.2 Port Selection Guidelines

**Privileged Ports (1-1023)**
- Require administrator/root access
- Generally avoided for educational projects

**Registered Ports (1024-49151)**
- Reserved for specific services
- Safe to use if not conflicting

**Dynamic/Private Ports (49152-65535)**
- Ideal for educational projects
- Least likely to conflict

**Recommended Ports:**
- 4444 (default, memorable)
- 5555 (alternative, memorable)
- 8080 (HTTP alternative)
- 9000-9999 (development range)

---

## Appendix E: Troubleshooting Guide

### E.1 Common Issues and Solutions

| Problem | Possible Cause | Solution |
|---------|---------------|----------|
| "Connection refused" | Server not running | Start server before client |
| "Address already in use" | Port already bound | Use different port or kill process |
| Client hangs | Waiting for server response | Check server is running, verify port |
| Messages not appearing | Buffering issue | Enable autoFlush in PrintWriter |
| Cannot connect remotely | Firewall blocking | Allow port in firewall, use correct IP |
| Compilation errors | Wrong Java version | Use Java 7 or higher |

### E.2 Diagnostic Commands

**Check if port is in use (Windows):**
```powershell
netstat -ano | findstr :4444
```

**Check if port is in use (Linux/Mac):**
```bash
lsof -i :4444
netstat -an | grep 4444
```

**Find process using port (Windows):**
```powershell
netstat -ano | findstr :4444
taskkill /PID <pid> /F
```

**Test connectivity:**
```bash
telnet localhost 4444
nc localhost 4444  # netcat
```

---

## Appendix F: Educational Lesson Plan

### F.1 Recommended Teaching Sequence

**Week 1: Introduction to Networking**
- Concepts: IP, ports, TCP/IP, client-server
- Activity: Run the KKP server and client
- Homework: Read README.md, experiment with different ports

**Week 2: Understanding Sockets**
- Concepts: ServerSocket, Socket, I/O streams
- Activity: Trace code execution, add debug logging
- Homework: Draw sequence diagrams for message flow

**Week 3: State Machines**
- Concepts: States, transitions, validation
- Activity: Modify state machine, add new state
- Homework: Create state diagram for new protocol

**Week 4: Error Handling**
- Concepts: Exceptions, resource management, finally blocks
- Activity: Introduce errors, observe recovery
- Homework: Add try-with-resources, improve error messages

**Week 5: Extensions**
- Concepts: Multithreading, file I/O, design patterns
- Activity: Implement chosen extension
- Homework: Complete extension, write documentation

### F.2 Learning Objectives Mapped to Code

| Learning Objective | Code Location | Key Concept |
|--------------------|---------------|-------------|
| TCP socket creation | KnockKnockServer.java:46 | `new ServerSocket(port)` |
| Accepting connections | KnockKnockServer.java:50 | `serverSocket.accept()` |
| I/O stream setup | KnockKnockServer.java:55-56 | PrintWriter, BufferedReader |
| State machine design | KnockKnockProtocol.java:20-24 | State constants |
| Input validation | KnockKnockProtocol.java:67-76 | Case-insensitive matching |
| Resource cleanup | KnockKnockServer.java:90-100 | finally block |
| Error handling | KnockKnockServer.java:86-89 | try-catch IOException |

### F.3 Assessment Ideas

**Knowledge Checks:**
- Explain the purpose of each state in the state machine
- Describe what happens when `accept()` is called
- Identify the order of resource cleanup and why

**Practical Exercises:**
- Add a new joke to the database
- Change the protocol to require "Knock knock" instead of "Knock! Knock!"
- Implement timeout after 30 seconds of inactivity

**Project Extensions:**
- Create a GUI client using Swing
- Implement multi-threaded server
- Add authentication mechanism
- Create logging framework integration

---

## Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-01 | KKP Working Group | Initial RFC publication |

---

## Acknowledgments

This protocol and implementation are derived from Oracle's Java Socket Programming Tutorial, adapted for educational purposes with extensive documentation and commentary to facilitate learning.

---

**End of RFC: Knock-Knock Protocol Specification**
