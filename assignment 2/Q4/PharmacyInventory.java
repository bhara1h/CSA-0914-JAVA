import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class PharmacyInventory {
    private final Map<String, Medicine> medicines;

    public PharmacyInventory(String collectionType) {
        if ("HashMap".equals(collectionType)) {
            medicines = new HashMap<>();
        } else if ("TreeMap".equals(collectionType)) {
            medicines = new TreeMap<>();
        } else if ("LinkedHashMap".equals(collectionType)) {
            medicines = new LinkedHashMap<>();
        } else {
            throw new IllegalArgumentException("Unknown inventory map: " + collectionType);
        }
    }

    public void add(Medicine medicine) {
        if (medicine == null) {
            throw new IllegalArgumentException("Medicine is required.");
        }
        medicines.put(medicine.getCode(), medicine);
    }

    public Medicine find(String code) {
        return medicines.get(code);
    }

    public int size() {
        return medicines.size();
    }

    public Collection<Medicine> medicines() {
        return medicines.values();
    }
}
