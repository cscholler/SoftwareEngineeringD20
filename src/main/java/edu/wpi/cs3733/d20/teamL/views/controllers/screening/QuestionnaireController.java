package edu.wpi.cs3733.d20.teamL.views.controllers.screening;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import edu.wpi.cs3733.d20.teamL.entities.Question;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class QuestionnaireController {

    private ArrayList<Question> allQuestions;
    private ArrayList<VBox> questionCards = new ArrayList<>();
    private ArrayList<Integer> weights = new ArrayList<>();

    private Label questionnaireTitle = new Label("");
    private VBox dialogVBox = new VBox();

    private int index = 0, counter = 0;

    private boolean testFinished, done;

    public QuestionnaireController(ArrayList<Question> questionsList){
        dialogVBox.getStylesheets().add("/edu/wpi/cs3733/d20/teamL/css/GlobalStyleSheet.css");
        allQuestions = questionsList;
        testFinished = false;
        done = false;

        int i=0;
        while (i < allQuestions.size() - 1) {
            VBox allQuestionsBox = new VBox();
            int currentQuestionOrder = allQuestions.get(i).getOrder();
            ToggleGroup toggleGroup = new ToggleGroup();
            while (allQuestions.get(i).getOrder() == (currentQuestionOrder)) {
                if (allQuestions.get(i).getOrder() == -1) questionnaireTitle.setText(allQuestions.get(i).getQuestion());
                else if (allQuestions.get(i).getWeight() == -1 && allQuestions.get(i).getOrder() != -3){
                    Label sectionHeader = new Label(allQuestions.get(i).getQuestion());
                    sectionHeader.getStyleClass().add("service-request-label-fx");
                    allQuestionsBox.getChildren().add(sectionHeader);
                } else if (allQuestions.get(i).getRecs() == 0 && allQuestions.get(i).getOrder() != -3) {
                    JFXCheckBox andQuestion = new JFXCheckBox(allQuestions.get(i).getQuestion());
                    andQuestion.getStyleClass().add("check-box-jfx");
                    andQuestion.setStyle("-fx-font-size: 18;");
                    allQuestionsBox.getChildren().add(andQuestion);
                } else if (allQuestions.get(i).getRecs() == 1 && allQuestions.get(i).getOrder() != -3){
                    JFXRadioButton orQuestion = new JFXRadioButton(allQuestions.get(i).getQuestion());
                    orQuestion.getStyleClass().add("radio-button-jfx");
                    allQuestionsBox.getChildren().add(orQuestion);
                    orQuestion.setToggleGroup(toggleGroup);
                    orQuestion.setStyle("-fx-font-size: 18;");
                } else if (allQuestions.get(i).getOrder() == -3) {
                    Label label = new Label(allQuestions.get(i).getQuestion());
                    allQuestionsBox.getChildren().add(label);
                }
                if(i == allQuestions.size() - 1) {
                    break;
                }
                i++;
            }
            questionCards.add(allQuestionsBox);
        }
    }

    public Label getQuestionnaireTitle() {
        return questionnaireTitle;
    }

    public VBox nextClicked() {

        if (index == questionCards.size()-2) {
            testFinished = true;
        }

        //go to next set of questions
        if (index < questionCards.size()-1) {
            index++;
            dialogVBox.getChildren().clear();
            dialogVBox.getChildren().addAll(questionCards.get(index-1).getChildren());
        }
        // creates results page
        else {
            done = true;

            dialogVBox.getChildren().clear();
            dialogVBox.setSpacing(5);
            Label bwhSuggestions = new Label("BWH's suggestions for you:");
            bwhSuggestions.getStyleClass().add("service-request-label-fx");
            bwhSuggestions.setPadding(new Insets(0,0,5,0));
            dialogVBox.getChildren().add(bwhSuggestions);

            int grandTotal = 0;
            for (int i : weights) {
                grandTotal += i;
            }

            for(Question q : allQuestions) {
                if (q.getOrder() == -3) {
                    //always show
                    if(q.getWeight() == 0) {
                        Label l = new Label("\u2022 " + q.getQuestion());
                        l.setStyle("-fx-font-size: 18;");
                        dialogVBox.getChildren().add(l);
                    } else if (q.getRecs() == -10 && q.getWeight() < grandTotal) {
                        Label l = new Label("\u2022 " + q.getQuestion());
                        l.setStyle("-fx-font-size: 18;");
                        dialogVBox.getChildren().add(l);
                    } else if (q.getRecs() == -1 && weights.get(1) > q.getWeight() && weights.get(3) == 0 && weights.get(0) == 0) {
                        Label l = new Label("\u2022 " + q.getQuestion());
                        l.setStyle("-fx-font-size: 18;");
                        dialogVBox.getChildren().add(l);
                    } else if (q.getRecs() == 1 && weights.get(1) > q.getWeight() && (weights.get(3) > 0 || weights.get(0) > 0)) {
                        Label l = new Label("\u2022 " + q.getQuestion());
                        l.setStyle("-fx-font-size: 18;");
                        dialogVBox.getChildren().add(l);
                    }
                }
            }
        }


        return dialogVBox;
    }

    public void calculateScore() {

        int total = 0;

        ObservableList<Node> children = dialogVBox.getChildren();

        //account for first label
        counter++;

        //calculates score with check boxes
        if (allQuestions.get(counter - 1).getRecs() == 0) {
            for (int i = 1; i < children.size(); i++) {
                JFXCheckBox box = (JFXCheckBox) children.get(i);
                if (box.isSelected()) {
                    total += allQuestions.get(counter).getWeight();
                }
                counter++;
            }
            //calculates score with radiobuttons
        } else if (allQuestions.get(counter - 1).getRecs() == 1) {
            for (int i = 1; i < children.size(); i++) {
                JFXRadioButton box = (JFXRadioButton) children.get(i);
                if (box.isSelected()) {
                    total += allQuestions.get(counter).getWeight();
                }
                counter++;
            }
        }
        weights.add(index-1, total);
    }

    public boolean getTestFinished() {
        return testFinished;
    }

    public boolean getDone() {
        return done;
    }
}
