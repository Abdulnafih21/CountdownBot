import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class CountdownBot {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 1. New Year Resolution First
        setResolution(scanner);

        // 2. Countdown to New Year 2025
        printCountdown();

        // 3. Trivia Game (Multiple Questions)
        playTriviaGame(scanner);

        // 4. User Ranking System
        int userPoints = trackUserRanking();

        // 5. Party Planning
        partyPlanning(scanner);

        // Close the scanner
        scanner.close();
    }

    // 1. New Year Resolution First
    public static void setResolution(Scanner scanner) {
        System.out.println("What's your New Year resolution?");
        String resolution = scanner.nextLine();
        System.out.println("Your resolution is: " + resolution);
        System.out.println("Great! Stay focused on your resolution and track your progress throughout the year.");
    }

    // 2. Countdown to New Year 2025
    public static void printCountdown() {
        LocalDateTime newYearEve = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        LocalDateTime now = LocalDateTime.now();
        long secondsLeft = java.time.Duration.between(now, newYearEve).getSeconds();
        System.out.println("Countdown to 2025: " + secondsLeft + " seconds");
    }

    // 3. Trivia Game (Multiple Questions)
    public static void playTriviaGame(Scanner scanner) {
        String[][] triviaQuestions = {
            {"What year is the New Year's Eve for this countdown?", "2025"},
            {"Who is the author of 'The Great Gatsby'?", "F. Scott Fitzgerald"},
            {"What is the capital of France?", "Paris"},
            {"Which planet is known as the Red Planet?", "Mars"},
            {"Who painted the Mona Lisa?", "Leonardo da Vinci"}
        };

        int points = 0;

        System.out.println("Welcome to the trivia game!");
        
        // Loop through the trivia questions
        for (String[] question : triviaQuestions) {
            System.out.println("Question: " + question[0]);
            String answer = scanner.nextLine();
            
            if (answer.equalsIgnoreCase(question[1])) {
                System.out.println("Correct!");
                points += 10;  // Add points for each correct answer
            } else {
                System.out.println("Incorrect! The correct answer was: " + question[1]);
            }
        }

        System.out.println("You earned " + points + " points in the trivia game!");
    }

    // 4. User Ranking System
    public static int trackUserRanking() {
        // For simplicity, we are just giving 10 points for trivia win and goal setting.
        // You can expand it based on user interaction, like answering more trivia, completing challenges, etc.
        int points = 10;
        System.out.println("User Ranking System: You have " + points + " points.");
        return points;
    }

    // 5. Party Planning
    public static void partyPlanning(Scanner scanner) {
        System.out.println("When are you planning your party? Please provide a date (e.g., 2025-12-31): ");
        String partyDate = scanner.nextLine();
        System.out.println("Great! Your party is planned for: " + partyDate);

        // Give tips based on the date
        System.out.println("Tips for making your party more enjoyable:");
        givePartyTips(partyDate);
    }

    // Provide party tips based on the date
    public static void givePartyTips(String date) {
        LocalDateTime partyDate = LocalDateTime.parse(date + "T00:00:00");
        LocalDateTime now = LocalDateTime.now();

        if (partyDate.isAfter(now)) {
            System.out.println("1. Plan the music playlist early! Consider including crowd favorites.");
            System.out.println("2. Decorate with balloons, streamers, and lights to create a festive atmosphere.");
            System.out.println("3. Offer a variety of food and drinks, keeping dietary restrictions in mind.");
            System.out.println("4. Organize games or activities to keep guests entertained.");
            System.out.println("5. Consider having a photo booth with fun props!");
            System.out.println("6. Hire a professional photographer or set up a DIY photo area for fun memories.");
            System.out.println("7. Make sure there’s plenty of seating for guests to relax and socialize.");
            System.out.println("8. Have a theme for your party to create excitement and make it memorable.");
            System.out.println("9. Send invites early, and consider online RSVP tools to track attendance.");
            System.out.println("10. Ensure you have enough chargers and power outlets for guests’ devices.");
        } else {
            System.out.println("Your party has already passed, but keep these tips in mind for future events!");
        }
    }
}
