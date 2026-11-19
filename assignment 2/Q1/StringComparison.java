public final class StringComparison {
    private StringComparison() {
    }

    public static String build(String[] fields) {
        String ticket = fields[0];
        for (int field = 1; field < fields.length; field++) {
            ticket = ticket + "|" + fields[field];
        }
        return ticket;
    }
}
