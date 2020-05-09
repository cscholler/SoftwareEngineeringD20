package edu.wpi.cs3733.d20.teamL.views.controllers.screening;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXRadioButton;
import edu.wpi.cs3733.d20.teamL.entities.Question;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.util.ArrayList;

public class QuestionnaireController {
    private ArrayList<Question> allQuestions = new ArrayList<>();
    private ArrayList<VBox> questionCards = new ArrayList<>();
    private ArrayList<Integer> weights = new ArrayList<>();

    @Inject
    private IDatabaseCache cache;

    @FXML
    private Label questionnaireTitle = new Label("");
    @FXML
    private VBox dialogVBox;
    @FXML
    private JFXDialogLayout screeningDialog;
    @FXML
    private JFXButton btnNext;
    private int index = 1;

    @FXML
    private void initialize(){
        allQuestions = cache.getQuestions();

        int i=0;
        while (i < allQuestions.size() - 1) {
            VBox allQuestionsBox = new VBox();
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
                if(i == allQuestions.size() - 1) {
                    break;
                }
                i++;
            }
            questionCards.add(allQuestionsBox);
        }

        dialogVBox = questionCards.get(0);
        screeningDialog.setHeading(questionnaireTitle);
    }

    @FXML
    private void nextClicked() {
        int total = 0;

        ObservableList<Node> children = dialogVBox.getChildren();

        //is a checkbox
        if (allQuestions.get(index - 1).getRecs() == 0) {
            for (Node n : children) {
                JFXCheckBox box = (JFXCheckBox) n;
                if (((JFXCheckBox) n).isSelected()) {
                    total += 1;
                }
            }
            //is a radiobutton
        } else if (allQuestions.get(index - 1).getRecs() == 1 && allQuestions.get(index - 1).getWeight() >= 0) {
            for (Node n : children) {
                JFXRadioButton box = (JFXRadioButton) n;
                if (((JFXCheckBox) n).isSelected()) {
                    total += 1;
                }
            }
        }

        weights.set(index-1, total);

        //go to next set of questions
        dialogVBox = questionCards.get(index);
        if (index < questionCards.size()) {
            index++;
        }
        else {
            dialogVBox.getChildren().clear();

            int grandTotal = 0;
            for (int i : weights) {
                grandTotal += weights.get(i);
            }

            for(Question q : allQuestions) {
                if (q.getOrder() == -3) {
                    //always show
                    if(q.getWeight() == 0) {
                        Label l = new Label(q.getQuestion());
                        dialogVBox.getChildren().add(l);
                    } else if (q.getRecs() == -10 && q.getWeight() < grandTotal){
                        Label l = new Label(q.getQuestion());
                        dialogVBox.getChildren().add(l);
                    } else if (weights.get(q.getRecs() - 1) >= 1 && q.getWeight() < grandTotal - weights.get(q.getRecs() -1)) {
                        Label l = new Label(q.getQuestion());
                        dialogVBox.getChildren().add(l);
                    } else if (q.getRecs() < 0) {
                        if(weights.get(Math.abs(q.getRecs()) -1) < 1 && q.getWeight() < grandTotal - weights.get(Math.abs(q.getRecs()-1))) {
                            Label l = new Label(q.getQuestion());
                            dialogVBox.getChildren().add(l);
                        }

                    }
                }
            }


            //TODO show results
        }
    }
}
