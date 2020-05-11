package edu.wpi.cs3733.d20.teamL.views.controllers.logged_in;

import com.google.inject.Inject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import edu.wpi.cs3733.d20.teamL.services.IHTTPClientService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SideBarController {
    private final FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
    @Inject
	private IHTTPClientService httpClient;

    @FXML
    private void addUserClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("admin/AddPerson").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void changePasswordClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("admin/ChangePassword").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    public void btnImportClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("dialogues/ImportDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    public void btnExportClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("dialogues/ExportDialogue").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    private void timeoutPressed() {
        try {
            Parent root = loaderFactory.getFXMLLoader("admin/KioskTimeout").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    @FXML
    public void handleAnalytics() {
        try {
            Parent root = loaderFactory.getFXMLLoader("admin/AnalyticsPage").load();
            loaderFactory.setupScene(new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

	@FXML
	private void rebuildDatabaseClicked() {
		try {
			Parent root = loaderFactory.getFXMLLoader("admin/RebuildDatabaseDialogue").load();
			loaderFactory.setupPopup(new Stage(), new Scene(root));

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{    \"gallery_name\":\"users\"}");
			Request request = new Request.Builder()
					.url("https://kairosapi-karios-v1.p.rapidapi.com/gallery/remove")
					.post(body)
					.addHeader("x-rapidapi-host", "kairosapi-karios-v1.p.rapidapi.com")
					.addHeader("x-rapidapi-key", "e9d19d8ab2mshee9ab7d6044378bp106222jsnc2e9a579d919")
					.addHeader("content-type", "application/json")
					.addHeader("accept", "application/json")
					.build();

			Response response = httpClient.getClient().newCall(request).execute();
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	}
}
