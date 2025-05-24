package ma.enset.quizapp1;

public class ScoreEntry implements Comparable<ScoreEntry> {
    private String playerName;
    private int score;
    private long timestamp; // For tiebreaking and sorting by recency if needed

    public ScoreEntry(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
        this.timestamp = System.currentTimeMillis();
    }

    public ScoreEntry(String playerName, int score, long timestamp) {
        this.playerName = playerName;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(ScoreEntry other) {
        // Sort by score (descending)
        int scoreComparison = Integer.compare(other.score, this.score);
        if (scoreComparison != 0) {
            return scoreComparison;
        }
        // If scores are equal, sort by timestamp (most recent first)
        return Long.compare(other.timestamp, this.timestamp);
    }
} 