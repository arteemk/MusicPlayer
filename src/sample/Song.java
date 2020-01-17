package sample;

import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;

class Song {
    String URL;
    String name;
    String artist;
    String album;
    int length;
    Image cover;

    Song(String URL) {
        this.URL = URL;
        this.name = "Unknown song";
        this.artist = "Unknown artist";
        this.album = "Unknown album";
        this.cover = new Image(new File("pic/default cover.png").toURI().toString());
    }
}
