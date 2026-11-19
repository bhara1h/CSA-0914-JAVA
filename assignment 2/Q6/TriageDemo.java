public final class TriageDemo {
    private TriageDemo() {
    }

    public static void main(String[] args) {
        Triage triage = new Triage();
        triage.admit(new Patient("Meena", 1, 0));
        triage.admit(new Patient("Kumar", 1, 1));
        triage.admit(new Patient("Lata", 2, 2));
        System.out.println("Next patient: " + triage.serveNext());
        System.out.println("Waiting count: " + triage.waitingCount());
    }
}
