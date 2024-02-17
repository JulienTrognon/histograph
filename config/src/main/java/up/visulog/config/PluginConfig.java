package up.visulog.config;

import java.util.List;

public class PluginConfig {

    private boolean html_output;
    private boolean json_output;
    private List<String> chartTypes;

    public PluginConfig() {

    }

    public boolean isHtml_output() {
        return html_output;
    }

    public void setHtml_output(boolean html_output) {
        this.html_output = html_output;
    }

    public boolean isJson_output() {
        return json_output;
    }

    public void setJson_output(boolean json_output) {
        this.json_output = json_output;
    }

    public List<String> getChartTypes() {
        return chartTypes;
    }

    public void setChartTypes(List<String> chartTypes) {
        this.chartTypes = chartTypes;
    }
}
