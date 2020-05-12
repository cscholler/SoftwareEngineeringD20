package edu.wpi.cs3733.d20.teamL.services;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class HTTPClientService implements IHTTPClientService {
    private OkHttpClient client = new OkHttpClient();
    private String currLang = "en";

    public void setCurrLang(String currLang) {
        this.currLang = currLang;
    }

    public String getCurrLang() {
        return currLang;
    }

    @Override
    public OkHttpClient getClient() {
        return client;
    }

    @Override
    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public String translate(String from, String language, String text) throws IOException {

        HttpUrl.Builder urlBuilder
                = HttpUrl.parse("https://microsoft-azure-translation-v1.p.rapidapi.com/translate").newBuilder();
        urlBuilder.addQueryParameter("to", language);
        urlBuilder.addQueryParameter("text", text);
        urlBuilder.addQueryParameter("from", from);

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-host", "microsoft-azure-translation-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "47cff1c2f7msh808be5a504f1a49p175068jsn3e1302e95cb5")
                .addHeader("accept", "application/json")
                .build();

        Response response = client.newCall(request).execute();

        text = response.body().string();
        System.out.println(text);
        int endIndex = text.indexOf('<', 5);
        int startIndex = text.indexOf('>') + 1;
        text = text.substring(startIndex, endIndex);
        log.info(text);
        currLang = language;
        return text;
    }

    public String getLang(String text) throws IOException {
        HttpUrl.Builder urlBuilder
                = HttpUrl.parse("https://microsoft-azure-translation-v1.p.rapidapi.com/Detect").newBuilder();
        urlBuilder.addQueryParameter("text", text);

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-host", "microsoft-azure-translation-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "47cff1c2f7msh808be5a504f1a49p175068jsn3e1302e95cb5")
                .addHeader("accept", "application/json")
                .build();

        Response response = client.newCall(request).execute();

        text = response.body().string();
        int endIndex = text.indexOf('<', 5);
        int startIndex = text.indexOf('>') + 1;
        text = text.substring(startIndex, endIndex);
        return text;
    }

}
