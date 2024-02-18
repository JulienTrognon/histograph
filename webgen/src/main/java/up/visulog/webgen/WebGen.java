package up.visulog.webgen;

import org.json.JSONArray;
import org.json.JSONObject;
import up.visulog.analyzer.*;
import up.visulog.util.*;
import java.awt.Desktop;
import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.FileWriter;
import java.io.IOException;
// import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.net.URI;

import java.io.InputStream;
import java.io.InputStreamReader;
// import up.visulog.util.Logger;

public class WebGen {

    private String htmlContent;
    private final static String resultPath = "histoGRAPH/output/html/All";

    public WebGen(AnalyzerResult result) {
        try {
            this.htmlContent = result.toHTML();

            String jsCodeBtnUpdate = "";

            for (var x : result.getSubResults()) {
                String name = x.getPluginName();

                jsCodeBtnUpdate += "if (typeof chart" + name + " !== 'undefined') {\n" +
                        "if (dark) {\n" +
                        "   chart" + name + ".options.theme = \"dark1\";\n" +
                        "} else {\n" +
                        "   chart" + name + ".options.theme = \"light1\";\n" +
                        "}\n" +
                        "   chart" + name + ".render();\n" +
                        "}\n";
            }
            InputStream template = WebGen.class.getClassLoader().getResourceAsStream("template.html");
            String htmlString = InputStreamToString(template);
            htmlString = htmlString.replace("$remplacer", this.htmlContent).replace("$replaceUpdate", jsCodeBtnUpdate);
            SaveFile.save(htmlString, "index", "html", "All");
            openHtml();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File findUsingIOApi(String sdir) {
        File dir = new File(sdir);
        if (dir.isDirectory()) {
            Optional<File> opFile = Arrays.stream(dir.listFiles(File::isFile))
                    .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

            if (opFile.isPresent()) {
                return opFile.get();
            }
        }

        return null;
    }

    private String getResultFolder() {
        try {
            File f = new File(resultPath);
            String path = f.getCanonicalPath();
            return path;
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String InputStreamToString(InputStream input) {
        String text = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

        return text;
    }

    public void openHtml() throws IOException {
        File s = findUsingIOApi(getResultFolder());
        URI u = s.toURI();
        try {

            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(u);
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void jsonToJava(String json_s) {

        JSONObject json = new JSONObject(json_s);
        String plugin_name = json.getString("name");
        System.out.println("Nom du plugin: " + plugin_name);
        JSONArray json_list = json.getJSONArray("data");
        for (int x = 0; x < json_list.length(); x++) {
            String author = json_list.getJSONObject(x).getString("author");
            int nbrCommits = json_list.getJSONObject(x).getInt("nbrCommits");
            System.out.println("Auteur: " + author);
            System.out.println("Nombre de comits: " + nbrCommits);
        }
    }
}
