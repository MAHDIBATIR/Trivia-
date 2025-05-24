package ma.enset.quizapp1;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private Integer imageResource; // Resource ID for an image, can be null

    public Question(String questionText, List<String> options, String correctAnswer, Integer imageResource) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.imageResource = imageResource;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public Integer getImageResource() {
        return imageResource;
    }
} 