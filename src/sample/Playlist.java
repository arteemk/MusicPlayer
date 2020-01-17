package sample;

import java.util.ArrayList;

public class Playlist {
    ArrayList<Song> list;

    Playlist() {
        list = new ArrayList<>();
    }

    void addToLibrary(Song song) {
        list.add(song);
    }
}
