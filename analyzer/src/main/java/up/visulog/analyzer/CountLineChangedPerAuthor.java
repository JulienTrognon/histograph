package up.visulog.analyzer;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import up.visulog.api.git.Commit;
import up.visulog.util.SaveFile;

public class CountLineChangedPerAuthor implements AnalyzerPlugin {

    private static Configuration configuration = null;
    private static PluginConfig pluginConfig = null;
    private Result result;
    public static final String pluginName = "countLineChangedPerAuthor";

    public CountLineChangedPerAuthor(PluginConfig pluginConfig, Configuration generalConfiguration) {
        configuration = generalConfiguration;
        this.pluginConfig = pluginConfig;
    }

    static Result processLog(List<Commit> log) {
        var result = new Result();
        for (var commit : log) {
            result.lineChangedPerAuthor.put(commit.change, commit.author);
        }
        result.regroupAuthor();
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null)
            run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {

        private final Map<List<Map<Path, Integer[]>>, String> lineChangedPerAuthor = new HashMap<>();
        private final Map<String, Integer[]> countLineChangedPerAuthor = new HashMap<>();

        Map<List<Map<Path, Integer[]>>, String> getLineChangedPerAuthor() {
            return lineChangedPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return lineChangedPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv(){

            String e = "<div class=\"chart card\">\n" +
                    "    <div class=\"graph\" id=\"chartContainer" + getPluginName() + "\"></div>\n" +
                    "    <div id=\"charts-button"+ getPluginName() +"\" style=\"margin-bottom: 10px\"></div>\n" +
                    "    <script type=\"text/javascript\">\n" +
                    "        var chart" + getPluginName() + " = new CanvasJS.Chart(\"chartContainer" + getPluginName() + "\", {\n" +
                    "            theme: \"light1\",\n" +
                    "            animationEnabled: true,\n" +
                    "            backgroundColor: \"transparent\",\n";
                        String a =
                "title:{\n"+
                "text:\"Number of Line Chnages Per Author\"\n"+
                "},\n"+
                        "  exportEnabled: true,\n" +
                        "  zoomEnabled: true,\n" +
                        "  toolbar: {\n" +
                        "    itemBackgroundColorOnHover: \"#3e3e3e\",\n" +
                        "    buttonBorderColor: \"transparent\"\n" +
                        "  },"+
                        "            axisX: {\n" +
                        "                interval: 1,\n" +
                        "                labelAutoFit: true,\n" +
                        "            },"+
                "            axisY:{ \n"+
                        "title: \"Number of Lines\","+
                        "       scaleBreaks: {\n"+
                        "type: \"wavy\",\n" +
                        "           lineColor: \"#4DB051\",\n" +
                        "           lineThickness: 2,\n" +
                        "           spacing: 8,\n" +
                        "           fillOpacity: 0.9,\n" +
                        "           color: \"#E8EDF3\","+
                    "           autoCalculate: true,\n"+
                    "           maxNumberOfAutoBreaks: 3\n"+
                    "                    },\n"+
                    "               },\n"+
                "data:[{\n"+
                "theme: \"light1\",\n"+
                "animationEnabled :true, \n"+
                    "showInLegend: \"true\",\n"+
                    "name: \"Added Lines\",\n"+

                    "dataPoints:[\n";
                
                String b="";
                String c=
                      "showInLegend: \"true\",\n"+
                    "name: \"Deleted lines\",\n"+

                "dataPoints:[\n";;
                String d=
                      "showInLegend: \"true\",\n"+
                    "name: \"Total\" ,\n"+

                "dataPoints:[\n";;
                
                for (var item1 : countLineChangedPerAuthor.entrySet()) {
                    var line1=item1.getKey().split("<");
                    if(line1.length>0){
                    b+="{label:'"+line1[0]+"', y:"+item1.getValue()[0]+"},\n";
                    }else{
                        b+="{label:'"+item1.getKey()+"', y:"+item1.getValue()[0]+"},\n";

                    }
                }

                for (var item2 : countLineChangedPerAuthor.entrySet()) {
                    var line2=item2.getKey().split("<");
                    if(line2.length>0){
                    c+="{label:'"+line2[0]+"', y:"+item2.getValue()[1]+"},\n";
                    }else{
                        c+="{label:'"+item2.getKey()+"', y:"+item2.getValue()[1]+"},\n";

                    }                }

                for (var item3 : countLineChangedPerAuthor.entrySet()) {
                    var line3=item3.getKey().split("<");
                    if(line3.length>0){
                    d+="{label:'"+line3[0]+"', y:"+(item3.getValue()[1]+item3.getValue()[0])+"},\n";
                    }else{
                        d+="{label:'"+item3.getKey()+"', y:"+(item3.getValue()[1]+item3.getValue()[0])+"},\n";

                    }
                }

                b+="]\n"+
                "},\n"+
                "{\n";

                c+="]\n"+
                "},\n"+
                "{\n";

                d+="]\n"+
                "}\n"+
                "]\n"+
                "});\n";

                String z="chart" + getPluginName() + ".render();\n" +
                    "        let chartsSelector"+ getPluginName() + " = document.querySelector('#charts-button"+ getPluginName() +"');\n" +
                    "        \n" +
                    "        let t"+ getPluginName() +" = ";

                String s="";
                s = "[";
            for(String x : pluginConfig.getChartTypes()){
                s += "\"" + x + "\",";
            }
            s = s.substring(0,s.length()-1);
            s += "];";    



             String v="let lastActiveButton"+ getPluginName() +";"+
                    "for(let chart of t"+ getPluginName() +") {\n" +
                    "            let chartButton = document.createElement('button');\n" +
                    "            chartButton.className = \"chartButton\";\n" +
                    "            chartButton.innerText = chart;\n" +
                    "            chartButton.onclick = () => {\n" +
                    "                renderChart"+ getPluginName() +"(chart, chartButton);\n" +
                    "                lastActiveButton"+ getPluginName() +" = chartButton;\n" +
                    "            }\n" +
                    "            chartsSelector"+ getPluginName() +".append(chartButton);\n" +
                    "        }\n" +
                    "\n" +
                    "        function renderChart"+ getPluginName() +"(type, button) {\n" +
                    "            if(lastActiveButton"+ getPluginName() +" !== button) {\n" +
                    "                chart" + getPluginName() + ".options.data[0].type = type;\n" +
                     "                chart" + getPluginName() + ".options.data[1].type = type;\n" +
                      "                chart" + getPluginName() + ".options.data[2].type = type;\n" +
                    "                chart"+ getPluginName() +".render();\n" +
                    "                button.classList.add('activeBTN');\n" +
                    "                if (lastActiveButton"+ getPluginName() +" != null) {\n" +
                    "                    lastActiveButton"+ getPluginName() +".classList.toggle('activeBTN');\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "</div> ";  

                    StringBuilder html = new StringBuilder();
               html.append(e).append(a).append(b).append(c).append(d).append(z).append(s).append(v);
               return html.toString();



        }

        @Override
        public String getJson() {
            JSONArray array = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("name", getPluginName());
            for (var element : countLineChangedPerAuthor.entrySet()) {
                JSONObject item = new JSONObject();
                item.put("total:", element.getValue()[0] + element.getValue()[1]);
                item.put("line added:", element.getValue()[0]);
                item.put("line deleted:", element.getValue()[1]);
                item.put("author", element.getKey());
                array.put(item);
            }
            json.put("data", array);
            return json.toString(2);
        }

        private void regroupAuthor() {
            Map<String, Integer[]> mail = new HashMap<>();
            for (var author : lineChangedPerAuthor.entrySet()) {
                if (author.getKey() != null) {
                    for (var e : author.getKey()) {
                        for (var element : e.entrySet()) {
                            if (!mail.containsKey(
                                    author.getValue().split(" ")[author.getValue().split(" ").length - 1])) {
                                Integer[] tmp = element.getValue();
                                mail.put(author.getValue().split(" ")[author.getValue().split(" ").length
                                        - 1], tmp);
                            } else {
                                Integer[] tmp = mail
                                        .get(author.getValue().split(" ")[author.getValue().split(" ").length - 1]);
                                tmp[0] = tmp[0] + element.getValue()[0];
                                tmp[1] = tmp[1] + element.getValue()[1];
                                mail.replace(author.getValue().split(" ")[author.getValue().split(" ").length
                                        - 1], tmp);
                            }
                        }
                    }
                }
            }
            for (var element : mail.entrySet()) {
                for (var author : lineChangedPerAuthor.entrySet()) {
                    boolean in = false;
                    for (var e : countLineChangedPerAuthor.entrySet()) {
                        if (element.getKey().equals(e.getKey().split(" ")[e.getKey().split(" ").length - 1]))
                            in = true;
                    }
                    if (author.getValue().split(" ")[author.getValue().split(" ").length - 1]
                            .equals(element.getKey()) && !in)
                        countLineChangedPerAuthor.put(author.getValue(), element.getValue());
                }
            }
        }

        String print(Integer[] t) {
            StringBuilder sb = new StringBuilder();
            for (var i : t)
                sb.append(" " + i);
            return sb.toString();
        }

        @Override
        public void jsonToFile() {
            if (!pluginConfig.isJson_output()) {
                return;
            }
            SaveFile.save(getJson(), getPluginName(), "json",getPluginName());
        }

        @Override
        public String getPluginName() {
            return CountLineChangedPerAuthor.pluginName;
        }

        @Override
        public void htmlToFile() {
            if(!pluginConfig.isHtml_output()){
                return;
            }
            String html =   "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>"+
                    "<meta charset=\"UTF-8\">\n" +
                    "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"+
                    "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>" +
                    "    <style>\n" +
                    "        .chartButton{\n" +
                    "            font-size: 15px;\n" +
                    "            padding: 3px 10px;\n" +
                    "            margin: 10px 5px;\n" +
                    "            border: 2px solid #4CAF50;\n" +
                    "            border-radius: 15px 14px;\n" +
                    "            background-color: white;\n" +
                    "            cursor: pointer;\n" +
                    "            color: black;\n" +
                    "        }\n" +
                    "\n" +
                    "        .chartButton:hover{\n" +
                    "            background-color: #4CAF50;\n" +
                    "            color: white;\n" +
                    "        }\n" +
                    "      .graph{ height: 400px; }\n"+
                    "#charts-button"+ getPluginName() +"{\n" +
                    "                   text-align: center;\n" +
                    "               }"+
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


        
    

}
}
