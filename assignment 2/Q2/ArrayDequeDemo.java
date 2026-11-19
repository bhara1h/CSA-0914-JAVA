import java.util.ArrayDeque;
import java.util.Deque;

public final class ArrayDequeDemo {
    private ArrayDequeDemo() {
    }

    public static void main(String[] args) {
        Deque<String> upNext = new ArrayDeque<>();
        upNext.addLast("Blue Train");
        upNext.addLast("Quiet Night");
        upNext.addFirst("Play Next");
        System.out.println("Up Next: " + upNext);
        System.out.println("Now playing: " + upNext.removeFirst());
        System.out.println("Remaining: " + upNext);
    }
}
