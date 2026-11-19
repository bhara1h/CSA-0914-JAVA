import java.util.ArrayList;
import java.util.List;

public final class ArrayListDemo {
    private ArrayListDemo() {
    }

    public static void main(String[] args) {
        List<String> playlist = new ArrayList<>();
        playlist.add("Blue Train");
        playlist.add("Quiet Night");
        playlist.add(1, "Campus Radio");
        playlist.remove(0);
        System.out.println("ArrayList playlist: " + playlist);
        System.out.println("k-th song (index 1): " + playlist.get(1));
    }
}
