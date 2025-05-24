# Quiz App - Dynamic Questions

This Android application is a quiz game with dynamically loaded questions from a JSON file and Firebase Authentication.

## Features

- User authentication with Firebase (login, register, profile management)
- Dynamic questions loaded from a JSON file
- Countdown timer for each question (30 seconds)
- Automatic progression to next question when time runs out
- Real-time score display throughout the quiz
- Support for question images
- Top 5 high scores leaderboard
- Final score summary
- User profile management

## Firebase Setup

To use the Firebase Authentication features:

1. Create a project on [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app with package name `ma.enset.quizapp1`
3. Download the `google-services.json` file
4. Place the downloaded file in the `app` directory, replacing the placeholder file
5. Enable Email/Password authentication in the Firebase Console:
   - Go to Authentication > Sign-in method
   - Enable Email/Password provider

## How to Customize Quiz Questions

To modify the quiz questions, follow these steps:

1. Navigate to the `app/src/main/assets` folder in your project
2. Open the `questions.json` file
3. Edit the file following this structure:

```json
[
  {
    "questionText": "Your question text here",
    "options": [
      "Option 1",
      "Option 2", 
      "Option 3",
      "Option 4"
    ],
    "correctAnswer": "Option that is correct (must exactly match one of the options)",
    "imageResource": "image_name_without_extension"
  },
  // Add more questions here...
]
```

### Adding Images to Questions

1. To add an image to a question:
   - Place your image in the `app/src/main/res/drawable` folder
   - Use the filename (without extension) as the `imageResource` value
   - Example: For an image named "movie_poster.jpg", use `"imageResource": "movie_poster"`

2. To remove images from a question:
   - Set `"imageResource": null`

### Important Notes

- Each question must have exactly one correct answer
- The correct answer text must exactly match one of the options
- Don't forget commas between objects in the JSON array
- Make sure your JSON is valid - you can use online validators
- The app will use default hardcoded questions if the JSON file cannot be loaded or is empty

## Authentication Features

- **User Registration**: Create new accounts with email and password
- **User Login**: Authenticate with existing credentials
- **Profile Management**: Update username, change password
- **Account Deletion**: Allow users to delete their accounts
- **Session Management**: Auto-login for returning users

## Modifying the App

To make further changes to the app:

1. The question handling logic is in `DynamicQuizActivity.java`
2. The question data model is in `Question.java`
3. The quiz UI layout is in `activity_dynamic_quiz.xml`
4. Authentication logic is in `MainActivity.java`, `RegisterActivity.java` and `FirebaseAuthHelper.java`
5. User profile management is in `ProfileActivity.java`

### Customizing the Timer

To change the countdown timer duration:
- Open `DynamicQuizActivity.java`
- Find the constant `QUESTION_TIMER_DURATION` (currently set to 30 seconds)
- Change the value to your desired duration in milliseconds 