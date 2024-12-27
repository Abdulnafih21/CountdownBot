package com.yourname.abdulnafih.countdownbot; // Added package declaration

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Scanner;

public class CountdownBot {
    public static void main(String[] args) {
        // Start the interactive menu with continuous countdown
        startInteractiveMenu();
    }

    // Method to continuously display countdown to 2025
    private static void displayCountdownTo2025() {
        // Set the target date (New Year's Eve 2025)
        LocalDateTime newYearEve = LocalDateTime.of(2025, 12, 31, 23, 59, 59, 0);

        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Calculate the countdown
        long secondsLeft = Duration.between(now, newYearEve).getSeconds();

        // Display the countdown
        System.out.println("Countdown to 2025: " + secondsLeft + " seconds\n");
    }

    // Method to start the interactive menu with continuous countdown
    private static void startInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Display the countdown every time before showing the menu
            displayCountdownTo2025();

            // Show welcome message and menu options
            System.out.println("Welcome to the Countdown Bot! 🎉");
            System.out.println("Please choose an option:");
            System.out.println("1. New Year Resolution");
            System.out.println("2. Countdown to 2025");
            System.out.println("3. Trivia Quiz");
            System.out.println("4. Party Planning");

            // Get user choice
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            // Handle user's choice
            switch (choice) {
                case 1:
                    newYearResolution(scanner);
                    break;
                case 2:
                    // Simply call the countdown method
                    break;
                case 3:
                    triviaQuiz(scanner);
                    break;
                case 4:
                    partyPlanning(scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Please choose a valid option.");
            }
        }
    }

    // Method to handle New Year Resolution input
    private static void newYearResolution(Scanner scanner) {
        System.out.println("What is your New Year's resolution for 2025?");
        String resolution = scanner.nextLine();
        System.out.println("Great! Your resolution is to " + resolution + ". Stay focused and make it happen in 2025!");
    }

    // Method for trivia quiz
    private static void triviaQuiz(Scanner scanner) {
        int score = 0;

        System.out.println("Trivia Time! Let's see how much you know.");

        // Question 1
        System.out.println("Question 1: What is the capital of France?");
        String answer1 = scanner.nextLine();
        if (answer1.equalsIgnoreCase("Paris")) {
            score++;
            System.out.println("Correct!");
        } else {
            System.out.println("Incorrect. The correct answer is Paris.");
        }

        // Question 2
        System.out.println("Question 2: Who invented the telephone?");
        String answer2 = scanner.nextLine();
        if (answer2.equalsIgnoreCase("Alexander Graham Bell")) {
            score++;
            System.out.println("Correct!");
        } else {
            System.out.println("Incorrect. The correct answer is Alexander Graham Bell.");
        }

        System.out.println("Your final score: " + score);
    }

    // Method for party planning
    private static void partyPlanning(Scanner scanner) {
        System.out.println("When are you planning your New Year party?");
        String date = scanner.nextLine();

        System.out.println("Awesome! Here are some tips to make your party more enjoyable:");
        System.out.println("1. Decorate your space with festive colors and lights.");
        System.out.println("2. Create a fun playlist with upbeat music.");
        System.out.println("3. Have some interactive games ready for guests.");
        System.out.println("4. Don't forget the snacks and drinks to keep everyone energized!");
        System.out.println("5. Plan some surprise activities to keep the excitement going.");

        System.out.println("Your party is sure to be a hit! Enjoy and make memories!");
    }
}

