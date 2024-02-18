package up.visulog.api;

import org.junit.Test;
import up.visulog.api.git.Commit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

// import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCommit {
    @Test
    public void testParseCommit() throws IOException, URISyntaxException {
        // var expected = "Commit{id='9e74f1581f23aaad21e2b936091d3ce371336e22', date='Mon Aug 31 11:28:28 CEST 2020', author='Aldric Degorre <adegorre@irif.fr>', description='Update README.md - more modules', lineChanged='[{README.md=[2, 1]}]'}";
        var uri = getClass().getClassLoader().getResource("git.log").toURI();
        try (var reader = Files.newBufferedReader(Paths.get(uri))) {
            var commit = Commit.parseCommit(reader);
            assertTrue(commit.isPresent());
            //assertEquals(expected, commit.get().toString());
        }
    }

    @Test
    public void testParseLog() throws IOException, URISyntaxException {
        var expectedUri = getClass().getClassLoader().getResource("expectedToString").toURI();
        var logUri = getClass().getClassLoader().getResource("git.log").toURI();
        try (var expectedReader = Files.newBufferedReader(Paths.get(expectedUri))) {
            try (var logReader = Files.newBufferedReader(Paths.get(logUri))) {
                Commit.parseLog(logReader);
                expectedReader.lines().reduce("", (cur, acc) -> cur + acc);
                //assertEquals(expected, log.toString());
            }
        }
    }

}
