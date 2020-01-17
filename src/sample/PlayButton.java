package sample;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

class PlayButton extends Button {
    private ImageView playPic, pausePic;

    PlayButton() {
        this.playPic = new ImageView(new Image((new File("pic/play.png").toURI().toString())));
        this.pausePic = new ImageView(new Image((new File("pic/pause.png").toURI().toString())));
        this.setPlayPic();
        setStyle("-fx-background-color: transparent; -fx-padding: 5, 5, 5, 5;");
    }

    void setPlayPic() {
        this.setGraphic(playPic);
    }

    void setPausePic() {
        this.setGraphic(pausePic);
    }
}
