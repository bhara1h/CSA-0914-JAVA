import java.util.Objects;

public final class Team {
    private final String name;
    private final int points;
    private final int goalDifference;

    public Team(String name, int points, int goalDifference) {
        this.name = Objects.requireNonNull(name, "name");
        this.points = points;
        this.goalDifference = goalDifference;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    @Override
    public String toString() {
        return name + "(" + points + "," + goalDifference + ")";
    }
}
