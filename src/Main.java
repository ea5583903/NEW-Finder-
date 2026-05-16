import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            DesktopFrame frame = new DesktopFrame();
            frame.setVisible(true);
        });
    }
}

class DesktopFrame extends JFrame {
    private static final Color DESKTOP = new Color(104, 144, 152);
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JDesktopPane desktop = new JDesktopPane();
    private FinderFrame finder;
    private PRunFrame pRun;
    private TerminalFrame terminal;
    private NotepadFrame notepad;
    private AppCreatorFrame appCreator;
    private FileEditorFrame fileEditor;
    private BrowserFrame browser;
    private CalculatorFrame calculator;
    private ClockFrame clock;
    private SystemInfoFrame systemInfo;
    private HelpFrame help;

    DesktopFrame() {
        super("Mactonish System");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        setJMenuBar(buildSystemMenu());

        desktop.setBackground(DESKTOP);
        desktop.setLayout(null);
        setContentPane(desktop);

        addDesktopIcon("Finder", 34, 34, this::openFinder);
        addDesktopIcon("P-Run", 34, 134, this::openPRun);
        addDesktopIcon("Terminal", 34, 234, this::openTerminal);
        addDesktopIcon("Notepad", 34, 334, this::openNotepad);
        addDesktopIcon("App Maker", 34, 434, this::openAppCreator);
        addDesktopIcon("File Edit", 34, 534, this::openFileEditor);
        addDesktopIcon("Browser", 34, 634, this::openBrowser);
        addDesktopIcon("Calculator", 140, 34, this::openCalculator);
        addDesktopIcon("Clock", 140, 134, this::openClock);
        addDesktopIcon("Sys Info", 140, 234, this::openSystemInfo);
        addDesktopIcon("Help", 140, 334, this::openHelp);
        addDeskPlate();
        openFinder();
    }

    private JMenuBar buildSystemMenu() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(PAPER);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));

        JMenu apple = new JMenu("Mactonish");
        JMenuItem about = new JMenuItem("About This Computer");
        about.addActionListener(event -> JOptionPane.showMessageDialog(
                this,
                "Mactonish System 1.0\nFinder, P-Run, Terminal, Notepad, App Maker, File Edit, Browser, Calculator, Clock, Sys Info, and Help are built in.",
                "About This Computer",
                JOptionPane.INFORMATION_MESSAGE
        ));
        JMenuItem quit = new JMenuItem("Shut Down");
        quit.addActionListener(event -> dispose());
        apple.add(about);
        apple.add(quit);

        JMenu apps = new JMenu("Apps");
        JMenuItem finderItem = new JMenuItem("Finder");
        finderItem.addActionListener(event -> openFinder());
        JMenuItem pRunItem = new JMenuItem("P-Run");
        pRunItem.addActionListener(event -> openPRun());
        JMenuItem terminalItem = new JMenuItem("Terminal");
        terminalItem.addActionListener(event -> openTerminal());
        JMenuItem notepadItem = new JMenuItem("Notepad");
        notepadItem.addActionListener(event -> openNotepad());
        JMenuItem appCreatorItem = new JMenuItem("App Maker");
        appCreatorItem.addActionListener(event -> openAppCreator());
        JMenuItem fileEditorItem = new JMenuItem("File Edit");
        fileEditorItem.addActionListener(event -> openFileEditor());
        JMenuItem browserItem = new JMenuItem("Browser");
        browserItem.addActionListener(event -> openBrowser());
        JMenuItem calculatorItem = new JMenuItem("Calculator");
        calculatorItem.addActionListener(event -> openCalculator());
        JMenuItem clockItem = new JMenuItem("Clock");
        clockItem.addActionListener(event -> openClock());
        JMenuItem systemInfoItem = new JMenuItem("Sys Info");
        systemInfoItem.addActionListener(event -> openSystemInfo());
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(event -> openHelp());
        apps.add(finderItem);
        apps.add(pRunItem);
        apps.add(terminalItem);
        apps.add(notepadItem);
        apps.add(appCreatorItem);
        apps.add(fileEditorItem);
        apps.add(browserItem);
        apps.add(calculatorItem);
        apps.add(clockItem);
        apps.add(systemInfoItem);
        apps.add(helpItem);

        JMenu view = new JMenu("Desktop");
        JMenuItem arrange = new JMenuItem("Clean Up Icons");
        arrange.addActionListener(event -> arrangeIcons());
        view.add(arrange);

        bar.add(apple);
        bar.add(apps);
        bar.add(view);
        return bar;
    }

    private void addDesktopIcon(String label, int x, int y, Runnable action) {
        JButton icon = new JButton("<html><center>[ ]<br>" + label + "</center></html>");
        icon.setBounds(x, y, 92, 76);
        icon.setHorizontalTextPosition(SwingConstants.CENTER);
        icon.setVerticalTextPosition(SwingConstants.BOTTOM);
        icon.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        icon.setForeground(INK);
        icon.setBackground(PAPER);
        icon.setFocusPainted(false);
        icon.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INK, 2),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        icon.addActionListener(event -> action.run());
        desktop.add(icon, JLayeredPane.DEFAULT_LAYER);
    }

    private void addDeskPlate() {
        JLabel plate = new JLabel("  Welcome to Mactonish. Double-click Finder to view files.  ");
        plate.setOpaque(true);
        plate.setBackground(PAPER);
        plate.setForeground(INK);
        plate.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        plate.setBorder(BorderFactory.createLineBorder(INK, 2));
        plate.setBounds(160, 34, 420, 30);
        desktop.add(plate, JLayeredPane.DEFAULT_LAYER);
    }

    private void openFinder() {
        try {
            if (finder == null || finder.isClosed()) {
                finder = new FinderFrame();
                desktop.add(finder, JLayeredPane.PALETTE_LAYER);
                finder.setVisible(true);
            }
            finder.setIcon(false);
            finder.moveToFront();
            finder.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openPRun() {
        try {
            if (pRun == null || pRun.isClosed()) {
                pRun = new PRunFrame();
                desktop.add(pRun, JLayeredPane.PALETTE_LAYER);
                pRun.setVisible(true);
            }
            pRun.setIcon(false);
            pRun.moveToFront();
            pRun.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openTerminal() {
        try {
            if (terminal == null || terminal.isClosed()) {
                terminal = new TerminalFrame();
                desktop.add(terminal, JLayeredPane.PALETTE_LAYER);
                terminal.setVisible(true);
            }
            terminal.setIcon(false);
            terminal.moveToFront();
            terminal.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openNotepad() {
        try {
            if (notepad == null || notepad.isClosed()) {
                notepad = new NotepadFrame();
                desktop.add(notepad, JLayeredPane.PALETTE_LAYER);
                notepad.setVisible(true);
            }
            notepad.setIcon(false);
            notepad.moveToFront();
            notepad.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openAppCreator() {
        try {
            if (appCreator == null || appCreator.isClosed()) {
                appCreator = new AppCreatorFrame();
                desktop.add(appCreator, JLayeredPane.PALETTE_LAYER);
                appCreator.setVisible(true);
            }
            appCreator.setIcon(false);
            appCreator.moveToFront();
            appCreator.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openFileEditor() {
        try {
            if (fileEditor == null || fileEditor.isClosed()) {
                fileEditor = new FileEditorFrame();
                desktop.add(fileEditor, JLayeredPane.PALETTE_LAYER);
                fileEditor.setVisible(true);
            }
            fileEditor.setIcon(false);
            fileEditor.moveToFront();
            fileEditor.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openBrowser() {
        try {
            if (browser == null || browser.isClosed()) {
                browser = new BrowserFrame();
                desktop.add(browser, JLayeredPane.PALETTE_LAYER);
                browser.setVisible(true);
            }
            browser.setIcon(false);
            browser.moveToFront();
            browser.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openCalculator() {
        try {
            if (calculator == null || calculator.isClosed()) {
                calculator = new CalculatorFrame();
                desktop.add(calculator, JLayeredPane.PALETTE_LAYER);
                calculator.setVisible(true);
            }
            calculator.setIcon(false);
            calculator.moveToFront();
            calculator.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openClock() {
        try {
            if (clock == null || clock.isClosed()) {
                clock = new ClockFrame();
                desktop.add(clock, JLayeredPane.PALETTE_LAYER);
                clock.setVisible(true);
            }
            clock.setIcon(false);
            clock.moveToFront();
            clock.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openSystemInfo() {
        try {
            if (systemInfo == null || systemInfo.isClosed()) {
                systemInfo = new SystemInfoFrame();
                desktop.add(systemInfo, JLayeredPane.PALETTE_LAYER);
                systemInfo.setVisible(true);
            }
            systemInfo.setIcon(false);
            systemInfo.moveToFront();
            systemInfo.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openHelp() {
        try {
            if (help == null || help.isClosed()) {
                help = new HelpFrame();
                desktop.add(help, JLayeredPane.PALETTE_LAYER);
                help.setVisible(true);
            }
            help.setIcon(false);
            help.moveToFront();
            help.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void arrangeIcons() {
        int y = 34;
        for (Component component : desktop.getComponents()) {
            if (component instanceof JButton) {
                component.setBounds(34, y, 92, 76);
                y += 100;
            }
        }
    }
}

class PRunFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;
    private static final Color SHADE = new Color(184, 184, 176);

    private final JFileChooser chooser = new JFileChooser();
    private final JTextField fileField = new JTextField();
    private final JTextArea output = new JTextArea();
    private final JLabel status = new JLabel(" ready ");
    private File selectedFile;

    PRunFrame() {
        super("P-Run", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(720, 460);
        setMinimumSize(new Dimension(560, 340));
        setLocation(250, 130);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        fileField.setEditable(true);
        fileField.setFont(retroFont(Font.PLAIN, 12));
        fileField.setBackground(PAPER);
        fileField.setForeground(INK);
        fileField.addActionListener(event -> runSelectedFile());

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        output.setBackground(Color.BLACK);
        output.setForeground(new Color(216, 216, 196));
        output.setCaretColor(Color.WHITE);
        output.setText("P-Run guesses how to run files and folders.\nChoose a file/folder or type its path, then press Run.\n");

        setJMenuBar(buildMenu());
        setContentPane(buildWindow());
    }

    private JPanel buildWindow() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(PAPER);
        root.setBorder(BorderFactory.createLineBorder(INK, 3));

        JPanel title = new JPanel(new BorderLayout());
        title.setBackground(PAPER);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        JLabel titleText = new JLabel(" P-RUN PROGRAM RUNNER ", JLabel.CENTER);
        titleText.setFont(retroFont(Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout(6, 6));
        controls.setBackground(PAPER);
        controls.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(PAPER);
        buttons.add(retroButton("Choose File...", this::chooseFile));
        buttons.add(retroButton("Run", this::runSelectedFile));
        buttons.add(retroButton("Clear", () -> output.setText("")));
        controls.add(fileField, BorderLayout.CENTER);
        controls.add(buttons, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(PAPER);
        bottom.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));
        status.setFont(retroFont(Font.PLAIN, 12));
        bottom.add(status, BorderLayout.WEST);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(controls, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(output), BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem choose = new JMenuItem("Choose File...");
        choose.addActionListener(event -> chooseFile());
        JMenuItem run = new JMenuItem("Run");
        run.addActionListener(event -> runSelectedFile());
        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(event -> dispose());
        file.add(choose);
        file.add(run);
        file.add(close);
        menuBar.add(file);
        return menuBar;
    }

    private JButton retroButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setFont(retroFont(Font.BOLD, 12));
        button.setForeground(INK);
        button.setBackground(PAPER);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INK, 2),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        button.addActionListener(event -> action.run());
        return button;
    }

    private Font retroFont(int style, int size) {
        return new Font(Font.MONOSPACED, style, size);
    }

    private void chooseFile() {
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            fileField.setText(selectedFile.getAbsolutePath());
            status.setText(" selected ");
            output.append("\nSelected: " + selectedFile.getAbsolutePath() + "\n");
            output.append("Plan: " + describePlan(selectedFile) + "\n");
        }
    }

    private void runSelectedFile() {
        selectedFile = fileFromTypedPath();
        if (selectedFile == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        if (!selectedFile.exists()) {
            output.append("\nCannot run that selection.\n");
            return;
        }

        status.setText(" running ");
        fileField.setText(selectedFile.getAbsolutePath());
        output.append("\nRunning: " + selectedFile.getAbsolutePath() + "\n");
        new SwingWorker<Integer, String>() {
            protected Integer doInBackground() throws Exception {
                return runFile(selectedFile);
            }

            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    output.append(chunk);
                    output.setCaretPosition(output.getDocument().getLength());
                }
            }

            protected void done() {
                try {
                    int exit = get();
                    status.setText(" exit " + exit + " ");
                    output.append("\n[P-Run finished with exit code " + exit + "]\n");
                } catch (Exception exception) {
                    status.setText(" failed ");
                    output.append("\n[P-Run failed: " + exception.getMessage() + "]\n");
                }
            }

            private int runFile(File file) throws IOException, InterruptedException {
                if (file.isDirectory()) {
                    return runFolder(file);
                }

                File directory = file.getParentFile();
                String name = file.getName();
                String lower = name.toLowerCase(Locale.ROOT);

                if (lower.endsWith(".java")) {
                    int compileExit = runProcess(Arrays.asList("javac", name), directory);
                    if (compileExit != 0) {
                        return compileExit;
                    }
                    return runProcess(Arrays.asList("java", className(name)), directory);
                }
                if (lower.endsWith(".class")) {
                    return runProcess(Arrays.asList("java", className(name)), directory);
                }
                if (lower.endsWith(".jar")) {
                    return runProcess(Arrays.asList("java", "-jar", name), directory);
                }
                if (lower.endsWith(".sh") || lower.endsWith(".command")) {
                    return runProcess(Arrays.asList("sh", name), directory);
                }
                if (lower.endsWith(".py")) {
                    return runProcess(Arrays.asList("python3", name), directory);
                }
                if (lower.endsWith(".js")) {
                    return runProcess(Arrays.asList("node", name), directory);
                }
                if (lower.endsWith(".rb")) {
                    return runProcess(Arrays.asList("ruby", name), directory);
                }
                if (lower.endsWith(".go")) {
                    return runProcess(Arrays.asList("go", "run", name), directory);
                }
                if (file.canExecute()) {
                    return runProcess(Arrays.asList("./" + name), directory);
                }
                publish("No runner known for ." + extension(name) + "\n");
                return 127;
            }

            private int runFolder(File folder) throws IOException, InterruptedException {
                File runScript = child(folder, "run.sh");
                if (runScript.exists()) {
                    return runShellScript(runScript, folder);
                }

                File startScript = child(folder, "start.sh");
                if (startScript.exists()) {
                    return runShellScript(startScript, folder);
                }

                File commandScript = child(folder, "run.command");
                if (commandScript.exists()) {
                    return runShellScript(commandScript, folder);
                }

                File packageJson = child(folder, "package.json");
                if (packageJson.exists()) {
                    String json = Files.readString(packageJson.toPath(), StandardCharsets.UTF_8);
                    if (json.contains("\"start\"")) {
                        return runProcess(Arrays.asList("npm", "start"), folder);
                    }
                    if (json.contains("\"dev\"")) {
                        return runProcess(Arrays.asList("npm", "run", "dev"), folder);
                    }
                }

                File gradlew = child(folder, "gradlew");
                if (gradlew.exists()) {
                    return runProcess(Arrays.asList("./gradlew", "run"), folder);
                }

                File mvnw = child(folder, "mvnw");
                if (mvnw.exists()) {
                    return runProcess(Arrays.asList("./mvnw", "spring-boot:run"), folder);
                }

                if (child(folder, "Cargo.toml").exists()) {
                    return runProcess(Arrays.asList("cargo", "run"), folder);
                }
                if (child(folder, "go.mod").exists()) {
                    return runProcess(Arrays.asList("go", "run", "."), folder);
                }
                if (child(folder, "pom.xml").exists()) {
                    return runProcess(Arrays.asList("mvn", "exec:java"), folder);
                }
                if (child(folder, "build.gradle").exists() || child(folder, "build.gradle.kts").exists()) {
                    return runProcess(Arrays.asList("gradle", "run"), folder);
                }
                if (child(folder, "main.py").exists()) {
                    return runProcess(Arrays.asList("python3", "main.py"), folder);
                }
                if (child(folder, "app.py").exists()) {
                    return runProcess(Arrays.asList("python3", "app.py"), folder);
                }
                if (child(folder, "index.js").exists()) {
                    return runProcess(Arrays.asList("node", "index.js"), folder);
                }
                if (child(folder, "server.js").exists()) {
                    return runProcess(Arrays.asList("node", "server.js"), folder);
                }
                if (child(folder, "Main.java").exists()) {
                    int compileExit = runProcess(Arrays.asList("javac", "Main.java"), folder);
                    if (compileExit != 0) {
                        return compileExit;
                    }
                    return runProcess(Arrays.asList("java", "Main"), folder);
                }
                if (child(child(folder, "src"), "Main.java").exists()) {
                    int compileExit = runProcess(Arrays.asList("javac", "-d", "out", "src/Main.java"), folder);
                    if (compileExit != 0) {
                        return compileExit;
                    }
                    return runProcess(Arrays.asList("java", "-cp", "out", "Main"), folder);
                }

                publish("No folder runner found. Looked for run.sh, package.json, Gradle/Maven/Cargo/Go, and common main files.\n");
                return 127;
            }

            private int runShellScript(File script, File folder) throws IOException, InterruptedException {
                if (script.canExecute()) {
                    return runProcess(Arrays.asList("./" + script.getName()), folder);
                }
                return runProcess(Arrays.asList("sh", script.getName()), folder);
            }

            private int runProcess(List<String> command, File directory) throws IOException, InterruptedException {
                publish("$ " + String.join(" ", command) + "\n");
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.directory(directory);
                builder.redirectErrorStream(true);
                Process process = builder.start();
                String text = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                if (!text.isEmpty()) {
                    publish(text);
                    if (!text.endsWith("\n")) {
                        publish("\n");
                    }
                }
                return process.waitFor();
            }
        }.execute();
    }

    private File fileFromTypedPath() {
        String typed = fileField.getText().trim();
        if (typed.isEmpty()) {
            return selectedFile;
        }
        if ((typed.startsWith("\"") && typed.endsWith("\"")) || (typed.startsWith("'") && typed.endsWith("'"))) {
            typed = typed.substring(1, typed.length() - 1);
        }
        if (typed.equals("~")) {
            typed = System.getProperty("user.home");
        } else if (typed.startsWith("~/")) {
            typed = System.getProperty("user.home") + typed.substring(1);
        }
        return new File(typed);
    }

    private String describePlan(File file) {
        if (file.isDirectory()) {
            return "folder scan: run.sh, start scripts, npm start/dev, Gradle/Maven, Cargo, Go, Python, Node, Java";
        }
        String lower = file.getName().toLowerCase(Locale.ROOT);
        if (lower.endsWith(".java")) {
            return "javac file.java, then java ClassName";
        }
        if (lower.endsWith(".class")) {
            return "java ClassName";
        }
        if (lower.endsWith(".jar")) {
            return "java -jar file.jar";
        }
        if (lower.endsWith(".sh") || lower.endsWith(".command")) {
            return "sh script";
        }
        if (lower.endsWith(".py")) {
            return "python3 script";
        }
        if (lower.endsWith(".js")) {
            return "node script";
        }
        if (lower.endsWith(".rb")) {
            return "ruby script";
        }
        if (lower.endsWith(".go")) {
            return "go run file.go";
        }
        if (file.canExecute()) {
            return "run executable directly";
        }
        return "unknown extension";
    }

    private String className(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    private String extension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 && dot + 1 < fileName.length() ? fileName.substring(dot + 1) : "unknown";
    }

    private File child(File folder, String name) {
        return new File(folder, name);
    }
}

class TerminalFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JTextArea output = new JTextArea();
    private final JTextField input = new JTextField();
    private final JLabel status = new JLabel(" ");
    private File workingDirectory = new File(System.getProperty("user.home"));

    TerminalFrame() {
        super("Terminal", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(720, 420);
        setMinimumSize(new Dimension(520, 320));
        setLocation(300, 170);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        output.setBackground(Color.BLACK);
        output.setForeground(new Color(216, 216, 196));
        output.setCaretColor(Color.WHITE);
        output.setText("Mactonish Terminal\nType commands below. Use cd to change folders.\n\n");

        input.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        input.setBackground(PAPER);
        input.setForeground(INK);
        input.addActionListener(event -> runCommand());

        setJMenuBar(buildMenu());
        setContentPane(buildWindow());
        updateStatus();
    }

    private JPanel buildWindow() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(PAPER);
        root.setBorder(BorderFactory.createLineBorder(INK, 3));

        JPanel title = new JPanel(new BorderLayout());
        title.setBackground(PAPER);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        JLabel titleText = new JLabel(" TERMINAL ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel commandBar = new JPanel(new BorderLayout(6, 0));
        commandBar.setBackground(PAPER);
        commandBar.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
        commandBar.add(new JLabel(" $ "), BorderLayout.WEST);
        commandBar.add(input, BorderLayout.CENTER);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        JPanel bottom = new JPanel(new BorderLayout(0, 4));
        bottom.setBackground(PAPER);
        bottom.add(commandBar, BorderLayout.CENTER);
        bottom.add(status, BorderLayout.SOUTH);

        root.add(title, BorderLayout.NORTH);
        root.add(new JScrollPane(output), BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu terminal = new JMenu("Terminal");
        JMenuItem clear = new JMenuItem("Clear");
        clear.addActionListener(event -> output.setText(""));
        JMenuItem home = new JMenuItem("Go Home");
        home.addActionListener(event -> {
            workingDirectory = new File(System.getProperty("user.home"));
            updateStatus();
        });
        terminal.add(clear);
        terminal.add(home);
        menuBar.add(terminal);
        return menuBar;
    }

    private void runCommand() {
        String command = input.getText().trim();
        input.setText("");
        if (command.isEmpty()) {
            return;
        }
        output.append("$ " + command + "\n");
        if (command.equals("clear")) {
            output.setText("");
            return;
        }
        if (command.equals("pwd")) {
            output.append(workingDirectory.getAbsolutePath() + "\n");
            scrollOutput();
            return;
        }
        if (command.equals("exit")) {
            dispose();
            return;
        }
        if (command.startsWith("cd")) {
            changeDirectory(command);
            return;
        }

        input.setEnabled(false);
        new SwingWorker<Integer, String>() {
            protected Integer doInBackground() throws Exception {
                ProcessBuilder builder = new ProcessBuilder("sh", "-c", command);
                builder.directory(workingDirectory);
                builder.redirectErrorStream(true);
                Process process = builder.start();
                String text = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                if (!text.isEmpty()) {
                    publish(text);
                    if (!text.endsWith("\n")) {
                        publish("\n");
                    }
                }
                return process.waitFor();
            }

            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    output.append(chunk);
                }
                scrollOutput();
            }

            protected void done() {
                try {
                    output.append("[exit " + get() + "]\n");
                } catch (Exception exception) {
                    output.append("[failed: " + exception.getMessage() + "]\n");
                }
                input.setEnabled(true);
                input.requestFocusInWindow();
                scrollOutput();
            }
        }.execute();
    }

    private void changeDirectory(String command) {
        String target = command.length() > 2 ? command.substring(2).trim() : "~";
        if ((target.startsWith("\"") && target.endsWith("\"")) || (target.startsWith("'") && target.endsWith("'"))) {
            target = target.substring(1, target.length() - 1);
        }
        File next;
        if (target.equals("~")) {
            next = new File(System.getProperty("user.home"));
        } else if (target.startsWith("~/")) {
            next = new File(System.getProperty("user.home") + target.substring(1));
        } else {
            next = new File(target);
            if (!next.isAbsolute()) {
                next = new File(workingDirectory, target);
            }
        }
        if (next.exists() && next.isDirectory()) {
            workingDirectory = next;
            updateStatus();
        } else {
            output.append("cd: no such directory: " + target + "\n");
        }
        scrollOutput();
    }

    private void updateStatus() {
        status.setText("  cwd: " + workingDirectory.getAbsolutePath());
    }

    private void scrollOutput() {
        output.setCaretPosition(output.getDocument().getLength());
    }
}

class NotepadFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JFileChooser chooser = new JFileChooser();
    private final JTextArea text = new JTextArea();
    private final JLabel status = new JLabel(" untitled ");
    private File currentFile;
    private boolean dirty;
    private boolean loading;

    NotepadFrame() {
        super("Notepad", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(640, 460);
        setMinimumSize(new Dimension(460, 320));
        setLocation(350, 210);

        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        text.setBackground(PAPER);
        text.setForeground(INK);
        text.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) {
                markDirty();
            }

            public void removeUpdate(DocumentEvent event) {
                markDirty();
            }

            public void changedUpdate(DocumentEvent event) {
                markDirty();
            }
        });

        setJMenuBar(buildMenu());
        setContentPane(buildWindow());
    }

    private JPanel buildWindow() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAPER);
        root.setBorder(BorderFactory.createLineBorder(INK, 3));

        JPanel title = new JPanel(new BorderLayout());
        title.setBackground(PAPER);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        JLabel titleText = new JLabel(" NOTEPAD ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(title, BorderLayout.NORTH);
        root.add(new JScrollPane(text), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem fresh = new JMenuItem("New");
        fresh.addActionListener(event -> newNote());
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(event -> openNote());
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(event -> saveNote());
        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.addActionListener(event -> saveNoteAs());
        file.add(fresh);
        file.add(open);
        file.add(save);
        file.add(saveAs);
        menuBar.add(file);
        return menuBar;
    }

    private void newNote() {
        if (!confirmDiscard()) {
            return;
        }
        loading = true;
        text.setText("");
        currentFile = null;
        dirty = false;
        status.setText(" untitled ");
        loading = false;
    }

    private void openNote() {
        if (!confirmDiscard()) {
            return;
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                currentFile = chooser.getSelectedFile();
                loading = true;
                text.setText(Files.readString(currentFile.toPath(), StandardCharsets.UTF_8));
                text.setCaretPosition(0);
                dirty = false;
                status.setText(" " + currentFile.getAbsolutePath() + " ");
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(this, "Open failed:\n" + exception.getMessage(), "Notepad", JOptionPane.ERROR_MESSAGE);
            } finally {
                loading = false;
            }
        }
    }

    private void saveNote() {
        if (currentFile == null) {
            saveNoteAs();
            return;
        }
        try {
            Files.writeString(currentFile.toPath(), text.getText(), StandardCharsets.UTF_8);
            dirty = false;
            status.setText(" " + currentFile.getAbsolutePath() + " saved ");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Save failed:\n" + exception.getMessage(), "Notepad", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveNoteAs() {
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            saveNote();
        }
    }

    private void markDirty() {
        if (!loading) {
            dirty = true;
            status.setText((currentFile == null ? " untitled" : " " + currentFile.getAbsolutePath()) + " modified ");
        }
    }

    private boolean confirmDiscard() {
        if (!dirty) {
            return true;
        }
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Discard unsaved Notepad changes?",
                "Notepad",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }
}

class AppCreatorFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JFileChooser chooser = new JFileChooser();
    private final JTextField nameField = new JTextField("MyApp");
    private final JTextField folderField = new JTextField(new File(System.getProperty("user.home"), "MactonishApps").getAbsolutePath());
    private final JComboBox<String> languageBox = new JComboBox<>(new String[]{"Java", "Rust"});
    private final JTextArea output = new JTextArea();
    private final JLabel status = new JLabel(" ready ");

    AppCreatorFrame() {
        super("App Maker", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(660, 430);
        setMinimumSize(new Dimension(500, 330));
        setLocation(390, 250);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        nameField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        folderField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        output.setBackground(Color.BLACK);
        output.setForeground(new Color(216, 216, 196));
        output.setText("App Maker creates runnable Java or Rust app folders.\nGenerated folders include run.sh for P-Run.\n");

        setJMenuBar(buildMenu());
        setContentPane(buildWindow());
    }

    private JPanel buildWindow() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(PAPER);
        root.setBorder(BorderFactory.createLineBorder(INK, 3));

        JPanel title = new JPanel(new BorderLayout());
        title.setBackground(PAPER);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        JLabel titleText = new JLabel(" APP MAKER ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel form = new JPanel(new BorderLayout(6, 6));
        form.setBackground(PAPER);
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        JPanel fields = new JPanel(new java.awt.GridLayout(3, 2, 6, 6));
        fields.setBackground(PAPER);
        fields.add(new JLabel(" App name "));
        fields.add(nameField);
        fields.add(new JLabel(" Language "));
        fields.add(languageBox);
        fields.add(new JLabel(" Create in "));
        fields.add(folderField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(PAPER);
        buttons.add(retroButton("Choose Folder...", this::chooseFolder));
        buttons.add(retroButton("Create App", this::createApp));
        buttons.add(retroButton("Clear", () -> output.setText("")));

        form.add(fields, BorderLayout.CENTER);
        form.add(buttons, BorderLayout.SOUTH);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(output), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu app = new JMenu("App");
        JMenuItem create = new JMenuItem("Create App");
        create.addActionListener(event -> createApp());
        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(event -> dispose());
        app.add(create);
        app.add(close);
        menuBar.add(app);
        return menuBar;
    }

    private JButton retroButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        button.setForeground(INK);
        button.setBackground(PAPER);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INK, 2),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        button.addActionListener(event -> action.run());
        return button;
    }

    private void chooseFolder() {
        File current = new File(folderField.getText().trim());
        if (current.exists()) {
            chooser.setCurrentDirectory(current);
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            folderField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void createApp() {
        String appName = sanitizeName(nameField.getText());
        if (appName.isBlank()) {
            Toolkit.getDefaultToolkit().beep();
            output.append("\nApp name is empty.\n");
            return;
        }

        File parent = resolveFolder(folderField.getText().trim());
        File appFolder = new File(parent, appName);
        if (appFolder.exists()) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "That folder already exists. Add/replace starter files inside it?",
                    "App Maker",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            Files.createDirectories(appFolder.toPath());
            if ("Rust".equals(languageBox.getSelectedItem())) {
                createRustApp(appFolder, appName);
            } else {
                createJavaApp(appFolder, appName);
            }
            status.setText(" created ");
            output.append("\nCreated " + languageBox.getSelectedItem() + " app:\n");
            output.append(appFolder.getAbsolutePath() + "\n");
            output.append("Run it with P-Run by choosing that folder, or from Terminal:\n");
            output.append("cd \"" + appFolder.getAbsolutePath() + "\" && ./run.sh\n");
        } catch (IOException exception) {
            status.setText(" failed ");
            JOptionPane.showMessageDialog(this, "Create failed:\n" + exception.getMessage(), "App Maker", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createJavaApp(File folder, String appName) throws IOException {
        String className = toJavaClassName(appName);
        write(new File(folder, className + ".java"), """
                public class %s {
                    public static void main(String[] args) {
                        System.out.println("Hello from %s!");
                    }
                }
                """.formatted(className, appName));
        write(new File(folder, "run.sh"), """
                #!/usr/bin/env sh
                set -eu

                javac %s.java
                java %s
                """.formatted(className, className));
        write(new File(folder, "README.txt"), """
                %s

                A Java app made with Mactonish App Maker.
                Run with ./run.sh or choose this folder in P-Run.
                """.formatted(appName));
        new File(folder, "run.sh").setExecutable(true);
    }

    private void createRustApp(File folder, String appName) throws IOException {
        File src = new File(folder, "src");
        Files.createDirectories(src.toPath());
        write(new File(folder, "Cargo.toml"), """
                [package]
                name = "%s"
                version = "0.1.0"
                edition = "2021"

                [dependencies]
                """.formatted(toRustPackageName(appName)));
        write(new File(src, "main.rs"), """
                fn main() {
                    println!("Hello from %s!");
                }
                """.formatted(appName));
        write(new File(folder, "run.sh"), """
                #!/usr/bin/env sh
                set -eu

                cargo run
                """);
        write(new File(folder, "README.txt"), """
                %s

                A Rust app made with Mactonish App Maker.
                Run with ./run.sh, cargo run, or choose this folder in P-Run.
                """.formatted(appName));
        new File(folder, "run.sh").setExecutable(true);
    }

    private void write(File file, String content) throws IOException {
        Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
    }

    private File resolveFolder(String typed) {
        if (typed.isBlank() || typed.equals("~")) {
            return new File(System.getProperty("user.home"));
        }
        if (typed.startsWith("~/")) {
            return new File(System.getProperty("user.home") + typed.substring(1));
        }
        return new File(typed);
    }

    private String sanitizeName(String value) {
        return value.trim().replaceAll("[^A-Za-z0-9_-]", "_");
    }

    private String toJavaClassName(String value) {
        StringBuilder builder = new StringBuilder();
        for (String part : value.split("[^A-Za-z0-9]+")) {
            if (part.isBlank()) {
                continue;
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        if (builder.isEmpty() || !Character.isJavaIdentifierStart(builder.charAt(0))) {
            builder.insert(0, "App");
        }
        return builder.toString();
    }

    private String toRustPackageName(String value) {
        String name = value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "-");
        while (name.contains("--")) {
            name = name.replace("--", "-");
        }
        if (name.isBlank() || !Character.isLetter(name.charAt(0))) {
            name = "app-" + name;
        }
        return name;
    }
}

class FileEditorFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JFileChooser chooser = new JFileChooser();
    private final JTextField pathField = new JTextField();
    private final JTextArea text = new JTextArea();
    private final JLabel status = new JLabel(" no file ");
    private File currentFile;
    private boolean dirty;
    private boolean loading;

    FileEditorFrame() {
        super("File Edit", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(520, 360));
        setLocation(430, 110);

        pathField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        pathField.setBackground(PAPER);
        pathField.setForeground(INK);
        pathField.addActionListener(event -> openTypedPath());

        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        text.setBackground(PAPER);
        text.setForeground(INK);
        text.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) {
                markDirty();
            }

            public void removeUpdate(DocumentEvent event) {
                markDirty();
            }

            public void changedUpdate(DocumentEvent event) {
                markDirty();
            }
        });

        setJMenuBar(buildMenu());
        setContentPane(buildWindow());
    }

    private JPanel buildWindow() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(PAPER);
        root.setBorder(BorderFactory.createLineBorder(INK, 3));

        JPanel title = new JPanel(new BorderLayout());
        title.setBackground(PAPER);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        JLabel titleText = new JLabel(" FILE EDITOR ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout(6, 6));
        controls.setBackground(PAPER);
        controls.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(PAPER);
        buttons.add(retroButton("Open...", this::openChooser));
        buttons.add(retroButton("Open Path", this::openTypedPath));
        buttons.add(retroButton("Save", this::saveFile));
        buttons.add(retroButton("Save As...", this::saveFileAs));
        controls.add(pathField, BorderLayout.CENTER);
        controls.add(buttons, BorderLayout.SOUTH);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(controls, BorderLayout.CENTER);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(text), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(event -> openChooser());
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(event -> saveFile());
        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.addActionListener(event -> saveFileAs());
        file.add(open);
        file.add(save);
        file.add(saveAs);
        menuBar.add(file);
        return menuBar;
    }

    private JButton retroButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        button.setForeground(INK);
        button.setBackground(PAPER);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INK, 2),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        button.addActionListener(event -> action.run());
        return button;
    }

    private void openChooser() {
        if (!confirmDiscard()) {
            return;
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            openFile(chooser.getSelectedFile());
        }
    }

    private void openTypedPath() {
        if (!confirmDiscard()) {
            return;
        }
        openFile(resolvePath(pathField.getText().trim()));
    }

    private void openFile(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            status.setText(" cannot open ");
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        try {
            loading = true;
            currentFile = file;
            pathField.setText(file.getAbsolutePath());
            text.setText(Files.readString(file.toPath(), StandardCharsets.UTF_8));
            text.setCaretPosition(0);
            dirty = false;
            status.setText(" " + file.getAbsolutePath() + " ");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Open failed:\n" + exception.getMessage(), "File Edit", JOptionPane.ERROR_MESSAGE);
        } finally {
            loading = false;
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            saveFileAs();
            return;
        }
        try {
            Files.writeString(currentFile.toPath(), text.getText(), StandardCharsets.UTF_8);
            dirty = false;
            status.setText(" " + currentFile.getAbsolutePath() + " saved ");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Save failed:\n" + exception.getMessage(), "File Edit", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFileAs() {
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            pathField.setText(currentFile.getAbsolutePath());
            saveFile();
        }
    }

    private File resolvePath(String typed) {
        if (typed.isBlank() || typed.equals("~")) {
            return new File(System.getProperty("user.home"));
        }
        if ((typed.startsWith("\"") && typed.endsWith("\"")) || (typed.startsWith("'") && typed.endsWith("'"))) {
            typed = typed.substring(1, typed.length() - 1);
        }
        if (typed.startsWith("~/")) {
            return new File(System.getProperty("user.home") + typed.substring(1));
        }
        return new File(typed);
    }

    private void markDirty() {
        if (!loading) {
            dirty = true;
            status.setText((currentFile == null ? " no file" : " " + currentFile.getAbsolutePath()) + " modified ");
        }
    }

    private boolean confirmDiscard() {
        if (!dirty) {
            return true;
        }
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Discard unsaved File Edit changes?",
                "File Edit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }
}

class BrowserFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JTextField address = new JTextField("https://example.com");
    private final JEditorPane page = new JEditorPane();
    private final JLabel status = new JLabel(" ready ");
    private URL currentUrl;

    BrowserFrame() {
        super("Browser", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(820, 560);
        setMinimumSize(new Dimension(560, 380));
        setLocation(460, 150);

        address.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        address.setBackground(PAPER);
        address.setForeground(INK);
        address.addActionListener(event -> loadTypedAddress());

        page.setEditable(false);
        page.setBackground(Color.WHITE);
        page.addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                loadUrl(event.getURL());
            }
        });
        page.setText("<html><body><h1>Mactonish Browser</h1><p>Type a URL, search words, or local HTML file path and press Go.</p><p>JavaScript pages can be opened externally.</p></body></html>");

        setJMenuBar(buildMenu());
        setContentPane(buildWindow());
    }

    private JPanel buildWindow() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(PAPER);
        root.setBorder(BorderFactory.createLineBorder(INK, 3));

        JPanel title = new JPanel(new BorderLayout());
        title.setBackground(PAPER);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        JLabel titleText = new JLabel(" WEB BROWSER ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout(6, 0));
        controls.setBackground(PAPER);
        controls.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttons.setBackground(PAPER);
        buttons.add(retroButton("Go", this::loadTypedAddress));
        buttons.add(retroButton("Open External", this::openExternal));
        controls.add(address, BorderLayout.CENTER);
        controls.add(buttons, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(controls, BorderLayout.CENTER);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(page), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu browser = new JMenu("Browser");
        JMenuItem go = new JMenuItem("Go");
        go.addActionListener(event -> loadTypedAddress());
        JMenuItem reload = new JMenuItem("Reload");
        reload.addActionListener(event -> {
            if (currentUrl != null) {
                loadUrl(currentUrl);
            }
        });
        JMenuItem external = new JMenuItem("Open External");
        external.addActionListener(event -> openExternal());
        browser.add(go);
        browser.add(reload);
        browser.add(external);
        menuBar.add(browser);
        return menuBar;
    }

    private JButton retroButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        button.setForeground(INK);
        button.setBackground(PAPER);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INK, 2),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        button.addActionListener(event -> action.run());
        return button;
    }

    private void loadTypedAddress() {
        try {
            loadUrl(toUrl(address.getText().trim()));
        } catch (Exception exception) {
            status.setText(" bad address ");
            JOptionPane.showMessageDialog(this, "Could not read address:\n" + exception.getMessage(), "Browser", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUrl(URL url) {
        if (url == null) {
            return;
        }
        status.setText(" loading ");
        address.setText(url.toExternalForm());
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                page.setPage(url);
                return null;
            }

            protected void done() {
                try {
                    get();
                    currentUrl = url;
                    status.setText(" " + url.toExternalForm() + " ");
                } catch (Exception exception) {
                    status.setText(" failed ");
                    page.setText("<html><body><h1>Load failed</h1><pre>" + escapeHtml(exception.getMessage()) + "</pre></body></html>");
                }
            }
        }.execute();
    }

    private void openExternal() {
        try {
            URL url = currentUrl != null ? currentUrl : toUrl(address.getText().trim());
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(url.toURI());
                status.setText(" opened external ");
            } else {
                status.setText(" external browser unavailable ");
            }
        } catch (Exception exception) {
            status.setText(" external failed ");
            JOptionPane.showMessageDialog(this, "Could not open external browser:\n" + exception.getMessage(), "Browser", JOptionPane.ERROR_MESSAGE);
        }
    }

    private URL toUrl(String typed) throws MalformedURLException {
        if (typed.startsWith("http://") || typed.startsWith("https://") || typed.startsWith("file:")) {
            return URI.create(typed).toURL();
        }
        if (typed.startsWith("~/")) {
            typed = System.getProperty("user.home") + typed.substring(1);
        }
        File file = new File(typed);
        if (file.exists()) {
            return file.toURI().toURL();
        }
        if (typed.contains(" ") || !typed.contains(".")) {
            String query = URLEncoder.encode(typed, StandardCharsets.UTF_8);
            return URI.create("https://duckduckgo.com/html/?q=" + query).toURL();
        }
        return URI.create("https://" + typed).toURL();
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

class FinderFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;
    private static final Color SHADE = new Color(184, 184, 176);
    private static final Color SELECTED = Color.BLACK;

    private final DefaultTreeModel treeModel;
    private final JTree tree;
    private final FileTableModel tableModel = new FileTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextArea editor = new JTextArea();
    private final JLabel editorTitle = new JLabel(" NANO: no file loaded ");
    private final JLabel editorStatus = new JLabel(" ");
    private final JLabel pathLabel = new JLabel(" ");
    private final JLabel countLabel = new JLabel(" ");
    private final JFileChooser chooser = new JFileChooser();

    private File currentDirectory;
    private File editorFile;
    private boolean editorDirty;
    private boolean loadingEditor;

    FinderFrame() {
        super("Finder", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setSize(1080, 680);
        setLocation(150, 82);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        installMenu();
        installStyles();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode("This Computer", null));
        for (File rootFile : File.listRoots()) {
            root.add(directoryNode(rootFile.getPath(), rootFile));
        }
        addQuickLocation(root, "Home Folder", new File(System.getProperty("user.home")));
        addQuickLocation(root, "Desktop", new File(System.getProperty("user.home"), "Desktop"));
        addQuickLocation(root, "Documents", new File(System.getProperty("user.home"), "Documents"));

        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new FinderTreeRenderer());
        tree.addTreeSelectionListener(event -> {
            File file = selectedTreeFile();
            if (file != null) {
                openDirectory(file);
            }
        });
        tree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeExpanded(javax.swing.event.TreeExpansionEvent event) {
                populateChildren((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
            }

            public void treeCollapsed(javax.swing.event.TreeExpansionEvent event) {
            }
        });

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setRowHeight(24);
        table.setFont(retroFont(Font.PLAIN, 13));
        table.getTableHeader().setFont(retroFont(Font.BOLD, 12));
        table.getTableHeader().setBackground(SHADE);
        table.getTableHeader().setForeground(INK);
        table.getSelectionModel().addListSelectionListener(this::previewSelection);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    File file = selectedTableFile();
                    if (file != null && file.isDirectory()) {
                        openDirectory(file);
                    }
                }
            }
        });

        editor.setEditable(false);
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        editor.setBackground(Color.BLACK);
        editor.setForeground(new Color(216, 216, 196));
        editor.setCaretColor(Color.WHITE);
        editor.setSelectionColor(Color.WHITE);
        editor.setSelectedTextColor(Color.BLACK);
        editor.setLineWrap(false);
        editor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) {
                markEditorDirty();
            }

            public void removeUpdate(DocumentEvent event) {
                markEditorDirty();
            }

            public void changedUpdate(DocumentEvent event) {
                markEditorDirty();
            }
        });
        installEditorKeys();

        setContentPane(buildWindow());
        File home = new File(System.getProperty("user.home"));
        openDirectory(home.exists() ? home : File.listRoots()[0]);
    }

    private JPanel buildWindow() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(PAPER);
        root.setBorder(BorderFactory.createLineBorder(INK, 3));

        JPanel title = new JPanel(new BorderLayout());
        title.setBackground(PAPER);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        JLabel closeBox = new JLabel("  □  ");
        closeBox.setFont(retroFont(Font.BOLD, 16));
        JLabel titleText = new JLabel(" EMF FINDER - ALL FILES ", JLabel.CENTER);
        titleText.setFont(retroFont(Font.BOLD, 15));
        title.add(closeBox, BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);
        root.add(title, BorderLayout.NORTH);

        JSplitPane browser = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                framedScroll(tree),
                buildRightPane()
        );
        browser.setDividerLocation(260);
        browser.setContinuousLayout(true);
        browser.setBorder(BorderFactory.createEmptyBorder());
        root.add(browser, BorderLayout.CENTER);

        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(PAPER);
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));
        status.add(pathLabel, BorderLayout.CENTER);
        status.add(countLabel, BorderLayout.EAST);
        root.add(status, BorderLayout.SOUTH);

        return root;
    }

    private Component buildRightPane() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(PAPER);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        toolbar.setBackground(PAPER);
        toolbar.add(retroButton("Back", () -> {
            if (currentDirectory != null && currentDirectory.getParentFile() != null) {
                openDirectory(currentDirectory.getParentFile());
            }
        }));
        toolbar.add(retroButton("Open Disk...", this::chooseFolder));
        toolbar.add(retroButton("Refresh", this::refreshDirectory));
        toolbar.add(retroButton("Save", this::saveEditor));
        toolbar.add(retroButton("Reload", this::reloadEditor));

        JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, framedScroll(table), buildEditorPanel());
        right.setDividerLocation(385);
        right.setResizeWeight(0.72);
        right.setBorder(BorderFactory.createEmptyBorder());

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(right, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(INK, 2));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SHADE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        editorTitle.setFont(retroFont(Font.BOLD, 12));
        editorStatus.setFont(retroFont(Font.PLAIN, 12));
        header.add(editorTitle, BorderLayout.WEST);
        header.add(editorStatus, BorderLayout.EAST);

        JLabel commands = new JLabel(" ^O Save   ^R Reload   ^X Close Buffer   Double-click folders to open ");
        commands.setOpaque(true);
        commands.setBackground(SHADE);
        commands.setForeground(INK);
        commands.setFont(retroFont(Font.BOLD, 12));
        commands.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(editor), BorderLayout.CENTER);
        panel.add(commands, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane framedScroll(Component component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INK, 2),
                BorderFactory.createLineBorder(Color.WHITE, 2)
        ));
        scroll.getViewport().setBackground(PAPER);
        return scroll;
    }

    private JButton retroButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setFont(retroFont(Font.BOLD, 12));
        button.setForeground(INK);
        button.setBackground(PAPER);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INK, 2),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        button.addActionListener(event -> action.run());
        return button;
    }

    private void installMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open Folder...");
        open.addActionListener(event -> chooseFolder());
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(event -> dispose());
        file.add(open);
        file.add(quit);

        JMenu view = new JMenu("View");
        JMenuItem refresh = new JMenuItem("Refresh");
        refresh.addActionListener(event -> refreshDirectory());
        view.add(refresh);

        menuBar.add(file);
        menuBar.add(view);
        setJMenuBar(menuBar);
    }

    private void installStyles() {
        Font font = retroFont(Font.PLAIN, 13);
        UIManager.put("Panel.background", PAPER);
        UIManager.put("Label.font", font);
        UIManager.put("Label.foreground", INK);
        UIManager.put("List.font", font);
        UIManager.put("Tree.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("Table.selectionBackground", SELECTED);
        UIManager.put("Table.selectionForeground", Color.WHITE);
    }

    private Font retroFont(int style, int size) {
        String[] candidates = {"Chicago", "Monaco", "Geneva", Font.MONOSPACED};
        for (String candidate : candidates) {
            Font font = new Font(candidate, style, size);
            if (font.getFamily().equalsIgnoreCase(candidate) || Font.MONOSPACED.equals(candidate)) {
                return font;
            }
        }
        return new Font(Font.MONOSPACED, style, size);
    }

    private void addQuickLocation(DefaultMutableTreeNode root, String label, File file) {
        if (file.exists() && file.isDirectory()) {
            root.add(directoryNode(label, file));
        }
    }

    private DefaultMutableTreeNode directoryNode(String label, File file) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FileNode(label, file));
        if (hasDirectoryChildren(file)) {
            node.add(new DefaultMutableTreeNode(new PlaceholderNode()));
        }
        return node;
    }

    private File selectedTreeFile() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            return null;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object value = node.getUserObject();
        if (value instanceof FileNode fileNode) {
            return fileNode.file();
        }
        return null;
    }

    private File selectedTableFile() {
        int row = table.getSelectedRow();
        return row >= 0 ? tableModel.fileAt(table.convertRowIndexToModel(row)) : null;
    }

    private void openDirectory(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return;
        }
        if (!confirmDiscardChanges()) {
            return;
        }
        currentDirectory = directory;
        refreshDirectory();
        closeEditorWithoutPrompt();
    }

    private void refreshDirectory() {
        if (currentDirectory == null) {
            return;
        }
        tableModel.setDirectory(currentDirectory);
        pathLabel.setText("  " + currentDirectory.getAbsolutePath());
        countLabel.setText("  " + tableModel.getRowCount() + " items  ");
    }

    private void chooseFolder() {
        if (currentDirectory != null) {
            chooser.setCurrentDirectory(currentDirectory);
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            openDirectory(chooser.getSelectedFile());
        }
    }

    private void populateChildren(DefaultMutableTreeNode node) {
        if (node.getChildCount() > 0 && node.getChildAt(0) instanceof DefaultMutableTreeNode child) {
            Object first = child.getUserObject();
            if (!(first instanceof PlaceholderNode)) {
                return;
            }
        }
        node.removeAllChildren();
        Object value = node.getUserObject();
        if (!(value instanceof FileNode fileNode) || fileNode.file() == null) {
            treeModel.reload(node);
            return;
        }

        File[] children = fileNode.file().listFiles(File::isDirectory);
        if (children != null) {
            Arrays.sort(children, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
            for (File child : children) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(child.getName(), child));
                if (hasDirectoryChildren(child)) {
                    childNode.add(new DefaultMutableTreeNode(new PlaceholderNode()));
                }
                node.add(childNode);
            }
        }
        treeModel.reload(node);
    }

    private boolean hasDirectoryChildren(File directory) {
        File[] children = directory.listFiles(File::isDirectory);
        return children != null && children.length > 0;
    }

    private void previewSelection(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }
        File file = selectedTableFile();
        if (file == null) {
            return;
        }
        loadEditor(file);
    }

    private void loadEditor(File file) {
        if (!confirmDiscardChanges()) {
            return;
        }
        loadingEditor = true;
        editorDirty = false;
        editorFile = null;
        editor.setEditable(false);
        editorTitle.setText(" NANO: " + file.getName() + " ");
        editorStatus.setText(" ");

        StringBuilder text = fileDetails(file);
        if (file.isDirectory()) {
            text.append("\nFolder selected. Double-click it in the list to open it.");
            editor.setText(text.toString());
            editor.setCaretPosition(0);
            loadingEditor = false;
            return;
        }

        try {
            String type = Files.probeContentType(file.toPath());
            if (Files.size(file.toPath()) > 1_000_000 || !isEditableText(file, type)) {
                text.append("Kind: ").append(type == null ? "unknown" : type).append('\n');
                text.append("\nThis file is not opened in NANO because it looks binary or too large.");
                editor.setText(text.toString());
                editor.setCaretPosition(0);
                loadingEditor = false;
                return;
            }

            editorFile = file;
            editor.setText(Files.readString(file.toPath(), StandardCharsets.UTF_8));
            editor.setEditable(file.canWrite());
            editor.setCaretPosition(0);
            editorStatus.setText(file.canWrite() ? " loaded " : " read only ");
        } catch (IOException exception) {
            text.append("\nOpen failed: ").append(exception.getMessage());
            editor.setText(text.toString());
            editor.setCaretPosition(0);
        } finally {
            loadingEditor = false;
        }
    }

    private StringBuilder fileDetails(File file) {
        StringBuilder text = new StringBuilder();
        text.append(file.isDirectory() ? "Folder" : "Document").append('\n');
        text.append("Name: ").append(file.getName()).append('\n');
        text.append("Path: ").append(file.getAbsolutePath()).append('\n');
        text.append("Modified: ").append(DateFormat.getDateTimeInstance().format(new Date(file.lastModified()))).append('\n');
        text.append("Readable: ").append(file.canRead() ? "yes" : "no").append('\n');
        text.append("Writable: ").append(file.canWrite() ? "yes" : "no").append('\n');
        if (file.isFile()) {
            text.append("Size: ").append(FileTableModel.formatSize(file.length())).append('\n');
        }
        return text;
    }

    private void saveEditor() {
        if (editorFile == null || !editor.isEditable()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        try {
            Files.writeString(editorFile.toPath(), editor.getText(), StandardCharsets.UTF_8);
            editorDirty = false;
            editorStatus.setText(" saved ");
            tableModel.setDirectory(currentDirectory);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Save failed:\n" + exception.getMessage(), "NANO", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadEditor() {
        if (editorFile != null) {
            loadEditor(editorFile);
        }
    }

    private void closeEditor() {
        if (!confirmDiscardChanges()) {
            return;
        }
        closeEditorWithoutPrompt();
    }

    private void closeEditorWithoutPrompt() {
        loadingEditor = true;
        editorFile = null;
        editorDirty = false;
        editor.setEditable(false);
        editor.setText("Select a text file to edit it in built-in NANO.");
        editor.setCaretPosition(0);
        editorTitle.setText(" NANO: no file loaded ");
        editorStatus.setText(" ");
        loadingEditor = false;
    }

    private void markEditorDirty() {
        if (!loadingEditor && editorFile != null) {
            editorDirty = true;
            editorStatus.setText(" modified ");
        }
    }

    private void installEditorKeys() {
        InputMap input = editor.getInputMap();
        ActionMap actions = editor.getActionMap();
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "nano-save");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "nano-save");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "nano-reload");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), "nano-close");
        actions.put("nano-save", new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                saveEditor();
            }
        });
        actions.put("nano-reload", new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                reloadEditor();
            }
        });
        actions.put("nano-close", new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                closeEditor();
            }
        });
    }

    private boolean confirmDiscardChanges() {
        if (!editorDirty) {
            return true;
        }
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Discard unsaved NANO changes?",
                "NANO",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }

    private boolean isEditableText(File file, String contentType) {
        String name = file.getName().toLowerCase();
        return isPreviewable(contentType)
                || name.endsWith(".java")
                || name.endsWith(".md")
                || name.endsWith(".txt")
                || name.endsWith(".sh")
                || name.endsWith(".xml")
                || name.endsWith(".json")
                || name.endsWith(".yml")
                || name.endsWith(".yaml")
                || name.endsWith(".properties")
                || name.endsWith(".gitignore");
    }

    private boolean isPreviewable(String contentType) {
        return contentType != null
                && (contentType.startsWith("text/")
                || contentType.contains("json")
                || contentType.contains("xml")
                || contentType.contains("javascript"));
    }

    static class FinderTreeRenderer extends DefaultTreeCellRenderer {
        private final Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");
        private final Icon computerIcon = UIManager.getIcon("FileView.computerIcon");

        FinderTreeRenderer() {
            setBackgroundNonSelectionColor(PAPER);
            setBackgroundSelectionColor(SELECTED);
            setTextNonSelectionColor(INK);
            setTextSelectionColor(Color.WHITE);
            setBorderSelectionColor(INK);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            label.setIcon(row == 0 ? computerIcon : folderIcon);
            return label;
        }
    }
}

class FileTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"Name", "Kind", "Size", "Modified"};
    private final List<File> files = new ArrayList<>();

    void setDirectory(File directory) {
        files.clear();
        File[] listed = directory.listFiles();
        if (listed != null) {
            Arrays.sort(listed, Comparator
                    .comparing((File file) -> !file.isDirectory())
                    .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER));
            files.addAll(Arrays.asList(listed));
        }
        fireTableDataChanged();
    }

    File fileAt(int row) {
        return files.get(row);
    }

    public int getRowCount() {
        return files.size();
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        File file = files.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> file.getName().isEmpty() ? file.getPath() : file.getName();
            case 1 -> file.isDirectory() ? "Folder" : "Document";
            case 2 -> file.isDirectory() ? "--" : formatSize(file.length());
            case 3 -> DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(file.lastModified()));
            default -> "";
        };
    }

    static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return (bytes / 1024) + " KB";
        }
        return String.format("%.1f MB", bytes / 1024.0 / 1024.0);
    }
}

record FileNode(String label, File file) {
    public String toString() {
        return label == null || label.isBlank() ? file.getPath() : label;
    }
}

class PlaceholderNode {
    public String toString() {
        return "Loading...";
    }
}

class CalculatorFrame extends JInternalFrame {
    private final JTextField display = new JTextField();

    CalculatorFrame() {
        super("Calculator", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(300, 360);
        setLocation(520, 210);

        display.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.addActionListener(event -> calculate());

        JPanel buttons = new JPanel(new java.awt.GridLayout(5, 4, 4, 4));
        String[] labels = {"7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "0", ".", "=", "+", "C", "(", ")", "Del"};
        for (String label : labels) {
            JButton button = new JButton(label);
            button.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            button.addActionListener(event -> press(label));
            buttons.add(button);
        }

        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        root.add(display, BorderLayout.NORTH);
        root.add(buttons, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void press(String label) {
        if (label.equals("C")) {
            display.setText("");
        } else if (label.equals("Del")) {
            String text = display.getText();
            display.setText(text.isEmpty() ? "" : text.substring(0, text.length() - 1));
        } else if (label.equals("=")) {
            calculate();
        } else {
            display.setText(display.getText() + label);
        }
    }

    private void calculate() {
        try {
            display.setText(Double.toString(new ExpressionParser(display.getText()).parse()));
        } catch (RuntimeException exception) {
            display.setText("Error");
        }
    }

    static class ExpressionParser {
        private final String text;
        private int index;

        ExpressionParser(String text) {
            this.text = text.replace(" ", "");
        }

        double parse() {
            double value = expression();
            if (index != text.length()) {
                throw new IllegalArgumentException();
            }
            return value;
        }

        private double expression() {
            double value = term();
            while (index < text.length()) {
                char operator = text.charAt(index);
                if (operator != '+' && operator != '-') {
                    return value;
                }
                index++;
                value = operator == '+' ? value + term() : value - term();
            }
            return value;
        }

        private double term() {
            double value = factor();
            while (index < text.length()) {
                char operator = text.charAt(index);
                if (operator != '*' && operator != '/') {
                    return value;
                }
                index++;
                value = operator == '*' ? value * factor() : value / factor();
            }
            return value;
        }

        private double factor() {
            if (index < text.length() && text.charAt(index) == '(') {
                index++;
                double value = expression();
                if (index >= text.length() || text.charAt(index) != ')') {
                    throw new IllegalArgumentException();
                }
                index++;
                return value;
            }
            int start = index;
            if (index < text.length() && (text.charAt(index) == '+' || text.charAt(index) == '-')) {
                index++;
            }
            while (index < text.length() && (Character.isDigit(text.charAt(index)) || text.charAt(index) == '.')) {
                index++;
            }
            return Double.parseDouble(text.substring(start, index));
        }
    }
}

class ClockFrame extends JInternalFrame {
    private final JLabel time = new JLabel("", JLabel.CENTER);
    private final JLabel date = new JLabel("", JLabel.CENTER);

    ClockFrame() {
        super("Clock", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(360, 180);
        setLocation(560, 250);

        time.setFont(new Font(Font.MONOSPACED, Font.BOLD, 32));
        date.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JPanel root = new JPanel(new java.awt.GridLayout(2, 1));
        root.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        root.add(time);
        root.add(date);
        setContentPane(root);
        tick();
        new javax.swing.Timer(1000, event -> tick()).start();
    }

    private void tick() {
        Date now = new Date();
        time.setText(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(now));
        date.setText(DateFormat.getDateInstance(DateFormat.FULL).format(now));
    }
}

class SystemInfoFrame extends JInternalFrame {
    private final JTextArea info = new JTextArea();

    SystemInfoFrame() {
        super("Sys Info", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(560, 420);
        setLocation(590, 290);
        info.setEditable(false);
        info.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        setContentPane(new JScrollPane(info));
        refresh();
        setJMenuBar(menu());
    }

    private JMenuBar menu() {
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("System");
        JMenuItem refresh = new JMenuItem("Refresh");
        refresh.addActionListener(event -> refresh());
        menu.add(refresh);
        bar.add(menu);
        return bar;
    }

    private void refresh() {
        Runtime runtime = Runtime.getRuntime();
        File home = new File(System.getProperty("user.home"));
        info.setText("""
                Mactonish System Information

                Java: %s
                JVM: %s
                OS: %s %s
                User: %s
                Home: %s
                Working Dir: %s

                Memory Used: %s
                Memory Free: %s
                Memory Max: %s
                CPU Cores: %d

                Home Free Space: %s
                Home Total Space: %s
                """.formatted(
                System.getProperty("java.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("user.name"),
                home.getAbsolutePath(),
                new File(".").getAbsolutePath(),
                FileTableModel.formatSize(runtime.totalMemory() - runtime.freeMemory()),
                FileTableModel.formatSize(runtime.freeMemory()),
                FileTableModel.formatSize(runtime.maxMemory()),
                runtime.availableProcessors(),
                FileTableModel.formatSize(home.getFreeSpace()),
                FileTableModel.formatSize(home.getTotalSpace())
        ));
    }
}

class HelpFrame extends JInternalFrame {
    HelpFrame() {
        super("Help", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(620, 460);
        setLocation(620, 330);
        JTextArea help = new JTextArea();
        help.setEditable(false);
        help.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        help.setText("""
                Mactonish Help

                Finder      Browse files and edit text/code in built-in NANO.
                P-Run       Run files or project folders by extension/signature.
                Terminal    Run shell commands. Use cd, pwd, clear, exit.
                Notepad     Write quick notes and save text files.
                App Maker   Create runnable Java or Rust app folders.
                File Edit   Open a text file by chooser/path and edit it.
                Browser     Browse simple HTML/search; use Open External for JS.
                Calculator  Basic arithmetic with parentheses.
                Clock       Local date and time.
                Sys Info    Java, OS, memory, CPU, and disk information.

                Tips

                - P-Run understands run.sh, package.json, Cargo.toml, go.mod,
                  Gradle/Maven files, Python/Node entry files, Java files, and
                  executable files.
                - App Maker projects include run.sh so P-Run can launch them.
                - Browser is intentionally lightweight. Modern web apps need
                  Open External.
                """);
        setContentPane(new JScrollPane(help));
    }
}
