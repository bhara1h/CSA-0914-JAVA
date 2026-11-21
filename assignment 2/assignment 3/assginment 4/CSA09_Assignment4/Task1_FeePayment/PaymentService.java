import java.math.BigDecimal;

/**
 * Contains the payment rules independently of the Swing interface.
 */
public class PaymentService {
    public String processPayment(String studentId, String studentName,
                                 BigDecimal amount, String paymentMethod) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name is required.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Fee amount must be greater than zero.");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Select a payment method.");
        }

        // This assignment simulates a successful payment; no real charge is made.
        return "Payment successful for " + studentName.trim()
                + " (ID: " + studentId.trim() + ")"
                + "\nAmount: " + amount.toPlainString()
                + "\nMethod: " + paymentMethod;
    }
}
