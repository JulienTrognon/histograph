package up.visulog.api.git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class Commit {
    // FIXME: (some of) these fields could have more specialized types than String
    public final String id;
    public final Date date;
    public final String author;
    public final String description;
    public final String mergedFrom;
    public final LinkedList<Map<Path, Integer[]>> change;

    public Commit(String id, String author, Date date, String description, String mergedFrom,
            LinkedList<Map<Path, Integer[]>> change) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.description = description;
        this.mergedFrom = mergedFrom;
        this.change = change;
    }

    // TODO: factor this out (similar code will have to be used for all git
    // commands)
    public static List<Commit> parseLogFromCommand(Path gitPath) {
        ProcessBuilder builder = new ProcessBuilder("git", "log", "--numstat").directory(gitPath.toFile());
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException("Error running \"git log --numstat\".", e);
        }
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return parseLog(reader);
    }

    public static List<Commit> parseLog(BufferedReader reader) {
        var result = new ArrayList<Commit>();
        Optional<Commit> commit = parseCommit(reader);
        while (commit.isPresent()) {
            result.add(commit.get());
            commit = parseCommit(reader);
        }
        return result;
    }

    /**
     * Parses a log item and outputs a commit object. Exceptions will be thrown in
     * case the input does not have the proper format. Returns an empty optional if
     * there is nothing to parse anymore.
     */
    public static Optional<Commit> parseCommit(BufferedReader input) {
        try {
            var line = input.readLine();
            if (line == null)
                return Optional.empty(); // if no line can be read, we are done reading the buffer
            var idChunks = line.split(" ");
            if (!idChunks[0].equals("commit"))
                parseError();
            var builder = new CommitBuilder(idChunks[1]);

            line = input.readLine();
            while (!line.isEmpty()) {
                var colonPos = line.indexOf(":");
                var fieldName = line.substring(0, colonPos);
                var fieldContent = line.substring(colonPos + 1).trim();
                switch (fieldName) {
                    case "Author":
                        builder.setAuthor(fieldContent);
                        break;
                    case "Merge":
                        builder.setMergedFrom(fieldContent);
                        break;
                    case "Date":
                        try {
                            var item = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH)
                                    .parse(fieldContent);
                            builder.setDate(item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    default: // TODO: warn the user that some field was ignored
                }
                line = input.readLine(); // prepare next iteration
                if (line == null)
                    parseError(); // end of stream is not supposed to happen now (commit data incomplete)
            }

            // now read the commit message per se
            var description = input.lines() // get a stream of lines to work with
                    .takeWhile(currentLine -> !currentLine.isEmpty()) // take all lines until the first empty one
                                                                      // (commits are separated by empty lines). Remark:
                                                                      // commit messages are indented with spaces, so
                                                                      // any blank line in the message contains at least
                                                                      // a couple of spaces.
                    .map(String::trim) // remove indentation
                    .reduce("", (accumulator, currentLine) -> accumulator + currentLine); // concatenate everything
            builder.setDescription(description);

            input.mark(30);
            while (line.isEmpty())
                line = input.readLine();
            if (line.startsWith("commit"))
                input.reset();
            else {
                LinkedList<Map<Path, Integer[]>> c = new LinkedList<>();
                while (!line.isEmpty()) {
                    Map<Path, Integer[]> lines = new HashMap<>();
                    String[] l = line.replaceAll("\\s+", " ").split(" ");
                    if (l[0].equals("-"))
                        l[0] = "0";
                    if (l[1].equals("-"))
                        l[1] = "0";
                    Integer[] nbChange = { Integer.parseInt(l[0]), Integer.parseInt(l[1]) };
                    lines.put(Paths.get(l[2]), nbChange);
                    c.add(lines);
                    line = input.readLine();
                    if (line == null)
                        break;
                }
                builder.setLineChanged(c);
            }
            return Optional.of(builder.createCommit());
        } catch (IOException e) {
            parseError();
        }
        return Optional.empty(); // this is supposed to be unreachable, as parseError should never return
    }

    // Helper function for generating parsing exceptions. This function *always*
    // quits on an exception. It *never* returns.
    private static void parseError() {
        throw new RuntimeException("Wrong commit format.");
    }

    private static String changeToString(LinkedList<Map<Path, Integer[]>> c) {
        StringBuilder sb = new StringBuilder();
        for (var e : c) {
            for (var el : e.entrySet()) {
                sb.append("[{" + el.getKey() + "=" + Arrays.toString(el.getValue()) + "}]");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Commit{" + "id='" + id + '\'' + (mergedFrom != null ? ("mergedFrom...='" + mergedFrom + '\'') : "")
                + ", date='" + date + '\'' + ", author='" + author + '\'' + ", description='" + description + '\''
                + (change != null ? (", lineChanged='" + changeToString(change) + '\'' + "}") : "}");
    }
}
