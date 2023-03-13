package hangman;

import com.sun.source.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.lang.*;

public class EvilHangmanGame  implements IEvilHangmanGame {

  private HashSet<String> dictionarySet = new HashSet<String>();
  private HashSet<String> wordSet = new HashSet<String>();
  private SortedSet<Character> guessedLetters = new TreeSet<Character>();
  private HashMap<String, HashSet<String> > partitions = new HashMap<String, HashSet<String>>();
  private String currentWord = "";
  private char currentChar = 'a';
  private int guessesRemaining;
  private int lengthOfWord;

  public EvilHangmanGame() {}

  public void setGuesses(int guessesRemaining) { this.guessesRemaining = guessesRemaining; }
  public void setLengthOfWord(int lengthOfWord) { this.lengthOfWord = lengthOfWord; }
  public int getGuesses() { return guessesRemaining; }
  public int getLengthOfWord() { return lengthOfWord; }
  public void setCurrentWord(String word) { currentWord = word; }

  @Override
  public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
    if (wordLength <= 0 ) {
      throw new EmptyDictionaryException();
    }

    if (dictionarySet.size() != 0) {
      dictionarySet.clear();
    }
    if (wordSet.size() != 0) {
      wordSet.clear();
    }
    if (guessedLetters.size() != 0) {
      guessedLetters.clear();
    }
    if (partitions.size() != 0) {
      partitions.clear();
    }

    Scanner scanner = null;
        scanner = new Scanner(dictionary);
        if (!scanner.hasNext()) {
          throw new EmptyDictionaryException();
        }
        while(scanner.hasNext()) {
          String nextWord = scanner.next();
          dictionarySet.add(nextWord);
        }
        scanner.close();
      lengthOfWord = wordLength;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < wordLength; i++) {
        sb.append("-");
      }
      currentWord = sb.toString();

      for (String word : dictionarySet) {
        if (word.length() == lengthOfWord) {
          wordSet.add(word);
        }
      }

      if (wordSet.size() <= 0 || dictionarySet.size() <= 0) {
        throw new EmptyDictionaryException();
      }

  }

  public void partitionWordSet() {
    if (partitions != null) {
      partitions.clear();
    }

    for (String word : wordSet) {
      String subsetKey = getSubsetKey(word, currentChar);
      if (partitions.get(subsetKey) != null) {
        HashSet<String> addedSet = partitions.get(subsetKey);
        addedSet.add(word);
        partitions.put(subsetKey, addedSet);
      } else {
        HashSet<String> addedSet = new HashSet<String>();
        addedSet.add(word);
        partitions.put(subsetKey, addedSet);
      }
    }

    String largestKey = null;
    for (Map.Entry<String, HashSet<String>> entry : partitions.entrySet()) {
      if (largestKey == null) {
        largestKey = entry.getKey();
        continue;
      }
      if (entry.getValue().size() > partitions.get(largestKey).size()) {
        largestKey = entry.getKey();
      } else if (entry.getValue().size() == partitions.get(largestKey).size()) {
        if (getLetter(entry.getKey()) < getLetter(largestKey)) {
          largestKey = entry.getKey();
        } else if (getLetter(entry.getKey()) > getLetter(largestKey)) {
          largestKey = largestKey;
        } else {
          Boolean largestFound = false;
          int currentIndex = lengthOfWord - 1;
          while (largestFound != true) {
            if (entry.getKey().charAt(currentIndex) == '-' && largestKey.charAt(currentIndex) != '-') {
              largestKey = largestKey;
              largestFound = true;
            } else if (entry.getKey().charAt(currentIndex) != '-' && largestKey.charAt(currentIndex) == '-') {
              largestKey = entry.getKey();
              largestFound = true;
            }
            currentIndex -= 1;
            if (currentIndex < 0) {
              largestKey = entry.getKey();
              largestFound = true;
            }
          }
        }
      }
    }
    wordSet = partitions.get(largestKey);
    String newWord = combineStrings(currentWord, largestKey);
    if (newWord.equals(currentWord)) {
      System.out.println("Sorry, there are no " + currentChar + "\n");
      guessesRemaining--;
    } else {
      System.out.println("Yes, there is " + getLetter(largestKey) + " " + currentChar);
    }
    System.out.println("\n");
    currentWord = combineStrings(currentWord, largestKey);
    if (getLetter(currentWord) == lengthOfWord) {
      System.out.println("Congrats you win! The word was \"" + currentWord + "\"");
    } else if (guessesRemaining <= 0) {
      System.out.println("Sorry, you lost! The word was \"" + getRandomWord() + "\"");
    }
  }

  public String combineStrings(String current, String other) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < lengthOfWord; i++) {
      if (current.charAt(i) != '-') {
        builder.append(current.charAt(i));
      } else if (other.charAt(i) != '-') {
        builder.append(other.charAt(i));
      } else {
        builder.append('-');
      }
    }
    return builder.toString();
  }

  public int getLetter(String string) {
    int totalCount = 0;
    for (int i = 0; i < string.length(); i++) {
      if (string.charAt(i) != '-') {
        totalCount += 1;
      }
    }
    return totalCount;
  }

  public void printHangman() {
    System.out.println("You have " + guessesRemaining + " guesses left");
    //System.out.println(wordSet.toString());
    if (guessedLetters != null) {
      System.out.println("Used letter: " + guessedLetters.toString());
    } else {
      System.out.println("Used letter: ");
    }
    System.out.println("Word: " + currentWord);

  }

  public void getNextGuess() {
    Boolean validGuess = false;
    String nextGuess = null;
    while(validGuess == false) {
      System.out.print("Enter guess: ");
      Scanner scan = new Scanner(System.in);
      nextGuess = scan.next();
      Boolean isAlpha = true;
      Character guessedChar = nextGuess.charAt(0);
      if (!(guessedChar >= 'A' && guessedChar <= 'Z') && !(guessedChar >= 'a' && guessedChar <= 'z')) {
        isAlpha = false;
      }
      if (nextGuess.length() == 1 && isAlpha) {
        validGuess = true;
        guessedChar = java.lang.Character.toLowerCase(guessedChar);
        currentChar = guessedChar;
        if (guessedLetters != null && guessedLetters.contains(guessedChar)) {
          System.out.print("Guess already made!");
          validGuess = false;
        } else {
          guessedLetters.add(guessedChar);
        }
      } else {
        System.out.print("Invalid guess! ");
      }
    }
    partitionWordSet();
  }

  public String getSubsetKey(String word, char guessedLetter) {
    String returnable = word;
    for (int i = 0; i < word.length(); i++) {
      if (returnable.charAt(i) != guessedLetter) {
        returnable = returnable.substring(0, i) + '-' + returnable.substring(i + 1);
      }
    }
    return returnable;
  }

  @Override
  public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
      if (!(guess >= 'A' && guess <= 'Z') && !(guess >= 'a' && guess <= 'z')) {
        System.out.println("Invalid guess!");
        return null;
      }
        guess = java.lang.Character.toLowerCase(guess);
        currentChar = guess;

      if (guessedLetters != null && guessedLetters.contains(guess)) {
        System.out.print("Guess already made!");
        throw new GuessAlreadyMadeException();
      } else {
        guessedLetters.add(guess);
      }
    partitionWordSet();
    return wordSet;
  }

  public String getRandomWord() {
    for (String word : wordSet) {
      return word;
    }
    return "";
  }

  @Override
  public SortedSet<Character> getGuessedLetters() {
    return guessedLetters;
  }
}
