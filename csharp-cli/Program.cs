using System.Diagnostics;
using System.Globalization;
using System.Text;
using System.Text.RegularExpressions;

Console.OutputEncoding = Encoding.UTF8;
Console.InputEncoding = Encoding.UTF8;

new DellNano().Run();

internal sealed class DellNano
{
    private readonly string hostHome;
    private readonly string diskBase;
    private readonly List<string> notes = [];
    private string home;
    private string cwd;
    private string diskName = "main";
    private bool running = true;
    private bool unicodeLogo = true;

    public DellNano()
    {
        hostHome = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
        if (string.IsNullOrWhiteSpace(hostHome))
        {
            hostHome = Directory.GetCurrentDirectory();
        }

        diskBase = Path.Combine(hostHome, ".dell-nano", "disks");
        home = DiskPath(diskName);
        cwd = home;
    }

    public void Run()
    {
        LoadingScreen();
        ChooseDisk();
        PrintDesktop();

        while (running)
        {
            Console.Write("\n" + ShortPath(cwd) + "$: ");
            string? line = Console.ReadLine();
            if (line is null)
            {
                Console.WriteLine();
                break;
            }

            line = line.Trim();
            if (line.Length == 0)
            {
                continue;
            }

            Dispatch(line);
        }

        Console.WriteLine("Dell Nano has shut down.");
    }

    private void LoadingScreen()
    {
        Clear();
        Console.WriteLine(Logo());
        Console.WriteLine();
        Console.WriteLine("Dell Nano BIOS A09");
        Console.WriteLine("C# text mode boot shell");
        Console.WriteLine();

        foreach (string step in new[]
        {
            "Checking memory",
            "Detecting keyboard",
            "Mounting local disk",
            "Loading command shell",
            "Starting desktop services"
        })
        {
            Console.Write((step + " ").PadRight(32, '.'));
            Thread.Sleep(140);
            Console.WriteLine(" OK");
        }

        Console.Write("\nPress Enter to continue...");
        Console.ReadLine();
        Clear();
    }

    private void ChooseDisk()
    {
        Clear();
        Console.WriteLine(Logo());
        Console.WriteLine();
        Console.WriteLine("Disk selection");
        Console.WriteLine("Choose a user disk, or create a new one with an mxp:// name.");
        Console.WriteLine();

        List<string> disks = AvailableDisks();
        if (disks.Count == 0)
        {
            Console.WriteLine("No disks found yet.");
        }
        else
        {
            for (int i = 0; i < disks.Count; i++)
            {
                Console.WriteLine($"  {i + 1}) mxp://{disks[i]}");
            }
        }
        Console.WriteLine("  N) Create new disk");
        Console.WriteLine();

        while (true)
        {
            Console.Write("Boot disk: ");
            string choice = (Console.ReadLine() ?? string.Empty).Trim();

            if (choice.Length == 0)
            {
                if (disks.Count == 0)
                {
                    CreateDiskInteractive();
                }
                else
                {
                    SelectDisk(disks[0]);
                }
                return;
            }

            if (choice.Equals("n", StringComparison.OrdinalIgnoreCase) ||
                choice.Equals("new", StringComparison.OrdinalIgnoreCase))
            {
                CreateDiskInteractive();
                return;
            }

            if (choice.StartsWith("mxp://", StringComparison.OrdinalIgnoreCase))
            {
                string name = SanitizeDiskName(choice[6..]);
                if (name.Length > 0 && Directory.Exists(DiskPath(name)))
                {
                    SelectDisk(name);
                    return;
                }
            }

            if (int.TryParse(choice, out int number) && number >= 1 && number <= disks.Count)
            {
                SelectDisk(disks[number - 1]);
                return;
            }

            Console.WriteLine("Choose a listed number, mxp://name, or N.");
        }
    }

    private void CreateDiskInteractive()
    {
        while (true)
        {
            Console.Write("New disk name: ");
            string name = SanitizeDiskName(Console.ReadLine() ?? "main");
            if (name.Length == 0)
            {
                Console.WriteLine("Use letters, numbers, dashes, or underscores.");
                continue;
            }

            if (!Directory.Exists(DiskPath(name)) && !CreateDiskFiles(name))
            {
                continue;
            }

            SelectDisk(name);
            return;
        }
    }

    private void SelectDisk(string name)
    {
        diskName = name;
        home = DiskPath(name);
        if (!Directory.Exists(home) && !CreateDiskFiles(name))
        {
            Console.WriteLine($"Could not boot mxp://{name}. Falling back to host home.");
            home = hostHome;
        }

        home = FullPath(home);
        cwd = home;
        notes.Clear();
        Clear();
    }

    private void PrintDesktop()
    {
        Console.WriteLine(Logo());
        Console.WriteLine();
        Console.WriteLine("Welcome to Dell Nano Terminal Desktop");
        Console.WriteLine($"Boot disk: mxp://{diskName}");
        Console.WriteLine("Type 'help' for apps and commands. No login. No BSODs.");
    }

    private void Dispatch(string line)
    {
        (string rawCommand, string args) = SplitCommand(line);
        string command = rawCommand.ToLowerInvariant();

        switch (command)
        {
            case "help":
            case "?":
                Help();
                break;
            case "apps":
                Console.WriteLine("Finder, Notepad, Terminal, Calculator, Clock, Calendar, Notes, SysInfo");
                break;
            case "about":
                About();
                break;
            case "clear":
            case "cls":
                Clear();
                break;
            case "exit":
            case "quit":
            case "shutdown":
                running = false;
                break;
            case "pwd":
                Console.WriteLine(cwd);
                break;
            case "cd":
                ChangeDirectory(args);
                break;
            case "ls":
            case "dir":
                if (args.TrimStart().StartsWith('-'))
                {
                    RunShellCommand(line, DefaultShell());
                }
                else
                {
                    ListFiles(args);
                }
                break;
            case "cat":
            case "type":
                if (args.TrimStart().StartsWith('-'))
                {
                    RunShellCommand(line, DefaultShell());
                }
                else
                {
                    CatFile(args);
                }
                break;
            case "write":
                WriteFile(args);
                break;
            case "note":
                Note(args);
                break;
            case "notes":
                ListNotes();
                break;
            case "calc":
                Calculator(args);
                break;
            case "clock":
            case "time":
                Console.WriteLine(DateTime.Now.ToString("dddd, MMMM d, yyyy h:mm:ss tt zzz", CultureInfo.CurrentCulture));
                break;
            case "calendar":
            case "cal":
                Calendar(args);
                break;
            case "sysinfo":
                SysInfo();
                break;
            case "disks":
                ShowDisks();
                break;
            case "createdisk":
                CreateDiskCommand(args);
                break;
            case "finder":
                Finder(args);
                break;
            case "notepad":
                Notepad(args);
                break;
            case "terminal":
                Terminal();
                break;
            case "logo":
                LogoCommand(args);
                break;
            case "shell":
            case "sh":
                RunShellCommand(args, "/bin/sh");
                break;
            case "bash":
                RunShellCommand(args, "/bin/bash");
                break;
            case "zsh":
                RunShellCommand(args, "/bin/zsh");
                break;
            default:
                RunShellCommand(line, DefaultShell());
                break;
        }
    }

    private static (string Command, string Args) SplitCommand(string line)
    {
        string[] parts = line.Split([' ', '\t'], 2, StringSplitOptions.RemoveEmptyEntries);
        return (parts.ElementAtOrDefault(0) ?? string.Empty, parts.ElementAtOrDefault(1) ?? string.Empty);
    }

    private static void Help()
    {
        Console.WriteLine("""

Built-in apps
  finder [path]       Browse files in a compact table
  notepad [file]      Edit a text file until you type .save or .cancel
  terminal            Show shell command help for this CLI
  calc <expr>         Calculate basic arithmetic
  clock               Show local date and time
  calendar [month]    Show this month or a month number
  note <text>         Save a quick note in memory
  notes               List quick notes
  sysinfo             Show C#, OS, memory, and disk info
  disks               List available mxp:// disks

Commands
  ls [path]           List files
  cd [path]           Change directory
  pwd                 Show current directory
  cat <file>          Print a text file
  write <file>        Write a new text file interactively
  logo ascii          Switch to an ASCII logo
  logo unicode        Switch to the Unicode logo
  shell <command>     Run a command with /bin/sh
  bash <command>      Run a command with /bin/bash
  zsh <command>       Run a command with /bin/zsh
  clear               Clear the screen
  createdisk <name>   Create another mxp:// disk
  about               About Dell Nano
  shutdown            Exit

Unknown commands are passed to your default shell from the current mxp:// directory.

""");
    }

    private static void About()
    {
        Console.WriteLine("""
Dell Nano
C# text-mode desktop shell.
Boots to a disk picker, supports mxp:// user disks, and runs built-ins plus shell commands.
User disks live as mxp:// names and store files under ~/.dell-nano/disks.

""");
    }

    private void Terminal()
    {
        Console.WriteLine("This is the terminal. Run built-ins or normal shell commands at the prompt.");
        Console.WriteLine($"Unknown commands run through {DefaultShell()} from the current mxp:// directory.");
        Console.WriteLine("Use bash <command>, zsh <command>, or shell <command> to choose a shell.");
    }

    private void ChangeDirectory(string args)
    {
        string target = string.IsNullOrWhiteSpace(args) ? home : args.Trim();
        string path = ResolvePath(target);
        if (!Directory.Exists(path))
        {
            Console.WriteLine($"cd: no such directory: {target}");
            return;
        }
        cwd = FullPath(path);
    }

    private void ListFiles(string args)
    {
        string path = string.IsNullOrWhiteSpace(args) ? cwd : ResolvePath(args.Trim());
        Finder(path);
    }

    private void Finder(string args)
    {
        string path = string.IsNullOrWhiteSpace(args) ? cwd : ResolvePath(args.Trim());
        if (!Directory.Exists(path))
        {
            Console.WriteLine($"finder: not a directory: {path}");
            return;
        }

        Console.WriteLine();
        Console.WriteLine("FINDER  " + ShortPath(FullPath(path)));
        Console.WriteLine(new string('-', 58));
        Console.WriteLine("{0,-4} {1,-8} {2,-10} {3}", "#", "Type", "Size", "Name");
        Console.WriteLine(new string('-', 58));

        int index = 1;
        foreach (string item in Directory.EnumerateFileSystemEntries(path).OrderBy(Path.GetFileName, StringComparer.OrdinalIgnoreCase))
        {
            string name = Path.GetFileName(item);
            string type = Directory.Exists(item) ? "folder" : "file";
            string size = File.Exists(item) ? FormatBytes(new FileInfo(item).Length) : "-";
            Console.WriteLine("{0,-4} {1,-8} {2,-10} {3}", index, type, size, name);
            index++;
        }

        if (index == 1)
        {
            Console.WriteLine("(empty)");
        }
    }

    private void CatFile(string args)
    {
        if (string.IsNullOrWhiteSpace(args))
        {
            Console.WriteLine("cat: choose a text file");
            return;
        }

        string file = ResolvePath(args.Trim());
        if (!File.Exists(file))
        {
            Console.WriteLine("cat: choose a text file");
            return;
        }

        Console.Write(File.ReadAllText(file));
        if (new FileInfo(file).Length > 0)
        {
            Console.WriteLine();
        }
    }

    private void WriteFile(string args)
    {
        if (string.IsNullOrWhiteSpace(args))
        {
            Console.WriteLine("write: provide a file path");
            return;
        }

        string file = ResolvePath(args.Trim());
        Console.WriteLine("Enter text. Type .save on its own line to save, or .cancel to discard.");
        List<string> lines = [];
        while (true)
        {
            Console.Write("write> ");
            string? line = Console.ReadLine();
            if (line is null || line.Trim() == ".cancel")
            {
                Console.WriteLine("discarded");
                return;
            }
            if (line.Trim() == ".save")
            {
                break;
            }
            lines.Add(line);
        }

        string? directory = Path.GetDirectoryName(file);
        if (string.IsNullOrEmpty(directory) || !Directory.Exists(directory))
        {
            Console.WriteLine($"write: directory does not exist: {directory}");
            return;
        }

        File.WriteAllText(file, string.Join(Environment.NewLine, lines) + Environment.NewLine);
        Console.WriteLine($"saved {file}");
    }

    private void Notepad(string args)
    {
        string? file = string.IsNullOrWhiteSpace(args) ? null : ResolvePath(args.Trim());
        if (file is not null && File.Exists(file))
        {
            Console.WriteLine($"\nCurrent contents of {file}:");
            Console.WriteLine(new string('-', 48));
            CatFile(file);
            Console.WriteLine(new string('-', 48));
        }

        if (file is null)
        {
            Console.Write("Save as: ");
            string? name = Console.ReadLine();
            if (string.IsNullOrWhiteSpace(name))
            {
                Console.WriteLine("notepad cancelled");
                return;
            }
            file = ResolvePath(name.Trim());
        }

        WriteFile(file);
    }

    private void Note(string args)
    {
        string text = args.Trim();
        if (text.Length == 0)
        {
            Console.WriteLine("note: add note text after the command");
            return;
        }

        notes.Add("[" + DateTime.Now.ToString("HH:mm:ss", CultureInfo.InvariantCulture) + "] " + text);
        Console.WriteLine("note saved");
    }

    private void ListNotes()
    {
        if (notes.Count == 0)
        {
            Console.WriteLine("No notes yet.");
            return;
        }

        for (int i = 0; i < notes.Count; i++)
        {
            Console.WriteLine($"{i + 1}. {notes[i]}");
        }
    }

    private static void Calculator(string args)
    {
        string expression = args.Trim();
        if (expression.Length == 0)
        {
            Console.WriteLine("calc: example: calc (2 + 3) * 4");
            return;
        }

        if (!Regex.IsMatch(expression, "^[0-9+\\-*/(). %]+$"))
        {
            Console.WriteLine("calc: only numbers and arithmetic operators are allowed");
            return;
        }

        try
        {
            double result = new ExpressionParser(expression).Parse();
            Console.WriteLine(expression + " = " + TrimNumber(result));
        }
        catch
        {
            Console.WriteLine("calc: invalid expression");
        }
    }

    private static void Calendar(string args)
    {
        int year = DateTime.Now.Year;
        int month = DateTime.Now.Month;
        if (int.TryParse(args.Trim(), out int requested))
        {
            month = Math.Clamp(requested, 1, 12);
        }

        DateTime first = new(year, month, 1);
        int days = DateTime.DaysInMonth(year, month);
        int offset = (int)first.DayOfWeek;

        Console.WriteLine();
        Console.WriteLine(first.ToString("MMMM yyyy", CultureInfo.CurrentCulture).PadLeft(14).PadRight(20));
        Console.WriteLine("Su Mo Tu We Th Fr Sa");
        Console.Write(new string(' ', offset * 3));
        for (int day = 1; day <= days; day++)
        {
            Console.Write(day.ToString().PadLeft(2) + " ");
            if ((day + offset) % 7 == 0)
            {
                Console.WriteLine();
            }
        }
        Console.WriteLine();
    }

    private void SysInfo()
    {
        Console.WriteLine("Dell Nano System Information");
        Console.WriteLine(".NET:     " + Environment.Version);
        Console.WriteLine("OS:       " + Environment.OSVersion);
        Console.WriteLine("Machine:  " + Environment.MachineName);
        Console.WriteLine("User:     " + Environment.UserName);
        Console.WriteLine("Disk URI: mxp://" + diskName);
        Console.WriteLine("Disk Dir: " + home);
        Console.WriteLine("CWD:      " + cwd);
        DriveInfo? drive = DriveInfo.GetDrives().FirstOrDefault(d => cwd.StartsWith(d.RootDirectory.FullName, StringComparison.Ordinal));
        Console.WriteLine("Disk:     " + (drive is null ? "unknown" : FormatBytes(drive.AvailableFreeSpace) + " free"));
    }

    private void ShowDisks()
    {
        List<string> disks = AvailableDisks();
        if (disks.Count == 0)
        {
            Console.WriteLine("No mxp:// disks found.");
            return;
        }

        foreach (string disk in disks)
        {
            Console.WriteLine((disk == diskName ? "*" : " ") + " mxp://" + disk);
        }
    }

    private void CreateDiskCommand(string args)
    {
        string name = SanitizeDiskName(args);
        if (name.Length == 0)
        {
            Console.WriteLine("createdisk: provide a name, like createdisk school");
            return;
        }

        if (Directory.Exists(DiskPath(name)))
        {
            Console.WriteLine($"mxp://{name} already exists");
            return;
        }

        if (CreateDiskFiles(name))
        {
            Console.WriteLine($"created mxp://{name}");
        }
    }

    private void LogoCommand(string args)
    {
        string mode = args.Trim().ToLowerInvariant();
        if (mode is "ascii" or "a")
        {
            unicodeLogo = false;
            Console.WriteLine(Logo());
            return;
        }
        if (mode is "unicode" or "u")
        {
            unicodeLogo = true;
            Console.WriteLine(Logo());
            return;
        }
        Console.WriteLine("logo: use 'logo ascii' or 'logo unicode'");
    }

    private void RunShellCommand(string command, string shell)
    {
        command = command.Trim();
        if (command.Length == 0)
        {
            Console.WriteLine("shell: provide a command");
            return;
        }

        if (!File.Exists(shell))
        {
            Console.WriteLine($"shell: {shell} is not available");
            return;
        }

        try
        {
            using Process process = new();
            process.StartInfo.FileName = shell;
            process.StartInfo.ArgumentList.Add("-lc");
            process.StartInfo.ArgumentList.Add(command);
            process.StartInfo.WorkingDirectory = cwd;
            process.StartInfo.UseShellExecute = false;
            process.Start();
            process.WaitForExit();
            if (process.ExitCode != 0)
            {
                Console.WriteLine($"[exit {process.ExitCode}]");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine("shell: " + ex.Message);
        }
    }

    private static string DefaultShell()
    {
        string? shell = Environment.GetEnvironmentVariable("SHELL");
        if (!string.IsNullOrWhiteSpace(shell) && File.Exists(shell))
        {
            return shell;
        }
        if (File.Exists("/bin/zsh")) return "/bin/zsh";
        if (File.Exists("/bin/bash")) return "/bin/bash";
        return "/bin/sh";
    }

    private string ResolvePath(string path)
    {
        if (path.Length == 0 || path == ".") return cwd;
        if (path == "~") return home;
        if (path.StartsWith("mxp://", StringComparison.OrdinalIgnoreCase)) return ResolveDiskUri(path);
        if (path.StartsWith("~/", StringComparison.Ordinal)) return Path.Combine(home, path[2..]);
        if (Path.IsPathRooted(path)) return path;
        return Path.Combine(cwd, path);
    }

    private string ShortPath(string path)
    {
        string full = FullPath(path);
        if (full.StartsWith(home, StringComparison.Ordinal))
        {
            string suffix = full[home.Length..].Replace(Path.DirectorySeparatorChar, '/');
            return "mxp://" + diskName + suffix;
        }
        return path;
    }

    private List<string> AvailableDisks()
    {
        Directory.CreateDirectory(diskBase);
        return Directory.EnumerateDirectories(diskBase)
            .Select(Path.GetFileName)
            .Where(name => !string.IsNullOrWhiteSpace(name))
            .Cast<string>()
            .OrderBy(name => name, StringComparer.OrdinalIgnoreCase)
            .ToList();
    }

    private string SanitizeDiskName(string name)
    {
        name = name.Trim();
        if (name.StartsWith("mxp://", StringComparison.OrdinalIgnoreCase))
        {
            name = name[6..];
        }
        name = Regex.Replace(name, "[^A-Za-z0-9_-]+", "-").Trim('-', '_').ToLowerInvariant();
        return name;
    }

    private string DiskPath(string name) => Path.Combine(diskBase, name);

    private bool CreateDiskFiles(string name)
    {
        try
        {
            string path = DiskPath(name);
            Directory.CreateDirectory(path);
            Directory.CreateDirectory(Path.Combine(path, "Documents"));
            string readme = Path.Combine(path, "README.txt");
            if (!File.Exists(readme))
            {
                File.WriteAllText(readme, $"Welcome to mxp://{name}\nThis is your Dell Nano user disk.\n");
            }
            return true;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Could not create mxp://{name}: {ex.Message}");
            return false;
        }
    }

    private string ResolveDiskUri(string uri)
    {
        string rest = uri[6..];
        string[] parts = rest.Split('/', 2);
        string name = SanitizeDiskName(parts[0]);
        if (name.Length == 0)
        {
            return home;
        }
        string path = DiskPath(name);
        if (parts.Length == 2 && parts[1].Length > 0)
        {
            path = Path.Combine(path, parts[1].Replace('/', Path.DirectorySeparatorChar));
        }
        return Directory.Exists(path) || File.Exists(path) ? FullPath(path) : path;
    }

    private static string FullPath(string path)
    {
        try
        {
            return Path.GetFullPath(path);
        }
        catch
        {
            return path;
        }
    }

    private static string FormatBytes(long bytes)
    {
        string[] units = ["B", "KB", "MB", "GB", "TB"];
        double value = bytes;
        int unit = 0;
        while (value >= 1024 && unit < units.Length - 1)
        {
            value /= 1024;
            unit++;
        }
        return unit == 0 ? $"{value:0}{units[unit]}" : $"{value:0.0}{units[unit]}";
    }

    private static string TrimNumber(double value)
    {
        return value.ToString("0.######", CultureInfo.InvariantCulture);
    }

    private string Logo() => unicodeLogo ? UnicodeLogo : AsciiLogo;

    private static void Clear()
    {
        Console.Write("\u001b[2J\u001b[H");
    }

    private const string UnicodeLogo = """
⠀⠀⠀⠀⠀⠀⠀⢀⣠⣤⣤⣶⣶⣶⣶⣤⣤⣄⡀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⢀⣤⣾⣿⣿⠿⠟⠛⠛⠛⠛⠻⠿⣿⣿⣷⣤⡀⠀⠀⠀⠀
⠀⠀⠀⣴⣿⣿⠟⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⠻⣿⣿⣦⠀⠀⠀
⠀⢀⣾⣿⡿⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢿⣿⣷⡀⠀
⠀⣾⣿⡟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢻⣿⣷⠀
⢠⣿⣿⠁⢰⣶⣶⣶⣄⢀⣴⡾⣂⣀⢰⣶⠀⠀⣶⣶⠀⠀⠈⣿⣿⡄
⢸⣿⣿⠀⢸⣿⡇⣸⣿⣿⣧⣾⢋⣵⢾⣿⠀⠀⣿⣿⠀⠀⠀⣿⣿⡇
⠘⣿⣿⡀⠸⠿⠿⠟⠋⠈⠛⢿⠟⠁⠸⠿⠿⠿⠻⠿⠿⠇⢀⣿⣿⠃
⠀⢿⣿⣧⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣼⣿⡿⠀
⠀⠈⢿⣿⣷⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣾⣿⡿⠁⠀
⠀⠀⠀⠻⣿⣿⣦⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣴⣿⣿⠟⠀⠀⠀
⠀⠀⠀⠀⠈⠛⢿⣿⣿⣶⣦⣤⣤⣤⣤⣴⣶⣿⣿⡿⠛⠁⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠈⠙⠛⠛⠿⠿⠿⠿⠛⠛⠋⠁⠀⠀⠀⠀⠀⠀⠀

                    D E L L   N A N O
          C# direct-to-terminal personal shell
""";

    private const string AsciiLogo = """
       ______    _______  __      __
      |  _ \ \  |  _____| \ \    / /
      | | | | | | |____    \ \  / /
      | | | | | |  ____|    \ \/ /
      | |_| | | | |_____    / /\ \
      |____/ /  |_______|  /_/  \_\

                    D E L L   N A N O
          C# direct-to-terminal personal shell
""";

    private sealed class ExpressionParser
    {
        private readonly string text;
        private int pos;

        public ExpressionParser(string text)
        {
            this.text = text;
        }

        public double Parse()
        {
            double value = ParseExpression();
            SkipSpaces();
            if (pos != text.Length)
            {
                throw new FormatException();
            }
            return value;
        }

        private double ParseExpression()
        {
            double value = ParseTerm();
            while (true)
            {
                SkipSpaces();
                if (Match('+')) value += ParseTerm();
                else if (Match('-')) value -= ParseTerm();
                else return value;
            }
        }

        private double ParseTerm()
        {
            double value = ParseFactor();
            while (true)
            {
                SkipSpaces();
                if (Match('*')) value *= ParseFactor();
                else if (Match('/')) value /= ParseFactor();
                else if (Match('%')) value %= ParseFactor();
                else return value;
            }
        }

        private double ParseFactor()
        {
            SkipSpaces();
            if (Match('+')) return ParseFactor();
            if (Match('-')) return -ParseFactor();
            if (Match('('))
            {
                double value = ParseExpression();
                if (!Match(')')) throw new FormatException();
                return value;
            }

            int start = pos;
            while (pos < text.Length && (char.IsDigit(text[pos]) || text[pos] == '.'))
            {
                pos++;
            }
            if (start == pos) throw new FormatException();
            return double.Parse(text[start..pos], CultureInfo.InvariantCulture);
        }

        private bool Match(char expected)
        {
            SkipSpaces();
            if (pos >= text.Length || text[pos] != expected) return false;
            pos++;
            return true;
        }

        private void SkipSpaces()
        {
            while (pos < text.Length && char.IsWhiteSpace(text[pos]))
            {
                pos++;
            }
        }
    }
}
