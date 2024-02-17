package up.visulog.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.yaml.snakeyaml.Yaml;
import org.apache.commons.validator.routines.UrlValidator;

import up.visulog.util.Logger;

public class Config {

    static Logger log = new Logger();
    DataConfig dataConfig = null;

    public DataConfig getDataConfig() {
        return dataConfig;
    }

    public Config() {
        verifConfig();
    }

    public Map<String, Object> recoverDataConfig() {
        Map<String, Object> dataConf = null;
        try {
            File config = new File("histoGRAPH/config.yml");
            if (!config.exists()) {
                generateConfig();
            } else {
                InputStream inputStream = new FileInputStream(config);
                Yaml yaml = new Yaml();
                dataConf = yaml.load(inputStream);
            }
        } catch (IOException ex) {
            log.error(ex.toString());
        }
        return dataConf;
    }

    /**
     *
     * @return false if config are not correct
     */
    public boolean verifConfig() {
        Map<String, Object> data = recoverDataConfig();
        if (data != null) {
            dataConfig = new DataConfig(data);
            boolean verifGood = true;
            for (Map.Entry<String, Object> mapentry : data.entrySet()) {
                switch (mapentry.getKey()) {
                    case "project_name":
                        if (!dataConfig.getUseWebApi() && ((String) mapentry.getValue()).length() == 0) {
                            verifGood = false;
                            log.error("Config fields: project_name should not be empty when not using wep api");
                        }
                        break;
                    case "git_path":
                        if (dataConfig.getUseOtherGitPath() && ((String) mapentry.getValue()).length() == 0) {
                            verifGood = false;
                            log.error("Config fields: git_path should not be empty when using another git path");
                        }
                        break;
                    case "service":
                        if (dataConfig.getUseWebApi() && ((String) mapentry.getValue()).length() == 0) {
                            verifGood = false;
                            log.error("Config fields: service should not be empty when using wep api");
                        }
                        break;
                    case "url":
                        String[] scheme = { "http", "https" };
                        UrlValidator urlValidator = new UrlValidator(scheme, UrlValidator.ALLOW_LOCAL_URLS);
                        if (dataConfig.getUseWebApi() && !urlValidator.isValid((String) mapentry.getValue())) {
                            verifGood = false;
                            log.error("Config fields: url should be an valid url when using wep api");
                        }
                        break;
                    case "parameter":
                        if (dataConfig.getUseWebApi() && dataConfig.getPrivateRepo()
                                && ((String) mapentry.getValue()).length() == 0) {
                            verifGood = false;
                            log.error("Config fields: parameter should not be empty when using wep api and private repo");
                        }
                        break;
                    case "token":
                        if (dataConfig.getUseWebApi() && dataConfig.getPrivateRepo()
                                && ((String) mapentry.getValue()).length() == 0) {
                            verifGood = false;
                            log.error("Config fields: token should not be empty when using wep api and private repo");
                        }
                        break;
                    case "plugins":
                        @SuppressWarnings("unchecked")
                        ArrayList<LinkedHashMap<String, Object>> plugins = (ArrayList<LinkedHashMap<String, Object>>) mapentry
                                .getValue();
                        verifGood = verifPlugins(plugins);
                        break;
                    default:
                        if (dataConfig.getUseWebApi() && mapentry.getValue() == null) {
                            verifGood = false;
                            log.error("Config fields: " + mapentry.getKey() + " should not be empty when using wep api");
                        }
                        break;
                }
            }
            return verifGood;
        }
        return false;
    }

    public static boolean verifPlugins(List<LinkedHashMap<String, Object>> plugins) {
        for (int i = 0; i < plugins.size(); i++) {
            LinkedHashMap<String, Object> pluginsMap = plugins.get(i);
            for (Map.Entry<String, Object> mapentry : pluginsMap.entrySet()) {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> plugin = (LinkedHashMap<String, Object>) mapentry.getValue();
                boolean enable = true;
                for (Map.Entry<String, Object> entry : plugin.entrySet()) {
                    if (entry.getKey().equals("enable")) {
                        if (entry.getValue() == null) {
                            log.error("Config fields: " + mapentry.getKey() + " " + entry.getKey()
                                    + " should be initialized as a boolean");
                            return false;
                        } else {
                            if (entry.getValue() instanceof Boolean)
                                enable = (boolean) entry.getValue();
                            else
                                log.error("Config fields: " + mapentry.getKey() + " " + entry.getKey()
                                        + " should be a boolean");
                        }
                    } else if (enable && entry.getValue() == null) {
                        log.error("Config fields: " + mapentry.getKey() + " " + entry.getKey()
                                + " should not be empty when the 'enable' field is at true");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void generateConfig() {
        try {
            boolean force = false;
            File dir = new File("histoGRAPH");
            File configFile = new File("histoGRAPH/config.yml");
            if (!dir.exists()) {
                dir.mkdirs();
                force = true;
            }
            if (force || !configFile.exists() || !verifConfig()) {
                Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/config/config.yml")),
                        configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                if (configFile.exists()) {
                    log.info("Config was created, Please configure the config and restart the program");
                    System.exit(0);
                }
            }
        } catch (IOException ex) {
            log.error(ex.toString());
        }
    }
}