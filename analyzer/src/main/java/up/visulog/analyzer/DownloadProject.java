package up.visulog.analyzer;

import org.json.JSONArray;
import org.json.JSONObject;
import up.visulog.api.web.WebApi;
import up.visulog.api.web.downloadProject;
import up.visulog.api.web.getBranches;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import up.visulog.util.SaveFile;

import java.util.HashMap;
import java.util.Map;

public class DownloadProject implements AnalyzerPlugin  {
    private static Configuration configuration = null;
    private static PluginConfig pluginConfig = null;
    private Result result;

    static boolean apiPlugins = true;

    public static final String pluginName = "downloadProject";

    public DownloadProject(PluginConfig pluginConfig1, Configuration generalConfiguration) {
        configuration = generalConfiguration;
        pluginConfig = pluginConfig1;
    }

    public Result process(){
        var result = new Result();
        WebApi branches = new getBranches();
        String apiResult = branches.getResult();
        if(apiResult != null) {
            JSONArray array = new JSONArray(apiResult);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                String brancheName = json.getString("name");
                result.downloadUrl.put(brancheName, new downloadProject(brancheName, "zip").getUrl());
            }
        }
        return result;
    }

    @Override
    public void run() {
        result = process();
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private Map<String, String> downloadUrl = new HashMap<>();

        Map<String, String> downloadUrl() {
            return downloadUrl;
        }

        @Override
        public String getResultAsString() {
            return downloadUrl.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            if(downloadUrl == null || downloadUrl.size() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return "";
            }

            String html = "";
            String a = "<div class=\"chart card\">\n"+
                        "<p>Download Project</p>\n";

            String warn = "     <div class=\"warning\">\n" +
                    "                <span style=\"font-size: 17px; font-weight: bold;\">Danger!</span><br>\n" +
                    "                Your repository is private. Download url contains your private token.\n" +
                    "            </div>\n";

            String btnContainer1 = "             <div id=\"charts-button " + getPluginName() +"\">\n";


            String btn = "";
            for (var url : downloadUrl.entrySet()) {
                btn +=  "            <a href=\"" + url.getValue() + "\" style=\"text-decoration: none\">\n" +
                        "                <button class=\"chartButton\">" + url.getKey() + "</button>\n" +
                        "            </a>\n";
            }
            String btnContainer2 =         "        </div>\n" +
                    "    <style>\n" +
                    "        .warning{\n" +
                    "            color: #a94442;\n" +
                    "            background-color: #f2dede;\n" +
                    "            padding: 5px 15px;\n" +
                    "            margin-bottom: 5px;\n" +
                    "            margin-top: 5px;\n" +
                    "            border: 2px solid #ebccd1;\n" +
                    "            border-radius: 15px 14px;\n" +
                    "         }\n" +
                    "    </style>\n" +
                    "</div>";


            if(configuration.getDataConfig().getPrivateRepo()){
                html = a + warn + btnContainer1 + btn + btnContainer2;
            }else {
                html = a + btnContainer1 + btn + btnContainer2;
            }

            return html;
        }

        @Override
        public String getJson() {
            if(downloadUrl == null || downloadUrl.size() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return "";
            }
            JSONArray array = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("name", getPluginName());
            for (var element : downloadUrl.entrySet()) {
                JSONObject item = new JSONObject();
                item.put("name", element.getKey());
                item.put("url", element.getValue());
                array.put(item);
            }
            json.put("data", array);
            return json.toString(2);
        }

        @Override
        public void jsonToFile() {
            if(!pluginConfig.isJson_output() || downloadUrl == null || downloadUrl.size() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return;
            }
            SaveFile.save(getJson(), getPluginName(), "json", getPluginName());
        }

        @Override
        public void htmlToFile() {
            if(!pluginConfig.isHtml_output() || downloadUrl == null || downloadUrl.size() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return;
            }
            String html=
                    "<!DOCTYPE html>\n"+
                            "<html lang=\"en\">\n"+
                            "<head><meta charset=\"UTF-8\">\n"+
                            "</head>\n"+
                            "<style>\n" +
                            "    .card::-webkit-scrollbar {\n" +
                            "        width: 5px;\n" +
                            "        margin: 10px;\n" +
                            "        height: 5px;\n" +
                            "\n" +
                            "    }\n" +
                            "\n" +
                            "    /* Track */\n" +
                            "    .card::-webkit-scrollbar-track {\n" +
                            "        background: #A5D6A7;\n" +
                            "        border-radius: 20px;\n" +
                            "        margin: 10px;\n" +
                            "\n" +
                            "\n" +
                            "    }\n" +
                            "\n" +
                            "    /* Handle */\n" +
                            "    .card::-webkit-scrollbar-thumb {\n" +
                            "        background: #66BB6A;\n" +
                            "        border-radius: 20px;\n" +
                            "        margin: 10px;\n" +
                            "\n" +
                            "    }\n" +
                            ".chartButton{\n" +
                            "          font-size: 15px;\n" +
                            "          padding: 4px;\n" +
                            "          margin: 2px;\n" +
                            "          border: 2px solid #4CAF50;\n" +
                            "          border-radius: 10px;\n" +
                            "          cursor: pointer;\n" +
                            "          color: black;\n" +
                            "          background: #b8c6db linear-gradient(315deg, #b8c6db 0%, #f5f7fa 74%) no-repeat fixed center center;\n" +
                            "\n" +
                            "\t\t}\n" +
                            "\t\t.chartButton:hover{\n" +
                            "          background: #3bb78f linear-gradient(315deg, #3bb78f 0%, #0bab64 74%) no-repeat center center fixed;\n" +
                            "          color: white;\n" +
                            "\t\t}\n" +
                            "\n" +
                            "\t\t.activeBTN{\n" +
                            "          font-size: 15px;\n" +
                            "          padding: 4px;\n" +
                            "          margin: 2px;\n" +
                            "          border: 2px solid #71af74;\n" +
                            "          border-radius: 10px;\n" +
                            "          cursor: default;\n" +
                            "          color: white;\n" +
                            "          background: #3bb78f linear-gradient(315deg, #3bb78f 0%, #0bab64 74%) no-repeat center center fixed;\n" +
                            "\t\t}"+

                            "\n" +
                            "    /* Handle on hover */\n" +
                            "    .card::-webkit-scrollbar-thumb:hover {\n" +
                            "        background: #43A047;\n" +
                            "        border-radius: 20px;\n" +
                            "        margin: 10px;\n" +
                            "\n" +
                            "    }\n" +
                            "\n" +
                            "    .card{\n" +
                            "        border: 2px solid #4CAF50;\n" +
                            "        border-radius: 15px 14px;\n" +
                            "        padding: 5px;\n" +
                            "        overflow-y: auto;\n" +
                            "        max-height: 500px;\n" +
                            "        text-align: center;\n" +
                            "    }\n" +
                            "\n" +
                            "    .card p {\n" +
                            "        margin: 5px;\n" +
                            "        font-size: 22px;\n" +
                            "    }\n" +
                            "</style>"+
                            "<title>histoGRAPH - " + getPluginName() + "</title>" +
                            "<body>" +
                            getResultAsHtmlDiv() +
                            "<div style=\"text-align: center;\">histoGRAPH Copyright &copy; 2021</div>\n" +
                            "</body>\n" +
                            "</html>\n";


            SaveFile.save(html, getPluginName(), "html", getPluginName());
        }


        @Override
        public String getPluginName() {
            return pluginName;
        }
    }
}