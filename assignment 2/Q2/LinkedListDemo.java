import java.util.LinkedList;

public final class LinkedListDemo {
    private LinkedListDemo() {
    }

    public static void main(String[] args) {
        LinkedList<String> playlist = new LinkedList<>();
        playlist.addLast("Blue Train");
        playlist.addLast("Quiet Night");
        playlist.addFirst("Campus Radio");
        playlist.removeFirst();
        System.out.println("LinkedList playlist: " + playlist);
    }
}
