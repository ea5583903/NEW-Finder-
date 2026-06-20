#!/usr/bin/env php
<?php
declare(strict_types=1);

final class MactonishXP
{
    private string $cwd;
    private string $home;
    private string $hostHome;
    private string $diskBase;
    private string $diskName = 'main';
    private array $notes = [];
    private bool $running = true;

    public function __construct()
    {
        $this->hostHome = getenv('HOME') ?: getcwd();
        $this->diskBase = $this->hostHome . DIRECTORY_SEPARATOR . '.mactonishxp' . DIRECTORY_SEPARATOR . 'disks';
        $this->home = $this->diskPath($this->diskName);
        $this->cwd = $this->home;
    }

    public function run(): void
    {
        $this->loadingScreen();
        $this->chooseDisk();
        $this->printDesktop();

        while ($this->running) {
            $line = $this->readLine("\n" . $this->shortPath($this->cwd) . "$: ");
            if ($line === null) {
                echo "\n";
                break;
            }

            $line = trim($line);
            if ($line === '') {
                continue;
            }

            $this->dispatch($line);
        }

        echo "MactonishXP has shut down.\n";
    }

    private function loadingScreen(): void
    {
        $this->clear();
        echo $this->logo();
        echo "\n";
        echo "MactonishXP BIOS A09\n";
        echo "Dell-like text mode boot shell\n\n";

        $steps = [
            'Checking memory',
            'Detecting keyboard',
            'Mounting local disk',
            'Loading command shell',
            'Starting desktop services',
        ];

        foreach ($steps as $step) {
            echo str_pad($step . ' ', 32, '.');
            usleep(180000);
            echo " OK\n";
        }

        echo "\nPress Enter to continue...";
        $this->readLine('');
        $this->clear();
    }

    private function chooseDisk(): void
    {
        $this->clear();
        echo $this->logo();
        echo "\nDisk selection\n";
        echo "Choose a user disk, or create a new one with an mxp:// name.\n\n";

        $disks = $this->availableDisks();
        if ($disks === []) {
            echo "No disks found yet.\n";
        } else {
            foreach ($disks as $index => $disk) {
                echo '  ' . ($index + 1) . ') mxp://' . $disk . "\n";
            }
        }
        echo "  N) Create new disk\n\n";

        while (true) {
            $choice = $this->readLine('Boot disk: ');
            if ($choice === null) {
                if ($disks === []) {
                    $this->createDiskInteractive();
                } else {
                    $this->selectDisk($disks[0]);
                }
                return;
            }

            $choice = trim($choice);
            if ($choice === '') {
                if ($disks === []) {
                    $this->createDiskInteractive();
                } else {
                    $this->selectDisk($disks[0]);
                }
                return;
            }
            if (strtolower($choice) === 'n' || strtolower($choice) === 'new') {
                $this->createDiskInteractive();
                return;
            }
            if (str_starts_with(strtolower($choice), 'mxp://')) {
                $name = $this->sanitizeDiskName(substr($choice, 6));
                if ($name !== '' && is_dir($this->diskPath($name))) {
                    $this->selectDisk($name);
                    return;
                }
            }
            if (ctype_digit($choice)) {
                $offset = (int) $choice - 1;
                if (isset($disks[$offset])) {
                    $this->selectDisk($disks[$offset]);
                    return;
                }
            }
            echo "Choose a listed number, mxp://name, or N.\n";
        }
    }

    private function createDiskInteractive(): void
    {
        while (true) {
            $raw = $this->readLine('New disk name: ');
            if ($raw === null) {
                $raw = 'main';
            }
            $name = $this->sanitizeDiskName($raw);
            if ($name === '') {
                echo "Use letters, numbers, dashes, or underscores.\n";
                continue;
            }

            $path = $this->diskPath($name);
            if (!is_dir($path)) {
                if (!$this->createDiskFiles($name)) {
                    continue;
                }
            }
            $this->selectDisk($name);
            return;
        }
    }

    private function selectDisk(string $name): void
    {
        $this->diskName = $name;
        $this->home = $this->diskPath($name);
        if (!is_dir($this->home)) {
            if (!$this->createDiskFiles($name)) {
                echo "Could not boot mxp://$name. Falling back to host home.\n";
                $this->home = $this->hostHome;
            }
        }
        $real = realpath($this->home);
        $this->home = $real === false ? $this->home : $real;
        $this->cwd = $this->home;
        $this->notes = [];
        $this->clear();
    }

    private function printDesktop(): void
    {
        echo $this->logo();
        echo "\nWelcome to MactonishXP Terminal Desktop\n";
        echo "Boot disk: mxp://" . $this->diskName . "\n";
        echo "Type 'help' for apps and commands. No login. No BSODs.\n";
    }

    private function dispatch(string $line): void
    {
        [$command, $args] = $this->splitCommand($line);
        $command = strtolower($command);

        match ($command) {
            'help', '?' => $this->help(),
            'apps' => $this->apps(),
            'about' => $this->about(),
            'clear', 'cls' => $this->clear(),
            'exit', 'quit', 'shutdown' => $this->running = false,
            'pwd' => print($this->cwd . PHP_EOL),
            'cd' => $this->changeDirectory($args),
            'ls', 'dir' => str_starts_with(trim($args), '-') ? $this->runShellCommand($line, $this->defaultShell()) : $this->listFiles($args),
            'cat', 'type' => str_starts_with(trim($args), '-') ? $this->runShellCommand($line, $this->defaultShell()) : $this->catFile($args),
            'write' => $this->writeFile($args),
            'note' => $this->note($args),
            'notes' => $this->listNotes(),
            'calc' => $this->calculator($args),
            'clock', 'time' => $this->clock(),
            'calendar', 'cal' => $this->calendar($args),
            'sysinfo' => $this->sysInfo(),
            'disks' => $this->showDisks(),
            'createdisk' => $this->createDiskCommand($args),
            'finder' => $this->finder($args),
            'notepad' => $this->notepad($args),
            'terminal' => $this->terminal(),
            'shell', 'sh' => $this->runShellCommand($args, '/bin/sh'),
            'bash' => $this->runShellCommand($args, '/bin/bash'),
            'zsh' => $this->runShellCommand($args, '/bin/zsh'),
            default => $this->runShellCommand($line, $this->defaultShell()),
        };
    }

    private function help(): void
    {
        echo <<<TEXT

Built-in apps
  finder [path]       Browse files in a compact table
  notepad [file]      Edit a text file until you type .save or .cancel
  terminal            Show shell command help for this CLI
  calc <expr>         Calculate basic arithmetic
  clock               Show local date and time
  calendar [month]    Show this month or a month number
  note <text>         Save a quick note in memory
  notes               List quick notes
  sysinfo             Show PHP, OS, memory, and disk info
  disks               List available mxp:// disks

Commands
  ls [path]           List files
  cd [path]           Change directory
  pwd                Show current directory
  cat <file>          Print a text file
  write <file>        Write a new text file interactively
  shell <command>     Run a command with /bin/sh
  bash <command>      Run a command with /bin/bash
  zsh <command>       Run a command with /bin/zsh
  clear              Clear the screen
  createdisk <name>  Create another mxp:// disk
  about              About MactonishXP
  shutdown           Exit

Unknown commands are passed to your default shell from the current mxp:// directory.

TEXT;
    }

    private function apps(): void
    {
        echo "Finder, Notepad, Terminal, Calculator, Clock, Calendar, Notes, SysInfo\n";
    }

    private function about(): void
    {
        echo <<<TEXT
MactonishXP
Text-mode desktop shell for PHP CLI.
Boots directly to an ASCII desktop with no password gate and no crash screen.
User disks live as mxp:// names and store files under ~/.mactonishxp/disks.

TEXT;
    }

    private function terminal(): void
    {
        echo "This is the terminal. Run built-ins or normal shell commands at the prompt.\n";
        echo "Unknown commands run through " . $this->defaultShell() . " from the current mxp:// directory.\n";
        echo "Use bash <command>, zsh <command>, or shell <command> to choose a shell.\n";
    }

    private function changeDirectory(string $args): void
    {
        $target = trim($args) === '' ? $this->home : trim($args);
        $path = $this->resolvePath($target);

        if (!is_dir($path)) {
            echo "cd: no such directory: $target\n";
            return;
        }

        $real = realpath($path);
        $this->cwd = $real === false ? $path : $real;
    }

    private function listFiles(string $args): void
    {
        $path = trim($args) === '' ? $this->cwd : $this->resolvePath(trim($args));
        $this->finder($path);
    }

    private function finder(string $args): void
    {
        $path = trim($args) === '' ? $this->cwd : $this->resolvePath(trim($args));

        if (!is_dir($path)) {
            echo "finder: not a directory: $path\n";
            return;
        }

        $items = scandir($path);
        if ($items === false) {
            echo "finder: cannot read directory\n";
            return;
        }

        echo "\nFINDER  " . $this->shortPath($path) . "\n";
        echo str_repeat('-', 58) . "\n";
        printf("%-4s %-8s %-10s %s\n", '#', 'Type', 'Size', 'Name');
        echo str_repeat('-', 58) . "\n";

        $index = 1;
        foreach ($items as $item) {
            if ($item === '.' || $item === '..') {
                continue;
            }

            $full = rtrim($path, DIRECTORY_SEPARATOR) . DIRECTORY_SEPARATOR . $item;
            $type = is_dir($full) ? 'folder' : 'file';
            $size = is_file($full) ? $this->formatBytes((int) filesize($full)) : '-';
            printf("%-4d %-8s %-10s %s\n", $index, $type, $size, $item);
            $index++;
        }

        if ($index === 1) {
            echo "(empty)\n";
        }
    }

    private function catFile(string $args): void
    {
        $file = $this->resolvePath(trim($args));
        if (trim($args) === '' || !is_file($file)) {
            echo "cat: choose a text file\n";
            return;
        }

        $text = file_get_contents($file);
        echo $text === false ? "cat: could not read file\n" : $text . (str_ends_with($text, "\n") ? '' : "\n");
    }

    private function writeFile(string $args): void
    {
        $file = $this->resolvePath(trim($args));
        if (trim($args) === '') {
            echo "write: provide a file path\n";
            return;
        }

        echo "Enter text. Type .save on its own line to save, or .cancel to discard.\n";
        $lines = [];
        while (true) {
            $line = $this->readLine('write> ');
            if ($line === null || trim($line) === '.cancel') {
                echo "discarded\n";
                return;
            }
            if (trim($line) === '.save') {
                break;
            }
            $lines[] = $line;
        }

        $dir = dirname($file);
        if (!is_dir($dir)) {
            echo "write: directory does not exist: $dir\n";
            return;
        }

        file_put_contents($file, implode("\n", $lines) . "\n");
        echo "saved $file\n";
    }

    private function notepad(string $args): void
    {
        $file = trim($args) === '' ? null : $this->resolvePath(trim($args));
        if ($file !== null && is_file($file)) {
            echo "\nCurrent contents of $file:\n";
            echo str_repeat('-', 48) . "\n";
            $this->catFile($file);
            echo str_repeat('-', 48) . "\n";
        }

        if ($file === null) {
            $name = $this->readLine('Save as: ');
            if ($name === null || trim($name) === '') {
                echo "notepad cancelled\n";
                return;
            }
            $file = $this->resolvePath(trim($name));
        }

        $this->writeFile($file);
    }

    private function note(string $args): void
    {
        $text = trim($args);
        if ($text === '') {
            echo "note: add note text after the command\n";
            return;
        }

        $this->notes[] = '[' . date('H:i:s') . '] ' . $text;
        echo "note saved\n";
    }

    private function listNotes(): void
    {
        if ($this->notes === []) {
            echo "No notes yet.\n";
            return;
        }

        foreach ($this->notes as $index => $note) {
            echo ($index + 1) . ". $note\n";
        }
    }

    private function calculator(string $args): void
    {
        $expr = trim($args);
        if ($expr === '') {
            echo "calc: example: calc (2 + 3) * 4\n";
            return;
        }

        if (!preg_match('/^[0-9+\-*\/(). %]+$/', $expr)) {
            echo "calc: only numbers and arithmetic operators are allowed\n";
            return;
        }

        try {
            $result = eval('return ' . $expr . ';');
            echo $expr . " = " . $result . "\n";
        } catch (Throwable $error) {
            echo "calc: invalid expression\n";
        }
    }

    private function clock(): void
    {
        echo date('l, F j, Y g:i:s A T') . "\n";
    }

    private function calendar(string $args): void
    {
        $year = (int) date('Y');
        $month = trim($args) === '' ? (int) date('n') : max(1, min(12, (int) trim($args)));
        $first = mktime(0, 0, 0, $month, 1, $year);
        $days = (int) date('t', $first);
        $offset = (int) date('w', $first);

        echo "\n" . str_pad(date('F Y', $first), 20, ' ', STR_PAD_BOTH) . "\n";
        echo "Su Mo Tu We Th Fr Sa\n";
        echo str_repeat('   ', $offset);

        for ($day = 1; $day <= $days; $day++) {
            printf("%2d ", $day);
            if (($day + $offset) % 7 === 0) {
                echo "\n";
            }
        }
        echo "\n";
    }

    private function sysInfo(): void
    {
        echo "MactonishXP System Information\n";
        echo "PHP:      " . PHP_VERSION . "\n";
        echo "OS:       " . PHP_OS_FAMILY . " / " . php_uname('s') . " " . php_uname('r') . "\n";
        echo "Machine:  " . php_uname('m') . "\n";
        echo "User:     " . (getenv('USER') ?: 'unknown') . "\n";
        echo "Disk URI: mxp://" . $this->diskName . "\n";
        echo "Disk Dir: " . $this->home . "\n";
        echo "CWD:      " . $this->cwd . "\n";
        echo "Memory:   " . ini_get('memory_limit') . "\n";
        $free = @disk_free_space($this->cwd);
        echo "Disk:     " . ($free === false ? 'unknown' : $this->formatBytes((int) $free) . ' free') . "\n";
    }

    private function showDisks(): void
    {
        $disks = $this->availableDisks();
        if ($disks === []) {
            echo "No mxp:// disks found.\n";
            return;
        }

        foreach ($disks as $disk) {
            $marker = $disk === $this->diskName ? '*' : ' ';
            echo "$marker mxp://$disk\n";
        }
    }

    private function createDiskCommand(string $args): void
    {
        $name = $this->sanitizeDiskName($args);
        if ($name === '') {
            echo "createdisk: provide a name, like createdisk school\n";
            return;
        }

        $path = $this->diskPath($name);
        if (is_dir($path)) {
            echo "mxp://$name already exists\n";
            return;
        }

        if ($this->createDiskFiles($name)) {
            echo "created mxp://$name\n";
        }
    }

    private function runShellCommand(string $command, string $shell): void
    {
        $command = trim($command);
        if ($command === '') {
            echo "shell: provide a command\n";
            return;
        }

        if (!is_file($shell) || !is_executable($shell)) {
            echo "shell: $shell is not available\n";
            return;
        }

        $descriptorSpec = [
            0 => STDIN,
            1 => STDOUT,
            2 => STDERR,
        ];

        $process = @proc_open([$shell, '-lc', $command], $descriptorSpec, $pipes, $this->cwd);
        if (!is_resource($process)) {
            echo "shell: could not start $shell\n";
            return;
        }

        $exit = proc_close($process);
        if ($exit !== 0) {
            echo "[exit $exit]\n";
        }
    }

    private function defaultShell(): string
    {
        $shell = getenv('SHELL');
        if (is_string($shell) && $shell !== '' && is_executable($shell)) {
            return $shell;
        }
        if (is_executable('/bin/zsh')) {
            return '/bin/zsh';
        }
        if (is_executable('/bin/bash')) {
            return '/bin/bash';
        }
        return '/bin/sh';
    }

    private function splitCommand(string $line): array
    {
        $parts = preg_split('/\s+/', $line, 2);
        return [$parts[0] ?? '', $parts[1] ?? ''];
    }

    private function resolvePath(string $path): string
    {
        if ($path === '' || $path === '.') {
            return $this->cwd;
        }
        if ($path === '~') {
            return $this->home;
        }
        if (str_starts_with(strtolower($path), 'mxp://')) {
            return $this->resolveDiskUri($path);
        }
        if (str_starts_with($path, '~/')) {
            return $this->home . substr($path, 1);
        }
        if ($path[0] === DIRECTORY_SEPARATOR) {
            return $path;
        }
        return $this->cwd . DIRECTORY_SEPARATOR . $path;
    }

    private function shortPath(string $path): string
    {
        if (str_starts_with($path, $this->home)) {
            $suffix = substr($path, strlen($this->home));
            return 'mxp://' . $this->diskName . $suffix;
        }
        return $path;
    }

    private function availableDisks(): array
    {
        if (!is_dir($this->diskBase)) {
            if (!@mkdir($this->diskBase, 0775, true) && !is_dir($this->diskBase)) {
                return [];
            }
        }

        $items = @scandir($this->diskBase);
        if ($items === false) {
            return [];
        }

        $disks = [];
        foreach ($items as $item) {
            if ($item === '.' || $item === '..') {
                continue;
            }
            if (is_dir($this->diskBase . DIRECTORY_SEPARATOR . $item)) {
                $disks[] = $item;
            }
        }
        sort($disks, SORT_NATURAL | SORT_FLAG_CASE);
        return $disks;
    }

    private function sanitizeDiskName(string $name): string
    {
        $name = trim($name);
        if (str_starts_with(strtolower($name), 'mxp://')) {
            $name = substr($name, 6);
        }
        $name = preg_replace('/[^A-Za-z0-9_-]+/', '-', $name) ?? '';
        $name = trim($name, '-_');
        return strtolower($name);
    }

    private function diskPath(string $name): string
    {
        return $this->diskBase . DIRECTORY_SEPARATOR . $name;
    }

    private function createDiskFiles(string $name): bool
    {
        $path = $this->diskPath($name);
        if (!is_dir($path) && !@mkdir($path, 0775, true) && !is_dir($path)) {
            echo "Could not create mxp://$name at $path\n";
            return false;
        }

        $documents = $path . DIRECTORY_SEPARATOR . 'Documents';
        if (!is_dir($documents) && !@mkdir($documents, 0775, true) && !is_dir($documents)) {
            echo "Could not create Documents folder on mxp://$name\n";
            return false;
        }

        $readme = $path . DIRECTORY_SEPARATOR . 'README.txt';
        if (!is_file($readme)) {
            @file_put_contents($readme, "Welcome to mxp://$name\nThis is your MactonishXP user disk.\n");
        }

        return true;
    }

    private function resolveDiskUri(string $uri): string
    {
        $rest = substr($uri, 6);
        [$name, $path] = array_pad(explode('/', $rest, 2), 2, '');
        $name = $this->sanitizeDiskName($name);
        if ($name === '') {
            return $this->home;
        }

        $diskPath = $this->diskPath($name);
        if ($path === '') {
            $real = realpath($diskPath);
            return $real === false ? $diskPath : $real;
        }
        $resolved = $diskPath . DIRECTORY_SEPARATOR . str_replace('/', DIRECTORY_SEPARATOR, $path);
        $real = realpath($resolved);
        return $real === false ? $resolved : $real;
    }

    private function formatBytes(int $bytes): string
    {
        $units = ['B', 'KB', 'MB', 'GB', 'TB'];
        $value = (float) $bytes;
        $unit = 0;
        while ($value >= 1024 && $unit < count($units) - 1) {
            $value /= 1024;
            $unit++;
        }
        return sprintf($unit === 0 ? '%.0f%s' : '%.1f%s', $value, $units[$unit]);
    }

    private function readLine(string $prompt): ?string
    {
        $interactive = function_exists('stream_isatty') && stream_isatty(STDIN);
        if ($interactive && function_exists('readline')) {
            $line = readline($prompt);
            if ($line === false) {
                return null;
            }
            if (trim($line) !== '') {
                readline_add_history($line);
            }
            return $line;
        }

        echo $prompt;
        $line = fgets(STDIN);
        return $line === false ? null : rtrim($line, "\r\n");
    }

    private function clear(): void
    {
        echo "\033[2J\033[H";
    }

    private function logo(): string
    {
        return <<<'LOGO'
 __  __            _              _     _       _    __  ______
|  \/  | __ _  ___| |_ ___  _ __ (_)___| |__   \ \  / / |  _ \ \
| |\/| |/ _` |/ __| __/ _ \| '_ \| / __| '_ \   \ \/ /  | |_) | |
| |  | | (_| | (__| || (_) | | | | \__ \ | | |  / /\ \  |  __/| |
|_|  |_|\__,_|\___|\__\___/|_| |_|_|___/_| |_| /_/  \_\ |_|   |_|

              M A C T O N I S H X P
          direct-to-terminal personal shell
LOGO;
    }
}

(new MactonishXP())->run();
