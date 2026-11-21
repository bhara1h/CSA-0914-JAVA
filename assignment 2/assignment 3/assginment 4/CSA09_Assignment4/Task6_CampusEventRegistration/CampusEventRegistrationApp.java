import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Presentation Layer: Swing form for student event registration and search.
 */
public class CampusEventRegistrationApp extends JFrame {
    private final RegistrationService registrationService = new RegistrationService();
    private final JTextField studentNameField = new JTextField(18);
    private final JTextField registerNumberField = new JTextField(18);
    private final JComboBox<String> departmentBox = new JComboBox<>(new String[]{
            "Computer Science", "Information Technology", "Electronics",
            "Mechanical", "Civil", "Business"});
    private final JTextField emailField = new JTextField(18);
    private final JTextField phoneField = new JTextField(18);
    private final JComboBox<String> eventBox = new JComboBox<>(new String[]{
            "Technology Symposium", "Cultural Fest", "Sports Meet",
            "Career Fair", "Science Exhibition"});
    private final JTextField filterField = new JTextField(22);
    private final JButton registerButton = new JButton("Register");
    private final JButton searchButton = new JButton("Search");
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton previousButton = new JButton("Previous");
    private final JButton nextButton = new JButton("Next");
    private final JLabel pageLabel = new JLabel("Loading registrations...");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Student", "Register No.", "Department", "Email",
                "Phone", "Event", "Registered At"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable registrationTable = new JTable(tableModel);
    private int pageIndex;
    private int totalRecords;
    private boolean loading;

    public CampusEventRegistrationApp() {
        super("Campus Event Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createContent());
        setSize(1100, 680);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        loadPage(0);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student registration"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 6, 4, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addRow(form, constraints, 0, "Student name:", studentNameField);
        addRow(form, constraints, 1, "Register number:", registerNumberField);
        addRow(form, constraints, 2, "Department:", departmentBox);
        addRow(form, constraints, 3, "Email:", emailField);
        addRow(form, constraints, 4, "Phone:", phoneField);
        addRow(form, constraints, 5, "Select event:", eventBox);
        registerButton.addActionListener(event -> registerStudent());
        JButton clearFormButton = new JButton("Clear Form");
        clearFormButton.addActionListener(event -> clearForm());
        JPanel formButtons = new JPanel();
        formButtons.add(registerButton);
        formButtons.add(clearFormButton);
        constraints.gridx = 1;
        constraints.gridy = 6;
        constraints.weightx = 1;
        form.add(formButtons, constraints);

        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        filterPanel.add(new JLabel("Filter by student, register no., department, email, or event:"));
        filterPanel.add(filterField);
        searchButton.addActionListener(event -> loadPage(0));
        filterField.addActionListener(event -> loadPage(0));
        refreshButton.addActionListener(event -> {
            filterField.setText("");
            loadPage(0);
        });
        filterPanel.add(searchButton);
        filterPanel.add(refreshButton);

        JPanel top = new JPanel(new BorderLayout(4, 4));
        top.add(form, BorderLayout.CENTER);
        top.add(filterPanel, BorderLayout.SOUTH);

        registrationTable.setFillsViewportHeight(true);
        registrationTable.setAutoCreateRowSorter(true);
        JPanel pagination = new JPanel();
        previousButton.addActionListener(event -> {
            if (pageIndex > 0) {
                loadPage(pageIndex - 1);
            }
        });
        nextButton.addActionListener(event -> {
            int lastPage = totalRecords == 0 ? 0
                    : (totalRecords - 1) / registrationService.getPageSize();
            if (pageIndex < lastPage) {
                loadPage(pageIndex + 1);
            }
        });
        pagination.add(previousButton);
        pagination.add(pageLabel);
        pagination.add(nextButton);

        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(registrationTable), BorderLayout.CENTER);
        content.add(pagination, BorderLayout.SOUTH);
        return content;
    }

    private void addRow(JPanel panel, GridBagConstraints constraints, int row,
                        String label, java.awt.Component field) {
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.weightx = 0;
        panel.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(field, constraints);
    }

    private void registerStudent() {
        String name = studentNameField.getText();
        String registerNumber = registerNumberField.getText();
        String department = (String) departmentBox.getSelectedItem();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String selectedEvent = (String) eventBox.getSelectedItem();

        setLoading(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws SQLException {
                registrationService.register(name, registerNumber, department,
                        email, phone, selectedEvent);
                return null;
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    get();
                    JOptionPane.showMessageDialog(CampusEventRegistrationApp.this,
                            "Student registered successfully.",
                            "Registration complete", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    filterField.setText("");
                    loadPage(0);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showError("Registration was interrupted.", exception);
                } catch (ExecutionException exception) {
                    showError("Could not register student.", exception.getCause());
                }
            }
        }.execute();
    }

    private void loadPage(int requestedPage) {
        if (loading) {
            return;
        }
        String filter = filterField.getText().trim();
        setLoading(true);
        new SwingWorker<RegistrationPage, Void>() {
            @Override
            protected RegistrationPage doInBackground() throws SQLException {
                int count = registrationService.count(filter);
                int lastPage = count == 0 ? 0
                        : (count - 1) / registrationService.getPageSize();
                int safePage = Math.min(requestedPage, lastPage);
                List<Registration> registrations =
                        registrationService.getPage(filter, safePage);
                return new RegistrationPage(registrations, count, safePage);
            }

            @Override
            protected void done() {
                setLoading(false);
                try {
                    RegistrationPage result = get();
                    pageIndex = result.pageIndex;
                    totalRecords = result.totalRecords;
                    tableModel.setRowCount(0);
                    for (Registration registration : result.registrations) {
                        tableModel.addRow(new Object[]{
                                registration.getStudentName(),
                                registration.getRegisterNumber(),
                                registration.getDepartment(),
                                registration.getEmail(),
                                registration.getPhone(),
                                registration.getEventName(),
                                registration.getCreatedAt()
                        });
                    }
                    updatePaginationLabel();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showError("Loading registrations was interrupted.", exception);
                } catch (ExecutionException exception) {
                    tableModel.setRowCount(0);
                    totalRecords = 0;
                    updatePaginationLabel();
                    showError("Could not load registrations. Check database setup.",
                            exception.getCause());
                }
            }
        }.execute();
    }

    private void updatePaginationLabel() {
        int totalPages = totalRecords == 0 ? 0
                : (totalRecords + registrationService.getPageSize() - 1)
                        / registrationService.getPageSize();
        pageLabel.setText("Page " + (totalPages == 0 ? 0 : pageIndex + 1)
                + " of " + totalPages + "   |   " + totalRecords + " registration(s)");
        previousButton.setEnabled(!loading && pageIndex > 0);
        nextButton.setEnabled(!loading && pageIndex + 1 < totalPages);
    }

    private void setLoading(boolean isLoading) {
        loading = isLoading;
        registerButton.setEnabled(!isLoading);
        searchButton.setEnabled(!isLoading);
        refreshButton.setEnabled(!isLoading);
        previousButton.setEnabled(!isLoading && pageIndex > 0);
        int lastPage = totalRecords == 0 ? 0
                : (totalRecords - 1) / registrationService.getPageSize();
        nextButton.setEnabled(!isLoading && pageIndex < lastPage);
        if (isLoading) {
            pageLabel.setText("Working...");
        }
    }

    private void clearForm() {
        studentNameField.setText("");
        registerNumberField.setText("");
        emailField.setText("");
        phoneField.setText("");
        studentNameField.requestFocusInWindow();
    }

    private void showError(String message, Throwable cause) {
        String detail = cause == null || cause.getMessage() == null
                ? "" : "\nDetails: " + cause.getMessage();
        JOptionPane.showMessageDialog(this, message + detail,
                "Registration error", JOptionPane.ERROR_MESSAGE);
    }

    private static class RegistrationPage {
        private final List<Registration> registrations;
        private final int totalRecords;
        private final int pageIndex;

        RegistrationPage(List<Registration> registrations, int totalRecords, int pageIndex) {
            this.registrations = registrations;
            this.totalRecords = totalRecords;
            this.pageIndex = pageIndex;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new CampusEventRegistrationApp().setVisible(true));
    }
}
