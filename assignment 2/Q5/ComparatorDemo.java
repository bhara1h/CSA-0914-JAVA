import java.util.Comparator;
import java.util.List;

public final class ComparatorDemo {
    private ComparatorDemo() {
    }

    public static void main(String[] args) {
        Comparator<Team> standingsOrder = Comparator.comparingInt(Team::getPoints).reversed()
                .thenComparing(Comparator.comparingInt(Team::getGoalDifference).reversed())
                .thenComparing(Team::getName);
        List<Team> teams = new java.util.ArrayList<>(List.of(
                new Team("Panthers", 9, 4), new Team("Bisons", 9, 4),
                new Team("Eagles", 9, 6), new Team("Cobras", 6, 1)));
        teams.sort(standingsOrder);
        System.out.println(teams);
    }
}
