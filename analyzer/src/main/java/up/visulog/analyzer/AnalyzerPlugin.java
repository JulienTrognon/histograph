package up.visulog.analyzer;

// import org.json.JSONPropertyIgnore;
// import org.json.JSONPropertyName;

// import java.lang.module.Configuration;

public interface AnalyzerPlugin {
    interface Result {

        String getResultAsString();

        String getResultAsHtmlDiv();

        String getJson();

        void jsonToFile();

        String getPluginName();

        void htmlToFile();
    }

    /**
     * run this analyzer plugin
     */
    void run();

    /**
     *
     * @return the result of this analysis. Runs the analysis first if not already done.
     */
    Result getResult();
}
