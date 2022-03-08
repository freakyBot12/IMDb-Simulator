package actor;

import entertainment.Movie;
import entertainment.Serial;
import fileio.ActorInputData;
import fileio.ShowInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Actor {

    private String name;

    private String careerDescription;

    private ArrayList<String> filmography;

    private Map<ActorsAwards, Integer> awards;

    public Actor (ActorInputData actorInputData) {
        this.name = actorInputData.getName();
        this.careerDescription = actorInputData.getCareerDescription();
        this.filmography = new ArrayList<>(actorInputData.getFilmography());
        this.awards = new HashMap<>(actorInputData.getAwards());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public void setCareerDescription(String careerDescription) {
        this.careerDescription = careerDescription;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public void setFilmography(ArrayList<String> filmography) {
        this.filmography = filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public void setAwards(Map<ActorsAwards, Integer> awards) {
        this.awards = awards;
    }

    public double getRating(List<Movie> movies, List<Serial> serials) {
        double actorRating = 0;
        int ratings = 0;
        for (String show : filmography) {
            int ok = 0;
            for (Movie movie : movies) {
                if (show.equals(movie.getTitle())) {
                    actorRating += movie.getRating();
                    if (movie.getRating() > 0)
                        ratings++;
                    ok = 1;
                    break;
                }
            }
            if (ok == 1)
                continue;

            for (Serial serial:serials) {
                if (show.equals(serial.getTitle())) {
                    actorRating += serial.getRating();
                    if (serial.getRating() > 0)
                        ratings++;
                    break;
                }
            }
        }
        if (ratings > 0)
            actorRating /= ratings;
        return actorRating;
    }

    @Override
    public String toString() {
        return name;
    }
}
