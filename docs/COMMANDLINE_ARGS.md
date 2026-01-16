# Command-Line Arguments Guide

## Overview

The Knock-Knock Server and Client now support flexible command-line arguments, making the code practical for real-world use.

---

## Server Usage

### Syntax
```bash
java KnockKnockServer [PORT]
```

### Arguments

| Argument | Type | Required | Default | Range | Example |
|----------|------|----------|---------|-------|---------|
| `PORT` | Integer | No | 4444 | 1-65535 | 5555 |

### Examples

#### Default Port (4444)
```bash
java KnockKnockServer
```
Output:
```
Server started on port 4444. Waiting for client connections...
```

#### Custom Port
```bash
java KnockKnockServer 8888
```
Output:
```
Server started on port 8888. Waiting for client connections...
```

#### Invalid Port (Handled Gracefully)
```bash
java KnockKnockServer 99999
```
Output:
```
Error: Port must be between 1 and 65535. Using default port 4444
Server started on port 4444. Waiting for client connections...
```

#### Non-Numeric Port (Handled Gracefully)
```bash
java KnockKnockServer abc
```
Output:
```
Error: Invalid port number 'abc'. Using default port 4444
Server started on port 4444. Waiting for client connections...
```

---

## Client Usage

### Syntax
```bash
java KnockKnockClient [HOST] [PORT]
```

### Arguments

| Argument | Type | Required | Default | Example |
|----------|------|----------|---------|---------|
| `HOST` | String | No | localhost | 192.168.1.100 |
| `PORT` | Integer | No | 4444 | 5555 |

### Examples

#### Connect to Default (localhost:4444)
```bash
java KnockKnockClient
```
Output:
```
Connecting to server at localhost:4444...
Connected to server!
```

#### Connect to Specific Host (Default Port 4444)
```bash
java KnockKnockClient 192.168.1.100
```
Output:
```
Connecting to server at 192.168.1.100:4444...
Connected to server!
```

#### Connect to Specific Host and Port
```bash
java KnockKnockClient 192.168.1.100 8888
```
Output:
```
Connecting to server at 192.168.1.100:8888...
Connected to server!
```

#### Invalid Port (Handled Gracefully)
```bash
java KnockKnockClient localhost 99999
```
Output:
```
Error: Port must be between 1 and 65535. Using default port 4444
Connecting to server at localhost:4444...
```

#### Server Not Running
```bash
java KnockKnockClient localhost 4444
```
Output:
```
Connecting to server at localhost:4444...
Connection error: Connection refused (Connection refused)
Make sure the server is running on localhost:4444
```

---

## Common Scenarios

### Scenario 1: Both on Same Machine (Default)
```bash
# Terminal 1
java KnockKnockServer

# Terminal 2
java KnockKnockClient
```

### Scenario 2: Both on Same Machine, Custom Port
```bash
# Terminal 1
java KnockKnockServer 5555

# Terminal 2
java KnockKnockClient localhost 5555
```

### Scenario 3: Different Machines
Assume server is on `192.168.1.100`

```bash
# Machine A (Server)
java KnockKnockServer 4444

# Machine B (Client)
java KnockKnockClient 192.168.1.100 4444
```

### Scenario 4: Running Multiple Servers on Same Machine
```bash
# Terminal 1 - Server on port 4444
java KnockKnockServer 4444

# Terminal 2 - Another server on port 5555
java KnockKnockServer 5555

# Terminal 3 - Client connects to first server
java KnockKnockClient localhost 4444

# Terminal 4 - Another client connects to second server
java KnockKnockClient localhost 5555
```

---

## Port Selection Tips

### Well-Known Ports (0-1023)
- May require administrator/root privileges
- Examples: 80 (HTTP), 443 (HTTPS), 22 (SSH)
- Not recommended for learning projects

### Registered Ports (1024-49151)
- Generally safe to use
- Examples: 3306 (MySQL), 5432 (PostgreSQL)
- Good for local testing

### Dynamic/Private Ports (49152-65535)
- Best for temporary applications
- Least likely to conflict
- Recommended for learning projects

### Recommended for Learning
- `4444` - Good default, easy to remember
- `5555`, `6666`, `7777`, `8888` - Easy to remember alternatives
- `8080` - Common for web servers

---

## Error Handling

The code handles errors gracefully:

### Invalid Port Number
- **Non-numeric input** → Falls back to default (4444)
- **Out of range** (< 1 or > 65535) → Falls back to default (4444)
- **Error message** → Displayed, but program continues

### Connection Errors (Client)
- **Unknown host** → "Connection error: Unknown host 'xxx'"
- **Connection refused** → "Connection error: Connection refused"
- **Server not running** → Clear error message with help text

### Best Practice
Always check the console output to verify the port being used!

```bash
java KnockKnockServer 8080
# Output shows: "Server started on port 8080..."
# This confirms the server is using port 8080
```

---

## Environment-Specific Notes

### Linux/Mac
```bash
# Find available ports
lsof -i -P -n | grep LISTEN

# Find what's using a specific port
lsof -i :4444

# Get your IP address
ifconfig
```

### Windows
```bash
# Find available ports
netstat -ano | findstr LISTEN

# Find what's using a specific port
netstat -ano | findstr :4444

# Get your IP address
ipconfig
```

---

## Educational Value

Learning command-line argument parsing:
1. **`main(String[] args)`** - How arguments are passed to Java programs
2. **`args.length`** - How many arguments were provided
3. **`args[0]`, `args[1]`** - How to access individual arguments
4. **`Integer.parseInt()`** - How to convert strings to numbers
5. **Error handling** - Validating user input gracefully
6. **Default values** - Providing sensible defaults when arguments aren't provided

---

## Testing Command-Line Arguments

### Test with Default Values
```bash
# Server accepts default port
java KnockKnockServer

# Client connects with default host and port
java KnockKnockClient
```

### Test with Custom Values
```bash
# Server accepts custom port
java KnockKnockServer 7777

# Client connects to custom port
java KnockKnockClient localhost 7777
```

### Test Error Handling
```bash
# Invalid port number
java KnockKnockServer abc

# Port out of range
java KnockKnockServer 99999

# Non-numeric port in client
java KnockKnockClient localhost xyz
```

All should handle gracefully with error messages and fall back to defaults.

---

## Next Steps

1. **Run with defaults** - See basic functionality
2. **Try custom ports** - Understand command-line arguments
3. **Try different hosts** - Understand network connectivity
4. **Run multiple servers** - See port conflicts and workarounds
5. **Study the code** - See how argument parsing is implemented

---

This feature demonstrates how real Java applications handle configuration via command-line arguments!
