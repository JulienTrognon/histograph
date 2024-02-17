package up.visulog.analyzer;

import org.json.*;
import up.visulog.api.web.*;
import up.visulog.config.*;
import up.visulog.util.SaveFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class ProjectInfo implements AnalyzerPlugin  {
    private static Configuration configuration = null;
    private static PluginConfig pluginConfig = null;
    private Result result;

    public static final String pluginName = "projectInfo";

    public ProjectInfo(PluginConfig pluginConfig1, Configuration generalConfiguration) {
        configuration = generalConfiguration;
        pluginConfig = pluginConfig1;
    }

    public Result process() {
        var result = new Result();
        WebApi projectInfo = new getProject();
        String apiResult = projectInfo.getResult();
        if (apiResult != null && configuration.getDataConfig().getPrivateRepo()) {
            JSONObject json = new JSONObject(apiResult);

            result.projectInfo.put("projectName",json.getString("name"));
            result.projectInfo.put("createDate",json.getString("created_at"));
            result.projectInfo.put("projectURL",json.getString("web_url"));
            result.projectInfo.put("star_count",json.getInt("star_count"));
            result.projectInfo.put("forks_count",json.getInt("forks_count"));
            result.projectInfo.put("lastActivityDate",json.getString("last_activity_at"));
            result.projectInfo.put("visibility",json.getString("visibility"));
            result.projectInfo.put("open_issues_count",json.getInt("open_issues_count"));

            WebApi user = new getUser(json.getInt("creator_id")+"");
            String userResult = user.getResult();

            if(userResult != null) {
                JSONObject userJSON = new JSONObject(userResult);

                JSONObject userInfo = new JSONObject();
                userInfo.put("userName", userJSON.getString("name"));
                userInfo.put("userEmail", userJSON.getString("public_email"));
                userInfo.put("avatar_url", userJSON.getString("avatar_url"));
                userInfo.put("web_url", userJSON.getString("web_url"));
                result.projectInfo.put("userInfo", userInfo);
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
        private JSONObject projectInfo = new JSONObject();

        JSONObject projectInfo() {
            return projectInfo;
        }

        @Override
        public String getResultAsString() {
            return projectInfo.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            if (projectInfo == null || !configuration.getDataConfig().getUseWebApi() || projectInfo.length() <= 0) {
                return "";
            }
            InputStream template = ProjectInfo.class.getClassLoader().getResourceAsStream("ProjectInfoTemplate.html");
            String htmlString = InputStreamToString(template);

            htmlString = htmlString.replace("$star_num", projectInfo.getInt("star_count")+"").replace("$project_url", projectInfo.getString("projectURL")).replace("$project_name",projectInfo.getString("projectName"));
            htmlString = htmlString.replace("$fork_num", projectInfo.getInt("forks_count")+"").replace("$visu", projectInfo.getString("visibility")).replace("$issues_num",projectInfo.getInt("open_issues_count")+"");
            htmlString = htmlString.replace("$user_url", projectInfo.getJSONObject("userInfo").getString("web_url")).replace("$owner_img", projectInfo.getJSONObject("userInfo").getString("avatar_url"));
            htmlString = htmlString.replace("$owner_name", projectInfo.getJSONObject("userInfo").getString("userName")).replace("$owner_mail", projectInfo.getJSONObject("userInfo").getString("userEmail"));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm dd/MM/YYYY");
            Date t1 = null;
            Date t2 = null;
            try {
                t1 = format.parse(projectInfo.getString("createDate"));
                t2 = format.parse(projectInfo.getString("lastActivityDate"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            htmlString = htmlString.replace("$create_date", format2.format(t1)+"").replace("$last_date", format2.format(t2)+"");

            return htmlString;
        }

        private String InputStreamToString(InputStream input) {
            String text = new BufferedReader(
                    new InputStreamReader(input, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            return text;
        }

        @Override
        public String getJson() {
            if (projectInfo == null || !configuration.getDataConfig().getUseWebApi() || projectInfo.length() <= 0) {
                return "";
            }
            return projectInfo.toString(2);
        }
        
        @Override
        public void jsonToFile() {
            if (!pluginConfig.isJson_output() || projectInfo == null || !configuration.getDataConfig().getUseWebApi() || projectInfo.length() <= 0) {
                return;
            }
            SaveFile.save(getJson(), getPluginName(), "json", getPluginName());
        }

        @Override
        public void htmlToFile() {
            if (!pluginConfig.isHtml_output() || projectInfo == null || !configuration.getDataConfig().getUseWebApi() || projectInfo.length() <= 0) {
                return;
            }
            String html =   "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>"+
                    "<meta charset=\"UTF-8\">\n" +
                    "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"+
                    "    <style>\n" +
                    "\t\t.card{\n" +
                    "\t\t\tborder: 2px solid #4CAF50;\n" +
                    "\t\t\tborder-radius: 15px 14px;\n" +
                    "\t\t\tpadding: 5px;\n" +
                    "\t\t\toverflow-y: auto;\n" +
                    "\t\t\tmax-height: 500px;\n" +
                    "          text-align: center;\n"+
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.card p {\n" +
                    "\t\t\tmargin: 5px;\n" +
                    "\t\t\tfont-size: 22px;\n" +
                    "\t\t}"+
                    "\t\t.project{\n" +
                    "\t\t\twidth: 33%;\n" +
                    "\t\t\tmargin: 10px auto;\n"+
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.owner_img{\n" +
                    "\t\t\tborder-radius: 15px;\n" +
                    "\t\t\tborder: 2px solid rgb(68, 63, 63);\n" +
                    "\t\t\twidth: 64px;\n" +
                    "\t\t\theight: 64px;\n" +
                    "\t\t\tdisplay: flex;\n" +
                    "\t\t\tjustify-content: center;\n" +
                    "\t\t\talign-items: center;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.owner {\n" +
                    "\t\t\tborder: 2px solid rgb(68, 63, 63);\n" +
                    "\t\t\tborder-radius: 15px 14px;\n" +
                    "\t\t\tpadding: 5px;\n" +
                    "\n" +
                    "\t\t\twidth: auto;\n" +
                    "\t\t\tmargin-bottom: 5px;\n" +
                    "\t\t\tdisplay: grid;\n" +
                    "\t\t\tgrid-template-columns: 64px auto;\n" +
                    "\t\t\tgrid-template-rows: auto 64px;\n" +
                    "\t\t\tgap: 5px 5px;\n" +
                    "\t\t\tgrid-auto-flow: row;\n" +
                    "\t\t\tgrid-template-areas:\n" +
                    "            \"ownerT ownerT\"\n" +
                    "            \"img name_mail\";\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.ownerT{\n" +
                    "\t\t\tgrid-area: ownerT;\n" +
                    "\t\t\tpadding: 0;\n" +
                    "\t\t\tmargin: 0;\n" +
                    "\t\t\tfont-size: 20px;\n" +
                    "\t\t\ttext-decoration: underline;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.name_mail {\n" +
                    "\t\t\tdisplay: grid;\n" +
                    "\t\t\tgrid-template-columns: auto;\n" +
                    "\t\t\tgrid-template-rows: auto auto;\n" +
                    "\t\t\tgap: 10px 0px;\n" +
                    "\t\t\tgrid-auto-flow: row;\n" +
                    "\t\t\tgrid-template-areas:\n" +
                    "            \"name\"\n" +
                    "            \"mail\";\n" +
                    "\t\t\tgrid-area: name_mail;\n" +
                    "\n" +
                    "\t\t\ttext-align: left;\n" +
                    "\t\t\tmargin-left: 5px;\n" +
                    "\t\t\tmargin-top: auto;\n" +
                    "\t\t\tmargin-bottom: auto;\n" +
                    "\t\t\tfont-size: 18px;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.name { grid-area: name; }\n" +
                    "\n" +
                    "\t\t.mail { grid-area: mail; }\n" +
                    "\n" +
                    "\t\t.owner_img { grid-area: img; }\n" +
                    "\n" +
                    "\n" +
                    "\t\t.project_info {\n" +
                    "\t\t\tdisplay: grid;\n" +
                    "\t\t\tgrid-template-columns: auto auto auto auto;\n" +
                    "\t\t\tgrid-template-rows: auto auto auto auto;\n" +
                    "\t\t\tgap: 0px 0px;\n" +
                    "\t\t\tgrid-auto-flow: row dense;\n" +
                    "\t\t\tgrid-template-areas:\n" +
                    "            \"star project_name project_name fork\"\n" +
                    "            \"createDate createDate visibility1 visibility1\"\n" +
                    "            \"lastActivity lastActivity openIssues openIssues\"\n" +
                    "            \"owner owner owner owner\";\n" +
                    "\n" +
                    "\t\t\tborder: 2px solid rgb(68, 63, 63);\n" +
                    "\t\t\tborder-radius: 15px 14px;\n" +
                    "\t\t\tpadding: 5px;\n" +
                    "\t\t\tmargin: 5px 5px 10px;\n" +
                    "\n" +
                    "\t\t\tfont-size: 18px;\n" +
                    "\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.star {\n" +
                    "\t\t\tdisplay: grid;\n" +
                    "\t\t\tgrid-template-columns: 21px auto auto;\n" +
                    "\t\t\tgrid-template-rows: auto;\n" +
                    "\t\t\tgap: 5px 5px;\n" +
                    "\t\t\tgrid-auto-flow: row;\n" +
                    "\t\t\tgrid-template-areas:\n" +
                    "            \"start_img star_title star_num\";\n" +
                    "\t\t\tgrid-area: star;\n" +
                    "\n" +
                    "\t\t\tfont-size: 18px;\n" +
                    "\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.start_img {\n" +
                    "\t\t\tgrid-area: start_img;\n" +
                    "\t\t\twidth: 21px;\n" +
                    "\t\t\theight: 21px;\n" +
                    "\t\t\tmargin-top: auto;\n" +
                    "\t\t\tmargin-bottom: auto;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.star_title {\n" +
                    "\t\t\tgrid-area: star_title;\n" +
                    "\t\t\tmargin-top: auto;\n" +
                    "\t\t\tmargin-bottom: auto;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.star_num {\n" +
                    "\t\t\tgrid-area: star_num;\n" +
                    "\t\t\tmargin-top: auto;\n" +
                    "\t\t\tmargin-bottom: auto;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.fork {\n" +
                    "\t\t\tdisplay: grid;\n" +
                    "\t\t\tgrid-template-columns: auto auto auto;\n" +
                    "\t\t\tgrid-template-rows: auto;\n" +
                    "\t\t\tgap: 5px 5px;\n" +
                    "\t\t\tgrid-auto-flow: row;\n" +
                    "\t\t\tgrid-template-areas:\n" +
                    "            \"fork_img fork_title fork_num\";\n" +
                    "\t\t\tgrid-area: fork;\n" +
                    "\n" +
                    "\t\t\tfont-size: 18px;\n" +
                    "\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.fork_img {\n" +
                    "\t\t\tgrid-area: fork_img;\n" +
                    "\t\t\twidth: 21px;\n" +
                    "\t\t\theight: 21px;\n" +
                    "\t\t\tmargin-top: auto;\n" +
                    "\t\t\tmargin-bottom: auto;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.fork_title {\n" +
                    "\t\t\tgrid-area: fork_title;\n" +
                    "\t\t\tmargin-top: auto;\n" +
                    "\t\t\tmargin-bottom: auto;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.fork_num {\n" +
                    "\t\t\tgrid-area: fork_num;\n" +
                    "\t\t\tmargin-top: auto;\n" +
                    "\t\t\tmargin-bottom: auto;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.createDate { grid-area: createDate; text-align: left;}\n" +
                    "\n" +
                    "\t\t.visibility { grid-area: visibility1; text-align: right;}\n" +
                    "\n" +
                    "\t\t.lastActivity { grid-area: lastActivity; text-align: left;}\n" +
                    "\n" +
                    "\t\t.openIssues { grid-area: openIssues; text-align: right;}\n" +
                    "\n" +
                    "\t\t.owner {\n" +
                    "\t\t\tgrid-area: owner;\n" +
                    "\t\t\tmargin-left: 5px;\n" +
                    "\t\t\tmargin-right: 5px;\n" +
                    "\t\t\ttext-align: center;\n" +
                    "\t\t}"+
                    "    </style>"+
                    "<title>histoGRAPH - " + getPluginName() + "</title>" +
                    "</head>" +
                    "<body>" +
                    getResultAsHtmlDiv() +
                    "        <div style=\"text-align: center;\">histoGRAPH Copyright &copy; 2021</div>\n" +
                    "</body>\n" +
                    "</html>";
            SaveFile.save(html, getPluginName(), "html",getPluginName());
        }

        @Override
        public String getPluginName() {
            return pluginName;
        }
    }
}
