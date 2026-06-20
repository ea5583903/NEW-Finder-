# MactonishXP PHP CLI

Run from this folder:

```sh
./pshell
```

MactonishXP runs an ASCII system check, then asks which `mxp://` user disk to boot. It has no login and no BSOD screens.

User disks are stored under:

```text
~/.mactonishxp/disks
```

Useful commands:

- `help`
- `apps`
- `disks`
- `createdisk school`
- `finder`
- `finder mxp://school`
- `notepad notes.txt`
- `echo hello`
- `ls -la`
- `bash echo from bash`
- `zsh echo from zsh`
- `shell uname -a`
- `calc (2 + 3) * 4`
- `clock`
- `calendar`
- `sysinfo`
- `shutdown`

Built-in commands run first. Anything MactonishXP does not recognize is passed to your default shell from the current `mxp://` disk path.
