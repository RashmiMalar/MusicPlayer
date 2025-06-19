package model;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String genre;
    private String filepath;

    public Song(int id, String title, String artist, String genre, String filepath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.filepath = filepath;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getGenre() { return genre; }
    public String getFilepath() { return filepath; }
}
