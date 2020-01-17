package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

public class MusicPlayer extends Application {

    private VBox rootPlaylists;
    private VBox rootPlayer;
    private VBox listContainer;
    private HBox metadataContainer;
    private Media music;
    private MediaPlayer player;
    private Playlist playlist;

    private ImageView cover, volumeMin, volumeMax;
    private Label songName, artist, album, startTime, endTime, noSongsLabel;
    private PlayButton playButton;
    private Slider timeSlider;
    private boolean isPlaying;
    private int listIndex;
    private int currentLength;
    private int currentTime;
    private double volume;
    private Timer timer;
    private String defaultButtonStyle;
    private String activeButtonStyle;
    private String hoverButtonStyle;
    private Font font;

    public MusicPlayer() {
        isPlaying = false;
        listIndex = 0;
        currentTime = 0;
        volume = 1;
        startTime = new Label("0:00");
        endTime = new Label();
        volumeMin = new ImageView(new Image(new File("pic/volume min.png").toURI().toString()));
        volumeMax = new ImageView(new Image(new File("pic/volume max.png").toURI().toString()));
        songName = new Label();
        font = Font.font("Helvetica", 20);
        songName.setFont(font);
        artist = new Label();
        artist.setFont(font);
        album = new Label();
        album.setFont(font);
        noSongsLabel = new Label("No songs.");
        noSongsLabel.setFont(Font.font("Helvetica", 48));
        defaultButtonStyle = "-fx-background-color: \n" +
                "        rgba(0,0,0,0.08),\n" +
                "        linear-gradient(#9a9a9a, #909090),\n" +
                "        linear-gradient(white 0%, #f3f3f3 50%, #ececec 51%, #f2f2f2 100%);\n" +
                "    -fx-background-insets: 0 0 -1 0,0,1;\n" +
                "    -fx-background-radius: 5,5,4;\n" +
                "    -fx-padding: 3 30 3 30;\n" +
                "    -fx-text-fill: #242d35;\n" +
                "    -fx-font-size: 14px;";
        activeButtonStyle = "-fx-background-color: \n" +
                "        rgba(0,0,0,0.08),\n" +
                "        linear-gradient(#5a61af, #51536d),\n" +
                "        linear-gradient(#e4fbff 0%,#cee6fb 10%, #a5d3fb 50%, #88c6fb 51%, #d5faff 100%);\n" +
                "    -fx-background-insets: 0 0 -1 0,0,1;\n" +
                "    -fx-background-radius: 5,5,4;\n" +
                "    -fx-padding: 3 30 3 30;\n" +
                "    -fx-text-fill: #242d35;\n" +
                "    -fx-font-size: 14px;";
        hoverButtonStyle = "-fx-background-color: \n" +
                "        rgba(0,0,0,0.08),\n" +
                "        linear-gradient(#99999a, #909090),\n" +
                "        linear-gradient(white 30%, #f3f3f3 50%, #ececec 51%, #f2f2f2 100%);\n" +
                "    -fx-background-insets: 0 0 -1 0,0,1;\n" +
                "    -fx-background-radius: 5,5,4;\n" +
                "    -fx-padding: 3 30 3 30;\n" +
                "    -fx-text-fill: #242d35;\n" +
                "    -fx-font-size: 14px;";
    }

    private void play() {
        player.play();
        ifEndOfSong();
        isPlaying = true;
        playButton.setPausePic();
        rootPlayer.requestFocus();
    }

    private void pause() {
        player.pause();
        isPlaying = false;
        playButton.setPlayPic();
        rootPlayer.requestFocus();
    }

    private void initSong(Song song) {
        String nameURL = playlist.list.get(listIndex).URL;
        this.music = new Media(new File(nameURL).toURI().toString());
        player = new MediaPlayer(this.music);
        currentTime = 0;
        startTime.setText("0:00");

        player.setOnReady(() -> {
            song.length = (int) music.getDuration().toSeconds();
            currentLength = song.length;
            timeSlider.setMax(song.length);
            timeSlider.setValue(0);
            player.setVolume(volume);
            if (song.length % 60 >= 10) {
                endTime.setText(song.length / 60 + ":" + song.length % 60);
            } else {
                endTime.setText(song.length / 60 + ":0" + song.length % 60);
            }
            metadataContainer.getChildren().clear();
            metadataContainer.getChildren().addAll(artist, songName, album);
        });

        this.music.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            String key = change.getKey();
            Object value = change.getValueAdded();
            if (change.wasAdded()) {
                if (key.equals("artist")) {
                    song.artist = value.toString();
                    artist.setText(song.artist + "  —  ");
                }
                if (key.equals("title")) {
                    song.name = value.toString();
                    songName.setText(song.name);
                }
                if (key.equals("album")) {
                    song.album = value.toString();
                    album.setText("  (" + song.album + ")");
                }
                if (key.equals("image")) {
                    song.cover = (Image) value;
                    cover.setImage(song.cover);
                }
            }
        });
    }

    private void ifEndOfSong() {
        player.setOnEndOfMedia(() -> {
            ++listIndex;
            if (listIndex < playlist.list.size()) {
                initSong(playlist.list.get(listIndex));
                player.play();
            } else {
                listIndex = 0;
                initSong(playlist.list.get(listIndex));
                player.play();
            }
            metadataContainer.getChildren().clear();
        });
    }

    private void load(String URL) {
        Song song = new Song(URL);
        playlist.addToLibrary(song);
        initSong(song);
    }

    private void setTopButtonLook(Button button) {
        button.setStyle(defaultButtonStyle);
        button.setOnMouseEntered(event -> button.setStyle(hoverButtonStyle));
        button.setOnMouseExited(event -> button.setStyle(defaultButtonStyle));
        button.setOnMousePressed(event -> button.setStyle(activeButtonStyle));
        button.setOnMouseReleased(event -> button.setStyle(defaultButtonStyle));
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Music Player");
        primaryStage.getIcons().add(new Image(new File("pic/app.png").toURI().toString()));
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);

        /*
         *
         * UI музыкального плеера
         *
         * */

        rootPlayer = new VBox();
        rootPlayer.setAlignment(Pos.TOP_CENTER);
        rootPlayer.setSpacing(25);
        rootPlayer.setStyle("-fx-background-color: rgba(255,255,255);");
        root.getChildren().add(rootPlayer);
        rootPlayer.requestFocus();

        Button playlistButton = new Button("Playlist");
        setTopButtonLook(playlistButton);
        playlistButton.setOnAction(event -> rootPlaylists.toFront());
        rootPlayer.getChildren().add(playlistButton);

        // album cover element
        cover = new ImageView(new Image(new File("pic/default cover.png").toURI().toString(), 256, 256, false, false));
        cover.setPreserveRatio(true);
        cover.setFitHeight(256);
        cover.setFitWidth(256);
        rootPlayer.getChildren().add(cover);

        HBox timeContainer = new HBox();
        timeContainer.setAlignment(Pos.CENTER);
        timeContainer.setSpacing(15);
        rootPlayer.getChildren().add(timeContainer);

        timeContainer.getChildren().add(startTime);

        double sliderWidth = scene.getWidth() - 450;

        timeSlider = new Slider();
        timeSlider.setMaxWidth(sliderWidth);
        timeSlider.setMinWidth(sliderWidth);
        timeSlider.valueProperty().addListener((ov, old_val, new_val) -> {
            if (Math.abs((double) new_val - (double) old_val) > 1.5) {
                player.seek(new Duration((double) new_val * 1000));
                currentTime = new_val.intValue();
                if (new_val.doubleValue() % 60 >= 10) {
                    startTime.setText(new_val.intValue() / 60 + ":" + new_val.intValue() % 60);
                } else {
                    startTime.setText(new_val.intValue() / 60 + ":0" + new_val.intValue() % 60);
                }
            }
            rootPlayer.requestFocus();
        });
        timeContainer.getChildren().add(timeSlider);

        timeContainer.getChildren().add(endTime);

        class SongTimeTask extends TimerTask {
            double time;

            private SongTimeTask(double currentTime) {
                time = currentTime;
            }

            @Override
            public void run() {
                Platform.runLater(() -> {
                    Double exactTime = player.getCurrentTime().toSeconds();
                    currentTime = exactTime.intValue();
                    timeSlider.setValue(exactTime);
                    System.out.println(exactTime);
                    if (currentTime >= currentLength) {
                        ifEndOfSong();
                    }
                    if (currentTime % 60 >= 10) {
                        startTime.setText(currentTime / 60 + ":" + currentTime % 60);
                    } else {
                        startTime.setText(currentTime / 60 + ":0" + currentTime % 60);
                    }
                });
            }
        }

        metadataContainer = new HBox();
        metadataContainer.setAlignment(Pos.CENTER);
        rootPlayer.getChildren().add(metadataContainer);

        HBox controlContainer = new HBox();
        controlContainer.setAlignment(Pos.CENTER);
        controlContainer.setSpacing(15);
        rootPlayer.getChildren().add(controlContainer);

        Button prevButton = new Button();
        ImageView prevPic = new ImageView(new Image(new File("pic/prev.png").toURI().toString()));
        prevButton.setGraphic(prevPic);
        prevButton.setStyle("-fx-background-color: transparent; -fx-padding: 5, 5, 5, 5;");
        prevButton.setOnAction(event -> {
            if (isPlaying) {
                player.stop();
                player.play();
                startTime.setText("0:00");
                currentTime = 0;
            } else {
                player.stop();
                listIndex--;
                if (listIndex < 0) {
                    listIndex = 0;
                }
                initSong(playlist.list.get(listIndex));
                playButton.setPlayPic();
                isPlaying = false;
            }
            rootPlayer.requestFocus();
        });
        controlContainer.getChildren().add(prevButton);

        playButton = new PlayButton();
        playButton.setOnAction(event -> {
            if (!isPlaying) {
                timer = new Timer();
                SongTimeTask songTimeTask = new SongTimeTask(currentTime);
                timer.schedule(songTimeTask, 0, 1000);
                play();
            } else {
                pause();
                timer.cancel();
            }
        });
        controlContainer.getChildren().add(playButton);

        Button nextButton = new Button();
        ImageView nextPic = new ImageView(new Image(new File("pic/next.png").toURI().toString()));
        nextButton.setGraphic(nextPic);
        nextButton.setStyle("-fx-background-color: transparent; -fx-padding: 5, 5, 5, 5;");
        nextButton.setOnAction(event -> {
            if (isPlaying) {
                player.stop();
                listIndex++;
                if (listIndex >= playlist.list.size()) {
                    listIndex = 0;
                }
                initSong(playlist.list.get(listIndex));
                player.play();
                ifEndOfSong();
            } else {
                playButton.setPlayPic();
                isPlaying = false;
                player.stop();
                listIndex++;
                if (listIndex >= playlist.list.size()) {
                    listIndex = 0;
                }
                initSong(playlist.list.get(listIndex));
                ifEndOfSong();
            }
            rootPlayer.requestFocus();
        });
        controlContainer.getChildren().add(nextButton);

        HBox volumeContainer = new HBox();
        volumeContainer.setAlignment(Pos.CENTER);
        volumeContainer.setSpacing(15);
        rootPlayer.getChildren().add(volumeContainer);

        volumeContainer.getChildren().add(volumeMin);

        Slider volumeSlider = new Slider(0, 1.0, 1.0);
        volumeSlider.setMinWidth(scene.getWidth() - 525);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            volume = (double) newValue;
            player.setVolume(volume);
            rootPlayer.requestFocus();
        });
        volumeContainer.getChildren().add(volumeSlider);
        volumeContainer.getChildren().add(volumeMax);

        /*
         *
         * UI списка плейлистов
         *
         * */

        rootPlaylists = new VBox();
        rootPlaylists.setAlignment(Pos.TOP_CENTER);
        rootPlaylists.setSpacing(15);
        rootPlaylists.setStyle("-fx-background-color: rgba(255,255,255);");
        root.getChildren().add(rootPlaylists);

        Button playerButton = new Button("Player");
        setTopButtonLook(playerButton);
        playerButton.setOnAction(event -> rootPlayer.toFront());
        rootPlaylists.getChildren().add(playerButton);

        Button addSongButton = new Button("Add song");
        addSongButton.setFont(font);
        addSongButton.setGraphic(new ImageView(new Image((new File("pic/add.png").toURI().toString()))));
        addSongButton.setStyle("-fx-background-color: transparent; -fx-padding: 5, 5, 5, 5;");
        addSongButton.setOnAction(event -> {
            if (playlist == null) playlist = new Playlist();
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("music/"));
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Music files", "*.mp3"));
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            for (File file : files) {
                load(file.getAbsolutePath());
            }
            for (int i = 0; i < playlist.list.size(); i++){
                System.out.println(playlist.list.get(i).URL);
            }
            if (!playlist.list.isEmpty()) {
                listContainer.getChildren().remove(noSongsLabel);
            }
            listContainer.getChildren().clear();
            Button savePlaylistButton = new Button("Save playlist");
            savePlaylistButton.setFont(font);
            savePlaylistButton.setGraphic(new ImageView(new Image((new File("pic/save.png").toURI().toString()))));
            savePlaylistButton.setStyle("-fx-background-color: transparent; -fx-padding: 5, 5, 5, 5;");
            savePlaylistButton.setOnAction(event1 -> {
                // сериализация
                FileOutputStream outputStream = null;
                try {
                    final FileChooser fileChooserSave = new FileChooser();
                    fileChooserSave.setInitialDirectory(new File("playlists/"));
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Playlist", "*.dat");
                    fileChooserSave.getExtensionFilters().add(extFilter);
                    File file = fileChooserSave.showSaveDialog(primaryStage);
                    if (file != null) outputStream = new FileOutputStream(file.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ObjectOutputStream objectOutputStream = null;
                try {
                    objectOutputStream = new ObjectOutputStream(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // сохраняем в файл
                try {
                    if (objectOutputStream != null) {
                        StringBuilder paths = new StringBuilder();
                        for (int i = 0; i < playlist.list.size(); i++){
                            paths.append(playlist.list.get(i).URL).append(",");
                        }
                        objectOutputStream.writeObject(paths.toString());
                        System.out.println("Saved successfully!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //закрываем поток и освобождаем ресурсы
                try {
                    if (objectOutputStream != null) {
                        objectOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            listContainer.getChildren().add(savePlaylistButton);
        });

        HBox playlistFuncContainer = new HBox();
        playlistFuncContainer.setAlignment(Pos.CENTER);
        playlistFuncContainer.setSpacing(25);
        rootPlaylists.getChildren().add(playlistFuncContainer);

        Button loadPlaylistButton = new Button("Load playlist");
        loadPlaylistButton.setFont(font);
        loadPlaylistButton.setGraphic(new ImageView(new Image((new File("pic/load.png").toURI().toString()))));
        loadPlaylistButton.setStyle("-fx-background-color: transparent; -fx-padding: 5, 5, 5, 5;");
        loadPlaylistButton.setOnAction(event -> {
            FileInputStream fileInputStream = null;
            try {

                final FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File("playlists/"));
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Playlist", "*.dat"));
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) fileInputStream = new FileInputStream(file.getAbsolutePath());
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(fileInputStream);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                if (objectInputStream != null) {
                    playlist = new Playlist();
                    listIndex = 0;
                    if (playlist.list.isEmpty()) playlist.list.clear();
                    String paths = objectInputStream.readObject().toString();
                    String[] parts = paths.split(",");
                    System.out.println("paths was loaded:");
                    for (int i = 0; i < parts.length; i++){
                        System.out.println(parts[i]);
                    }
                    for (int i = 0; i < parts.length; i++){
                        load(parts[i]);
                    }
                }
            } catch (IOException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        playlistFuncContainer.getChildren().addAll(addSongButton, loadPlaylistButton);

        rootPlaylists.getChildren().add(new Separator());
        listContainer = new VBox();
        listContainer.setAlignment(Pos.CENTER);
        rootPlaylists.getChildren().add(listContainer);
        listContainer.getChildren().add(noSongsLabel);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
