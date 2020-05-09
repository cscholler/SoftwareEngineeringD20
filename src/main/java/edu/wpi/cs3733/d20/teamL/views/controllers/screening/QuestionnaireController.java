package edu.wpi.cs3733.d20.teamL.views.controllers.screening;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import edu.wpi.cs3733.d20.teamL.entities.Question;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class QuestionnaireController {
    //@Inject
    //private IDatabaseCache cache;

    private ArrayList<Question> allQuestions;
    private ArrayList<VBox> questionCards = new ArrayList<>();
    private ArrayList<Integer> weights = new ArrayList<>();

    private Label questionnaireTitle = new Label("");
    private VBox dialogVBox = new VBox();

    private JFXButton btnNext;
    private int index = 0, counter = 0;

    private boolean testFinished, done;

    public QuestionnaireController(ArrayList<Question> questionsList){
        //cache.cacheQuestionsFromDB();
        //allQuestions = cache.getQuestions();
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
                    allQuestionsBox.getChildren().add(andQuestion);
                } else if (allQuestions.get(i).getRecs() == 1 && allQuestions.get(i).getOrder() != -3){
                    JFXRadioButton orQuestion = new JFXRadioButton(allQuestions.get(i).getQuestion());
                    allQuestionsBox.getChildren().add(orQuestion);
                    orQuestion.setToggleGroup(toggleGroup);
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
        System.out.println("questioncards size: " + questionCards.size());
    }

    public Label getQuestionnaireTitle() {
        return questionnaireTitle;
    }

    public VBox nextClicked() {



        //int total = 0;
        System.out.println("questioncards size 2: " + questionCards.size());
        System.out.println("Index is: " + index);

        //dialogVBox.getChildren().clear();
        //dialogVBox.getChildren().addAll(questionCards.get(index-1).getChildren());

        //ObservableList<Node> children = dialogVBox.getChildren();

        //calculates score with check boxes
        /*if (allQuestions.get(index - 1).getRecs() == 0) {
            for (Node n : children) {
                JFXCheckBox box = (JFXCheckBox) n;
                if (((JFXCheckBox) n).isSelected()) {
                    total += 1;
                }
            }
            //calculates score with radiobuttons
        } else if (allQuestions.get(index - 1).getRecs() == 1 && allQuestions.get(index - 1).getWeight() >= 0) {
            for (Node n : children) {
                JFXRadioButton box = (JFXRadioButton) n;
                if (((JFXCheckBox) n).isSelected()) {
                    total += 1;
                }
            }
        }

        weights.add(index-1, total);*/

        if (index == questionCards.size()-2) {
            testFinished = true;
        }

        //go to next set of questions
        //dialogVBox = questionCards.get(index);
        if (index < questionCards.size()-1) {
            index++;
            dialogVBox.getChildren().clear();
            dialogVBox.getChildren().addAll(questionCards.get(index-1).getChildren());
        }
        // creates results page
        else {
            done = true;

            dialogVBox.getChildren().clear();

            int grandTotal = 0;
            for (int i : weights) {
                System.out.println("Weight= " + i);
                grandTotal += i;//weights.get(i);
            }

            for(Question q : allQuestions) {
                if (q.getOrder() == -3) {
                    //always show
                    if(q.getWeight() == 0) {
                        Label l = new Label(q.getQuestion());
                        dialogVBox.getChildren().add(l);
                    } else if (q.getRecs() == -10 && q.getWeight() < grandTotal) {
                        Label l = new Label(q.getQuestion());
                        dialogVBox.getChildren().add(l);
                    } else if (q.getRecs() == -1 && weights.get(1) > q.getWeight() && weights.get(3) == 0 && weights.get(0) == 0) {
                        Label l = new Label(q.getQuestion());
                        dialogVBox.getChildren().add(l);
                    } else if (q.getRecs() == 1 && weights.get(1) > q.getWeight() && (weights.get(3) > 0 || weights.get(0) > 0)) {
                        Label l = new Label(q.getQuestion());
                        dialogVBox.getChildren().add(l);
                    }//else if (q.getRecs() < 0) {
                       // if (weights.get(Math.abs(q.getRecs()) - 1) < 1 && q.getWeight() < grandTotal - weights.get(Math.abs(q.getRecs() - 1))) {
                         //   Label l = new Label(q.getQuestion());
                           // dialogVBox.getChildren().add(l);
                        //}

                    //}
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


        //System.out.println("Counter = " + counter);

        //calculates score with check boxes
        if (allQuestions.get(counter - 1).getRecs() == 0) {
            for (int i = 1; i < children.size(); i++) {
                JFXCheckBox box = (JFXCheckBox) children.get(i);
                if (box.isSelected()) {
                    System.out.println("Counter is: " + counter);
                    total += allQuestions.get(counter).getWeight();
                }
                counter++;
            }
            //calculates score with radiobuttons
        } else if (allQuestions.get(counter - 1).getRecs() == 1 /*&& allQuestions.get(counter - 1).getWeight() >= 0*/) {
            for (int i = 1; i < children.size(); i++) {
                JFXRadioButton box = (JFXRadioButton) children.get(i);
                if (box.isSelected()) {
                    //total += 1;
                    System.out.println("Counter is: " + counter);
                    total += allQuestions.get(counter).getWeight();
                }
                counter++;
            }
        }



        System.out.println("Weight is: " + total);
        //System.out.println("Index is: " + index);
        weights.add(index-1, total);

    }

    public boolean getTestFinished() {
        return testFinished;
    }

    public boolean getDone() {
        return done;
    }
}
