package edu.wpi.cs3733.d20.teamL.services;

import com.squareup.okhttp.OkHttpClient;

public class HTTPClientService implements IHTTPClientService {
    private OkHttpClient client = new OkHttpClient();

    @Override
    public OkHttpClient getClient() {
        return client;
    }

    @Override
    public void setClient(OkHttpClient client) {
        this.client = client;
    }
}
