package eu.aria.dm.moves;

/**
 * Created by WaterschootJB on 5-7-2017.
 */
public class Exchange {

    private Goal goal;
    private double relevance;

    public Exchange(Goal goal){
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
        if (!(o instanceof Exchange)) return false;

        Exchange exchange = (Exchange) o;

        return getGoal().equals(exchange.getGoal());
    }

}
