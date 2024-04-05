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
            if ("AJQK".contains(value)) { // Values A J Q K
                if (value.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value); // Values 2 - 10
        }

        public boolean isAce() {
            return value.equals("A");
        }

        @Override
        public String toString() {
            return "[" + value + "-" + type + "]";
        }
    }

    static ArrayList<Card> deck;
    static Scanner scanner = new Scanner(System.in);

    // Dealer
    static ArrayList<Card> dealerHand;
    static int dealerSum;
    static int dealerAceCount;

    // Player
    static ArrayList<Card> playerHand;
    static int playerSum;
    static int playerAceCount;

    // Flag to track game state
    static boolean gameInProgress = true;

    public static void main(String[] args) {
        while (gameInProgress) {
            startGame();

            // Player's turn
            while (playerSum < 21) {
                printDealerHand(true); // Hide one of the dealer's cards
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

            // Dealer's turn
            while (dealerSum < 17) {
                hit(dealerHand);
            }

            // Determine winner
            determineWinner();
            gameInProgress = false; // Disable further interactions after determining the winner

            // Ask if the user wants to play again
            System.out.println("Do you want to play again? (yes/no)");
            String playAgain = scanner.nextLine().toLowerCase();
            if (playAgain.equals("yes")) {
                gameInProgress = true; // Set the game to continue
            }
        }

        System.out.println("Thanks for playing!");
    }

    public static void startGame() {
        // Deck
        buildDeck();
        shuffleDeck();

        // Dealer
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        // Deal cards to dealer
        for (int i = 0; i < 2; i++) {
            Card card = deck.remove(deck.size() - 1);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerHand.add(card);
        }

        // Player
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        // Deal two cards to player
        for (int i = 0; i < 2; i++) {
            Card card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
    }

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
            reduceAce(playerSum, playerAceCount);
        } else {
            dealerSum += value;
            dealerAceCount += card.isAce() ? 1 : 0;
            reduceAce(dealerSum, dealerAceCount);
        }
    }

    public static void reduceAce(int sum, int aceCount) {
        while (sum > 21 && aceCount > 0) {
            sum -= 10;
            aceCount--;
        }
    }

    public static void determineWinner() {
        printDealerHand(false); // Reveal dealer's hidden card
        printPlayerHand();

        if (playerSum > 21) {
            System.out.println("You busted. Dealer wins.");
        } else if (dealerSum > 21 || playerSum > dealerSum) {
            System.out.println("You win!");
        } else if (playerSum == dealerSum) {
            System.out.println("It's a tie.");
        } else {
            System.out.println("Dealer wins.");
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
}
