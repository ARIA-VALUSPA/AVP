package eu.aria.dm.moves;

/**
 * Created by WaterschootJB on 4-7-2017.
 */
public class Goal{

    private class GoalStatus{

        private boolean completed;
        private boolean accomplished;
        private boolean hold;

        public GoalStatus(boolean completed, boolean accomplished){
            this.completed = completed;
            this.accomplished = accomplished;
            this.hold = false;
        }

        public boolean isCompleted() {
        return completed;
    }

        public void setCompleted(boolean completed) {
        this.completed = completed;
    }

        public boolean isAccomplished() {
        return accomplished;
    }

        public void setAccomplished(boolean accomplished) {
        this.accomplished = accomplished;
    }

        public boolean isOnHold() {return hold;}

        public void setOnHold(boolean hold) { this.hold = hold; }
    }

    private GoalStatus goalStatus;
    private String name;

    public Goal(String name){
        this.name = name;
        this.goalStatus = new GoalStatus(false,false);
    }

    public GoalStatus getStatus(){ return this.goalStatus;}

    public void setStatus(boolean completed, boolean accomplished){
        this.goalStatus.setAccomplished(accomplished);
        this.goalStatus.setCompleted(completed);}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Goal)) return false;

        Goal goal = (Goal) o;

        return name.equals(goal.name);
    }

}

