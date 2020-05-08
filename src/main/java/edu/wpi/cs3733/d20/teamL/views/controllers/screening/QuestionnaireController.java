package edu.wpi.cs3733.d20.teamL.views.controllers.screening;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import edu.wpi.cs3733.d20.teamL.entities.Question;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.util.ArrayList;

public class QuestionnaireController {
    private ArrayList<Question> allQuestions = new ArrayList<>();
    private ArrayList<VBox> questionCards = new ArrayList<>();

    @Inject
    private IDatabaseCache cache;

    @FXML
    private Label questionnaireTitle;
    @FXML
    private VBox allQuestionsBox;

    @FXML
    private void initialize(){
        allQuestions = cache.getQuestions();

        int i = 0;
        while (i < allQuestions.size() - 1) {
            int currentQuestionOrder = allQuestions.get(i).getOrder();
            ToggleGroup toggleGroup = new ToggleGroup();
            while (allQuestions.get(i).getOrder() == (currentQuestionOrder)) {
                if (allQuestions.get(i).getOrder() == -1) questionnaireTitle.setText(allQuestions.get(i).getQuestion());
                else if (allQuestions.get(i).getWeight() == -1){
                    Label sectionHeader = new Label(allQuestions.get(i).getQuestion());
                    sectionHeader.getStyleClass().add("service-request-label-fx");
                    allQuestionsBox.getChildren().add(sectionHeader);
                } else if (allQuestions.get(i).getRecs() == 0) {
                    JFXCheckBox andQuestion = new JFXCheckBox(allQuestions.get(i).getQuestion());
                    allQuestionsBox.getChildren().add(andQuestion);
                } else if (allQuestions.get(i).getRecs() == 1){
                    JFXRadioButton orQuestion = new JFXRadioButton(allQuestions.get(i).getQuestion());
                    allQuestionsBox.getChildren().add(orQuestion);
                    orQuestion.setToggleGroup(toggleGroup);
                }
                i++;
            }
        }
    }




}
