package htmlflow.test.model;

public class Track {
    final String name;
    final String url;
    final int listeners;

    public Track(String name) {
        this(name, null, 0);
    }

    public Track(String name, String url, int listeners) {
        this.name = name;
        this.url = url;
        this.listeners = listeners;
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
