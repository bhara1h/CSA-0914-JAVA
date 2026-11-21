import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import javax.swing.table.DefaultTableModel;

/**
 * Books and cancels sample hospital appointments in a table.
 */
public class HospitalAppointmentApp extends JFrame {
    private final JTextField patientNameField = new JTextField(16);
    private final JTextField patientIdField = new JTextField(16);
    private final JComboBox<String> departmentBox = new JComboBox<>(
            new String[]{"Cardiology", "Dermatology", "Pediatrics", "Orthopedics"});
    private final JComboBox<String> doctorBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(16);
    private final JTextField timeField = new JTextField(16);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Patient ID", "Patient", "Department", "Doctor", "Date", "Time"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable appointmentTable = new JTable(tableModel);

    public HospitalAppointmentApp() {
        super("Hospital Appointment Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createContent());
        updateDoctors();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        addRow(form, constraints, 0, "Patient name:", patientNameField);
        addRow(form, constraints, 1, "Patient ID:", patientIdField);
        addRow(form, constraints, 2, "Department:", departmentBox);
        addRow(form, constraints, 3, "Doctor:", doctorBox);
        addRow(form, constraints, 4, "Date (YYYY-MM-DD):", dateField);
        addRow(form, constraints, 5, "Time (HH:MM):", timeField);

        departmentBox.addActionListener(event -> updateDoctors());
        JButton bookButton = new JButton("Book Appointment");
        bookButton.addActionListener(event -> bookAppointment());
        JButton cancelButton = new JButton("Cancel Selected");
        cancelButton.addActionListener(event -> cancelAppointment());
        JPanel buttons = new JPanel();
        buttons.add(bookButton);
        buttons.add(cancelButton);
        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        appointmentTable.setFillsViewportHeight(true);
        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
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

    private void updateDoctors() {
        String department = (String) departmentBox.getSelectedItem();
        doctorBox.removeAllItems();
        if ("Cardiology".equals(department)) {
            doctorBox.addItem("Dr. Maya Patel");
            doctorBox.addItem("Dr. Liam Chen");
        } else if ("Dermatology".equals(department)) {
            doctorBox.addItem("Dr. Aisha Khan");
            doctorBox.addItem("Dr. Noah Brooks");
        } else if ("Pediatrics".equals(department)) {
            doctorBox.addItem("Dr. Sofia Garcia");
            doctorBox.addItem("Dr. Ethan Miller");
        } else if ("Orthopedics".equals(department)) {
            doctorBox.addItem("Dr. Ava Wilson");
            doctorBox.addItem("Dr. Arjun Rao");
        }
    }

    private void bookAppointment() {
        String patientName = patientNameField.getText().trim();
        String patientId = patientIdField.getText().trim();
        if (patientName.isEmpty() || patientId.isEmpty()) {
            showError("Patient name and patient ID are required.");
            return;
        }
        LocalDate date;
        LocalTime time;
        try {
            date = LocalDate.parse(dateField.getText().trim(),
                    DateTimeFormatter.ISO_LOCAL_DATE);
            time = LocalTime.parse(timeField.getText().trim(),
                    DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException exception) {
            showError("Enter a valid date (YYYY-MM-DD) and time (HH:MM).");
            return;
        }
        if (date.isBefore(LocalDate.now())) {
            showError("Appointment date cannot be in the past.");
            return;
        }

        String department = (String) departmentBox.getSelectedItem();
        String doctor = (String) doctorBox.getSelectedItem();
        tableModel.addRow(new Object[]{patientId, patientName, department, doctor,
                date.toString(), time.format(DateTimeFormatter.ofPattern("HH:mm"))});
        JOptionPane.showMessageDialog(this, "Appointment booked for "
                        + patientName + " on " + date + " at " + time + ".",
                "Booking confirmed", JOptionPane.INFORMATION_MESSAGE);
        patientNameField.setText("");
        patientIdField.setText("");
    }

    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("Select an appointment in the table to cancel.");
            return;
        }
        int confirmed = JOptionPane.showConfirmDialog(this,
                "Cancel the selected appointment?", "Confirm cancellation",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            tableModel.removeRow(appointmentTable.convertRowIndexToModel(selectedRow));
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid appointment",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalAppointmentApp().setVisible(true));
    }
}
