package up.visulog.cli;

import up.visulog.analyzer.Analyzer;

import up.visulog.api.web.WebApi;

import up.visulog.webgen.WebGen;
import up.visulog.config.*;
import up.visulog.util.Logger;

import java.io.File;
import java.nio.file.*;
import java.util.*;
// import up.visulog.webgen.*;

public class CLILauncher {

    private static Logger log = new Logger();

    public static void main(String[] args) {
        var config = makeConfigFromCommandLineArgs();
        if (config.isPresent()) {
            if(config.get().getPluginConfigs().size() == 0){
                log.warnExit("You have deactivated all plugins. Please activate at least one.");
            }
            log.info("Retrieving information from git.");
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            log.info("Data recover.");

            results.toJsonFile();
            results.toHtmlFile();
            new WebGen(results);
        } else log.errorFatal("An error has occurred.");

    }

    @SuppressWarnings("unchecked")
    static Optional<Configuration> makeConfigFromCommandLineArgs() {
        Config config = new Config();
        config.generateConfig();
        DataConfig dataFromConfig = config.getDataConfig();
        var enablePlugin = new HashMap<String, PluginConfig>();
        Path gitPath = null;
        if (dataFromConfig.getUseOtherGitPath()) {
            if (dataFromConfig.getGitPath().length() == 0) {
                log.errorFatal("You have activated \"use_other_git_path\". Please enter the path of your git folder.");
            }
            File gitFolder = new File(dataFromConfig.getGitPath() + "/.git");
            if (!gitFolder.isDirectory()) {
                log.errorFatal("The path does not correspond to a git folder");
            }
            gitPath = Paths.get(dataFromConfig.getGitPath());
        } else {
            gitPath = FileSystems.getDefault().getPath(".");
        }

        if (dataFromConfig.getUseWebApi()) {
            WebApi.setConfig(config);
            log.info("You have activated the API of " + dataFromConfig.getService());
        }

        var plugins = dataFromConfig.getPlugins();

        for (int i = 0; i < plugins.size(); i++) {
            LinkedHashMap<String, Object> pluginsMap = plugins.get(i);
            for (Map.Entry<String, Object> mapentry : pluginsMap.entrySet()) {
                LinkedHashMap<String, Object> plugin = (LinkedHashMap<String, Object>) mapentry.getValue();
                PluginConfig configPlugin = new PluginConfig();
                boolean enable = false;
                for (Map.Entry<String, Object> entry : plugin.entrySet()) {
                    if (entry.getKey().equals("enable")) {
                        enable = Boolean.parseBoolean(entry.getValue().toString());
                    }
                    if (entry.getKey().equals("html_output")) {
                        configPlugin.setHtml_output(Boolean.parseBoolean(entry.getValue().toString()));
                    }
                    if (entry.getKey().equals("json_output")) {
                        configPlugin.setJson_output(Boolean.parseBoolean(entry.getValue().toString()));
                    }
                    if (entry.getKey().equals("chartTypes")) {
                        configPlugin.setChartTypes((List<String>) entry.getValue());
                    }
                }
                if (enable) {
                    enablePlugin.put(mapentry.getKey(), configPlugin);
                }
                enable = false;
            }
        }
        return Optional.of(new Configuration(gitPath, enablePlugin, dataFromConfig));
    }
}