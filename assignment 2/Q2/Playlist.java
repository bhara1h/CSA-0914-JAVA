import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {
    private final List<String> songs = new ArrayList<>();

    public void addLast(String song) {
        songs.add(requireSong(song));
    }

    public void addFirst(String song) {
        songs.add(0, requireSong(song));
    }

    public void insertAt(int index, String song) {
        songs.add(index, requireSong(song));
    }

    public String removeFirst() {
        if (songs.isEmpty()) {
            throw new IllegalStateException("Playlist is empty.");
        }
        return songs.remove(0);
    }

    public String removeAt(int index) {
        return songs.remove(index);
    }

    public String getSong(int index) {
        return songs.get(index);
    }

    public int size() {
        return songs.size();
    }

    public List<String> getSongs() {
        return Collections.unmodifiableList(songs);
    }

    private String requireSong(String song) {
        if (song == null || song.trim().isEmpty()) {
            throw new IllegalArgumentException("Song title cannot be empty.");
        }
        return song.trim();
    }
}
