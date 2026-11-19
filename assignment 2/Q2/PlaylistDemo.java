public final class PlaylistDemo {
    private PlaylistDemo() {
    }

    public static void main(String[] args) {
        Playlist playlist = new Playlist();
        playlist.addLast("Blue Train");
        playlist.addLast("Quiet Night");
        playlist.addFirst("Campus Radio");
        playlist.insertAt(2, "Evening Walk");
        System.out.println("After inserts: " + playlist.getSongs());
        System.out.println("k-th song (index 2): " + playlist.getSong(2));
        System.out.println("Removed from front: " + playlist.removeFirst());
        System.out.println("Removed from middle: " + playlist.removeAt(1));
        System.out.println("Final playlist: " + playlist.getSongs());
    }
}
