package com.abdulnafih.countdownbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class CountdownBot extends ListenerAdapter {

    private static final LocalDateTime newYearEve = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
    private final Map<String, Integer> triviaScores = new HashMap<>();
    private final Map<String, List<String>> userGoals = new HashMap<>();
    private final List<TriviaQuestion> triviaQuestions = new ArrayList<>();
    private final Map<String, TriviaQuestion> activeTrivia = new HashMap<>();
    private final List<String> partyIdeas = new ArrayList<>();
    private final List<String> jokes = new ArrayList<>();
    private final List<String> funFacts = new ArrayList<>();
    private final Map<String, List<String>> userResolutions = new HashMap<>();
    private final List<String> motivationalQuotes = new ArrayList<>();
    
    public static void main(String[] args) throws LoginException {
        String token = "<your token>"; // Replace with your bot token
        JDABuilder.createDefault(token)
                .setActivity(Activity.playing("Countdown to 2025"))
                .addEventListeners(new CountdownBot())
                .build();
    }

    public CountdownBot() {
        loadTriviaQuestions();
        loadPartyIdeas();
        loadJokes();
        loadFunFacts();
        loadMotivationalQuotes();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String userId = event.getAuthor().getId();

        // Info menu
        if (message.equalsIgnoreCase("/info")) {
            StringBuilder infoMessage = new StringBuilder("**Welcome to the Countdown Bot!** 🎉\n");
            infoMessage.append("We are counting down to 2025! 🎉\n\n")
                    .append("Join in the fun and try to become the trivia champion! Answer questions correctly to climb to the top of the leaderboard. 🏆\n")
                    .append("Stay active and engaged this festive season for exciting challenges and surprises!\n\n")
                    .append("Here are some things you can do:\n")
                    .append("- `/countdown`: View the live countdown to New Year 2025.\n")
                    .append("- `/addresolution <resolution>`: Adds a resolution to the user's list.\n")
                    .append("- `/viewresolution`: Views all resolutions that the user has added.\n")
                    .append("- `/removeresolution <resolution number>`: Removes a resolution by the number specified by the user.\n")
                    .append("- `/motivate`: Get a motivational quote to help you stay focused on your resolutions.\n")
                    .append("- `/addgoal <your goal>`: Add a goal for 2025[share your goal in the format:/addgoal<your goal>].\n")
                    .append("- `/removegoal <goal number>`: Remove a goal for 2025.\n")
                    .append("- `/viewgoals`: View your current goals for 2025.\n")
                    .append("- `/trivia`: Start a trivia question and test your knowledge!\n")
                    .append("- `/leaderboard`: Check the current trivia leaderboard.\n")
                    .append("- `/party`: Get New Year's party ideas.\n")
                    .append("- `/joke`: Enjoy a New Year's joke.\n")
                    .append("- `/fact`: Learn a fun New Year's fact.\n");
            event.getChannel().sendMessage(infoMessage.toString()).queue();
        }

        // Greet the user
        if (message.equalsIgnoreCase("/greet")) {
            event.getChannel().sendMessage("Hello " + event.getAuthor().getName() + "! 🎉").queue();
        }

        // Countdown to 2025
        if (message.equalsIgnoreCase("/countdown")) {
            event.getChannel().sendMessage("Counting down to 2025...").queue();
            startCountdown(event);
        }

        // Add New Year's resolution
        if (message.startsWith("/addresolution")) {
            String resolution = message.replace("/addresolution ", "").trim();
            if (!resolution.isEmpty()) {
                userResolutions.putIfAbsent(userId, new ArrayList<>());
                List<String> resolutions = userResolutions.get(userId);
                resolutions.add(resolution); // Add the resolution to the user's list
                event.getChannel().sendMessage("Your resolution has been added: " + resolution).queue();
            } else {
                event.getChannel().sendMessage("Please provide a valid resolution.").queue();
            }
        }
        
        // View New Year's resolutions
        if (message.equalsIgnoreCase("/viewresolution")) {
            List<String> resolutions = userResolutions.getOrDefault(userId, new ArrayList<>());
            if (resolutions.isEmpty()) {
                event.getChannel().sendMessage("You have not added any resolutions yet. Use `/addresolution` to add one!").queue();
            } else {
                StringBuilder resolutionsMessage = new StringBuilder("Your resolutions:\n");
                for (int i = 0; i < resolutions.size(); i++) {
                    resolutionsMessage.append(i + 1).append(". ").append(resolutions.get(i)).append("\n");
                }
                event.getChannel().sendMessage(resolutionsMessage.toString()).queue();
            }
        }
        
        // Remove New Year's resolution
        if (message.startsWith("/removeresolution")) {
            String resolutionNumberString = message.replace("/removeresolution", "").trim();
            try {
                int resolutionNumber = Integer.parseInt(resolutionNumberString);
                List<String> resolutions = userResolutions.getOrDefault(userId, new ArrayList<>());
                
                if (resolutionNumber > 0 && resolutionNumber <= resolutions.size()) {
                    String removedResolution = resolutions.remove(resolutionNumber - 1); // Remove resolution by index
                    event.getChannel().sendMessage("Your resolution has been removed: " + removedResolution).queue();
                } else {
                    event.getChannel().sendMessage("Resolution number not found! Please check the number and try again.").queue();
                }
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Please provide a valid resolution number.").queue();
            }
        }

        // Party planning ideas
        if (message.equalsIgnoreCase("/party")) {
            handlePartyPlanning(event);
        }

        // Trivia command
        if (message.equalsIgnoreCase("/trivia")) {
            askTriviaQuestion(event);
        }

        // Leaderboard command
        if (message.equalsIgnoreCase("/leaderboard")) {
            displayLeaderboard(event);
        }

        // Handle trivia answer (case-sensitive)
        if (activeTrivia.containsKey(userId) && message.length() == 1) {
            checkTriviaAnswer(event, message.charAt(0));
        }

        // Add and view goals
        if (message.startsWith("/addgoal")) {
            addGoal(event, message);
        } else if (message.startsWith("/removegoal")) {
            removeGoal(event, message);
        } else if (message.equalsIgnoreCase("/viewgoals")) {
            viewGoals(event);
        }

        // Joke and Fun Fact commands
        if (message.equalsIgnoreCase("/joke")) {
            tellJoke(event);
        }

        if (message.equalsIgnoreCase("/fact")) {
            tellFact(event);
        }

        //motivate commands
        if (message.equalsIgnoreCase("/motivate")) {
            Collections.shuffle(motivationalQuotes);
            event.getChannel().sendMessage(motivationalQuotes.get(0)).queue();
        }
    }

    private void startCountdown(MessageReceivedEvent event) {
        Duration duration = Duration.between(LocalDateTime.now(), newYearEve);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        String countdownMessage = String.format("Time left until 2025: %d days, %d hours, %d minutes, and %d seconds!", 
                days, hours, minutes, seconds);
        event.getChannel().sendMessage(countdownMessage).queue();
    }

    private void handlePartyPlanning(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Here are 5 random New Year's party ideas:").queue();
        Collections.shuffle(partyIdeas);
        for (int i = 0; i < 5 && i < partyIdeas.size(); i++) {
            event.getChannel().sendMessage(partyIdeas.get(i)).queue();
        }
    }

    private void loadTriviaQuestions() {
        triviaQuestions.add(new TriviaQuestion("What is the capital of France?", List.of("A) Paris", "B) London", "C) Berlin", "D) Rome"), 'A'));
        triviaQuestions.add(new TriviaQuestion("Who wrote 'Romeo and Juliet'?", List.of("A) Charles Dickens", "B) William Shakespeare", "C) Mark Twain", "D) Jane Austen"), 'B'));
        triviaQuestions.add(new TriviaQuestion("Which planet is known as the Red Planet?", List.of("A) Venus", "B) Saturn", "C) Mars", "D) Jupiter"), 'C'));
        triviaQuestions.add(new TriviaQuestion("Which year did World War II end?", List.of("A) 1944", "B) 1945", "C) 1950", "D) 1960"), 'B'));
        triviaQuestions.add(new TriviaQuestion("What is the largest ocean on Earth?", List.of("A) Atlantic", "B) Indian", "C) Arctic", "D) Pacific"), 'D'));
        triviaQuestions.add(new TriviaQuestion("Who painted the Mona Lisa?", List.of("A) Vincent Van Gogh", "B) Pablo Picasso", "C) Leonardo da Vinci", "D) Claude Monet"), 'C'));
        triviaQuestions.add(new TriviaQuestion("What is the chemical symbol for water?", List.of("A) H2O", "B) O2", "C) CO2", "D) N2"), 'A'));
        triviaQuestions.add(new TriviaQuestion("What is the tallest mountain in the world?", List.of("A) Everest", "B) K2", "C) Kilimanjaro", "D) Elbrus"), 'A'));
        triviaQuestions.add(new TriviaQuestion("What is the smallest country in the world?", List.of("A) Monaco", "B) Vatican City", "C) Nauru", "D) San Marino"), 'B'));
        triviaQuestions.add(new TriviaQuestion("What is the currency of Japan?", List.of("A) Yen", "B) Dollar", "C) Euro", "D) Peso"), 'A'));
        triviaQuestions.add(new TriviaQuestion("Which company created the iPhone?", List.of("A) Samsung", "B) Apple", "C) Nokia", "D) Microsoft"), 'B'));
        triviaQuestions.add(new TriviaQuestion("What is the tallest animal in the world?", List.of("A) Elephant", "B) Giraffe", "C) Rhino", "D) Camel"), 'B'));
        triviaQuestions.add(new TriviaQuestion("What is the hottest planet in our solar system?", List.of("A) Mercury", "B) Venus", "C) Earth", "D) Mars"), 'B'));
        triviaQuestions.add(new TriviaQuestion("What is the national animal of Canada?", List.of("A) Beaver", "B) Moose", "C) Bear", "D) Eagle"), 'A'));
        triviaQuestions.add(new TriviaQuestion("What is the capital of Japan?", List.of("A) Tokyo", "B) Osaka", "C) Kyoto", "D) Sapporo"), 'A'));
    }

    private void loadPartyIdeas() {
        partyIdeas.add("Have a themed costume party.");
        partyIdeas.add("Host a virtual New Year’s Eve trivia game.");
        partyIdeas.add("Plan a New Year’s Eve movie marathon.");
        partyIdeas.add("Have a karaoke night.");
        partyIdeas.add("Create a DIY photo booth for your guests.");
        partyIdeas.add("Set up a New Year’s resolution board for everyone.");
        partyIdeas.add("Host a cocktail-making contest.");
        partyIdeas.add("Play a New Year’s Eve bingo game.");
        partyIdeas.add("Create a vision board for the new year.");
        partyIdeas.add("Plan a DIY spa night.");
        partyIdeas.add("Organize a scavenger hunt.");
        partyIdeas.add("Host a potluck dinner with a New Year’s theme.");
        partyIdeas.add("Create a 'time capsule' to open next year.");
        partyIdeas.add("Set up a new year's eve playlist for dancing.");
        partyIdeas.add("Decorate your home with a New Year's Eve theme.");
    }

    private void loadJokes() {
        jokes.add("Why did the New Year's resolution break up? Because it couldn't keep its promises!");
        jokes.add("What do you call a snowman with a six-pack? An abdominal snowman!");
        jokes.add("Why don’t skeletons fight each other? They don’t have the guts.");
        jokes.add("Why can't you give Elsa a balloon? Because she will let it go!");
        jokes.add("What do you get when you cross a snowman and a vampire? Frostbite!");
        jokes.add("Why did the computer go to the doctor? Because it had a virus!");
        jokes.add("What do you call fake spaghetti? An impasta!");
        jokes.add("Why don’t oysters share their pearls? Because they’re shellfish.");
        jokes.add("What do you get when you cross a snowman with a dog? Frostbite!");
        jokes.add("Why did the bicycle fall over? It was two-tired.");
        jokes.add("Why did the tomato turn red? Because it saw the salad dressing!");
        jokes.add("How does a penguin build its house? Igloos it together!");
        jokes.add("What do you get if you cross a fish with an elephant? Swimming trunks!");
        jokes.add("Why was the math book sad? Because it had too many problems.");
        jokes.add("Why did the scarecrow win an award? Because he was outstanding in his field!");
    }

    private void loadFunFacts() {
        funFacts.add("Did you know that New Year’s Eve is celebrated in different ways all over the world?");
        funFacts.add("In Japan, it is a tradition to eat soba noodles on New Year’s Eve for longevity.");
        funFacts.add("The first New Year’s Eve ball drop in Times Square was held in 1907.");
        funFacts.add("The New Year’s Eve ball weighs 11,875 pounds and is 12 feet in diameter.");
        funFacts.add("In Spain, it’s a tradition to eat 12 grapes at the stroke of midnight for good luck.");
        funFacts.add("In Greece, people throw pomegranates on the ground for luck on New Year’s Eve.");
        funFacts.add("The earliest known New Year’s celebration dates back to 2000 BC in Mesopotamia.");
        funFacts.add("The tradition of New Year’s resolutions dates back to ancient Babylon.");
        funFacts.add("In Scotland, New Year's Eve is known as Hogmanay.");
        funFacts.add("The first New Year's Eve ball in Times Square was made of iron and wood.");
        funFacts.add("Fireworks on New Year’s Eve symbolize the hope for a brighter future.");
        funFacts.add("The Chinese New Year is celebrated in February and follows the lunar calendar.");
        funFacts.add("The tradition of watching fireworks on New Year's Eve started in ancient China.");
        funFacts.add("New Year's Day was originally celebrated on March 25th in medieval Europe.");
        funFacts.add("In Denmark, people throw old dishes at friends' doors for good luck.");
    }

    private void askTriviaQuestion(MessageReceivedEvent event) {
        if (triviaQuestions.isEmpty()) {
            event.getChannel().sendMessage("No trivia questions available!").queue();
            return;
        }
        Collections.shuffle(triviaQuestions);
        TriviaQuestion question = triviaQuestions.get(0);
        activeTrivia.put(event.getAuthor().getId(), question);

        StringBuilder questionMessage = new StringBuilder(question.getQuestion());
        for (String option : question.getOptions()) {
            questionMessage.append("\n").append(option);
        }
        questionMessage.append("\nPlease reply with the correct answer (A, B, C, or D):");
        event.getChannel().sendMessage(questionMessage.toString()).queue();
    }

    private void checkTriviaAnswer(MessageReceivedEvent event, char answer) {
        TriviaQuestion currentTrivia = activeTrivia.get(event.getAuthor().getId());
        if (currentTrivia != null) {
            // Convert both the user's answer and the correct answer to lowercase
            if (Character.toLowerCase(answer) == Character.toLowerCase(currentTrivia.getAnswer())) {
                triviaScores.put(event.getAuthor().getId(), triviaScores.getOrDefault(event.getAuthor().getId(), 0) + 1);
                event.getChannel().sendMessage("Correct! 🎉 Your score is now " + triviaScores.get(event.getAuthor().getId())).queue();
            } else {
                event.getChannel().sendMessage("Wrong answer! The correct answer was: " + currentTrivia.getAnswer()).queue();
            }
            activeTrivia.remove(event.getAuthor().getId());
        } else {
            event.getChannel().sendMessage("No active trivia question found for you!").queue();
        }
    }
    

    private void addGoal(MessageReceivedEvent event, String message) {
        String goal = message.replace("/addgoal ", "").trim();
        userGoals.putIfAbsent(event.getAuthor().getId(), new ArrayList<>());
        List<String> goals = userGoals.get(event.getAuthor().getId());
        goals.add("Goal " + (goals.size() + 1) + ": " + goal); // Adds the goal with the correct number
        event.getChannel().sendMessage("Your goal has been added: Goal " + goals.size() + ": " + goal).queue();
    }
    

    private void removeGoal(MessageReceivedEvent event, String message) {
        String goalNumberString = message.replace("/removegoal ", "").trim();
        List<String> goals = userGoals.get(event.getAuthor().getId());
        
        if (goals != null) {
            int goalNumber;
            try {
                goalNumber = Integer.parseInt(goalNumberString.replaceAll("[^0-9]", "")); // Extracts the goal number
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Invalid goal number! Please specify the goal number like 'Goal 1'.").queue();
                return;
            }
    
            if (goalNumber > 0 && goalNumber <= goals.size()) {
                String removedGoal = goals.remove(goalNumber - 1); // Removes goal by index
                event.getChannel().sendMessage("Your goal has been removed: " + removedGoal).queue();
            } else {
                event.getChannel().sendMessage("Goal not found with number: " + goalNumber).queue();
            }
        }
    }
    

    private void viewGoals(MessageReceivedEvent event) {
        List<String> goals = userGoals.getOrDefault(event.getAuthor().getId(), new ArrayList<>());
        if (goals.isEmpty()) {
            event.getChannel().sendMessage("You have no goals set. Use `/addgoal` to add a goal!").queue();
        } else {
            StringBuilder goalsMessage = new StringBuilder("Your goals:\n");
            for (int i = 0; i < goals.size(); i++) {
                goalsMessage.append(goals.get(i)).append("\n"); // Lists each goal with its number
            }
            event.getChannel().sendMessage(goalsMessage.toString()).queue();
        }
    }


    private void loadMotivationalQuotes() {
        motivationalQuotes.add("Believe in yourself and all that you are. Know that there is something inside you that is greater than any obstacle.");
        motivationalQuotes.add("The only way to achieve the impossible is to believe it is possible.");
        motivationalQuotes.add("Don't watch the clock; do what it does. Keep going.");
        motivationalQuotes.add("The future depends on what you do today.");
        motivationalQuotes.add("Success is not the key to happiness. Happiness is the key to success.");
        motivationalQuotes.add("Your limitation—it's only your imagination.");
        motivationalQuotes.add("Dream big and dare to fail.");
        motivationalQuotes.add("Push yourself, because no one else is going to do it for you.");
        motivationalQuotes.add("Great things never come from comfort zones.");
        motivationalQuotes.add("Stay positive, work hard, make it happen.");
    }
    

    private void displayLeaderboard(MessageReceivedEvent event) {
        StringBuilder leaderboardMessage = new StringBuilder("Trivia Leaderboard:\n");
        triviaScores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> {
                    String userId = entry.getKey();
                    int score = entry.getValue();
                    String username;
                    try {
                        // Blocking call to fetch user from Discord API
                        username = event.getJDA().retrieveUserById(userId).complete().getName();
                    } catch (Exception e) {
                        username = "Unknown User"; // If user can't be fetched
                    }
                    leaderboardMessage.append(username).append(": ").append(score).append(" points\n");
                });
        event.getChannel().sendMessage(leaderboardMessage.toString()).queue();
    }
    

    private void tellJoke(MessageReceivedEvent event) {
        Collections.shuffle(jokes);
        event.getChannel().sendMessage(jokes.get(0)).queue();
    }

    private void tellFact(MessageReceivedEvent event) {
        Collections.shuffle(funFacts);
        event.getChannel().sendMessage(funFacts.get(0)).queue();
    }

    private static class TriviaQuestion {
        private final String question;
        private final List<String> options;
        private final char answer;

        public TriviaQuestion(String question, List<String> options, char answer) {
            this.question = question;
            this.options = options;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public List<String> getOptions() {
            return options;
        }

        public char getAnswer() {
            return answer;
        }
    }
}
