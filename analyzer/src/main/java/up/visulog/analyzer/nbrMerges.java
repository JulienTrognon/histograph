package up.visulog.analyzer;

import org.json.JSONArray;
import org.json.JSONObject;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import up.visulog.api.git.Commit;
import up.visulog.util.SaveFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class nbrMerges implements AnalyzerPlugin {
    private static Configuration configuration=null;
    private static PluginConfig pg=null;
    private Result result;

    public static final String pluginName = "nbrMerges";

    public nbrMerges(PluginConfig pg, Configuration generalConfiguration ) {
        nbrMerges.configuration = generalConfiguration;
        nbrMerges.pg=pg;
    }

    Result calcul(List<Commit> l){
        var result=new Result();
        for(var commit : l){
            var nb = result.nbMerges.getOrDefault(commit.author, 0);
            if(commit.mergedFrom!=null) {
                result.nbMerges.put(commit.author,nb+1);
            }
        }result.removeDuplicate();
        return result;
    }

    @Override
    public void run() {
        result = calcul(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }




    static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer>nbMerges=new HashMap<>();

        Map<String, Integer> getMergesPerAuthor() {
            return nbMerges;
        }

        @Override
        public String getResultAsString() {
            return nbMerges.toString();
        }

        @Override
        
        public String getResultAsHtmlDiv(){
            String a = "<div class=\"chart card\">\n" +
                    "    <div class=\"graph\" id=\"chartContainer" + getPluginName() + "\"></div>\n" +
                    "    <div id=\"charts-button"+ getPluginName() +"\" style=\"margin-bottom: 10px\"></div>\n" +
                    "    <script type=\"text/javascript\">\n" +
                    "        var chart" + getPluginName() + " = new CanvasJS.Chart(\"chartContainer" + getPluginName() + "\", {\n" +
                    "            theme: \"light1\",\n" +
                    "            animationEnabled: true,\n" +
                    "            backgroundColor: \"transparent\",\n"+
                    "            title:{\n" +
                    "                text: \"Merges Per Author\"\n" +
                    "            },\n" +
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
                    "              title: \"Number of Merges\",\n"+
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
                    "            data: [\n" +
                    "                {\n" +
                    "                    dataPoints: [";

            String b="";

            String c = "]\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        });\n" +
                    "        chart" + getPluginName() + ".render();\n" +
                    "        let chartsSelector"+ getPluginName() +" = document.querySelector('#charts-button"+ getPluginName() +"');\n" +
                    "        \n" +
                    "        let t"+ getPluginName() +" = ";

            String d = "";

            String f = "let lastActiveButton"+ getPluginName() +";"+
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
                    "                if(type === \"bar\" || type == \"stackedBar\"){\n" +
                    "                    chart"+ getPluginName() +".options.axisX.labelAngle = 180;\n" +
                    "                }else{\n" +
                    "                    chart"+ getPluginName() +".options.axisX.labelAngle = -70;\n" +
                    "                }"+
                    "                chart"+ getPluginName() +".render();\n" +
                    "                button.classList.add('activeBTN');\n" +
                    "                if (lastActiveButton"+ getPluginName() +" != null) {\n" +
                    "                    lastActiveButton"+ getPluginName() +".classList.toggle('activeBTN');\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "</div> ";
            for (var item : nbMerges.entrySet()) {
                var tab=item.getKey().split("<");
                if(tab.length>0){
                    b+="{label:'"+tab[0]+"', y:"+item.getValue()+"},\n";
                    
                }else{
                    b+="{label:'"+item.getKey()+"', y:"+item.getValue()+"},\n";
                }
            }
            d = "[";
            for(String x : pg.getChartTypes()){
                d += "\"" + x + "\",";
            }
            d = d.substring(0,d.length()-1);
            d += "];";

            StringBuilder html = new StringBuilder();
            html.append(a).append(b).append(c).append(d).append(f);
            return html.toString();
        }
        private void removeDuplicate(){
            ArrayList<Map.Entry<String, Integer>> authorSetList = new ArrayList<>(nbMerges.entrySet());
            for (int i = 0; i < authorSetList.size(); i++) {
                if(authorSetList.get(i).getKey().length() >= 1){
                    removeDuplicate(authorSetList, authorSetList.get(i), i+1, true, true);
                }
            }
        }
        private void removeDuplicate(ArrayList<Map.Entry<String, Integer>> authorSetList, @SuppressWarnings("rawtypes") Map.Entry element, int startId, boolean mail, boolean other) {    // use of additional variables to avoid hardcoding
            String[] author = element.getKey().toString().toLowerCase().split(" ");
            for (int j = startId; j < authorSetList.size(); j++) {
                String infoAuthor = authorSetList.get(j).getKey().toLowerCase();
                String[] infoAuthorSplit = authorSetList.get(j).getKey().toLowerCase().split(" ");
                int same = 0;
                for (String s : author) {
                    if (infoAuthor.contains(s)) {
                        same++;
                    }
                }
                if ((same == infoAuthorSplit.length && other) || (mail && infoAuthorSplit[infoAuthorSplit.length-1].equalsIgnoreCase(author[author.length-1]))) {
                    nbMerges.put(element.getKey().toString(), Integer.parseInt(element.getValue().toString())+authorSetList.get(j).getValue());
                    nbMerges.remove(authorSetList.get(j).getKey());
                }
            }
        }


        @Override
        public String getJson() {
            JSONArray array= new JSONArray();
            JSONObject json = new JSONObject();
            json.put("name", getPluginName());
            int total=0;
            for(var element: nbMerges.entrySet()){
                JSONObject item = new JSONObject();
                item.put("nbrMerges", element.getValue());
                item.put("author", element.getKey());
                array.put(item);
                total+=element.getValue();
            }json.put("data",array);
            json.put("totalMerges", total);
            return json.toString(2);

        }

        @Override
        public void jsonToFile() {
            if(!pg.isJson_output()){
                return;
            }
            SaveFile.save(getJson(), getPluginName(), "json",getPluginName());

        }

        @Override
        public String getPluginName() {
            return "nbrMerges";
        }

        @Override
        public void htmlToFile() {
            if(!pg.isHtml_output()){
                return;
            }
            String html =   "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n"+
                    "<meta charset=\"UTF-8\">\n" +
                    "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"+
                    "<script src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>\n" +
                    "    <style>\n" +
                    ".chartButton{\n" +
                    "\t\t\tfont-size: 15px;\n" +
                    "\t\t\tpadding: 4px;\n" +
                    "\t\t\tmargin: 2px;\n" +
                    "\t\t\tborder: 2px solid #4CAF50;\n" +
                    "\t\t\tborder-radius: 10px;\n" +
                    "\t\t\tcursor: pointer;\n" +
                    "\t\t\tcolor: black;\n" +
                    "\t\t\tbackground: #b8c6db linear-gradient(315deg, #b8c6db 0%, #f5f7fa 74%) no-repeat fixed center center;\n" +

                    "\n" +
                                      "\t\t}\n" +
                    "\t\t.chartButton:hover{\n" +
                    "\t\t\tbackground: #3bb78f linear-gradient(315deg, #3bb78f 0%, #0bab64 74%) no-repeat center center fixed;\n" +
                    "\t\t\tcolor: white;\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\t.activeBTN{\n" +
                    "\t\t\tfont-size: 15px;\n" +
                    "\t\t\tpadding: 4px;\n" +
                    "\t\t\tmargin: 2px;\n" +
                    "\t\t\tborder: 2px solid #71af74;\n" +
                    "\t\t\tborder-radius: 10px;\n" +
                    "\t\t\tcursor: default;\n" +
                    "\t\t\tcolor: white;\n" +
                    "\t\t\tbackground: #3bb78f linear-gradient(315deg, #3bb78f 0%, #0bab64 74%) no-repeat center center fixed;\n" +
                    "\t\t}"+
                    "      .graph{ height: 400px; }\n"+
                    "#charts-button"+ getPluginName() +"{\n" +
                    "                   text-align: center;\n" +
                    "               }"+
                    "    </style>\n"+
                    "<title>histoGRAPH - " + getPluginName() + "</title>\n" +
                    "</head>\n" +
                    "<body>\n" +

                    getResultAsHtmlDiv() +
                    "\n"+
                    "\n"+
                    " <div style=\"text-align: center;\">histoGRAPH Copyright &copy; 2021</div>\n" +
                    "</body>\n" +
                    "</html>";
            SaveFile.save(html, getPluginName(), "html",getPluginName());
        
        }
        

    }


}
