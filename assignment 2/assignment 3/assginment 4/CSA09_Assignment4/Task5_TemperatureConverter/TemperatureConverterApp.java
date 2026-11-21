import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Demonstrates four ways to handle Swing button actions.
 */
public class TemperatureConverterApp extends JFrame implements ActionListener {
    private final JTextField celsiusField = new JTextField(16);
    private final JTextField fahrenheitField = new JTextField(16);

    public TemperatureConverterApp() {
        super("Temperature Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fahrenheitField.setEditable(false);
        setContentPane(createContent());
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addRow(fields, constraints, 0, "Celsius:", celsiusField);
        addRow(fields, constraints, 1, "Fahrenheit:", fahrenheitField);

        JPanel buttons = new JPanel(new GridBagLayout());
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.insets = new Insets(4, 4, 4, 4);
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 0;
        JButton implementedListenerButton = new JButton("Convert (implements)");
        implementedListenerButton.addActionListener(this);
        buttons.add(implementedListenerButton, buttonConstraints);

        buttonConstraints.gridx = 1;
        JButton anonymousButton = new JButton("Convert (anonymous)");
        anonymousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                convertAndDisplay();
            }
        });
        buttons.add(anonymousButton, buttonConstraints);

        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 1;
        JButton lambdaButton = new JButton("Convert (lambda)");
        lambdaButton.addActionListener(event -> convertAndDisplay());
        buttons.add(lambdaButton, buttonConstraints);

        buttonConstraints.gridx = 1;
        JButton separateListenerButton = new JButton("Convert (separate class)");
        separateListenerButton.addActionListener(
                new SeparateTemperatureListener(celsiusField, fahrenheitField, this));
        buttons.add(separateListenerButton, buttonConstraints);

        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 2;
        buttonConstraints.gridwidth = 2;
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> {
            celsiusField.setText("");
            fahrenheitField.setText("");
        });
        buttons.add(clearButton, buttonConstraints);

        content.add(fields, BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);
        return content;
    }

    private void addRow(JPanel panel, GridBagConstraints constraints, int row,
                        String label, JTextField field) {
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.weightx = 0;
        panel.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(field, constraints);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        convertAndDisplay();
    }

    private void convertAndDisplay() {
        try {
            double celsius = parseCelsius(celsiusField.getText());
            fahrenheitField.setText(formatFahrenheit(celsius));
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Enter a valid Celsius number.",
                    "Invalid input", JOptionPane.ERROR_MESSAGE);
            celsiusField.requestFocusInWindow();
        }
    }

    static double parseCelsius(String input) {
        String value = input == null ? "" : input.trim();
        if (value.isEmpty()) {
            throw new NumberFormatException("Celsius value is required.");
        }
        double celsius = Double.parseDouble(value);
        if (Double.isNaN(celsius) || Double.isInfinite(celsius)) {
            throw new NumberFormatException("Celsius value must be finite.");
        }
        return celsius;
    }

    static String formatFahrenheit(double celsius) {
        double fahrenheit = (celsius * 9.0 / 5.0) + 32.0;
        return new DecimalFormat("0.##").format(fahrenheit);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TemperatureConverterApp().setVisible(true));
    }
}

/**
 * A top-level listener class demonstrating a separate listener implementation.
 */
class SeparateTemperatureListener implements ActionListener {
    private final JTextField celsiusField;
    private final JTextField fahrenheitField;
    private final java.awt.Component dialogParent;

    SeparateTemperatureListener(JTextField celsiusField, JTextField fahrenheitField,
                                java.awt.Component dialogParent) {
        this.celsiusField = celsiusField;
        this.fahrenheitField = fahrenheitField;
        this.dialogParent = dialogParent;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            double celsius = TemperatureConverterApp.parseCelsius(celsiusField.getText());
            fahrenheitField.setText(TemperatureConverterApp.formatFahrenheit(celsius));
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(dialogParent, "Enter a valid Celsius number.",
                    "Invalid input", JOptionPane.ERROR_MESSAGE);
            celsiusField.requestFocusInWindow();
        }
    }
}
