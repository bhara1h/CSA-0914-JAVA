public final class HashMapDemo {
    private HashMapDemo() {
    }

    public static void main(String[] args) {
        showInventory("HashMap");
    }

    static void showInventory(String type) {
        PharmacyInventory inventory = new PharmacyInventory(type);
        inventory.add(new Medicine("M002", "Saline", 20, 32.50));
        inventory.add(new Medicine("M001", "Bandage", 50, 12.00));
        System.out.println(type + " lookup M001: " + inventory.find("M001").getName());
        System.out.println("Entries: " + inventory.size());
    }
}
