package up.visulog.analyzer;

import org.json.*;
import up.visulog.api.web.*;
import up.visulog.config.*;
import up.visulog.util.SaveFile;
// import java.util.*;


public class LanguagesPercentage implements AnalyzerPlugin  {
    private static Configuration configuration = null;
    private static PluginConfig pluginConfig = null;
    private Result result;

    public static final String pluginName = "languagesPercentage";

    public LanguagesPercentage(PluginConfig pluginConfig1, Configuration generalConfiguration) {
        configuration = generalConfiguration;
        pluginConfig = pluginConfig1;
    }


    public Result process(){
        var result = new Result();

        WebApi api = new getPercentLanguage();
        String apiResult = api.getResult();
        result.percentage = apiResult;
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
        private String percentage = "";

        String percentage() {
            return percentage;
        }

        @Override
        public String getResultAsString() {
            return percentage;
        }

        @Override
        public String getResultAsHtmlDiv() {
            if(percentage == null || percentage.length() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return " ";
            }
            String a = "<div class=\"chart card\">\n" +
                    "    <div class=\"graph\" id=\"chartContainer" + getPluginName() + "\"></div>\n" +
                    "    <div id=\"charts-button" + getPluginName() + "\" style=\"margin-bottom: 10px\"></div>\n" +
                    "    <script type=\"text/javascript\">\n" +
                    "        var chart" + getPluginName() + " = new CanvasJS.Chart(\"chartContainer" + getPluginName() + "\", {\n" +
                    "            theme: \"light1\",\n" +
                    "            animationEnabled: true,\n" +
                    "            backgroundColor: \"transparent\",\n"+
                    "            title:{\n" +
                    "                text: \"Languages Percentage\"\n" +
                    "            }," +
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
                    "      maximum: 100,"+
                    "              title: \"Percentage\",\n"+
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
                    "                    // Change type to \"doughnut\", \"line\", \"splineArea\", etc.\n" +
                    "                    type: \"pie\",\n" +
                    "                    dataPoints: [";

            String b="";

            String c = "]\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        });\n" +
                    "        chart" + getPluginName() + ".render();\n" +
                    "        let chartsSelector" + getPluginName() + " = document.querySelector('#charts-button" + getPluginName() + "');\n" +
                    "        \n" +
                    "        let t" + getPluginName() + " = ";

            String d = "";

            String f = "let lastActiveButton" + getPluginName() + ";"+
                    "for(let chart of t" + getPluginName() + ") {\n" +
                    "            let chartButton = document.createElement('button');\n" +
                    "            chartButton.className = \"chartButton\";\n" +
                    "            chartButton.innerText = chart;\n" +
                    "            chartButton.onclick = () => {\n" +
                    "                renderChart" + getPluginName() + "(chart, chartButton);\n" +
                    "                lastActiveButton" + getPluginName() + " = chartButton;\n" +
                    "            }\n" +
                    "            chartsSelector" + getPluginName() + ".append(chartButton);\n" +
                    "        }\n" +
                    "\n" +
                    "        function renderChart" + getPluginName() + "(type, button) {\n" +
                    "            if(lastActiveButton" + getPluginName() + " !== button) {\n" +
                    "                chart" + getPluginName() + ".options.data[0].type = type;\n" +
                    "                if(type === \"bar\" || type == \"stackedBar\"){\n" +
                    "                    chart"+ getPluginName() +".options.axisX.labelAngle = 180;\n" +
                    "                }else{\n" +
                    "                    chart"+ getPluginName() +".options.axisX.labelAngle = -70;\n" +
                    "                }"+
                    "                chart"+ getPluginName() +".render();\n" +
                    "                button.classList.add('activeBTN');\n" +
                    "                if (lastActiveButton" + getPluginName() + " != null) {\n" +
                    "                    lastActiveButton" + getPluginName() + ".classList.toggle('activeBTN');\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "</div> ";
            JSONObject json = new JSONObject(percentage);
            JSONArray array = json.names();
            for (int i = 0; i < array.length(); i++) {
                b+="{label:'"+array.getString(i)+"', y:"+json.get(array.getString(i))+"},\n";
            }
            d = "[";
            for(String x : pluginConfig.getChartTypes()){
                d += "\"" + x + "\",";
            }
            d = d.substring(0,d.length()-1);
            d += "];";

            return a + b + c + d + f;
        }

        @Override
        public String getJson() {
            if(!pluginConfig.isJson_output() || percentage == null || percentage.length() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return "";
            }
            JSONObject json = new JSONObject();
            json.put("data", new JSONObject(percentage));
            json.put("name",getPluginName());
            return json.toString(2);
        }

        @Override
        public void jsonToFile() {
            if(!pluginConfig.isHtml_output() || percentage == null || percentage.length() <= 0 || !configuration.getDataConfig().getUseWebApi()){
                return;
            }
            SaveFile.save(getJson(), getPluginName(), "json",getPluginName());
        }

        @Override
        public void htmlToFile() {
            if(!pluginConfig.isHtml_output() || percentage == null || percentage.length() <= 0 || !configuration.getDataConfig().getUseWebApi()){
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

        @Override
        public String getPluginName() {
            return pluginName;
        }
    }
}
