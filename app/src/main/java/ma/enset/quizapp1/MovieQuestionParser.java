package ma.enset.quizapp1;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parser for movie quiz questions stored in CSV format.
 * New format: question,correct_answer,correct_choice_letter,choice_A,choice_B,choice_C,choice_D,choice_I
 */
public class MovieQuestionParser {

    /**
     * Parse questions from a CSV file in assets
     * @param context Application context
     * @param fileName Name of the CSV file in assets
     * @param randomize Whether to randomize the order of questions
     * @return List of Question objects
     */
    public static List<Question> parseQuestionsFromCSV(Context context, String fileName, boolean randomize) {
        List<Question> questions = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean firstLine = true; // Skip header if present
            while ((line = reader.readLine()) != null && questions.size() < 5) {  // Stop after 5 questions
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                Question question = parseQuestionLine(line);
                if (question != null) {
                    questions.add(question);
                }
            }

            reader.close();

            // Randomize question order if requested (but we'll only have max 5)
            if (randomize && !questions.isEmpty()) {
                Collections.shuffle(questions);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return questions;
    }
    /**
     * Parse a single line of CSV into a Question object
     * New format: question,correct_answer,correct_choice_letter,choice_A,choice_B,choice_C,choice_D,choice_I
     *
     * @param line CSV line to parse
     * @return Question object or null if parsing failed
     */
    public static Question parseQuestionLine(String line) {
        try {
            // Split the CSV line while handling quoted fields
            String[] parts = parseCSVLine(line);

            if (parts.length < 5) { // At least question, correct answer, correct letter, and 2 options
                return null;
            }

            String questionText = parts[0].trim();
            String correctAnswer = parts[1].trim();
            String correctChoiceLetter = parts[2].trim();

            // Extract all options
            List<String> options = new ArrayList<>();
            for (int i = 3; i < parts.length; i++) {
                if (!parts[i].isEmpty()) {
                    options.add(parts[i].trim());
                }
            }

            // Determine the correct answer index based on the correct choice letter
            int correctIndex = -1;
            if (correctChoiceLetter.length() == 1) {
                char letter = correctChoiceLetter.charAt(0);
                correctIndex = letter - 'A'; // Convert A->0, B->1, etc.
            }

            // Validate we have enough options and a valid correct index
            if (questionText.isEmpty() || correctAnswer.isEmpty() ||
                    correctIndex < 0 || correctIndex >= options.size() || options.size() < 2) {
                return null;
            }

            return new Question(questionText, options, correctAnswer, null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to parse a CSV line, handling quoted fields
     */
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Handle quote escaping (two quotes is an escaped quote)
                if (inQuotes && i < line.length() - 1 && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // Add the last field
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }
}