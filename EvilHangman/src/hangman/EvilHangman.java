package hangman;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeSet;



public class EvilHangman {

    public static void main(String[] args) throws EmptyDictionaryException, IOException {

        Boolean keepPlaying = true;
        Integer wordLength = 0;
        Integer guesses = 0;
        try {
            wordLength = Integer.parseInt(args[1]);
            guesses = Integer.parseInt(args[2]);
        } catch(NumberFormatException e) {
            System.out.println("There was an error: argument passed not valid");
            keepPlaying = false;
        }
        if (args[0].getClass() != String.class) {
            System.out.println("There was an error: argument passed not valid");
            keepPlaying = false;
        }
        String filePath = args[0];
        File file = new File(filePath);
        EvilHangmanGame hangmanGame = new EvilHangmanGame();
        hangmanGame.setGuesses(guesses);
        hangmanGame.setLengthOfWord(wordLength);


        while(keepPlaying == true) {
            try {
                hangmanGame.startGame(file, wordLength);
                hangmanGame.setGuesses(guesses);
            } catch (EmptyDictionaryException e) {

            }

            //System.out.println(hangmanGame.getSubsetKey("Nathan", 'a'));
            //hangmanGame.setCurrentWord(hangmanGame.getSubsetKey("Nathan", 'a'));
            while (hangmanGame.getGuesses() > 0) {
                hangmanGame.printHangman();
                hangmanGame.getNextGuess();
            }


            Boolean playAgain = true;
            while (playAgain == true) {
                System.out.print("Would you like to play again? (Y/N): ");
                Scanner scan = new Scanner(System.in);
                String next = scan.next();
                next = next.toLowerCase(Locale.ROOT);
                if (next.equals("y") || next.equals("yes")) {
                    playAgain = false;
                    keepPlaying = true;
                } else if (next.equals("n") || next.equals("no")) {
                    playAgain = false;
                    keepPlaying = false;
                } else {
                    System.out.println("Invalid selection.");
                    playAgain = true;
                    keepPlaying = true;
                }
            }
        }






    }
}
