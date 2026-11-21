import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * A small fee payment form. PaymentService performs the payment validation
 * and simulation, keeping that logic out of this presentation class.
 */
public class FeePaymentApp extends JFrame {
    private final JTextField studentIdField = new JTextField(20);
    private final JTextField studentNameField = new JTextField(20);
    private final JTextField amountField = new JTextField(20);
    private final JComboBox<String> methodBox =
            new JComboBox<>(new String[]{"Credit/Debit Card", "Bank Transfer", "Cash"});
    private final PaymentService paymentService = new PaymentService();

    public FeePaymentApp() {
        super("Student Fee Payment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createContent());
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        addRow(form, constraints, 0, "Student ID:", studentIdField);
        addRow(form, constraints, 1, "Student name:", studentNameField);
        addRow(form, constraints, 2, "Fee amount:", amountField);
        addRow(form, constraints, 3, "Payment method:", methodBox);

        JButton payButton = new JButton("Pay Fee");
        payButton.addActionListener(event -> processPayment());
        JPanel actions = new JPanel();
        actions.add(payButton);

        content.add(form, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);
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

    private void processPayment() {
        try {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                throw new IllegalArgumentException("Fee amount is required.");
            }
            BigDecimal amount = new BigDecimal(amountText);
            String message = paymentService.processPayment(
                    studentIdField.getText(),
                    studentNameField.getText(),
                    amount,
                    (String) methodBox.getSelectedItem());
            JOptionPane.showMessageDialog(this, message, "Payment complete",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric fee amount.",
                    "Invalid amount", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "Unable to process payment", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FeePaymentApp().setVisible(true));
    }
}
