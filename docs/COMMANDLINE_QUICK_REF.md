# Quick Reference - Command-Line Arguments

## TL;DR - Usage

### Server
```bash
java KnockKnockServer           # port 4444 (default)
java KnockKnockServer 5555      # port 5555 (custom)
```

### Client
```bash
java KnockKnockClient                      # localhost:4444 (default)
java KnockKnockClient 192.168.1.100        # host:4444 (custom host)
java KnockKnockClient 192.168.1.100 5555   # host:port (custom both)
```

---

## Common Scenarios

### Both on Same Machine
```bash
# Terminal 1
java KnockKnockServer

# Terminal 2
java KnockKnockClient
```

### Custom Port, Same Machine
```bash
# Terminal 1
java KnockKnockServer 8888

# Terminal 2
java KnockKnockClient localhost 8888
```

### Different Machines
```bash
# Machine A (192.168.1.100)
java KnockKnockServer 4444

# Machine B
java KnockKnockClient 192.168.1.100 4444
```

### Multiple Servers (Same Machine)
```bash
# Terminal 1
java KnockKnockServer 4444

# Terminal 2
java KnockKnockServer 5555

# Terminal 3
java KnockKnockClient localhost 4444

# Terminal 4
java KnockKnockClient localhost 5555
```

---

## Error Handling

All errors gracefully fall back to defaults:

```bash
java KnockKnockServer abc         # Invalid → Uses 4444
java KnockKnockServer 99999       # Out of range → Uses 4444
java KnockKnockClient localhost x # Invalid port → Uses 4444
```

---

## Port Tips

- **Default**: 4444 (easy to remember)
- **Alternatives**: 5555, 6666, 7777, 8888 (easy patterns)
- **Valid range**: 1-65535
- **Avoid**: 0-1023 (system ports, may need admin)

---

## Find Your IP

Need to know your server's IP for clients on other machines?

### Linux/Mac
```bash
ifconfig
# Look for inet 192.168.x.x
```

### Windows
```bash
ipconfig
# Look for IPv4 Address
```

---

## Full Documentation

For complete details, see:
- **[COMMANDLINE_ARGS.md](COMMANDLINE_ARGS.md)** - Comprehensive guide
- **[README.txt](README.txt)** - Network configuration section

---

That's it! Port and host are now configurable without changing code. 🎉
