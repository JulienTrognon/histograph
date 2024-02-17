package up.visulog.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import up.visulog.api.git.Commit;
import up.visulog.util.SaveFile;


public class CountAuthors implements AnalyzerPlugin {
    MyResult result;
    private static Configuration configuration = null;
    private static PluginConfig pluginConfig = null;
    public static final String pluginName = "countAuthors";

    public CountAuthors(PluginConfig pluginConfig1, Configuration generalConfiguration) {
        configuration = generalConfiguration;
        pluginConfig = pluginConfig1;
    }

    MyResult countAuthors(List<Commit> log) {
        var result = new MyResult();
        for (var commit : log) {
            result.authorSet.add(commit.author);
        }
        return result;
    }

    @Override
    public void run() {
        result = countAuthors(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Author{
        String name = "";
        String mail = "";

        public void setName(String name) {
            this.name = name;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }
    }

    static class MyResult implements AnalyzerPlugin.Result {
        HashSet<String> authorSet;

        public MyResult() {
            authorSet = new HashSet<>();
        }

        @Override
        public String getResultAsString() {
            return authorSet.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            ArrayList<String> authorSetList = new ArrayList<>(authorSet);
            for (int i = 0; i < authorSetList.size(); i++) {
                if(authorSetList.get(i).length() >= 1){
                    removeDuplicateByMail(authorSetList, authorSetList.get(i), i+1);
                }
                if(authorSetList.get(i).length() >= 2) {
                    removeDuplicateOther(authorSetList, authorSetList.get(i), i+1);
                }
            }
            StringBuilder html = new StringBuilder("<div class=\"chart card\">\n");
            String a="<ul style=\"list-style-type:none; padding: 0;\">\n";
            String b="<p>Contributors to this project:</p>\n";
            String c="";
            for (var item :authorSet){
                var line=item.split("<");
                if(line.length>0){
                     c+="<li>"+line[0]+"</li>\n";
                }else{
                    c+="<li>"+item+"</li>\n";
                }
            }
            String e="</ul>\n";
            String d="<div>Total number of authors:<strong> "+authorSet.size()+"</strong></div>\n";
            String f="</div>";
            html.append(b).append(a).append(c).append(e).append(d).append(f);
            return html.toString();

        }

        @Override
        public String getJson() {
            JSONArray array = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("name", getPluginName());

            ArrayList<String> authorSetList = new ArrayList<>(authorSet);
            for (int i = 0; i < authorSetList.size(); i++) {
                if(authorSetList.get(i).length() >= 1){
                    removeDuplicateByMail(authorSetList, authorSetList.get(i), i+1);
                }
                if(authorSetList.get(i).length() >= 2) {
                    removeDuplicateOther(authorSetList, authorSetList.get(i), i+1);
                }
            }

            for (var element : authorSet) {
                JSONObject item = new JSONObject();
                Author author = makeAuthor(element, 0, 1);
                item.put("name", author.name);
                item.put("mail", author.mail);
                array.put(item);
            }

            json.put("totalAuthors", authorSet.size());
            json.put("data", array);
            return json.toString(2);
        }

        @Override
        public void jsonToFile() {
            if(!pluginConfig.isJson_output()){
                return;
            }
            SaveFile.save(getJson(), getPluginName(), "json",getPluginName());
        }

        @Override
        public void htmlToFile() {
            if(!pluginConfig.isHtml_output()){
                return;
            }
            String html=
            "<!DOCTYPE html>\n"+
                "<html lang=\"en\">\n"+
                "<head><meta charset=\"UTF-8\">\n"+
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
                "</head>\n"+
                "<title>histoGRAPH - " + getPluginName() + "</title>" +
                "<body>" +
                      getResultAsHtmlDiv() +
                "<div style=\"text-align: center;\">histoGRAPH Copyright &copy; 2021</div>\n" +
                "</body>\n" +
                 "</html>\n";

            SaveFile.save(html, getPluginName(), "html",getPluginName());
        }


        private void removeDuplicateOther(ArrayList<String> authorSetList, String element, int startId) {    // use of additional variables to avoid hardcoding
            String[] author = element.toLowerCase().split(" ");
            for (int j = startId; j < authorSetList.size(); j++) {
                String infoAuthor = authorSetList.get(j).toLowerCase();
                String[] infoAuthorSplit = authorSetList.get(j).toLowerCase().split(" ");
                int same = 0;
                for (String s : author) {
                    if (infoAuthor.contains(s)) {
                        same++;
                    }
                }
                if (same == infoAuthorSplit.length) {
                    authorSet.remove(authorSetList.get(j));
                }
            }
        }

        private void removeDuplicateByMail(ArrayList<String> authorSetList, String element, int startId) {
            String[] author = element.split(" ");
            if (author.length >= 1) {
                for (int j = startId; j < authorSetList.size(); j++) {
                    String[] infoAuthor = authorSetList.get(j).split(" ");
                    if (infoAuthor[infoAuthor.length-1].equalsIgnoreCase(author[author.length-1])) {
                        authorSet.remove(authorSetList.get(j));
                    }
                }
            }
        }
        
        private Author makeAuthor(String element, int firstId,int lastId) {    // use of additional variables to avoid hardcoding
            String[] author = element.split(" <");
            Author to_return = new Author();

            if (author.length >= 1) {
                to_return.setName(author[firstId]);
            }
            if (author.length >= 2) {
                to_return.setMail(author[lastId].replaceAll("<", "").replaceAll(">", ""));
            }

            return to_return;
        }

        @Override
        public String getPluginName() {
            return CountAuthors.pluginName;
        }

    }
}
