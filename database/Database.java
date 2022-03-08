package database;

import actor.Actor;
import actor.ActorsAwards;
import entertainment.Movie;
import entertainment.Serial;
import entertainment.Show;
import fileio.*;
import org.json.simple.JSONArray;
import user.User;

import java.io.IOException;
import java.util.*;

public class Database {
    private List<Actor> actors = new ArrayList<>();

    private List<User> users = new ArrayList<>();

    private List<ActionInputData> commands = new ArrayList<>();

    private List<Movie> movies = new ArrayList<>();

    private List<Serial> serials = new ArrayList<>();

    public Database(Input input) {
        for (ActorInputData actorInputData : input.getActors()) {
            Actor actor = new Actor(actorInputData);
            this.actors.add(actor);
        }

        for (UserInputData userInputData : input.getUsers()) {
            User user = new User(userInputData);
            this.users.add(user);
        }

        this.commands.addAll(input.getCommands());

        for (MovieInputData movieInputData : input.getMovies()) {
            Movie movie = new Movie(movieInputData);
            this.movies.add(movie);
        }

        for (SerialInputData serialInputData : input.getSerials()) {
            Serial serial = new Serial(serialInputData);
            this.serials.add(serial);
        }
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<ActionInputData> getCommands() {
        return commands;
    }

    public void setCommands(List<ActionInputData> commands) {
        this.commands = commands;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Serial> getSerials() {
        return serials;
    }

    public void setSerials(List<Serial> serials) {
        this.serials = serials;
    }

    @SuppressWarnings("unchecked")
    public void view(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        String inputTitle = action.getTitle();
        for (User user : users) {
            if ((user.getUsername()).equals(action.getUsername())) {
                if (user.getHistory().containsKey(inputTitle)) {
                    int newNumberOfViews = user.getHistory().get(inputTitle) + 1;
                    user.getHistory().put(inputTitle, newNumberOfViews);
                    arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                            "success -> " + inputTitle
                                    + " was viewed with total views of " + user.getHistory().get(inputTitle)));
                } else {
                    user.getHistory().put(inputTitle, 1);
                    arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                            "success -> " + inputTitle
                                    + " was viewed with total views of " + user.getHistory().get(inputTitle)));
                }
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void favorite(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        for (User user : users) {
            if ((user.getUsername()).equals(action.getUsername())) {
                for (String favourite : user.getFavoriteMovies()) {
                    if (favourite.equals(action.getTitle())) {
                        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "error" +
                                " -> " + action.getTitle() + " is already in favourite list"));
                        return;
                    }
                }

                for (Map.Entry<String, Integer> entry : user.getHistory().entrySet()) {
                    if ((entry.getKey()).equals(action.getTitle())) {
                        user.getFavoriteMovies().add(action.getTitle());
                        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "success" +
                                " -> " + action.getTitle() + " was added as favourite"));
                        return;
                    }
                }
                arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "error -> " +
                        action.getTitle() + " is not seen"));
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void rating(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        for (User user : users) {
            if ((user.getUsername()).equals(action.getUsername())) {
                for (Movie movie : movies) {
                    if ((movie.getTitle()).equals(action.getTitle())) {
                        int ratingResult = user.giveRating(action.getTitle(), action.getGrade());
                        if (ratingResult == -2) {
                            arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                    "error -> " + action.getTitle() + " has" +
                                            " been already rated"));
                            return;
                        }
                        if (ratingResult == -1) {
                            arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                    "error -> " + action.getTitle() + " is" +
                                            " not seen"));
                            return;
                        }
                        if (ratingResult == 0) {
                            movie.getRatings().add(action.getGrade());
                            arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                    "success -> " + action.getTitle() + " was" +
                                            " rated with " + action.getGrade() + " by " + user.getUsername()));
                        }
                        return;
                    }
                }
                for (Serial serial : serials) {
                    if ((serial.getTitle()).equals(action.getTitle())) {
                        int ratingResult = user.giveRating(action.getTitle(), action.getSeasonNumber(),
                                action.getGrade());
                        if (ratingResult == -2) {
                            arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                    "error -> " + action.getTitle() + " has" +
                                            " been already rated"));
                            return;
                        }
                        if (ratingResult == -1) {
                            arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                    "error -> " + action.getTitle() + " is" +
                                            " not seen"));
                            return;
                        }
                        if (ratingResult == 0) {
                            serial.getSeasons().get(action.getSeasonNumber() - 1).getRatings().add(action.getGrade());
                            arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                    "success -> " + action.getTitle() + " was" +
                                            " rated with " + action.getGrade() + " by " + user.getUsername()));
                            return;
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void average(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Actor> actorsSortedList = new ArrayList<>();
        for (Actor actor : actors) {
            if (actor.getRating(movies, serials) > 0) {
                actorsSortedList.add(actor);
            }
        }
        actorsSortedList.sort(Comparator.comparing(Actor::getName));
        actorsSortedList.sort(Comparator.comparingDouble(o -> o.getRating(movies, serials)));
        if ((action.getSortType()).equals("desc"))
            Collections.reverse(actorsSortedList);

        if (actorsSortedList.size() > action.getNumber()) {
            actorsSortedList.subList(action.getNumber(), actorsSortedList.size()).clear();
        }
        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                actorsSortedList));
    }

    @SuppressWarnings("unchecked")
    public void awards(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Actor> actorSortedList = new ArrayList<>();

        List<ActorsAwards> awards = new ArrayList<>();
        for (String award : action.getFilters().get(3)) {
            awards.add(ActorsAwards.valueOf(award));
        }
        for (Actor actor : actors) {
            int ok = 0;
            for (ActorsAwards award : awards) {
                if (!actor.getAwards().containsKey(award)) {
                    ok = 1;
                    break;
                }
            }
            if (ok == 0)
                actorSortedList.add(actor);
        }
        actorSortedList.sort(Comparator.comparing(Actor::getName));
        actorSortedList.sort((o1, o2) -> {
            int numberOfAwards1 = 0;
            int numberOfAwards2 = 0;
            for (Map.Entry<ActorsAwards, Integer> entry : o1.getAwards().entrySet()) {
                numberOfAwards1 += entry.getValue();
            }
            for (Map.Entry<ActorsAwards, Integer> entry : o2.getAwards().entrySet()) {
                numberOfAwards2 += entry.getValue();
            }
            return Integer.compare(numberOfAwards1, numberOfAwards2);
        });

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(actorSortedList);
        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                actorSortedList));
    }

    @SuppressWarnings("unchecked")
    public void filter_description(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Actor> actorList = new ArrayList<>();
        List<String> inputWords = action.getFilters().get(2);
        int numberOfInputWords = inputWords.size();
        for (Actor actor : actors) {
            String[] description = (actor.getCareerDescription()).toLowerCase().split("\\W+");
            int counter = 0;
            for (String wordInput : inputWords) {
                for (String wordDescription : description) {
                    if (wordInput.equals(wordDescription)) {
                        counter++;
                        break;
                    }
                }
            }
            if (counter == numberOfInputWords)
                actorList.add(actor);
        }

        actorList.sort(Comparator.comparing(Actor::getName));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(actorList);
        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                actorList));
    }

    @SuppressWarnings("unchecked")
    public void queryForMovieByRatings(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Movie> sortedMoviesByRating = new ArrayList<>();
        for (Movie movie : movies) {
            if (action.getFilters().get(1).get(0) != null) {
                int ok = 0;
                String inputGenre = action.getFilters().get(1).get(0);
                for (String genre : movie.getGenres()) {
                    if (genre.equals(inputGenre)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    if (movie.getRating() > 0)
                        sortedMoviesByRating.add(movie);
                }
            } else {
                if (movie.getRating() > 0)
                    sortedMoviesByRating.add(movie);
            }
        }
        if ((action.getFilters().get(0).get(0)) != null) {
            int inputYear = Integer.parseInt(action.getFilters().get(0).get(0));
            sortedMoviesByRating.removeIf((movie -> movie.getYear() != inputYear));
        }


        sortedMoviesByRating.sort(Comparator.comparing(Show::getTitle));

        sortedMoviesByRating.sort(Comparator.comparingDouble(Movie::getRating));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(sortedMoviesByRating);

        if (sortedMoviesByRating.size() > action.getNumber()) {
            sortedMoviesByRating.subList(action.getNumber(), sortedMoviesByRating.size()).clear();
        }

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                sortedMoviesByRating));
    }

    @SuppressWarnings("unchecked")
    public void queryForMovieByFavourite(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Movie> sortedFavouriteMovies = new ArrayList<>();
        for (Movie movie : movies) {
            if (action.getFilters().get(1).get(0) != null) {
                int ok = 0;
                String inputGenre = action.getFilters().get(1).get(0);
                for (String genre : movie.getGenres()) {
                    if (genre.equals(inputGenre)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    sortedFavouriteMovies.add(movie);
                }
            } else {
                sortedFavouriteMovies.add(movie);
            }
        }

        if ((action.getFilters().get(0).get(0)) != null) {
            int inputYear = Integer.parseInt(action.getFilters().get(0).get(0));
            sortedFavouriteMovies.removeIf((movie -> movie.getYear() != inputYear));
        }

        sortedFavouriteMovies.removeIf(movie -> movie.favouriteAppearancesOfMovie(users) == 0);

        sortedFavouriteMovies.sort(Comparator.comparing(Show::getTitle));

        sortedFavouriteMovies.sort(Comparator.comparingInt(o -> o.favouriteAppearancesOfMovie(users)));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(sortedFavouriteMovies);

        if (sortedFavouriteMovies.size() > action.getNumber()) {
            sortedFavouriteMovies.subList(action.getNumber(), sortedFavouriteMovies.size()).clear();
        }

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                sortedFavouriteMovies));
    }

    @SuppressWarnings("unchecked")
    public void queryForMovieByLenght(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Movie> sortedLongestMovies = new ArrayList<>();
        for (Movie movie : movies) {
            if (action.getFilters().get(1).get(0) != null) {
                int ok = 0;
                String inputGenre = action.getFilters().get(1).get(0);
                for (String genre : movie.getGenres()) {
                    if (genre.equals(inputGenre)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    sortedLongestMovies.add(movie);
                }
            } else {
                sortedLongestMovies.add(movie);
            }
        }
        if ((action.getFilters().get(0).get(0)) != null) {
            int inputYear = Integer.parseInt(action.getFilters().get(0).get(0));
            sortedLongestMovies.removeIf((movie -> movie.getYear() != inputYear));
        }

        sortedLongestMovies.sort(Comparator.comparing(Show::getTitle));

        sortedLongestMovies.sort(Comparator.comparingInt(Movie::getDuration));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(sortedLongestMovies);

        if (sortedLongestMovies.size() > action.getNumber()) {
            sortedLongestMovies.subList(action.getNumber(), sortedLongestMovies.size()).clear();
        }

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                sortedLongestMovies));
    }

    @SuppressWarnings("unchecked")
    public void queryForMovieByMostViews(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Movie> sortedMostViewedMovies = new ArrayList<>();
        for (Movie movie : movies) {
            if (action.getFilters().get(1).get(0) != null) {
                int ok = 0;
                String inputGenre = action.getFilters().get(1).get(0);
                for (String genre : movie.getGenres()) {
                    if (genre.equals(inputGenre)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    sortedMostViewedMovies.add(movie);
                }
            } else {
                sortedMostViewedMovies.add(movie);
            }
        }
        if ((action.getFilters().get(0).get(0)) != null) {
            int inputYear = Integer.parseInt(action.getFilters().get(0).get(0));
            sortedMostViewedMovies.removeIf((movie -> movie.getYear() != inputYear));
        }

        sortedMostViewedMovies.removeIf(movie -> movie.totalNumberOfViews(users) == 0);

        sortedMostViewedMovies.sort(Comparator.comparing(Show::getTitle));

        sortedMostViewedMovies.sort(Comparator.comparingInt(o -> o.totalNumberOfViews(users)));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(sortedMostViewedMovies);

        if (sortedMostViewedMovies.size() > action.getNumber()) {
            sortedMostViewedMovies.subList(action.getNumber(), sortedMostViewedMovies.size()).clear();
        }

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                sortedMostViewedMovies));
    }

    @SuppressWarnings("unchecked")
    public void queryForSerialByRating(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Serial> sortedSerialsByRating = new ArrayList<>();
        for (Serial serial : serials) {
            if (action.getFilters().get(1).get(0) != null) {
                int ok = 0;
                String inputGenre = action.getFilters().get(1).get(0);
                for (String genre : serial.getGenres()) {
                    if (genre.equals(inputGenre)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    if (serial.getRating() > 0)
                        sortedSerialsByRating.add(serial);
                }
            } else {
                if (serial.getRating() > 0)
                    sortedSerialsByRating.add(serial);
            }
        }
        if ((action.getFilters().get(0).get(0)) != null) {
            int inputYear = Integer.parseInt(action.getFilters().get(0).get(0));
            sortedSerialsByRating.removeIf((serial -> serial.getYear() != inputYear));
        }


        sortedSerialsByRating.sort(Comparator.comparing(Show::getTitle));

        sortedSerialsByRating.sort(Comparator.comparingDouble(Serial::getRating));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(sortedSerialsByRating);

        if (sortedSerialsByRating.size() > action.getNumber()) {
            sortedSerialsByRating.subList(action.getNumber(), sortedSerialsByRating.size()).clear();
        }

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                sortedSerialsByRating));
    }

    @SuppressWarnings("unchecked")
    public void queryForSerialByFavourite(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Serial> sortedFavouriteSerials = new ArrayList<>();
        for (Serial serial : serials) {
            if (action.getFilters().get(1).get(0) != null) {
                int ok = 0;
                String inputGenre = action.getFilters().get(1).get(0);
                for (String genre : serial.getGenres()) {
                    if (genre.equals(inputGenre)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    sortedFavouriteSerials.add(serial);
                }
            } else {
                sortedFavouriteSerials.add(serial);
            }
        }
        if ((action.getFilters().get(0).get(0)) != null) {
            int inputYear = Integer.parseInt(action.getFilters().get(0).get(0));
            sortedFavouriteSerials.removeIf((serial -> serial.getYear() != inputYear));
        }

        sortedFavouriteSerials.removeIf(serial ->
                serial.favouriteAppearancesOfSerial(users) == 0);
        sortedFavouriteSerials.sort(Comparator.comparing(Show::getTitle));

        sortedFavouriteSerials.sort(Comparator.comparingInt(o -> o.favouriteAppearancesOfSerial(users)));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(sortedFavouriteSerials);

        if (sortedFavouriteSerials.size() > action.getNumber()) {
            sortedFavouriteSerials.subList(action.getNumber(), sortedFavouriteSerials.size()).clear();
        }

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                sortedFavouriteSerials));
    }

    @SuppressWarnings("unchecked")
    public void queryForSerialByLenght(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Serial> sortedLongestSerials = new ArrayList<>();
        for (Serial serial : serials) {
            if (action.getFilters().get(1).get(0) != null) {
                int ok = 0;
                String inputGenre = action.getFilters().get(1).get(0);
                for (String genre : serial.getGenres()) {
                    if (genre.equals(inputGenre)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    sortedLongestSerials.add(serial);
                }
            } else {
                sortedLongestSerials.add(serial);
            }
        }
        if ((action.getFilters().get(0).get(0)) != null) {
            int inputYear = Integer.parseInt(action.getFilters().get(0).get(0));
            sortedLongestSerials.removeIf((serial -> serial.getYear() != inputYear));
        }

        sortedLongestSerials.sort(Comparator.comparing(Show::getTitle));

        sortedLongestSerials.sort(Comparator.comparingInt(Serial::totalDuration));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(sortedLongestSerials);

        if (sortedLongestSerials.size() > action.getNumber()) {
            sortedLongestSerials.subList(action.getNumber(), sortedLongestSerials.size()).clear();
        }

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                sortedLongestSerials));
    }

    @SuppressWarnings("unchecked")
    public void queryForSerialByMostViews(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Serial> sortedMostViewedSerials = new ArrayList<>();
        for (Serial serial : serials) {
            if (action.getFilters().get(1).get(0) != null) {
                int ok = 0;
                String inputGenre = action.getFilters().get(1).get(0);
                for (String genre : serial.getGenres()) {
                    if (genre.equals(inputGenre)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    sortedMostViewedSerials.add(serial);
                }
            } else {
                sortedMostViewedSerials.add(serial);
            }
        }
        if ((action.getFilters().get(0).get(0)) != null) {
            int inputYear = Integer.parseInt(action.getFilters().get(0).get(0));
            sortedMostViewedSerials.removeIf((serial -> serial.getYear() != inputYear));
        }

        sortedMostViewedSerials.removeIf(serial -> serial.totalNumberOfViews(users) == 0);

        sortedMostViewedSerials.sort(Comparator.comparing(Show::getTitle));

        sortedMostViewedSerials.sort(Comparator.comparingInt(o -> o.totalNumberOfViews(users)));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(sortedMostViewedSerials);

        if (sortedMostViewedSerials.size() > action.getNumber()) {
            sortedMostViewedSerials.subList(action.getNumber(), sortedMostViewedSerials.size()).clear();
        }

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                sortedMostViewedSerials));
    }

    @SuppressWarnings("unchecked")
    public void queryUsers(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<User> sortedUserList = new ArrayList<>(users);

        sortedUserList.removeIf(user -> user.getNumberOfRatings() == 0);
        sortedUserList.sort(Comparator.comparing(User::getUsername));

        sortedUserList.sort(Comparator.comparingInt(User::getNumberOfRatings));

        if ((action.getSortType()).equals("desc"))
            Collections.reverse(sortedUserList);

        if (sortedUserList.size() > action.getNumber()) {
            sortedUserList.subList(action.getNumber(), sortedUserList.size()).clear();
        }

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", "Query result: " +
                sortedUserList));
    }

    @SuppressWarnings("unchecked")
    public void standardRecommendation(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        for (User user : users) {
            if ((user.getUsername()).equals(action.getUsername())) {
                String type = action.getType();
                String newType = type.substring(0, 1).toUpperCase() + type.substring(1);
                for (Movie movie : movies) {
                    if (!user.getHistory().containsKey(movie.getTitle())) {
                        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", newType +
                                "Recommendation result: " + movie.getTitle()));
                        return;
                    }
                }

                for (Serial serial : serials) {
                    if (!user.getHistory().containsKey(serial.getTitle())) {
                        arrayResult.add(fileWriter.writeFile(action.getActionId(), "", newType +
                                "Recommendation result: " + serial.getTitle()));
                        return;
                    }
                }
                arrayResult.add(fileWriter.writeFile(action.getActionId(), "", newType +
                        "Recommendation cannot be applied!"));
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void bestUnseenRecommendation(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        List<Movie> movieSortedByRatings = new ArrayList<>(movies);
        List<Serial> serialSortedByRatings = new ArrayList<>(serials);

        movieSortedByRatings.sort((o1, o2) -> Double.compare(o2.getRating(), o1.getRating()));

        serialSortedByRatings.sort((o1, o2) -> Double.compare(o2.getRating(), o1.getRating()));

        for (User user : users) {
            if ((user.getUsername()).equals(action.getUsername())) {
                for (Movie movie : movieSortedByRatings) {
                    if (!user.getHistory().containsKey(movie.getTitle())) {
                        for (Serial serial : serialSortedByRatings) {
                            if (!user.getHistory().containsKey(serial.getTitle()) &&
                                    serial.getRating() > movie.getRating()) {
                                arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                        "BestRatedUnseenRecommendation result: " + serial.getTitle()));
                                return;
                            }
                        }
                        arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                "BestRatedUnseenRecommendation result: " + movie.getTitle()));
                        return;
                    }
                }
                arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                        "BestRatedUnseenRecommendation cannot be applied!"));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void popularRecommendation(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        Map<String, Integer> genrePopularity = new HashMap<>();
        genrePopularity.put("TV Movie", 0);
        genrePopularity.put("Drama", 0);
        genrePopularity.put("Fantasy", 0);
        genrePopularity.put("Comedy", 0);
        genrePopularity.put("Family", 0);
        genrePopularity.put("War", 0);
        genrePopularity.put("Sci-Fi & Fantasy", 0);
        genrePopularity.put("Crime", 0);
        genrePopularity.put("Animation", 0);
        genrePopularity.put("Science Fiction", 0);
        genrePopularity.put("Action", 0);
        genrePopularity.put("Horror", 0);
        genrePopularity.put("Mystery", 0);
        genrePopularity.put("Western", 0);
        genrePopularity.put("Adventure", 0);
        genrePopularity.put("Action & Adventure", 0);
        genrePopularity.put("Romance", 0);
        genrePopularity.put("Thriller", 0);
        genrePopularity.put("Kids", 0);
        genrePopularity.put("History", 0);

        for (User user : users) {
            for (Map.Entry<String, Integer> entry : user.getHistory().entrySet()) {
                String videoName = entry.getKey();
                int numberOfViews = entry.getValue();
                for (Movie movie : movies) {
                    if ((movie.getTitle()).equals(videoName)) {
                        for (String movieGenre : movie.getGenres()) {
                            int PopularityOfGenre = genrePopularity.get(movieGenre);
                            PopularityOfGenre += numberOfViews;
                            genrePopularity.put(movieGenre, PopularityOfGenre);
                        }
                        break;
                    }
                }
                for (Serial serial : serials) {
                    if ((serial.getTitle()).equals(videoName)) {
                        for (String serialGenre : serial.getGenres()) {
                            int PopularityOfGenre = genrePopularity.get(serialGenre);
                            PopularityOfGenre += numberOfViews;
                            genrePopularity.put(serialGenre, PopularityOfGenre);
                        }
                        break;
                    }
                }
            }
        }
        List<String> sortedGenres;
        sortedGenres = genrePopularity.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).toList();
        List<String> reversedSortedGenres = new ArrayList<>(sortedGenres);
        Collections.reverse(reversedSortedGenres);

        for (User user : users) {
            if ((user.getUsername()).equals(action.getUsername())) {
                if (!user.getSubscriptionType().equals("PREMIUM")) {
                    arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                            "PopularRecommendation cannot be applied!"));
                    return;
                }

                for (String popularGenre : reversedSortedGenres) {
                    for (Movie movie : movies) {
                        for (String movieGenre : movie.getGenres()) {
                            if (movieGenre.equals(popularGenre) && !user.getHistory().containsKey(movie.getTitle())) {
                                arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                        "PopularRecommendation result: " + movie.getTitle()));
                                return;
                            }
                        }
                    }
                    for (Serial serial : serials) {
                        for (String serialGenre : serial.getGenres()) {
                            if (serialGenre.equals(popularGenre) && !user.getHistory().containsKey(serial.getTitle())) {
                                arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                        "PopularRecommendation result: " + serial.getTitle()));
                                return;
                            }
                        }
                    }
                }
                arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                        "PopularRecommendation cannot be applied!"));
                return;

            }
        }
    }

    @SuppressWarnings("unchecked")
    public void favoriteRecommendation(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        Map<String, Integer> moviesMap = new HashMap<>();
        for (Movie movie : movies)
            moviesMap.put(movie.getTitle(), 0);

        Map<String, Integer> serialMap = new HashMap<>();
        for (Serial serial : serials)
            serialMap.put(serial.getTitle(), 0);

        for (User user : users) {
            for (String favoriteVideo : user.getFavoriteMovies()) {
                int ok = 0;
                for (Movie movie : movies) {
                    if (favoriteVideo.equals(movie.getTitle())) {
                        int favoriteCounter = moviesMap.get(favoriteVideo);
                        favoriteCounter++;
                        moviesMap.put(favoriteVideo, favoriteCounter);
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1)
                    continue;
                for (Serial serial : serials) {
                    if (favoriteVideo.equals(serial.getTitle())) {
                        int favoriteCounter = serialMap.get(favoriteVideo);
                        favoriteCounter++;
                        serialMap.put(favoriteVideo, favoriteCounter);
                    }
                }
            }
        }
        Map<String, Integer> videoMap = new HashMap<>();
        videoMap.putAll(moviesMap);
        videoMap.putAll(serialMap);
        List<String> sortFavoriteVideos = new ArrayList<>();
        sortFavoriteVideos.addAll(movies.stream().map(Movie::getTitle).toList());
        sortFavoriteVideos.addAll(serials.stream().map(Serial::getTitle).toList());
        sortFavoriteVideos.sort(Comparator.comparing(videoMap::get).reversed());

        for (User user : users) {
            if ((user.getUsername()).equals(action.getUsername())) {
                if (!user.getSubscriptionType().equals("PREMIUM")) {
                    arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                            "FavoriteRecommendation cannot be applied!"));
                    return;
                }

                for (String favoriteMovieToWatch : sortFavoriteVideos) {
                    if (!user.getHistory().containsKey(favoriteMovieToWatch)) {
                        arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                                "FavoriteRecommendation result: " + favoriteMovieToWatch));
                        return;
                    }
                }
                arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                        "FavoriteRecommendation cannot be applied!"));
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void searchRecommendation(ActionInputData action, Writer fileWriter, JSONArray arrayResult) throws IOException {
        Map<String, Integer> genreMap = new HashMap<>();
        for (Movie movie : movies) {
            for (String genre : movie.getGenres())
                genreMap.put(genre, 0);
        }
        for (Serial serial : serials) {
            for (String genre : serial.getGenres())
                genreMap.put(genre, 0);
        }
        if (!genreMap.containsKey(action.getGenre())) {
            arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                    "SearchRecommendation cannot be applied!"));
            return;
        }
        List<Show> list = new ArrayList<>();

        for (User user : users) {
            if ((user.getUsername()).equals(action.getUsername())) {
                if (!user.getSubscriptionType().equals("PREMIUM")) {
                    arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                            "SearchRecommendation cannot be applied!"));
                    return;
                }
                for (Movie movie : movies) {
                    for (String genre : movie.getGenres()) {
                        if (genre.equals(action.getGenre()) && !user.getHistory().containsKey(movie.getTitle())) {
                            list.add(movie);
                            break;
                        }
                    }
                }

                for (Serial serial : serials) {
                    for (String genre : serial.getGenres()) {
                        if (genre.equals(action.getGenre()) && !user.getHistory().containsKey(serial.getTitle())) {
                            list.add(serial);
                            break;
                        }
                    }
                }
                break;
            }
        }

        if (list.size() == 0) {
            arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                    "SearchRecommendation cannot be applied!"));
            return;
        }

        list.sort(Comparator.comparing(Show::getTitle));

        list.sort(Comparator.comparingDouble(Show::getRating));

        arrayResult.add(fileWriter.writeFile(action.getActionId(), "",
                "SearchRecommendation result: " + list));
    }

    public void executeActions(Writer fileWriter, JSONArray arrayResult) throws IOException {
        for (ActionInputData action : commands) {
            switch (action.getActionType()) {

                case "command":

                    switch (action.getType()) {
                        case "view" -> view(action, fileWriter, arrayResult);
                        case "favorite" -> favorite(action, fileWriter, arrayResult);
                        case "rating" -> rating(action, fileWriter, arrayResult);
                    }
                    break;

                case "query":

                    switch (action.getObjectType()) {

                        case "actors":

                            switch (action.getCriteria()) {
                                case "average" -> average(action, fileWriter, arrayResult);
                                case "awards" -> awards(action, fileWriter, arrayResult);
                                case "filter_description" -> filter_description(action, fileWriter, arrayResult);
                            }
                            break;

                        case "movies":

                            switch (action.getCriteria()) {
                                case "ratings" -> queryForMovieByRatings(action, fileWriter, arrayResult);
                                case "favorite" -> queryForMovieByFavourite(action, fileWriter, arrayResult);
                                case "longest" -> queryForMovieByLenght(action, fileWriter, arrayResult);
                                case "most_viewed" -> queryForMovieByMostViews(action, fileWriter, arrayResult);
                            }
                            break;

                        case "shows":

                            switch (action.getCriteria()) {
                                case "ratings" -> queryForSerialByRating(action, fileWriter, arrayResult);
                                case "favorite" -> queryForSerialByFavourite(action, fileWriter, arrayResult);
                                case "longest" -> queryForSerialByLenght(action, fileWriter, arrayResult);
                                case "most_viewed" -> queryForSerialByMostViews(action, fileWriter, arrayResult);
                            }
                            break;

                        case "users":
                            queryUsers(action, fileWriter, arrayResult);
                            break;

                    }
                    break;

                case "recommendation":

                    switch (action.getType()) {
                        case "standard" -> standardRecommendation(action, fileWriter, arrayResult);
                        case "best_unseen" -> bestUnseenRecommendation(action, fileWriter, arrayResult);
                        case "popular" -> popularRecommendation(action, fileWriter, arrayResult);
                        case "favorite" -> favoriteRecommendation(action, fileWriter, arrayResult);
                        case "search" -> searchRecommendation(action, fileWriter, arrayResult);
                    }
            }
        }
    }
}
