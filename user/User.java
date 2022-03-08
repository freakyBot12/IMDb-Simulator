package user;

import fileio.UserInputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String username;

    private String subscriptionType;

    private Map<String, Integer> history;

    private Map<String, Double> ratings = new HashMap<>();

    private ArrayList<String> favoriteMovies;

    private int numberOfRatings = 0;


    public User(UserInputData userInputData) {
        this.username = userInputData.getUsername();
        this.subscriptionType = userInputData.getSubscriptionType();
        this.favoriteMovies = new ArrayList<>(userInputData.getFavoriteMovies());
        this.history = new HashMap<>(userInputData.getHistory());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public void setHistory(Map<String, Integer> history) {
        this.history = history;
    }

    public Map<String, Double> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Double> ratings) {
        this.ratings = ratings;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void setFavoriteMovies(ArrayList<String> favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public int giveRating(String movie, double rating) {
        if (!history.containsKey(movie)) {
            return -1;
        }
        if (ratings.containsKey(movie)) {
            return -2;
        }
        ratings.put(movie, rating);
        numberOfRatings++;
        return 0;
    }

    public int giveRating(String serial, int season, double rating) {
        String serialSeasonFullName = serial + " " + season;
        if (!history.containsKey(serial)) {
            return -1;
        }
        if (ratings.containsKey(serialSeasonFullName)) {
            return -2;
        }
        ratings.put(serialSeasonFullName, rating);
        numberOfRatings++;
        return 0;
    }

    @Override
    public String toString() {
        return username;
    }
}
