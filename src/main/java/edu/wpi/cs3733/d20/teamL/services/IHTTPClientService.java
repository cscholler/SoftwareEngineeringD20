package edu.wpi.cs3733.d20.teamL.services;

import com.squareup.okhttp.OkHttpClient;

public interface IHTTPClientService {
    OkHttpClient getClient();

    void setClient(OkHttpClient client);
}
