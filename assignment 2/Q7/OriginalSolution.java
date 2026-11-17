import java.util.List;

public class OriginalSolution {
    // Baseline deliberately uses immutable String concatenation for each field.
    public String createTicket(List<String> fields) {
        String ticket = "";
        for (String field : fields) {
            ticket += field;
        }
        return ticket;
    }

    public long createTickets(List<String> fields, int ticketCount) {
        long checksum = 0;
        for (int ticket = 0; ticket < ticketCount; ticket++) {
            checksum += createTicket(fields).length();
        }
        return checksum;
    }
}
