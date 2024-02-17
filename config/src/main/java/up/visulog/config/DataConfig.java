package up.visulog.config;

import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import up.visulog.util.Logger;

public class DataConfig {
    private String project_name, git_path, service, url, parameter, token;
    private boolean use_other_git_path, use_web_api, private_repo;
    private int project_id;
    private ArrayList<LinkedHashMap<String, Object>> plugins;
    private Map<String, Object> allData;

    static Logger log = new Logger();

    public DataConfig(Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            switch (entry.getKey()) {
                case "project_name":
                    if (entry.getValue() instanceof String)
                        project_name = (String) entry.getValue();
                    else
                        log.error("Config fields: project_name should be a String");
                    break;
                case "git_path":
                    if (entry.getValue() instanceof String)
                        git_path = ((String) entry.getValue()).replace("\\", "/");
                    else
                        log.error("Config fields: git_path should be a String");
                    break;
                case "service":
                    if (entry.getValue() instanceof String)
                        service = (String) entry.getValue();
                    else
                        log.error("Config fields: service should be a String");
                    break;
                case "url":
                    if (entry.getValue() instanceof String)
                        url = ((String) entry.getValue()).replace("\\", "/");
                    else
                        log.error("Config fields: url should be a String");
                    break;
                case "parameter":
                    if (entry.getValue() instanceof String)
                        parameter = (String) entry.getValue();
                    else
                        log.error("Config fields: parameter should be a String");
                    break;
                case "token":
                    if (entry.getValue() instanceof String)
                        token = (String) entry.getValue();
                    else
                        log.error("Config fields: token should be a String");
                    break;
                case "use_other_git_path":
                    if (entry.getValue() instanceof Boolean)
                        use_other_git_path = (boolean) entry.getValue();
                    else
                        log.error("Config fields: use_other_git_path should be a boolean");
                    break;
                case "use_web_api":
                    if (entry.getValue() instanceof Boolean)
                        use_web_api = (boolean) entry.getValue();
                    else
                        log.error("Config fields: use_web_api should be a boolean");
                    break;
                case "private_repo":
                    if (entry.getValue() instanceof Boolean)
                        private_repo = (boolean) entry.getValue();
                    else
                        log.error("Config fields: private_repo should be a boolean");
                    break;
                case "project_id":
                    if (entry.getValue() instanceof Integer)
                        project_id = (int) entry.getValue();
                    else
                        log.error("Config fields: project_id should be an int");
                    break;
                case "plugins":
                    if (entry.getValue() instanceof ArrayList) {
                        @SuppressWarnings("unchecked")
                        ArrayList<LinkedHashMap<String, Object>> tmp = (ArrayList<LinkedHashMap<String, Object>>) entry
                                .getValue();
                        plugins = tmp;
                    } else
                        log.errorFatal("Config fields: plugins should be an ArrayList");
                    break;
            }

        }
        allData = data;
    }

    // Getteur
    public Map<String, Object> getAllData() {
        return allData;
    }

    public String getProjetName() {
        return project_name;
    }

    public String getGitPath() {
        return git_path;
    }

    public String getService() {
        return service;
    }

    public String getUrl() {
        return url;
    }

    public String getParameter() {
        return parameter;
    }

    public String getToken() {
        return token;
    }

    public boolean getUseOtherGitPath() {
        return use_other_git_path;
    }

    public boolean getUseWebApi() {
        return use_web_api;
    }

    public boolean getPrivateRepo() {
        return private_repo;
    }

    public int getProjetId() {
        return project_id;
    }

    public List<LinkedHashMap<String, Object>> getPlugins() {
        return plugins;
    }
}
