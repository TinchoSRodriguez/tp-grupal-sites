import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static spark.Spark.*;

public class Server {

    public static BufferedReader getJSON(String urlString) {
        BufferedReader in = null;
        try {
            URL url = new URL(urlString);
            try {
                URLConnection urlConnection = url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                if(urlConnection instanceof HttpURLConnection) {
                    HttpURLConnection connection = (HttpURLConnection) urlConnection;
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    System.out.println("URL inválida.");
                }
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        } catch (MalformedURLException exception) {
            System.out.println(exception.getMessage());
        }
        return in;
    }

    public static Site[] sortSites(Site[] sites) {
        Site temp = null;
        for (int j = 0; j < sites.length; j++) {
            for (int i = j + 1; i < sites.length; i++) {
                if (sites[i].getName().compareTo(sites[j].getName()) < 0) {
                    temp = sites[j];
                    sites[j] = sites[i];
                    sites[i] = temp;
                }
            }
        }
        return sites;
    }

    public static void main(String[] args) {

        String apiURL = "https://api.mercadolibre.com/sites/";

        final Site[] sites = sortSites(new Gson().fromJson(getJSON(apiURL), Site[].class));

        port(8080);

        get("/sites", (req, res) -> {
            res.type("application/json");
            return new Gson().toJson(sites, Site[].class);
        });

        get("/sites/:id/categories", (req, res) -> {
            final Category[] categories = new Gson().fromJson(getJSON(apiURL+req.params(":id")+"/categories"), Category[].class);
            res.type("application/json");

            return new Gson().toJson(categories, Category[].class);
        });


    }
}