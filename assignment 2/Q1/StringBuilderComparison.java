public final class StringBuilderComparison {
    private StringBuilderComparison() {
    }

    public static String build(String[] fields) {
        StringBuilder ticket = new StringBuilder();
        ticket.append(fields[0]);
        for (int field = 1; field < fields.length; field++) {
            ticket.append('|').append(fields[field]);
        }
        return ticket.toString();
    }
}
