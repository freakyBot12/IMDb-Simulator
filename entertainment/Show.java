package entertainment;

import java.util.ArrayList;

public class Show {
    private String title;

    private int year;

    private ArrayList<String> cast;

    private ArrayList<String> genres;

    public Show(String title, int year,
                ArrayList<String> cast, ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public double getRating() {
        return 0;
    }

    @Override
    public String toString() {
        return title;
    }
}
