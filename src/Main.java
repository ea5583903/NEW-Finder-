import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            FinderFrame frame = new FinderFrame();
            frame.setVisible(true);
        });
    }
}

class FinderFrame extends JFrame {
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
        super("EMF Finder 1984");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setSize(1080, 680);
        setLocationRelativeTo(null);

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
