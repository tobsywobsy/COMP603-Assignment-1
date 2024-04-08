import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class BlackJack {
    private static class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) {
                if (value.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);
        }

        public boolean isAce() {
            return value.equals("A");
        }

        @Override
        public String toString() {
            return "[" + value + "-" + type + "]";
        }
    }
    
    //deck
    static ArrayList<Card> deck;
    static Scanner scanner = new Scanner(System.in);
    static String playerName;
    static int playerWins;

    //dealer
    static ArrayList<Card> dealerHand;
    static int dealerSum;
    static int dealerAceCount;

    //player
    static ArrayList<Card> playerHand;
    static int playerSum;
    static int playerAceCount;

    static boolean gameInProgress = true;

    public static void main(String[] args) {
        getPlayerName();

        while (gameInProgress) {
            startGame();

            //player turn
            while (playerSum < 21) {
                printDealerHand(true); //dealer's hidden card
                printPlayerHand();
                System.out.println("Your current total: " + playerSum);
                System.out.println("Hit (h) or Stand (s)?");
                String choice = scanner.nextLine().toLowerCase();
                if (choice.equals("h")) {
                    hit(playerHand);
                } else if (choice.equals("s")) {
                    break;
                }
            }

            //dealer turn
            while (dealerSum < 17) {
                hit(dealerHand);
            }

            //determine the winner
            determineWinner();
            gameInProgress = false; //disable further interactions after determining the winner

            //ask if the user wants to play again
            System.out.println("Do you want to play again? (yes/no)");
            String playAgain = scanner.nextLine().toLowerCase();
            if (playAgain.equals("yes")) {
                gameInProgress = true; //continues game
            }
        }

        System.out.println("Thanks for playing!");
    }

    public static void getPlayerName() {
        System.out.println("Enter your name:");
        playerName = scanner.nextLine();
        //loads player's wins
        playerWins = loadPlayerWins(playerName);
    }

    public static void startGame() {
        //deck
        buildDeck();
        shuffleDeck();

        //dealer
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        //deal cards to dealer
        for (int i = 0; i < 2; i++) {
            Card card = deck.remove(deck.size() - 1);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerHand.add(card);
        }

        //player
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        //deal cards to player
        for (int i = 0; i < 2; i++) {
            Card card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
    }
    
    //card values
    public static void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                Card card = new Card(value, type);
                deck.add(card);
            }
        }
    }
    
    //shuffles deck
    public static void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public static void hit(ArrayList<Card> hand) {
        Card card = deck.remove(deck.size() - 1);
        hand.add(card);
        int value = card.getValue();
        if (hand == playerHand) {
            playerSum += value;
            playerAceCount += card.isAce() ? 1 : 0;
            reduceAce(playerSum, playerAceCount); // Call reduceAce after updating the sum
        } else {
            dealerSum += value;
            dealerAceCount += card.isAce() ? 1 : 0;
            reduceAce(dealerSum, dealerAceCount); // Call reduceAce after updating the sum
        }
    }

    public static void reduceAce(int sum, int aceCount) {
        while (sum > 21 && aceCount > 0) {
            sum -= 10;
            aceCount--;
        }
    }

    public static void determineWinner() {
        printDealerHand(false); //reveal dealer's hidden card at end of game
        printPlayerHand();

        if (playerSum > 21) {
            System.out.println("You busted. Dealer wins.");
            updatePlayerWins(playerName, false);
        } else if (dealerSum > 21 || playerSum > dealerSum) {
            System.out.println("You win!");
            updatePlayerWins(playerName, true);
        } else if (playerSum == dealerSum) {
            System.out.println("It's a tie.");
        } else {
            System.out.println("Dealer wins.");
            updatePlayerWins(playerName, false);
        }
    }

    public static void printDealerHand(boolean hideCard) {
        if (hideCard) {
            System.out.println("Dealer's hand: [" + dealerHand.get(0) + "], [Hidden]");
        } else {
            System.out.println("Dealer's hand: " + dealerHand);
        }
    }

    public static void printPlayerHand() {
        System.out.println("Your hand: " + playerHand);
    }

    public static int loadPlayerWins(String playerName) {
        try {
            File file = new File("./resources/blackjack.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(playerName)) {
                    return Integer.parseInt(data[1]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void updatePlayerWins(String playerName, boolean won) {
        try {
            File file = new File("./resources/blackjack.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            boolean found = false;
            for (String l : lines) {
                String[] data = l.split(",");
                if (data[0].equals(playerName)) {
                    found = true;
                    int wins = Integer.parseInt(data[1]);
                    if (won) {
                        wins++;
                    }
                    writer.write(playerName + "," + wins + "\n");
                } else {
                    writer.write(l + "\n");
                }
            }
            if (!found) {
                if (won) {
                    writer.write(playerName + ",1\n");
                } else {
                    writer.write(playerName + ",0\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
