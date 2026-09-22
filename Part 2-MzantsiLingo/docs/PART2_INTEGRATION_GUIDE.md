# Simple Part 2 Integration Guide

## 1. Smart recommendation

Your existing app already has progress/quiz concepts.

After a quiz, create a list like:

```kotlin
val scores = listOf(
    TopicScore("Greetings", 90),
    TopicScore("Numbers", 45),
    TopicScore("Food", 80),
    TopicScore("Family", 70)
)
```

Then:

```kotlin
val recommendation = RecommendationEngine.recommend(scores)
```

Display:

```text
Today's Focus
Numbers

Let's practise Numbers. This is your main focus today.
```

Do not create a complicated AI system. This simple rule-based recommendation is enough
for a prototype.

## 2. WordFeeder

Create a simple screen with:
- one isiXhosa word;
- three answer buttons;
- 10 XP for a correct answer;
- 0 XP for an incorrect answer.

Use:

```kotlin
val game = WordFeeder()
val result = game.submit(question, selectedAnswer)
```

Show the result and updated XP.

## 3. Analytics

Use `LearningAnalyticsCalculator` with the data your app already collects.

Display:

```text
MY LEARNING

7 day streak
120 XP
25 words learned
72% quiz accuracy

Strongest: Greetings
Needs practice: Numbers
```

## 4. Cultural cards

Use `CulturalCards.cards` to show a small "Mzansi Moment" card after a lesson.

Keep it simple:
- word;
- meaning;
- example;
- translation.

## 5. Tests

Copy the test files into:

`app/src/test/java/com/example/mzantsilingo/outstanding/`

Run:

```text
./gradlew test
```

Fix any package/dependency issues caused by your existing Gradle setup.

## 6. GitHub Actions

Copy:

`.github/workflows/android-build.yml`

into the root of the repository.

Push to GitHub and open the Actions tab.

The workflow should show a green successful build.

## 7. Demonstration video

Show one connected story:

1. Open the app.
2. Complete a quiz.
3. Show the weak topic.
4. Show "Today's Focus".
5. Open WordFeeder.
6. Answer a question.
7. Receive XP.
8. Open analytics.
9. Show the cultural card.
10. Show GitHub Actions passing.

This is much better than demonstrating isolated screens.
