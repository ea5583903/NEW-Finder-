# Dell Nano PHP CLI

Run from this folder:

```sh
./pshell
```

Dell Nano runs a terminal system check, then asks which `mxp://` user disk to boot. It has no login and no BSOD screens.

User disks are stored under:

```text
~/.dell-nano/disks
```

Useful commands:

- `help`
- `apps`
- `disks`
- `createdisk school`
- `add command php`
- `add command c#`
- `add command rust`
- `add command java`
- `add command npm`
- `add command node`
- `add command yarn`
- `commands`
- `run script.php`
- `run app.js`
- `npmstart`
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

Built-in commands run first. Anything Dell Nano does not recognize is passed to your default shell from the current `mxp://` disk path.

Language command packs are saved on the selected `mxp://` disk. `add command <tool>` checks the computer first; if the tool is installed, Dell Nano enables helper commands for it.

Examples:

- PHP: `phpversion`, `runphp file.php`, `run file.php`
- C#: `dotnetversion`, `runcs app.csproj`, `dotnetrun`
- Rust: `rustversion`, `runrust main.rs`, `cargorun`, `cargotest`
- Java: `javaversion`, `compilejava Main.java`, `run Main.java`, `runjava Main`, `runjar app.jar`
- Node: `nodeversion`, `runnode app.js`, `run app.js`
- npm: `npmversion`, `npminstall`, `npmstart`, `npmdev`, `npmtest`, `run project-folder`
- Yarn: `yarnversion`, `yarninstall`, `yarnstart`, `yarndev`, `yarntest`
