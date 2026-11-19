import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Q5Demo {
    private static final Comparator<Team> STANDINGS_ORDER =
            Comparator.comparingInt(Team::getPoints).reversed()
                    .thenComparing(Comparator.comparingInt(Team::getGoalDifference).reversed())
                    .thenComparing(Team::getName);

    private Q5Demo() {
    }

    public static void main(String[] args) {
        Leaderboard<Team> leaderboard = new Leaderboard<>(STANDINGS_ORDER);
        leaderboard.add(new Team("Panthers", 9, 4));
        leaderboard.add(new Team("Cobras", 6, 1));
        leaderboard.add(new Team("Bisons", 9, 4));
        leaderboard.add(new Team("Eagles", 9, 6));

        List<Team> originalBeforeSort = leaderboard.originalEntries();
        List<Team> sorted = leaderboard.sortedCopy();
        require(sorted.toString().equals(
                "[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]"),
                "Unexpected standings: " + sorted);
        require(leaderboard.originalEntries().equals(originalBeforeSort),
                "sortedCopy must not mutate the leaderboard's original list");
        List<Team> independentCopy = leaderboard.sortedCopy();
        independentCopy.clear();
        require(leaderboard.originalEntries().equals(originalBeforeSort),
                "Changing a returned list must not affect leaderboard entries");
        Leaderboard<Team> copiedTeams = new Leaderboard<>(STANDINGS_ORDER);
        List<? extends Team> wildcardInput = List.of(new Team("Falcons", 3, 0));
        copiedTeams.addAll(wildcardInput);
        require(copiedTeams.sortedCopy().size() == 1,
                "addAll must accept a list of the leaderboard element type");

        List<Team> sameRank = new ArrayList<>(List.of(
                new Team("Zulu", 5, 0), new Team("Alpha", 5, 0)));
        sameRank.sort(STANDINGS_ORDER);
        require(sameRank.toString().equals("[Alpha(5,0), Zulu(5,0)]"),
                "Name must break points/goal-difference ties");

        List<Team> exactTie = new ArrayList<>(List.of(
                new Team("Echo", 2, -1), new Team("Echo", 2, -1)));
        Team firstExactTie = exactTie.get(0);
        exactTie.sort(STANDINGS_ORDER);
        require(exactTie.get(0) == firstExactTie, "Java's stable sort must preserve exact ties");

        Comparator<Team> deliberatelyFaulty =
                Comparator.comparingInt(Team::getPoints).reversed();
        List<Team> faultyResult = new ArrayList<>(List.of(
                new Team("Panthers", 9, 4), new Team("Bisons", 9, 4)));
        faultyResult.sort(deliberatelyFaulty);
        require(deliberatelyFaulty.compare(faultyResult.get(0), faultyResult.get(1)) == 0,
                "Faulty comparator should fail to distinguish same-point teams");
        require(!faultyResult.get(0).getName().equals("Bisons"),
                "The deliberately faulty comparator should demonstrate wrong tie ordering");

        System.out.println(sorted);
        System.out.println("Sorted leaderboard: " + sorted);
        System.out.println("Original insertion order: " + leaderboard.originalEntries());
        System.out.println("Points-only faulty comparator on a tied sample: " + faultyResult);
        System.out.println("Tie checks: name tie-break passed; exact ties remain stable.");
        System.out.println("All Q5 checks passed.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
