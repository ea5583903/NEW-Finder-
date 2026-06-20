# Dell Nano C# CLI

Run from this folder:

```sh
./cshell
```

This is the C# console version of Dell Nano. It has the same terminal-style flow as the PHP version:

- Unicode and ASCII logo modes
- System check loading screen
- `mxp://<name>` disk picker and disk creation
- Built-in Finder, Notepad, Terminal, Calculator, Clock, Calendar, Notes, SysInfo
- Shell passthrough for Bash, Zsh, and normal shell commands

User disks are shared with the PHP version under:

```text
~/.dell-nano/disks
```

Useful commands:

- `help`
- `logo unicode`
- `logo ascii`
- `disks`
- `createdisk school`
- `finder mxp://school`
- `notepad notes.txt`
- `echo hello`
- `ls -la`
- `bash echo from bash`
- `zsh echo from zsh`
- `calc (2 + 3) * 4`
- `shutdown`
