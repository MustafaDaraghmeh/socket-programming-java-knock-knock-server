# Knock-Knock Server: Learn Java Socket Programming

A practical, fully-commented educational example of TCP socket programming in Java. Build a working knock-knock joke server and client, and learn how network communication works under the hood.

```
┌─────────────┐                  ┌─────────────┐
│   CLIENT    │                  │   SERVER    │
│             │                  │             │
│ "Who's      │  ────TCP─────>   │ "Knock!     │
│  there?"    │                  │ Knock!"     │
│             │ <────TCP─────    │             │
└─────────────┘                  └─────────────┘
```

## 🚀 Quick Start (3 Minutes)

### 1. Compile
```bash
javac *.java
```

### 2. Start Server (Terminal 1)
```bash
java KnockKnockServer           # Default port 4444
# OR with custom port:
java KnockKnockServer 5555
```

### 3. Start Client (Terminal 2)
```bash
java KnockKnockClient                      # Default: localhost:4444
# OR with custom host/port:
java KnockKnockClient 192.168.1.100 5555
```

### 4. Play!
```
Server: Knock! Knock!
You: Who's there?

Server: Turnip
You: Turnip who?

Server: Turnip the heat, it's cold in here! Want another? (y/n)
You: y
```

That's it! You've just created a working TCP/IP application. 🎉

---

## 📚 What You'll Learn

### Networking Concepts
- **ServerSocket** - How servers listen for connections
- **Socket** - How clients connect to servers
- **I/O Streams** - How data flows over the network
- **Localhost & Ports** - Basic networking concepts
- **TCP/IP Communication** - Real network protocol in action

### Java Best Practices
- Exception handling with try-catch-finally
- Resource cleanup and preventing memory leaks
- Code documentation and comments
- Naming conventions
- Input validation

### Software Design Patterns
- **State Machine Pattern** - Protocol flow control
- **Client-Server Architecture** - How distributed systems work
- **Separation of Concerns** - Protocol logic separated from I/O
- **Protocol Design** - Creating rules for communication

---

## 📖 Learning Paths

### Level 1: Just Run It (5 minutes)
✓ Follow Quick Start above  
✓ See how a server and client communicate  
✓ Play some knock-knock jokes

### Level 2: Understand the Code (20 minutes)
1. Open `KnockKnockServer.java` - Notice the 7 numbered steps
2. Open `KnockKnockClient.java` - Notice the 5 numbered steps
3. Open `KnockKnockProtocol.java` - Look at the state machine diagram
4. Read the inline comments - They explain every decision

### Level 3: Understand Each Concept (30 minutes)
- **ServerSocket (line 23)** - How the server listens
- **Socket (line 31)** - How the client connects
- **PrintWriter/BufferedReader** - How messages are sent/received
- **State Machine** - How the protocol controls conversation

### Level 4: Modify It (1 hour)
Try these challenges:
- [ ] Add more jokes to the `clues` and `answers` arrays
- [ ] Change the port using command-line arguments
- [ ] Add timestamps to messages
- [ ] Add input validation
- [ ] Change the message format

### Level 5: Extend It (2+ hours)
Try these advanced challenges:
- [ ] Handle multiple clients at once (use Threading)
- [ ] Load jokes from a file
- [ ] Add simple authentication
- [ ] Make the port configurable (already done!)
- [ ] Add logging instead of `System.out.println()`

---

## 🎯 Code Structure

### Three Main Classes

**KnockKnockServer.java** - The Server
```java
// Step 1: Create ServerSocket (listens for connections)
// Step 2: Accept client connection (blocks until client connects)
// Step 3: Set up I/O streams (PrintWriter, BufferedReader)
// Step 4: Initialize protocol handler
// Step 5: Send initial greeting
// Step 6: Loop - read input, respond using protocol
// Step 7: Close resources
```

**KnockKnockClient.java** - The Client
```java
// Step 1: Create Socket (connect to server)
// Step 2: Set up I/O streams
// Step 3: Create user input reader
// Step 4: Loop - read from server, ask user, send response
// Step 5: Close resources
```

**KnockKnockProtocol.java** - The Protocol (State Machine)
```
WAITING → SENTKNOCKKNOCK → SENTCLUE → ANOTHER → (repeat)
                                      ↓
                                   WAITING
```

---

## 💡 Key Concepts Explained

### What is a Socket?
A socket is one end of a network connection. Think of it like a phone line:
- **ServerSocket** = Phone that listens for incoming calls
- **Socket** = The actual phone connection

### What is a Port?
A port is like a mailbox number on a computer. Default: **4444**
- Valid range: 1-65535
- Common ports: 80 (web), 22 (SSH), 3306 (database)

### What is a State Machine?
A state machine controls the conversation flow:
1. **WAITING** - Ready to tell a joke
2. **SENTKNOCKKNOCK** - Waiting for "Who's there?"
3. **SENTCLUE** - Waiting for "[Clue] who?"
4. **ANOTHER** - Asking if they want another joke

Each state validates input and transitions to the next state.

---

## 🔧 Command-Line Arguments

### Server
```bash
java KnockKnockServer           # Uses port 4444
java KnockKnockServer 5555      # Uses port 5555
java KnockKnockServer 8080      # Uses port 8080
```

### Client
```bash
java KnockKnockClient                      # localhost:4444
java KnockKnockClient example.com          # example.com:4444
java KnockKnockClient 192.168.1.100 5555   # custom host and port
```

**Full guide**: See [docs/COMMANDLINE_ARGS.md](docs/COMMANDLINE_ARGS.md)

---

## 🚨 Common Issues

| Problem | Solution |
|---------|----------|
| "Connection refused" | Start server first in another terminal |
| "Address already in use" | Use a different port: `java KnockKnockServer 5555` |
| Port doesn't change | Make sure both server and client use same port |
| Can't connect to another machine | Use server's IP address: `java KnockKnockClient 192.168.x.x 4444` |

---

## 🧪 Practical Scenarios

### Scenario 1: Multiple Servers (Same Machine)
```bash
# Terminal 1: First server on port 4444
java KnockKnockServer 4444

# Terminal 2: Second server on port 5555
java KnockKnockServer 5555

# Terminal 3: Connect to first
java KnockKnockClient localhost 4444

# Terminal 4: Connect to second
java KnockKnockClient localhost 5555
```

### Scenario 2: Server on Another Machine
```bash
# Machine A (IP: 192.168.1.100)
java KnockKnockServer 4444

# Machine B
java KnockKnockClient 192.168.1.100 4444
```

### Scenario 3: Troubleshooting Connection
```bash
# Terminal 1: Start server with debug output
java KnockKnockServer
# Shows: "Server started on port 4444..."

# Terminal 2: Try to connect
java KnockKnockClient localhost 4444
# Shows: "Connecting to server at localhost:4444..."
# Then: "Connected to server!" or connection error
```

---

## 📖 Full Documentation

- **[docs/GETTING_STARTED.md](docs/GETTING_STARTED.md)** - Beginner's guide with detailed explanations
- **[docs/QUICKSTART.md](docs/QUICKSTART.md)** - Hands-on quick start guide with 5 learning levels
- **[docs/COMMANDLINE_QUICK_REF.md](docs/COMMANDLINE_QUICK_REF.md)** - Quick reference for command-line usage
- **[docs/COMMANDLINE_ARGS.md](docs/COMMANDLINE_ARGS.md)** - Comprehensive argument guide with examples

---

## 🎓 For Educators

This project is designed as an educational tool:

✅ **Well-commented code** - Every line explained  
✅ **Clear structure** - 7 steps in server, 5 in client  
✅ **State machine** - Visual diagram included  
✅ **Progressive learning** - 5 levels from beginner to advanced  
✅ **Practical** - Real TCP/IP communication  
✅ **Extensible** - Easy to modify and build upon  

**Teaching tips:**
1. Have students run the code first (Level 1)
2. Have them read the code comments (Level 2)
3. Walk through each Java class explaining the concepts
4. Have them modify jokes and recompile
5. Challenge them with Level 4-5 tasks

---

## 🔍 Under the Hood

### How the Server Works
```
┌─────────────────────────────────┐
│  1. Create ServerSocket(4444)   │ ← Listens for connections
├─────────────────────────────────┤
│  2. Call accept()               │ ← Blocks until client connects
├─────────────────────────────────┤
│  3. Get I/O streams             │ ← Set up communication channels
├─────────────────────────────────┤
│  4. Send "Knock! Knock!"        │ ← Start the joke
├─────────────────────────────────┤
│  5. Read client input           │ ← Wait for client response
├─────────────────────────────────┤
│  6. Process with state machine  │ ← Validate and respond
├─────────────────────────────────┤
│  7. Loop or exit                │ ← Continue until "Bye."
└─────────────────────────────────┘
```

### How the Protocol Works
```
Server State Machine Transitions:

WAITING
   ↓ (send "Knock! Knock!")
SENTKNOCKKNOCK
   ↓ (client says "Who's there?")
SENTCLUE
   ↓ (client says "[Clue] who?")
ANOTHER
   ↓ (client says "y" or "n")
[REPEAT or EXIT]
```

---

## 📚 Learn More

### Related Topics
- **Multithreading** - Handle multiple clients simultaneously
- **NIO (Non-blocking I/O)** - Handle thousands of connections
- **Serialization** - Send objects instead of text
- **Protocol Design** - Create your own protocols
- **Network Security** - Encryption and authentication

### Resources
- [Java Socket Documentation](https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html)
- [Oracle Networking Tutorial](https://docs.oracle.com/javase/tutorial/networking/)
- [TCP/IP Basics](https://en.wikipedia.org/wiki/Internet_protocol_suite)

---

## 💻 Project Files

```
.
├── README.md                    ← You are here
├── KnockKnockServer.java        ← Server implementation
├── KnockKnockClient.java        ← Client implementation
├── KnockKnockProtocol.java      ← Protocol state machine
└── docs/
    ├── QUICKSTART.md            ← Detailed quick start guide
    ├── COMMANDLINE_QUICK_REF.md ← Command-line reference
    └── COMMANDLINE_ARGS.md      ← Full argument guide
```

---

## ✨ Features

✅ **Fully Working** - Compiles and runs perfectly  
✅ **Well-Documented** - 30+ inline comments  
✅ **Educational** - Teaches real networking concepts  
✅ **Practical** - Configurable host and port  
✅ **Error Handling** - Graceful error messages  
✅ **No Dependencies** - Uses only Java standard library  
✅ **Multiple Learning Paths** - 5 levels from beginner to advanced  

---

## 🎯 Next Steps

1. **Run it** - Follow Quick Start above
2. **Understand it** - Read the code comments
3. **Modify it** - Try Level 4 challenges
4. **Build on it** - Try Level 5 extensions
5. **Explore** - Learn about threading, NIO, protocols

---

## 📝 License

This is an educational project. Feel free to use, modify, and share!

---

**Happy Learning!** 🚀

Questions? Read the code comments or check the [docs/](docs/) folder for guides.

Once you understand this, you'll have mastered the fundamentals of network programming - the same concepts that power the entire internet!
