public final class StringBufferComparison {
    private StringBufferComparison() {
    }

    public static String build(String[] fields) {
        StringBuffer ticket = new StringBuffer();
        ticket.append(fields[0]);
        for (int field = 1; field < fields.length; field++) {
            ticket.append('|').append(fields[field]);
        }
        return ticket.toString();
    }
}
