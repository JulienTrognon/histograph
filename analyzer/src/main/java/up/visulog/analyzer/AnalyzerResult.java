package up.visulog.analyzer;

import up.visulog.config.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class AnalyzerResult {
    public List<AnalyzerPlugin.Result> getSubResults() {
        return subResults;
    }

    private final Configuration config;

    private final List<AnalyzerPlugin.Result> subResults;

    public AnalyzerResult(List<AnalyzerPlugin.Result> subResults, Configuration config, Configuration config1) {
        this.subResults = subResults;
        this.config = config1;
    }

    public Configuration getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return subResults.stream().map(AnalyzerPlugin.Result::getResultAsString).reduce("", (acc, cur) -> acc + "\n" + cur);
    }

    public String toHTML() {
        subResults.stream()
                  .sorted(Comparator.comparing(AnalyzerPlugin.Result :: getPluginName)
                  .reversed())
                  .collect(Collectors.toList());
        return subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + "\n" + cur);
    }

    public void toJsonFile() {
        for(var t: subResults){
            t.jsonToFile();
        }
    }

    public void toHtmlFile() {
        for(var t: subResults){
            t.htmlToFile();
        }
    }

    public String toJSON() {
        try {
            return subResults.stream().map(AnalyzerPlugin.Result::getJson).reduce("", (acc, cur) ->  cur);
        } catch (Exception e){
            e.printStackTrace();
        }
        return "{}";
    }

}
