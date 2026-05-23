import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
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
import javax.swing.JPasswordField;
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
import javax.swing.JWindow;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.GeneralSecurityException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;

public class Main {
    private static final String DESKTOP_PASSWORD = "misscircle";
    private static final String SECONDARY_DESKTOP_PASSWORD = "chip";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            if (!unlockDesktop()) {
                System.exit(0);
                return;
            }

            Win95Startup.show(() -> {
                DesktopFrame frame = new DesktopFrame();
                frame.setVisible(true);
            });
        });
    }

    private static boolean unlockDesktop() {
        while (true) {
            JPasswordField passwordField = new JPasswordField(18);
            passwordField.setEchoChar('*');
            JPanel panel = new JPanel(new BorderLayout(8, 8));
            panel.add(new JLabel("Enter desktop password:"), BorderLayout.NORTH);
            panel.add(passwordField, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Mactonish Login",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                int resetResult = JOptionPane.showOptionDialog(
                        null,
                        "Need help getting back into your account?",
                        "Mactonish Login",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Forgot Password", "Try Again", "Shut Down"},
                        "Forgot Password"
                );

                if (resetResult == JOptionPane.YES_OPTION) {
                    fakeResetAccount();
                    continue;
                }
                if (resetResult == JOptionPane.NO_OPTION) {
                    continue;
                }
                return false;
            }

            char[] entered = passwordField.getPassword();
            String password = new String(entered);
            boolean correct = DESKTOP_PASSWORD.equals(password) || SECONDARY_DESKTOP_PASSWORD.equals(password);
            Arrays.fill(entered, '\0');
            if (correct) {
                return true;
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Wrong password.",
                    "Mactonish Login",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void fakeResetAccount() {
        JTextField accountField = new JTextField(18);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JLabel("Account name:"), BorderLayout.NORTH);
        panel.add(accountField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Reset Account",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        JOptionPane.showMessageDialog(
                null,
                "Reset request accepted.\nChecking recovery disk...",
                "Reset Account",
                JOptionPane.INFORMATION_MESSAGE
        );
        JOptionPane.showMessageDialog(
                null,
                "Recovery complete.\nPassword hints are unavailable for this account.",
                "Reset Account",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}

class Win95Startup {
    private static final Color TEAL = new Color(0, 128, 128);
    private static final int DURATION_MS = 3600;

    static void show(Runnable afterStartup) {
        JWindow splash = new JWindow();
        StartupPanel panel = new StartupPanel();
        splash.setContentPane(panel);
        splash.setSize(panel.preferredSplashSize());
        splash.setLocationRelativeTo(null);
        splash.setAlwaysOnTop(true);
        splash.setVisible(true);
        playStartupSound();

        javax.swing.Timer timer = new javax.swing.Timer(DURATION_MS, event -> {
            splash.setVisible(false);
            splash.dispose();
            afterStartup.run();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static void playStartupSound() {
        new Thread(() -> {
            try {
                Path sound = extractSound();
                String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
                ProcessBuilder player;
                if (os.contains("mac")) {
                    player = new ProcessBuilder("afplay", sound.toString());
                } else if (os.contains("win")) {
                    String uri = sound.toUri().toString().replace("'", "''");
                    String command = "Add-Type -AssemblyName presentationCore; "
                            + "$player = New-Object System.Windows.Media.MediaPlayer; "
                            + "$player.Open([Uri]'" + uri + "'); "
                            + "$player.Play(); Start-Sleep -Milliseconds 5000";
                    player = new ProcessBuilder("powershell", "-NoProfile", "-Command", command);
                } else {
                    player = new ProcessBuilder("ffplay", "-nodisp", "-autoexit", "-loglevel", "quiet", sound.toString());
                }
                player.start();
            } catch (Exception ignored) {
            }
        }, "win95-startup-sound").start();
    }

    private static Path extractSound() throws IOException {
        Path sound = Files.createTempFile("mactonish-startup-", ".mp3");
        sound.toFile().deleteOnExit();
        try (InputStream input = Win95Startup.class.getResourceAsStream("/windows-xp-startup.mp3")) {
            if (input == null) {
                Path fallback = Path.of(System.getProperty("user.home"), "Downloads", "windows-xp-startup.mp3");
                if (!Files.exists(fallback)) {
                    fallback = Path.of(System.getProperty("user.home"), "downloads", "windows-xp-startup.mp3");
                }
                Files.copy(fallback, sound, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(input, sound, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return sound;
    }

    private static class StartupPanel extends JPanel {
        private final BufferedImage startupImage;

        StartupPanel() {
            setBackground(TEAL);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            startupImage = loadStartupImage();
        }

        Dimension preferredSplashSize() {
            if (startupImage == null) {
                return new Dimension(640, 420);
            }
            return new Dimension(startupImage.getWidth(), startupImage.getHeight());
        }

        private BufferedImage loadStartupImage() {
            try (InputStream input = Win95Startup.class.getResourceAsStream("/win.png")) {
                if (input != null) {
                    return ImageIO.read(input);
                }
            } catch (IOException ignored) {
            }

            try {
                Path fallback = Path.of(System.getProperty("user.home"), "Downloads", "win.png");
                if (!Files.exists(fallback)) {
                    fallback = Path.of(System.getProperty("user.home"), "downloads", "win.png");
                }
                if (Files.exists(fallback)) {
                    return ImageIO.read(fallback.toFile());
                }
            } catch (IOException ignored) {
            }
            return null;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            if (startupImage != null) {
                g.drawImage(startupImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(Color.BLACK);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 66));
                g.drawString("Windows", 142, 180);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 92));
                g.drawString("95", 392, 214);
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 19));
                g.drawString("Starting up...", 245, 258);
            }

            g.dispose();
        }
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
    private MusicEditorFrame musicEditor;
    private CalculatorFrame calculator;
    private ClockFrame clock;
    private SystemInfoFrame systemInfo;
    private HelpFrame help;
    private SshPhpFrame sshPhp;
    private PasswordVaultFrame passwordVault;
    private ImageViewerFrame imageViewer;
    private PaintFrame paint;
    private RemindersFrame reminders;

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
        addDesktopIcon("Music Edit", 34, 634, this::openMusicEditor);
        addDesktopIcon("Calculator", 140, 34, this::openCalculator);
        addDesktopIcon("Clock", 140, 134, this::openClock);
        addDesktopIcon("Sys Info", 140, 234, this::openSystemInfo);
        addDesktopIcon("Help", 140, 334, this::openHelp);
        addDesktopIcon("SSH", 140, 434, this::openSshPhp);
        addDesktopIcon("Vault", 140, 534, this::openPasswordVault);
        addDesktopIcon("Images", 246, 34, this::openImageViewer);
        addDesktopIcon("Paint", 246, 134, this::openPaint);
        addDesktopIcon("Reminders", 246, 234, this::openReminders);
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
                "Mactonish System 1.5.3\nFinder, P-Run, Terminal, Notepad, App Maker, File Edit, Music Edit, Calculator, Clock, Sys Info, Help, SSH Connect, Vault, Images, Paint, and Reminders are built in.",
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
        JMenuItem musicEditorItem = new JMenuItem("Music Edit");
        musicEditorItem.addActionListener(event -> openMusicEditor());
        JMenuItem calculatorItem = new JMenuItem("Calculator");
        calculatorItem.addActionListener(event -> openCalculator());
        JMenuItem clockItem = new JMenuItem("Clock");
        clockItem.addActionListener(event -> openClock());
        JMenuItem systemInfoItem = new JMenuItem("Sys Info");
        systemInfoItem.addActionListener(event -> openSystemInfo());
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(event -> openHelp());
        JMenuItem sshItem = new JMenuItem("SSH Connect");
        sshItem.addActionListener(event -> openSshPhp());
        JMenuItem vaultItem = new JMenuItem("Password Vault");
        vaultItem.addActionListener(event -> openPasswordVault());
        JMenuItem imagesItem = new JMenuItem("Image Viewer");
        imagesItem.addActionListener(event -> openImageViewer());
        JMenuItem paintItem = new JMenuItem("Paint");
        paintItem.addActionListener(event -> openPaint());
        JMenuItem remindersItem = new JMenuItem("Reminders");
        remindersItem.addActionListener(event -> openReminders());
        apps.add(finderItem);
        apps.add(pRunItem);
        apps.add(terminalItem);
        apps.add(notepadItem);
        apps.add(appCreatorItem);
        apps.add(fileEditorItem);
        apps.add(musicEditorItem);
        apps.add(calculatorItem);
        apps.add(clockItem);
        apps.add(systemInfoItem);
        apps.add(helpItem);
        apps.add(sshItem);
        apps.add(vaultItem);
        apps.add(imagesItem);
        apps.add(paintItem);
        apps.add(remindersItem);

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

    private void openMusicEditor() {
        try {
            if (musicEditor == null || musicEditor.isClosed()) {
                musicEditor = new MusicEditorFrame();
                desktop.add(musicEditor, JLayeredPane.PALETTE_LAYER);
                musicEditor.setVisible(true);
            }
            musicEditor.setIcon(false);
            musicEditor.moveToFront();
            musicEditor.setSelected(true);
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

    private void openSshPhp() {
        try {
            if (sshPhp == null || sshPhp.isClosed()) {
                sshPhp = new SshPhpFrame();
                desktop.add(sshPhp, JLayeredPane.PALETTE_LAYER);
                sshPhp.setVisible(true);
            }
            sshPhp.setIcon(false);
            sshPhp.moveToFront();
            sshPhp.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openPasswordVault() {
        try {
            if (passwordVault == null || passwordVault.isClosed()) {
                passwordVault = new PasswordVaultFrame();
                desktop.add(passwordVault, JLayeredPane.PALETTE_LAYER);
                passwordVault.setVisible(true);
            }
            passwordVault.setIcon(false);
            passwordVault.moveToFront();
            passwordVault.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openImageViewer() {
        try {
            if (imageViewer == null || imageViewer.isClosed()) {
                imageViewer = new ImageViewerFrame();
                desktop.add(imageViewer, JLayeredPane.PALETTE_LAYER);
                imageViewer.setVisible(true);
            }
            imageViewer.setIcon(false);
            imageViewer.moveToFront();
            imageViewer.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openPaint() {
        try {
            if (paint == null || paint.isClosed()) {
                paint = new PaintFrame();
                desktop.add(paint, JLayeredPane.PALETTE_LAYER);
                paint.setVisible(true);
            }
            paint.setIcon(false);
            paint.moveToFront();
            paint.setSelected(true);
        } catch (Exception ignored) {
        }
    }

    private void openReminders() {
        try {
            if (reminders == null || reminders.isClosed()) {
                reminders = new RemindersFrame();
                desktop.add(reminders, JLayeredPane.PALETTE_LAYER);
                reminders.setVisible(true);
            }
            reminders.setIcon(false);
            reminders.moveToFront();
            reminders.setSelected(true);
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
                if (lower.endsWith(".php")) {
                    return runProcess(Arrays.asList("php", name), directory);
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
                if (child(folder, "index.php").exists()) {
                    return runProcess(Arrays.asList("php", "index.php"), folder);
                }
                if (child(folder, "main.php").exists()) {
                    return runProcess(Arrays.asList("php", "main.php"), folder);
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
        if (lower.endsWith(".php")) {
            return "php script";
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

class SshPhpFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JTextField hostField = new JTextField();
    private final JTextField userField = new JTextField(System.getProperty("user.name"));
    private final JTextField portField = new JTextField("22");
    private final JTextField commandField = new JTextField("pwd");
    private final JTextField remotePathField = new JTextField("~/remote.txt");
    private final JTextField localPathField = new JTextField(System.getProperty("user.home"));
    private final JTextArea output = new JTextArea();
    private final JLabel status = new JLabel(" ready ");
    private final JFileChooser chooser = new JFileChooser();

    SshPhpFrame() {
        super("SSH Connect", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(780, 560);
        setMinimumSize(new Dimension(560, 380));
        setLocation(360, 170);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        for (JTextField field : Arrays.asList(hostField, userField, portField, commandField, remotePathField, localPathField)) {
            field.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            field.setBackground(PAPER);
            field.setForeground(INK);
        }

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        output.setBackground(Color.BLACK);
        output.setForeground(new Color(216, 216, 196));
        output.setText("SSH Connect uses PHP inside this app to run ssh/scp.\nIt still needs system php, ssh, and scp installed. Key-based auth is best.\n");

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
        JLabel titleText = new JLabel(" SSH CONNECT VIA PHP ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel fields = new JPanel(new java.awt.GridLayout(6, 2, 6, 6));
        fields.setBackground(PAPER);
        fields.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        fields.add(new JLabel(" Host "));
        fields.add(hostField);
        fields.add(new JLabel(" User "));
        fields.add(userField);
        fields.add(new JLabel(" Port "));
        fields.add(portField);
        fields.add(new JLabel(" Command "));
        fields.add(commandField);
        fields.add(new JLabel(" Remote Path "));
        fields.add(remotePathField);
        fields.add(new JLabel(" Local Path "));
        fields.add(localPathField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(PAPER);
        buttons.add(retroButton("Test", this::testConnection));
        buttons.add(retroButton("Run Command", this::runRemoteCommand));
        buttons.add(retroButton("Import", this::importRemote));
        buttons.add(retroButton("Export", this::exportLocal));
        buttons.add(retroButton("Choose Local...", this::chooseLocal));
        buttons.add(retroButton("PHP Version", this::phpVersion));
        buttons.add(retroButton("Clear", () -> output.setText("")));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(fields, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(output), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu ssh = new JMenu("SSH");
        JMenuItem test = new JMenuItem("Test");
        test.addActionListener(event -> testConnection());
        JMenuItem run = new JMenuItem("Run Command");
        run.addActionListener(event -> runRemoteCommand());
        JMenuItem in = new JMenuItem("Import");
        in.addActionListener(event -> importRemote());
        JMenuItem out = new JMenuItem("Export");
        out.addActionListener(event -> exportLocal());
        ssh.add(test);
        ssh.add(run);
        ssh.add(in);
        ssh.add(out);
        bar.add(ssh);
        return bar;
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

    private void testConnection() {
        runViaPhp(Arrays.asList("ssh", "-p", port(), target(), "echo connected; uname -a"));
    }

    private void runRemoteCommand() {
        runViaPhp(Arrays.asList("ssh", "-p", port(), target(), commandField.getText().trim()));
    }

    private void importRemote() {
        File local = resolveLocal(localPathField.getText().trim());
        runViaPhp(Arrays.asList("scp", "-P", port(), "-r", target() + ":" + remotePathField.getText().trim(), local.getAbsolutePath()));
    }

    private void exportLocal() {
        File local = resolveLocal(localPathField.getText().trim());
        if (!local.exists()) {
            output.append("\nLocal path does not exist: " + local.getAbsolutePath() + "\n");
            return;
        }
        runViaPhp(Arrays.asList("scp", "-P", port(), "-r", local.getAbsolutePath(), target() + ":" + remotePathField.getText().trim()));
    }

    private void chooseLocal() {
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            localPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void phpVersion() {
        runProcess(Arrays.asList("php", "-v"));
    }

    private void runViaPhp(List<String> command) {
        if (hostField.getText().trim().isEmpty() || userField.getText().trim().isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            output.append("\nHost and user are required.\n");
            return;
        }
        try {
            File script = File.createTempFile("mactonish-ssh-", ".php");
            script.deleteOnExit();
            Files.writeString(script.toPath(), """
                    <?php
                    $args = array_slice($argv, 1);
                    $cmd = implode(' ', array_map('escapeshellarg', $args));
                    passthru($cmd, $exit);
                    exit($exit);
                    ?>
                    """, StandardCharsets.UTF_8);
            List<String> phpCommand = new ArrayList<>();
            phpCommand.add("php");
            phpCommand.add(script.getAbsolutePath());
            phpCommand.addAll(command);
            runProcess(phpCommand);
        } catch (IOException exception) {
            status.setText(" failed ");
            output.append("\nCould not create PHP runner: " + exception.getMessage() + "\n");
        }
    }

    private void runProcess(List<String> command) {
        status.setText(" running ");
        output.append("\n$ " + String.join(" ", command) + "\n");
        new SwingWorker<Integer, String>() {
            protected Integer doInBackground() throws Exception {
                ProcessBuilder builder = new ProcessBuilder(command);
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
                output.setCaretPosition(output.getDocument().getLength());
            }

            protected void done() {
                try {
                    int exit = get();
                    status.setText(" exit " + exit + " ");
                    output.append("[exit " + exit + "]\n");
                } catch (Exception exception) {
                    status.setText(" failed ");
                    output.append("[failed: " + exception.getMessage() + "]\n");
                }
                output.setCaretPosition(output.getDocument().getLength());
            }
        }.execute();
    }

    private String target() {
        return userField.getText().trim() + "@" + hostField.getText().trim();
    }

    private String port() {
        String port = portField.getText().trim();
        return port.isEmpty() ? "22" : port;
    }

    private File resolveLocal(String typed) {
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

class MusicEditorFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JFileChooser chooser = new JFileChooser();
    private final JTextArea score = new JTextArea();
    private final JTextField tempoField = new JTextField("120");
    private final JLabel status = new JLabel(" ready ");
    private File currentFile;
    private volatile boolean stopRequested;

    MusicEditorFrame() {
        super("Music Edit", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(560, 360));
        setLocation(460, 150);

        score.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        score.setBackground(PAPER);
        score.setForeground(INK);
        score.setText("""
                # Mactonish song
                # Type notes like: C4 D4 E4 F4 G4 A4 B4 C5
                # Use R for rest, and optional beats: C4:2 D4:0.5 R:1
                C4 D4 E4 F4 G4 A4 B4 C5
                """);
        tempoField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

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
        JLabel titleText = new JLabel(" MUSIC EDITOR ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout(6, 0));
        controls.setBackground(PAPER);
        controls.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(PAPER);
        buttons.add(new JLabel("Tempo"));
        buttons.add(tempoField);
        buttons.add(retroButton("Play", this::playSong));
        buttons.add(retroButton("Stop", this::allNotesOff));
        buttons.add(retroButton("Open...", this::openSong));
        buttons.add(retroButton("Save", this::saveSong));
        buttons.add(retroButton("Save As...", this::saveSongAs));
        controls.add(buttons, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(controls, BorderLayout.CENTER);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(score), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu music = new JMenu("Music");
        JMenuItem play = new JMenuItem("Play");
        play.addActionListener(event -> playSong());
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(event -> openSong());
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(event -> saveSong());
        music.add(play);
        music.add(open);
        music.add(save);
        menuBar.add(music);
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

    private void playSong() {
        stopRequested = false;
        status.setText(" playing ");
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                playScore(score.getText(), tempo());
                return null;
            }

            protected void done() {
                try {
                    get();
                    status.setText(" done ");
                } catch (Exception exception) {
                    status.setText(" play failed ");
                    JOptionPane.showMessageDialog(MusicEditorFrame.this, "Playback failed:\n" + exception.getMessage(), "Music Edit", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void playScore(String text, int tempo) throws MidiUnavailableException, InterruptedException {
        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();
        try {
            MidiChannel channel = synth.getChannels()[0];
            channel.programChange(0);
            double beatMs = 60000.0 / tempo;
            for (String line : text.split("\\R")) {
                String clean = line.split("#", 2)[0];
                for (String token : clean.split("\\s+")) {
                    if (stopRequested) {
                        break;
                    }
                    if (token.isBlank()) {
                        continue;
                    }
                    NoteEvent event = parseNote(token);
                    int duration = Math.max(40, (int) (beatMs * event.beats));
                    if (event.pitch >= 0) {
                        channel.noteOn(event.pitch, 90);
                        Thread.sleep(duration);
                        channel.noteOff(event.pitch);
                    } else {
                        Thread.sleep(duration);
                    }
                }
            }
        } finally {
            synth.close();
        }
    }

    private NoteEvent parseNote(String token) {
        String[] parts = token.split(":", 2);
        double beats = parts.length == 2 ? Double.parseDouble(parts[1]) : 1.0;
        if (parts[0].equalsIgnoreCase("R")) {
            return new NoteEvent(-1, beats);
        }
        String note = parts[0].toUpperCase(Locale.ROOT);
        int octave = Character.getNumericValue(note.charAt(note.length() - 1));
        String name = note.substring(0, note.length() - 1);
        List<String> names = Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");
        int semitone = names.indexOf(name);
        if (semitone < 0) {
            throw new IllegalArgumentException("Bad note: " + token);
        }
        return new NoteEvent(12 * (octave + 1) + semitone, beats);
    }

    private int tempo() {
        return Math.max(30, Math.min(300, Integer.parseInt(tempoField.getText().trim())));
    }

    private void allNotesOff() {
        stopRequested = true;
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            for (MidiChannel channel : synth.getChannels()) {
                if (channel != null) {
                    channel.allNotesOff();
                }
            }
            synth.close();
            status.setText(" stopped ");
        } catch (MidiUnavailableException exception) {
            status.setText(" stop failed ");
        }
    }

    private void openSong() {
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                currentFile = chooser.getSelectedFile();
                score.setText(Files.readString(currentFile.toPath(), StandardCharsets.UTF_8));
                status.setText(" " + currentFile.getAbsolutePath() + " ");
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(this, "Open failed:\n" + exception.getMessage(), "Music Edit", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveSong() {
        if (currentFile == null) {
            saveSongAs();
            return;
        }
        try {
            Files.writeString(currentFile.toPath(), score.getText(), StandardCharsets.UTF_8);
            status.setText(" saved ");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Save failed:\n" + exception.getMessage(), "Music Edit", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSongAs() {
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            saveSong();
        }
    }

    record NoteEvent(int pitch, double beats) {
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

class PasswordVaultFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;
    private static final File STORE = new File(new File(System.getProperty("user.home"), ".mactonish"), "vault.dat");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final JPasswordField masterField = new JPasswordField();
    private final JLabel status = new JLabel(" locked ");
    private final VaultTableModel model = new VaultTableModel();
    private final JTable table = new JTable(model);
    private final JCheckBox reveal = new JCheckBox("Reveal");
    private char[] masterPassword;

    PasswordVaultFrame() {
        super("Password Vault", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(760, 440);
        setMinimumSize(new Dimension(560, 320));
        setLocation(420, 180);

        masterField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        masterField.addActionListener(event -> unlock());
        table.setEnabled(false);
        table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        table.setRowHeight(24);
        reveal.setBackground(PAPER);
        reveal.addActionListener(event -> model.setReveal(reveal.isSelected()));

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
        JLabel titleText = new JLabel(" PASSWORD VAULT ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel unlockBar = new JPanel(new BorderLayout(6, 0));
        unlockBar.setBackground(PAPER);
        unlockBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        unlockBar.add(new JLabel(" Master "), BorderLayout.WEST);
        unlockBar.add(masterField, BorderLayout.CENTER);

        JPanel unlockButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        unlockButtons.setBackground(PAPER);
        unlockButtons.add(retroButton("Unlock", this::unlock));
        unlockButtons.add(retroButton("Create Vault", this::createVault));
        unlockButtons.add(retroButton("Lock", this::lock));
        unlockBar.add(unlockButtons, BorderLayout.EAST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(PAPER);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        buttons.add(retroButton("Add", this::addEntry));
        buttons.add(retroButton("Remove", this::removeEntry));
        buttons.add(retroButton("Save", this::save));
        buttons.add(reveal);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(unlockBar, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu vault = new JMenu("Vault");
        JMenuItem unlock = new JMenuItem("Unlock");
        unlock.addActionListener(event -> unlock());
        JMenuItem create = new JMenuItem("Create Vault");
        create.addActionListener(event -> createVault());
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(event -> save());
        JMenuItem lock = new JMenuItem("Lock");
        lock.addActionListener(event -> lock());
        vault.add(unlock);
        vault.add(create);
        vault.add(save);
        vault.add(lock);
        bar.add(vault);
        return bar;
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

    private void unlock() {
        if (!STORE.exists() || STORE.length() == 0) {
            createVault();
            return;
        }
        char[] password = masterField.getPassword();
        if (password.length == 0) {
            Toolkit.getDefaultToolkit().beep();
            status.setText(" enter master password ");
            return;
        }
        try {
            model.load(decrypt(Files.readString(STORE.toPath(), StandardCharsets.UTF_8), password));
            status.setText(" unlocked " + model.getRowCount() + " entries ");
            masterPassword = Arrays.copyOf(password, password.length);
            table.setEnabled(true);
            masterField.setText("");
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unlock failed. Check the master password.", "Password Vault", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void createVault() {
        if (STORE.exists() && STORE.length() > 0) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "A vault already exists. Replace it with a new empty vault?",
                    "Password Vault",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        JPasswordField first = new JPasswordField();
        JPasswordField second = new JPasswordField();
        first.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        second.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel panel = new JPanel(new java.awt.GridLayout(2, 2, 6, 6));
        panel.add(new JLabel("New master password"));
        panel.add(first);
        panel.add(new JLabel("Confirm password"));
        panel.add(second);

        int choice = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Create Password Vault",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (choice != JOptionPane.OK_OPTION) {
            clear(first.getPassword(), second.getPassword());
            return;
        }

        char[] password = first.getPassword();
        char[] confirm = second.getPassword();
        try {
            if (password.length == 0) {
                Toolkit.getDefaultToolkit().beep();
                status.setText(" password required ");
                return;
            }
            if (!Arrays.equals(password, confirm)) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Password Vault", JOptionPane.ERROR_MESSAGE);
                return;
            }
            model.load("");
            masterPassword = Arrays.copyOf(password, password.length);
            table.setEnabled(true);
            reveal.setSelected(false);
            model.setReveal(false);
            masterField.setText("");
            save();
            status.setText(" new vault created ");
        } finally {
            clear(password, confirm);
        }
    }

    private void save() {
        if (masterPassword == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        try {
            Files.createDirectories(STORE.getParentFile().toPath());
            Files.writeString(STORE.toPath(), encrypt(model.dump(), masterPassword), StandardCharsets.UTF_8);
            status.setText(" saved " + STORE.getAbsolutePath() + " ");
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Save failed:\n" + exception.getMessage(), "Password Vault", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lock() {
        model.load("");
        table.setEnabled(false);
        if (masterPassword != null) {
            Arrays.fill(masterPassword, '\0');
        }
        masterPassword = null;
        status.setText(" locked ");
    }

    private void clear(char[]... values) {
        for (char[] value : values) {
            if (value != null) {
                Arrays.fill(value, '\0');
            }
        }
    }

    private void addEntry() {
        if (masterPassword == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        model.addEntry();
    }

    private void removeEntry() {
        if (masterPassword == null || table.getSelectedRow() < 0) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        model.removeEntry(table.convertRowIndexToModel(table.getSelectedRow()));
    }

    private String encrypt(String plainText, char[] password) throws GeneralSecurityException {
        byte[] salt = new byte[16];
        byte[] nonce = new byte[12];
        RANDOM.nextBytes(salt);
        RANDOM.nextBytes(nonce);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key(password, salt), new GCMParameterSpec(128, nonce));
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return "MVAULT1\n"
                + Base64.getEncoder().encodeToString(salt) + "\n"
                + Base64.getEncoder().encodeToString(nonce) + "\n"
                + Base64.getEncoder().encodeToString(cipherText) + "\n";
    }

    private String decrypt(String stored, char[] password) throws GeneralSecurityException {
        String[] parts = stored.split("\\R");
        if (parts.length < 4 || !parts[0].equals("MVAULT1")) {
            throw new GeneralSecurityException("Unknown vault format");
        }
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] nonce = Base64.getDecoder().decode(parts[2]);
        byte[] cipherText = Base64.getDecoder().decode(parts[3]);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key(password, salt), new GCMParameterSpec(128, nonce));
        return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
    }

    private SecretKey key(char[] password, byte[] salt) throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 120_000, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    record VaultEntry(String service, String user, String password, String notes) {
    }

    static class VaultTableModel extends AbstractTableModel {
        private static final String[] COLUMNS = {"Service", "User", "Password", "Notes"};
        private final List<VaultEntry> entries = new ArrayList<>();
        private boolean reveal;

        public int getRowCount() {
            return entries.size();
        }

        public int getColumnCount() {
            return COLUMNS.length;
        }

        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        public boolean isCellEditable(int row, int column) {
            return true;
        }

        public Object getValueAt(int row, int column) {
            VaultEntry entry = entries.get(row);
            return switch (column) {
                case 0 -> entry.service();
                case 1 -> entry.user();
                case 2 -> reveal ? entry.password() : "********";
                case 3 -> entry.notes();
                default -> "";
            };
        }

        public void setValueAt(Object value, int row, int column) {
            VaultEntry old = entries.get(row);
            String text = value == null ? "" : value.toString();
            entries.set(row, switch (column) {
                case 0 -> new VaultEntry(text, old.user(), old.password(), old.notes());
                case 1 -> new VaultEntry(old.service(), text, old.password(), old.notes());
                case 2 -> new VaultEntry(old.service(), old.user(), text, old.notes());
                case 3 -> new VaultEntry(old.service(), old.user(), old.password(), text);
                default -> old;
            });
            fireTableRowsUpdated(row, row);
        }

        void setReveal(boolean reveal) {
            this.reveal = reveal;
            fireTableDataChanged();
        }

        void addEntry() {
            entries.add(new VaultEntry("", "", "", ""));
            fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
        }

        void removeEntry(int row) {
            entries.remove(row);
            fireTableRowsDeleted(row, row);
        }

        void load(String text) {
            entries.clear();
            for (String line : text.split("\\R")) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\t", -1);
                entries.add(new VaultEntry(unpack(parts, 0), unpack(parts, 1), unpack(parts, 2), unpack(parts, 3)));
            }
            fireTableDataChanged();
        }

        String dump() {
            StringBuilder out = new StringBuilder();
            for (VaultEntry entry : entries) {
                out.append(pack(entry.service())).append('\t')
                        .append(pack(entry.user())).append('\t')
                        .append(pack(entry.password())).append('\t')
                        .append(pack(entry.notes())).append('\n');
            }
            return out.toString();
        }

        private static String pack(String value) {
            return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
        }

        private static String unpack(String[] parts, int index) {
            if (index >= parts.length || parts[index].isEmpty()) {
                return "";
            }
            return new String(Base64.getDecoder().decode(parts[index]), StandardCharsets.UTF_8);
        }
    }
}

class ImageViewerFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JFileChooser chooser = new JFileChooser();
    private final JLabel imageLabel = new JLabel("Open an image.", JLabel.CENTER);
    private final JLabel status = new JLabel(" no image ");
    private BufferedImage image;
    private File currentFile;
    private double zoom = 1.0;

    ImageViewerFrame() {
        super("Image Viewer", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(520, 360));
        setLocation(450, 210);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
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
        JLabel titleText = new JLabel(" IMAGE VIEWER ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(PAPER);
        buttons.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        buttons.add(retroButton("Open...", this::openImage));
        buttons.add(retroButton("Fit", this::fit));
        buttons.add(retroButton("100%", () -> setZoom(1.0)));
        buttons.add(retroButton("+", () -> setZoom(zoom * 1.25)));
        buttons.add(retroButton("-", () -> setZoom(zoom / 1.25)));
        buttons.add(retroButton("Rotate", this::rotate));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.CENTER);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(event -> openImage());
        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(event -> dispose());
        file.add(open);
        file.add(close);
        bar.add(file);
        return bar;
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

    private void openImage() {
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            BufferedImage loaded = ImageIO.read(chooser.getSelectedFile());
            if (loaded == null) {
                throw new IOException("Unsupported image format");
            }
            image = loaded;
            currentFile = chooser.getSelectedFile();
            zoom = 1.0;
            updateImage();
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Open failed:\n" + exception.getMessage(), "Image Viewer", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fit() {
        if (image == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        Dimension size = imageLabel.getParent().getSize();
        double x = Math.max(0.1, (size.getWidth() - 24) / image.getWidth());
        double y = Math.max(0.1, (size.getHeight() - 24) / image.getHeight());
        setZoom(Math.min(x, y));
    }

    private void setZoom(double nextZoom) {
        if (image == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        zoom = Math.max(0.1, Math.min(8.0, nextZoom));
        updateImage();
    }

    private void rotate() {
        if (image == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        BufferedImage rotated = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotated.createGraphics();
        g.translate(image.getHeight(), 0);
        g.rotate(Math.PI / 2);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        image = rotated;
        updateImage();
    }

    private void updateImage() {
        int width = Math.max(1, (int) Math.round(image.getWidth() * zoom));
        int height = Math.max(1, (int) Math.round(image.getHeight() * zoom));
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        imageLabel.setText("");
        imageLabel.setIcon(new ImageIcon(scaled));
        status.setText(" " + currentFile.getName() + " " + image.getWidth() + "x" + image.getHeight() + " zoom " + Math.round(zoom * 100) + "% ");
    }
}

class PaintFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;

    private final JFileChooser chooser = new JFileChooser();
    private final PaintCanvas canvas = new PaintCanvas();
    private final JComboBox<String> colorBox = new JComboBox<>(new String[]{"Black", "Red", "Blue", "Green", "Yellow", "White"});
    private final JComboBox<Integer> brushBox = new JComboBox<>(new Integer[]{2, 4, 8, 12, 20, 32});
    private final JLabel status = new JLabel(" 640x360 ");

    PaintFrame() {
        super("Paint", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(760, 520);
        setMinimumSize(new Dimension(560, 360));
        setLocation(480, 240);
        colorBox.addActionListener(event -> canvas.setPaintColor(selectedColor()));
        brushBox.addActionListener(event -> canvas.setBrush((Integer) brushBox.getSelectedItem()));
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
        JLabel titleText = new JLabel(" PAINT ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tools.setBackground(PAPER);
        tools.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        tools.add(retroButton("New", this::newCanvas));
        tools.add(retroButton("Open...", this::openImage));
        tools.add(retroButton("Save...", this::saveImage));
        tools.add(new JLabel(" Color "));
        tools.add(colorBox);
        tools.add(new JLabel(" Brush "));
        tools.add(brushBox);
        tools.add(retroButton("Clear", canvas::clear));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(tools, BorderLayout.CENTER);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(canvas), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem fresh = new JMenuItem("New");
        fresh.addActionListener(event -> newCanvas());
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(event -> openImage());
        JMenuItem save = new JMenuItem("Save...");
        save.addActionListener(event -> saveImage());
        file.add(fresh);
        file.add(open);
        file.add(save);
        bar.add(file);
        return bar;
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

    private void newCanvas() {
        canvas.newImage(640, 360);
        status.setText(" 640x360 new ");
    }

    private void openImage() {
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            BufferedImage loaded = ImageIO.read(chooser.getSelectedFile());
            if (loaded == null) {
                throw new IOException("Unsupported image format");
            }
            canvas.setImage(loaded);
            status.setText(" " + chooser.getSelectedFile().getName() + " " + loaded.getWidth() + "x" + loaded.getHeight() + " ");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Open failed:\n" + exception.getMessage(), "Paint", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveImage() {
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
            file = new File(file.getParentFile(), file.getName() + ".png");
        }
        try {
            ImageIO.write(canvas.image(), "png", file);
            status.setText(" saved " + file.getAbsolutePath() + " ");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Save failed:\n" + exception.getMessage(), "Paint", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Color selectedColor() {
        return switch ((String) colorBox.getSelectedItem()) {
            case "Red" -> Color.RED;
            case "Blue" -> Color.BLUE;
            case "Green" -> new Color(0, 130, 40);
            case "Yellow" -> Color.YELLOW;
            case "White" -> Color.WHITE;
            default -> Color.BLACK;
        };
    }

    static class PaintCanvas extends JPanel {
        private BufferedImage image;
        private Color paintColor = Color.BLACK;
        private int brush = 2;
        private int lastX = -1;
        private int lastY = -1;

        PaintCanvas() {
            setBackground(Color.LIGHT_GRAY);
            newImage(640, 360);
            MouseAdapter mouse = new MouseAdapter() {
                public void mousePressed(MouseEvent event) {
                    lastX = event.getX();
                    lastY = event.getY();
                    drawTo(lastX, lastY);
                }

                public void mouseDragged(MouseEvent event) {
                    drawLine(event.getX(), event.getY());
                }

                public void mouseReleased(MouseEvent event) {
                    lastX = -1;
                    lastY = -1;
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        void newImage(int width, int height) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            clear();
            setPreferredSize(new Dimension(width, height));
            revalidate();
        }

        void setImage(BufferedImage loaded) {
            image = new BufferedImage(loaded.getWidth(), loaded.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(loaded, 0, 0, null);
            g.dispose();
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            revalidate();
            repaint();
        }

        BufferedImage image() {
            return image;
        }

        void setPaintColor(Color paintColor) {
            this.paintColor = paintColor;
        }

        void setBrush(int brush) {
            this.brush = brush;
        }

        void clear() {
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.dispose();
            repaint();
        }

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.drawImage(image, 0, 0, null);
        }

        private void drawLine(int x, int y) {
            Graphics2D g = image.createGraphics();
            g.setColor(paintColor);
            g.setStroke(new BasicStroke(brush, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(lastX, lastY, x, y);
            g.dispose();
            lastX = x;
            lastY = y;
            repaint();
        }

        private void drawTo(int x, int y) {
            Graphics2D g = image.createGraphics();
            g.setColor(paintColor);
            g.fillOval(x - brush / 2, y - brush / 2, brush, brush);
            g.dispose();
            repaint();
        }
    }
}

class RemindersFrame extends JInternalFrame {
    private static final Color PAPER = new Color(238, 238, 226);
    private static final Color INK = Color.BLACK;
    private static final File STORE = new File(new File(System.getProperty("user.home"), ".mactonish"), "reminders.tsv");

    private final ReminderTableModel model = new ReminderTableModel();
    private final JTable table = new JTable(model);
    private final JTextField taskField = new JTextField();
    private final JTextField dueField = new JTextField();
    private final JLabel status = new JLabel(" ");

    RemindersFrame() {
        super("Reminders", true, true, true, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(700, 430);
        setMinimumSize(new Dimension(520, 320));
        setLocation(510, 270);
        table.setRowHeight(24);
        table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        taskField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        dueField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        taskField.addActionListener(event -> addReminder());
        dueField.addActionListener(event -> addReminder());
        setJMenuBar(buildMenu());
        setContentPane(buildWindow());
        load();
    }

    private JPanel buildWindow() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(PAPER);
        root.setBorder(BorderFactory.createLineBorder(INK, 3));

        JPanel title = new JPanel(new BorderLayout());
        title.setBackground(PAPER);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        JLabel titleText = new JLabel(" REMINDERS ", JLabel.CENTER);
        titleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        title.add(new JLabel("  □  "), BorderLayout.WEST);
        title.add(titleText, BorderLayout.CENTER);

        JPanel fields = new JPanel(new java.awt.GridLayout(2, 2, 6, 6));
        fields.setBackground(PAPER);
        fields.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        fields.add(new JLabel(" Task "));
        fields.add(taskField);
        fields.add(new JLabel(" Due "));
        fields.add(dueField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(PAPER);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        buttons.add(retroButton("Add", this::addReminder));
        buttons.add(retroButton("Remove", this::removeReminder));
        buttons.add(retroButton("Save", this::save));
        buttons.add(retroButton("Clear Done", this::clearDone));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PAPER);
        top.add(title, BorderLayout.NORTH);
        top.add(fields, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        status.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, INK));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu reminders = new JMenu("Reminders");
        JMenuItem add = new JMenuItem("Add");
        add.addActionListener(event -> addReminder());
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(event -> save());
        JMenuItem reload = new JMenuItem("Reload");
        reload.addActionListener(event -> load());
        reminders.add(add);
        reminders.add(save);
        reminders.add(reload);
        bar.add(reminders);
        return bar;
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

    private void addReminder() {
        String task = taskField.getText().trim();
        if (task.isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        model.addReminder(new Reminder(false, task, dueField.getText().trim()));
        taskField.setText("");
        dueField.setText("");
        save();
    }

    private void removeReminder() {
        if (table.getSelectedRow() < 0) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        model.removeReminder(table.convertRowIndexToModel(table.getSelectedRow()));
        save();
    }

    private void clearDone() {
        model.clearDone();
        save();
    }

    private void load() {
        try {
            if (STORE.exists()) {
                model.load(Files.readString(STORE.toPath(), StandardCharsets.UTF_8));
            }
            status.setText(" loaded " + model.getRowCount() + " reminders ");
        } catch (IOException exception) {
            status.setText(" load failed ");
        }
    }

    private void save() {
        try {
            Files.createDirectories(STORE.getParentFile().toPath());
            Files.writeString(STORE.toPath(), model.dump(), StandardCharsets.UTF_8);
            status.setText(" saved " + model.getRowCount() + " reminders ");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Save failed:\n" + exception.getMessage(), "Reminders", JOptionPane.ERROR_MESSAGE);
        }
    }

    record Reminder(boolean done, String task, String due) {
    }

    static class ReminderTableModel extends AbstractTableModel {
        private static final String[] COLUMNS = {"Done", "Task", "Due"};
        private final List<Reminder> reminders = new ArrayList<>();

        public int getRowCount() {
            return reminders.size();
        }

        public int getColumnCount() {
            return COLUMNS.length;
        }

        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        public Class<?> getColumnClass(int column) {
            return column == 0 ? Boolean.class : String.class;
        }

        public boolean isCellEditable(int row, int column) {
            return true;
        }

        public Object getValueAt(int row, int column) {
            Reminder reminder = reminders.get(row);
            return switch (column) {
                case 0 -> reminder.done();
                case 1 -> reminder.task();
                case 2 -> reminder.due();
                default -> "";
            };
        }

        public void setValueAt(Object value, int row, int column) {
            Reminder old = reminders.get(row);
            reminders.set(row, switch (column) {
                case 0 -> new Reminder(Boolean.TRUE.equals(value), old.task(), old.due());
                case 1 -> new Reminder(old.done(), value == null ? "" : value.toString(), old.due());
                case 2 -> new Reminder(old.done(), old.task(), value == null ? "" : value.toString());
                default -> old;
            });
            fireTableRowsUpdated(row, row);
        }

        void addReminder(Reminder reminder) {
            reminders.add(reminder);
            fireTableRowsInserted(reminders.size() - 1, reminders.size() - 1);
        }

        void removeReminder(int row) {
            reminders.remove(row);
            fireTableRowsDeleted(row, row);
        }

        void clearDone() {
            reminders.removeIf(Reminder::done);
            fireTableDataChanged();
        }

        void load(String text) {
            reminders.clear();
            for (String line : text.split("\\R")) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\t", -1);
                reminders.add(new Reminder(Boolean.parseBoolean(unpack(parts, 0)), unpack(parts, 1), unpack(parts, 2)));
            }
            fireTableDataChanged();
        }

        String dump() {
            StringBuilder out = new StringBuilder();
            for (Reminder reminder : reminders) {
                out.append(pack(Boolean.toString(reminder.done()))).append('\t')
                        .append(pack(reminder.task())).append('\t')
                        .append(pack(reminder.due())).append('\n');
            }
            return out.toString();
        }

        private static String pack(String value) {
            return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
        }

        private static String unpack(String[] parts, int index) {
            if (index >= parts.length || parts[index].isEmpty()) {
                return "";
            }
            return new String(Base64.getDecoder().decode(parts[index]), StandardCharsets.UTF_8);
        }
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
                Music Edit  Write note patterns, play them with MIDI, save songs.
                SSH Connect Connect with ssh/scp through PHP inside this app.
                Vault       Keep encrypted local passwords behind a master password.
                Images      Open, zoom, fit, and rotate image files.
                Paint       Draw simple PNG images with colors and brush sizes.
                Reminders   Track tasks with due text and done checkboxes.
                Calculator  Basic arithmetic with parentheses.
                Clock       Local date and time.
                Sys Info    Java, OS, memory, CPU, and disk information.

                Tips

                - P-Run understands run.sh, package.json, Cargo.toml, go.mod,
                  Gradle/Maven files, Python/Node/PHP entry files, Java files,
                  and executable files.
                - App Maker projects include run.sh so P-Run can launch them.
                - Music Edit uses notes like C4 D#4 R:1 A4:0.5. Lines can use
                  # comments, and tempo controls the beat length.
                - SSH Connect uses PHP to run system ssh/scp commands and keeps
                  output inside the Mactonish window.
                - Vault and Reminders save under ~/.mactonish.
                """);
        setContentPane(new JScrollPane(help));
    }
}
