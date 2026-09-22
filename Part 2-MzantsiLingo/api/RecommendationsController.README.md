# Recommendations API

Add `RecommendationsController.cs` to:

`MzansiLingo.API/Controllers/`

This intentionally uses a very simple algorithm:
- receive topic scores;
- find the lowest score;
- return that topic as the recommended practice.

Example:

GET `/api/recommendations?greetings=90&numbers=45&food=80&family=70`

The response will identify Numbers as the recommended topic.

IMPORTANT:
This is a simple prototype implementation. It does not require a new database table.
Once your Android app is working, you can replace the query parameters with scores retrieved
from your existing user progress data.
