import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class Leaderboard<T> {
    private final List<T> entries = new ArrayList<>();
    private final Comparator<? super T> comparator;

    public Leaderboard(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    public void add(T entry) {
        entries.add(Objects.requireNonNull(entry, "entry"));
    }

    public void addAll(List<? extends T> newEntries) {
        Objects.requireNonNull(newEntries, "newEntries");
        for (T entry : newEntries) {
            add(entry);
        }
    }

    public List<T> sortedCopy() {
        List<T> result = new ArrayList<>(entries);
        result.sort(comparator);
        return result;
    }

    public List<T> originalEntries() {
        return new ArrayList<>(entries);
    }
}
