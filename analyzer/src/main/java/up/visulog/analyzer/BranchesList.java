package up.visulog.analyzer;

import org.json.*;
import up.visulog.api.web.*;
import up.visulog.config.*;
import up.visulog.util.SaveFile;
import java.util.*;


public class BranchesList implements AnalyzerPlugin  {
    private static Configuration configuration = null;
    private static PluginConfig pluginConfig = null;
    private Result result;

    public static final String pluginName = "branchesList";

    public BranchesList(PluginConfig pluginConfig1, Configuration generalConfiguration) {
        configuration = generalConfiguration;
        pluginConfig = pluginConfig1;
    }

    public Result process() {
        var result = new Result();
        WebApi branches = new getBranches();
        String apiResult = branches.getResult();
        if (apiResult != null) {
            JSONArray array = new JSONArray(apiResult);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                result.branches.put(json.getString("name"), json.getBoolean("merged"));
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
        private Map<String, Boolean> branches = new HashMap<>();

        Map<String, Boolean> branches() {
            return branches;
        }

        @Override
        public String getResultAsString() {
            return branches.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            if(branches == null || branches.size() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return "";
            }else{
                 StringBuilder html = new StringBuilder("<div class=\"chart card\">\n");
            String a="<ul style=\"list-style-type:none; padding: 0;\">\n";
            String b="<p>Branches List for this Project:</p>\n";
            String c="";
            for (var item :branches.entrySet()){
                if(item.getValue()){
                     c+="<li><strong>"+item.getKey()+"</strong>: merged.</li>\n";
                }else{
                    c+="<li><strong>"+item.getKey()+"</strong>: not merged yet.</li>\n";
                }
            }
            String e="</ul>\n<div>Total number of branches: <strong>"+branches.size()+"</strong></div>\n";
            String f="</div>";
            html.append(b).append(a).append(c).append(e).append(f);
            return html.toString();
            }
        }

        @Override
        public String getJson() {
            JSONObject json = new JSONObject();
            if(branches == null || branches.size() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return "";
            }
            JSONArray array = new JSONArray();
            for (var element: branches.entrySet()) {
                JSONObject item = new JSONObject();
                item.put(element.getKey(), element.getValue());
                array.put(item);
            }
            json.put("data", array);
            json.put("name", getPluginName());
            return json.toString(2);
        }
        
        @Override
        public void jsonToFile() {
            if(!pluginConfig.isJson_output() || branches == null || branches.size() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return;
            }
            SaveFile.save(getJson(), getPluginName(), "json", getPluginName());
        }

        @Override
        public void htmlToFile() {
            if(!pluginConfig.isHtml_output() || branches == null || branches.size() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return;
            }else{
                String html=
            "<!DOCTYPE html>\n"+
                "<html lang=\"en\">\n"+
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
                "<head><meta charset=\"UTF-8\">\n"+
                "</head>\n"+
                "<title>histoGRAPH - " + getPluginName() + "</title>" +
                "<body>" +
                      getResultAsHtmlDiv() +
                "<div style=\"text-align: center;\">histoGRAPH Copyright &copy; 2021</div>\n" +
                "</body>\n" +
                 "</html>\n";

            SaveFile.save(html, getPluginName(), "html",getPluginName());
            }
        }

        @Override
        public String getPluginName() {
            return pluginName;
        }
    }
}
