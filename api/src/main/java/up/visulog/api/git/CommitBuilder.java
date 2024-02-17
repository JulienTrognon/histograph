package up.visulog.api.git;
import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;


public class CommitBuilder {
    private final String id;
    private String author;
    private Date date;
    private String description;
    private String mergedFrom;
    private LinkedList<Map<Path, Integer[]>> change;

    public CommitBuilder(String id) {
        this.id = id;
    }

    public CommitBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public CommitBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public CommitBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommitBuilder setMergedFrom(String mergedFrom) {
        this.mergedFrom = mergedFrom;
        return this;
    }

    public CommitBuilder setLineChanged(LinkedList<Map<Path, Integer[]>> change) {
        this.change = change;
        return this;
    }

    public Commit createCommit() {
        return new Commit(id, author, date, description, mergedFrom, change);
    }
}