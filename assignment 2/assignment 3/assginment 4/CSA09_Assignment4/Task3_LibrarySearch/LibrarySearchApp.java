import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Searches a small built-in book catalog by title, author, or ISBN.
 */
public class LibrarySearchApp extends JFrame {
    private static final String[][] CATALOG = {
        {"Clean Code", "Robert C. Martin", "9780132350884", "Software Engineering", "2008"},
        {"Effective Java", "Joshua Bloch", "9780134685991", "Programming", "2018"},
        {"The Hobbit", "J. R. R. Tolkien", "9780547928227", "Fantasy", "1937"},
        {"A Brief History of Time", "Stephen Hawking", "9780553380163", "Science", "1988"},
        {"Introduction to Algorithms", "Thomas H. Cormen", "9780262046305", "Computer Science", "2022"},
        {"Pride and Prejudice", "Jane Austen", "9780141439518", "Classic Fiction", "1813"}
    };

    private final JTextField searchField = new JTextField(28);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Title", "Author", "ISBN", "Category", "Year"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable bookTable = new JTable(tableModel);

    public LibrarySearchApp() {
        super("Library Book Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createContent());
        refreshCatalog();
        setSize(820, 420);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        searchPanel.add(new JLabel("Search title, author, or ISBN:"));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(event -> searchBooks());
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> {
            searchField.setText("");
            tableModel.setRowCount(0);
        });
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refreshCatalog());
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        searchPanel.add(refreshButton);

        bookTable.setFillsViewportHeight(true);
        bookTable.setAutoCreateRowSorter(true);
        content.add(searchPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(bookTable), BorderLayout.CENTER);
        return content;
    }

    private void searchBooks() {
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        List<String[]> matches = new ArrayList<>();
        for (String[] book : CATALOG) {
            if (book[0].toLowerCase(Locale.ROOT).contains(query)
                    || book[1].toLowerCase(Locale.ROOT).contains(query)
                    || book[2].toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(book);
            }
        }
        displayBooks(matches);
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No books found for that search.",
                    "No results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshCatalog() {
        displayBooks(Arrays.asList(CATALOG));
    }

    private void displayBooks(List<String[]> books) {
        tableModel.setRowCount(0);
        for (String[] book : books) {
            tableModel.addRow(book);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchApp().setVisible(true));
    }
}
