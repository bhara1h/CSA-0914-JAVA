import java.util.List;

public class RedesignedSolution {
    // A mutable builder reuses one buffer while assembling an individual ticket.
    public String createTicket(List<String> fields) {
        StringBuilder ticket = new StringBuilder(fields.size() * 16);
        for (String field : fields) {
            ticket.append(field);
        }
        return ticket.toString();
    }

    public long createTickets(List<String> fields, int ticketCount) {
        long checksum = 0;
        for (int ticket = 0; ticket < ticketCount; ticket++) {
            checksum += createTicket(fields).length();
        }
        return checksum;
    }
}
