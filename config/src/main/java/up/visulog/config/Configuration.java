package up.visulog.config;

import java.nio.file.Path;
import java.util.Map;

public class Configuration {

    private final Path gitPath;
    private final Map<String, PluginConfig> plugins;
    private final DataConfig dataConfig;

    public Configuration(Path gitPath, Map<String, PluginConfig> plugins, DataConfig dataConfig) {
        this.gitPath = gitPath;
        this.plugins = Map.copyOf(plugins);
        this.dataConfig = dataConfig;
    }

    public Path getGitPath() {
        return gitPath;
    }

    public DataConfig getDataConfig() {
        return dataConfig;
    }

    public Map<String, PluginConfig> getPluginConfigs() {
        return plugins;
    }
}
