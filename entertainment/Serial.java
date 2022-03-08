package entertainment;

import fileio.SerialInputData;
import fileio.ShowInput;
import user.User;

import java.util.ArrayList;
import java.util.List;


public class Serial extends Show {

    private int numberOfSeasons;

    private ArrayList<Season> seasons;

    public Serial(SerialInputData serialInputData) {
        super(serialInputData.getTitle(), serialInputData.getYear(), serialInputData.getCast(),
                serialInputData.getGenres());
        this.numberOfSeasons = serialInputData.getNumberSeason();
        this.seasons = serialInputData.getSeasons();

    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(ArrayList<Season> seasons) {
        this.seasons = seasons;
    }

    public void addRating(int seasonNumber, double rating) {
        seasons.get(seasonNumber - 1).getRatings().add(rating);
    }

    public double getRating() {
        double serialRating = 0;
        for (Season season : seasons) {
            double seasonRating = 0;
            for (Double rating : season.getRatings()) {
                if (rating != 0)
                    seasonRating += rating;
            }
            if (season.getRatings().size() > 0)
                seasonRating /= season.getRatings().size();

            serialRating += seasonRating;
        }
        serialRating /= numberOfSeasons;

        return serialRating;
    }

    public int favouriteAppearancesOfSerial(List<User> users) {
        int favouriteAppearances = 0;
        for (User user : users) {
            for (String favourite : user.getFavoriteMovies()) {
                if (getTitle().equals(favourite))
                    favouriteAppearances++;
            }
        }
        return favouriteAppearances;
    }

    public int totalDuration() {
        int totalDuration = 0;
        for (Season season : seasons) {
            totalDuration += season.getDuration();
        }
        return totalDuration;
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
