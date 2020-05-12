package edu.wpi.cs3733.d20.teamL.services;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

public interface IHTTPClientService {
    OkHttpClient getClient();
    String currLang = "en";

    void setClient(OkHttpClient client);

    String translate(String from, String language, String text) throws IOException;

    String getLang(String text) throws IOException;

    String getCurrLang();

    void setCurrLang(String d);
}
