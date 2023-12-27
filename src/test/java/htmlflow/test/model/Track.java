package htmlflow.test.model;

import java.time.LocalDate;

public class Track {
    final String artist;
    final String name;
    final String url;
    final LocalDate diedDate;
    final int listeners;

    public Track(String name) {
        this(null, name, null, 0, null);
    }

    public Track(String band, String name) {
        this(band, name, null, 0, null);
    }

    public Track(String band, String name, LocalDate died) {
        this(band, name, null, 0, died);
    }

    public Track(String band, String name, String url, int listeners, LocalDate died) {
        this.artist = band;
        this.name = name;
        this.url = url;
        this.listeners = listeners;
        this.diedDate = died;
    }

    public String getArtist() {
        return artist;
    }

    public LocalDate getDiedDate() {
        return diedDate;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getListeners() {
        return listeners;
    }
}
