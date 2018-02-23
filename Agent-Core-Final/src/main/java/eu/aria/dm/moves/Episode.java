package eu.aria.dm.moves;

/**
 * Created by WaterschootJB on 5-7-2017.
 */
public class Episode {

    private Goal goal;
    private double relevance;

    public Episode(Goal goal) {
        this.goal = goal;
        this.relevance = 0.0;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Episode)) return false;

        Episode episode = (Episode) o;

        return getGoal().equals(episode.getGoal());
    }


}
