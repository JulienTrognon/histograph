package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Analyzer {
    private final Configuration config;

    public Analyzer(Configuration config) {
        this.config = config;
    }

    public AnalyzerResult computeResults() {
        List<AnalyzerPlugin> plugins = new ArrayList<>();
        for (var pluginConfigEntry: config.getPluginConfigs().entrySet()) {
            var pluginName = pluginConfigEntry.getKey();
            var pluginConfig = pluginConfigEntry.getValue();
            var plugin = makePlugin(pluginName, pluginConfig);
            plugin.ifPresent(plugins::add);
        }
        // run all the plugins
        // TODO: try running them in parallel
        for (var plugin: plugins) plugin.run();

        // store the results together in an AnalyzerResult instance and return it
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()),config, config);
    }

    // TODO: find a way so that the list of plugins is not hardcoded in this factory
    private Optional<AnalyzerPlugin> makePlugin(String pluginName, PluginConfig pluginConfig) {
        switch (pluginName) {
            case "countCommits" : return Optional.of(new CountCommitsPerAuthorPlugin(pluginConfig, config));
            case "countCommitPerDay" : return Optional.of(new CountCommitPerDay(pluginConfig, config));
            case "countAuthors" : return Optional.of(new CountAuthors(pluginConfig, config));
            case "countLineChangedPerAuthor": return Optional.of(new CountLineChangedPerAuthor(pluginConfig, config));
            case "languagesPercentage": return Optional.of(new LanguagesPercentage(pluginConfig, config));
            case "downloadProject": return Optional.of(new DownloadProject(pluginConfig, config));
            case "nbrMerges": return Optional.of(new nbrMerges(pluginConfig, config));
            case "branchesList": return Optional.of(new BranchesList(pluginConfig, config));
            case "projectInfo": return Optional.of(new ProjectInfo(pluginConfig, config));
            default : return Optional.empty();
            

        }
    }

}
