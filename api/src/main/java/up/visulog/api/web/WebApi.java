package up.visulog.api.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import up.visulog.config.*;
import up.visulog.util.Logger;

public abstract class WebApi {

    private final String url, api_url, api_version = "/api/v4";
    private static Logger log = new Logger();

    public static Config config = null;

    private boolean ok = false;

    /* il faudrait repmpalcer par config */
    public WebApi(String api_url) {

        if (config != null && config.getDataConfig().getUseWebApi()) {
            this.api_url = api_url.replaceAll(":id", config.getDataConfig().getProjetId() + "");
            this.url = createUrl(config.getDataConfig().getUrl());
        } else {
            this.url = null;
            this.api_url = null;
        }
    }

    public String getUrl() {
        return url;
    }

    public static void setConfig(Config config) {
        WebApi.config = config;
    }

    private String createUrl(String url) {
        boolean private_repo = config.getDataConfig().getPrivateRepo();
        String parameter = config.getDataConfig().getParameter();
        String token = config.getDataConfig().getToken();
        if (private_repo) {
            if (api_url.contains("/repository/archive")) {
                return url + api_version + api_url + "&per_page=999999999&" + parameter + "=" + token;
            }
            return url + api_version + api_url + "?&per_page=999999999&" + parameter + "=" + token;
        }
        if (api_url.contains("/repository/archive")) {
            return url + api_version + api_url + "&per_page=999999999&";
        }
        return url + api_version + api_url + "?&per_page=999999999&";
    }

    private HttpURLConnection makeConnection() {

        try {
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000 * 10);
            conn.setReadTimeout(1000 * 10);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "utf-8");
            conn.connect();

            if (conn.getResponseCode() >= 400) {
                log.error("Url response " + conn.getResponseCode() + " " + conn.getResponseMessage());
                log.error("Please check the correct functioning of your API data (token/url/project id/...)");
                ok = false;
            } else if (conn.getResponseCode() == 200) {
                ok = true;
                return conn;
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }

    public String getResult() {
        HttpURLConnection conn = null;

        if (config != null && config.getDataConfig().getUseWebApi()) {
            conn = makeConnection();
        } else {
            return null;
        }

        if (ok) {
            Scanner scan = null;
            try {
                if (conn != null) {
                    scan = new Scanner(conn.getURL().openStream());
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (scan != null && scan.hasNext()) {
                String result = scan.nextLine();
                scan.close();
                return result;
            }

            return null;
        }

        return null;
    }

}
