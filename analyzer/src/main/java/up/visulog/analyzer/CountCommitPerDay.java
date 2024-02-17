package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import up.visulog.api.git.Commit;
import java.util.Map;
import java.util.TreeMap;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import up.visulog.util.SaveFile;

import java.util.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;


public class CountCommitPerDay implements AnalyzerPlugin{

    private final static SimpleDateFormat dateCommitFormat=new SimpleDateFormat(" MMM d yyyy ",Locale.ENGLISH);
	private static Configuration configuration;
    private static PluginConfig pluginConfig = null;
	private Result result;

    public static final String pluginName = "countCommitPerDay";


    public CountCommitPerDay(PluginConfig pluginConfig, Configuration configuration){
		this.configuration=configuration;
        this.pluginConfig = pluginConfig;
	}

	static Result processLog(List<Commit> gitLog){
		var result=new Result();
		Map <Date, Integer> resultsDay=new TreeMap<>();

		for(var commit: gitLog){
			try{
                Date date=dateCommitFormat.parse(dateCommitFormat.format(commit.date));
				if(resultsDay.containsKey(date)){
					resultsDay.put(date, resultsDay.get(date)+1);
				}
				else{
					resultsDay.put(date,1);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
        Map<String, Integer> finalmap=new LinkedHashMap<>();
        for(var item: resultsDay.entrySet()){
            Integer c=item.getValue();
            String res=dateCommitFormat.format(item.getKey());
            finalmap.put(res,c);
        }
		result.commitsMap=finalmap;
		return result;
	}

	@Override
    public void run () {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }
   
    @Override
    public Result getResult () {
        if (result == null) run();
        return result;
    }

    /*********************************************/
    static class Result implements AnalyzerPlugin.Result {
        Map<String, Integer> commitsMap = new LinkedHashMap<>();

        @Override
        public String getPluginName(){
        	return CountCommitPerDay.pluginName;
        }




        @Override
        public String getResultAsString() {
            return commitsMap.toString();
        }

        @Override
        public String getResultAsHtmlDiv(){
            String a = "<div class=\"chart card\">\n" +
                    "<div class=\"graph\" id=\"chartContainer"+ getPluginName() +"\"></div>"+
                    "        <div id=\"charts-button" + getPluginName() + "\" style=\"margin-bottom: 10px\"></div>\n" +
                    "    <script type=\"text/javascript\">\n" +
                    "        var chart" + getPluginName() + " = new CanvasJS.Chart(\"chartContainer" + getPluginName() + "\", {\n" +
                    "            theme: \"light1\",\n" +
                    "backgroundColor: \"transparent\",\n"+
                    "            animationEnabled: true,\n" +
                    "            title:{\n" +
                    "                text: \"Commits Per Day\"\n" +
                    "            },\n" +
                    "  exportEnabled: true,\n" +
                    "  zoomEnabled: true,\n" +
                    "  toolbar: {\n" +
                    "    itemBackgroundColorOnHover: \"#3e3e3e\",\n" +
                    "    buttonBorderColor: \"transparent\"\n" +
                    "  },"+
                    "            axisX: {\n" +
                    "                labelAutoFit: true,\n" +
                    "            },"+
                    "            axisY:{ \n"+
                    "              title: \"Number of commits\",\n"+
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
                    "</div>";
            for (var item : commitsMap.entrySet()) {
                b+="{label:'"+item.getKey()+"', y:"+item.getValue()+"},\n";
            }
            d = "[";
            for(String x : pluginConfig.getChartTypes()){
                d += "\"" + x + "\",";
            }
            d = d.substring(0,d.length()-1);
            d += "];";

            StringBuilder html = new StringBuilder();
            html.append(a).append(b).append(c).append(d).append(f);
            return html.toString();
        }

        @Override
        public String getJson(){
        	JSONArray array = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("name", getPluginName());
            for (var element : commitsMap.entrySet()) {
                JSONObject item = new JSONObject();
                item.put("nbrCommits", element.getValue());
                item.put("Date", element.getKey());
                array.put(item);
            }
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
                    "        <div style=\"text-align: center;\">histoGRAPH Copyright &copy; 2021</div>\n" +
                    "</body>\n" +
                    "</html>";
            SaveFile.save(html, getPluginName(), "html",getPluginName());
        }

    }
}
