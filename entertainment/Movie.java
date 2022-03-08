package entertainment;

import fileio.MovieInputData;
import fileio.ShowInput;
import user.User;

import java.util.ArrayList;
import java.util.List;

public class Movie extends Show {
    private int duration;

    private List<Double> ratings = new ArrayList<>();

    public Movie(MovieInputData movieInputData) {
        super(movieInputData.getTitle(), movieInputData.getYear(), movieInputData.getCast(),
                movieInputData.getGenres());
        this.duration = movieInputData.getDuration();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Double> getRatings() {
        return ratings;
    }

    public void setRatings(List<Double> ratings) {
        this.ratings = ratings;
    }

    public double getRating() {
        double movieRating = 0;
        for (Double rating : ratings) {
            movieRating += rating;
        }
        if (ratings.size() > 0)
            movieRating /= ratings.size();
        return movieRating;
    }

    public int favouriteAppearancesOfMovie(List<User> users) {
        int favouriteAppearances = 0;
        for (User user : users) {
            for (String favourite : user.getFavoriteMovies()) {
                if (getTitle().equals(favourite))
                    favouriteAppearances++;
            }
        }
        return favouriteAppearances;
    }

    public int totalNumberOfViews(List<User> users) {
        int totalNumberOfViews = 0;
        for (User user : users) {
            if (user.getHistory().containsKey(getTitle()))
                totalNumberOfViews += user.getHistory().get(getTitle());
        }

        return totalNumberOfViews;
    }
}

