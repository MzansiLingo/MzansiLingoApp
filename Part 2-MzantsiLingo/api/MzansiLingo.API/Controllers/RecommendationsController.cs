using Microsoft.AspNetCore.Mvc;

namespace MzansiLingo.API.Controllers
{
    [ApiController]
    [Route("api/recommendations")]
    public class RecommendationsController : ControllerBase
    {
        [HttpGet]
        public IActionResult Get([FromQuery] int greetings = 80,
                                  [FromQuery] int numbers = 60,
                                  [FromQuery] int food = 75,
                                  [FromQuery] int family = 85)
        {
            var scores = new Dictionary<string, int>
            {
                ["Greetings"] = greetings,
                ["Numbers"] = numbers,
                ["Food"] = food,
                ["Family"] = family
            };

            var weakest = scores.OrderBy(x => x.Value).First();

            string message = weakest.Value < 60
                ? $"Let's practise {weakest.Key}. This is your main focus today."
                : $"A little more practice on {weakest.Key} will help.";

            return Ok(new
            {
                topic = weakest.Key,
                accuracy = weakest.Value,
                message
            });
        }
    }
}
