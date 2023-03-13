package spell;

import com.sun.source.tree.Tree;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeSet;

public class SpellCorrector implements ISpellCorrector {

  private Trie dictionary = new Trie();

  @Override
  public void useDictionary(String dictionaryFileName) throws IOException {
    File file = new File(dictionaryFileName);
    Scanner scanner = new Scanner(file);
    while (scanner.hasNext()) {
      String str = scanner.next();
      dictionary.add(str);
    }
  }

  @Override
  public String suggestSimilarWord(String inputWord) {
    inputWord = inputWord.toLowerCase(Locale.ROOT);
    if (dictionary.find(inputWord) != null) {
      //Input word was found in the dictionary
      return inputWord;
    } else {
      //Input word was NOT found in the dictionary and we need to generate a list of potential words.
      TreeSet<String> firstGenWords = findWords(inputWord);
      if (firstGenWords == null) {
        return null;
      }
      String currentWord = "";
      INode suggestedWordNode = null;
      for (String word : firstGenWords) {
        INode currentNode = dictionary.find(word);
        if (currentNode != null) {
          if (suggestedWordNode == null) {
            suggestedWordNode = currentNode;
            currentWord = word;
          } else if (currentNode.getValue() > suggestedWordNode.getValue()){
            suggestedWordNode = currentNode;
            currentWord = word;
          }
        }
      }
      return currentWord;
    }
  }

  private TreeSet<String> findWords(String inputWord) {
    //Create set of first words
    TreeSet<String> firstDegreeWords = new TreeSet();
    //Create each of the first degree words;
    for (int i = 0; i < inputWord.length(); i++) {
      String sub = inputWord.substring(0, i) + inputWord.substring(i + 1);
      firstDegreeWords.add(sub);
    }
    for (int i = 0; i < inputWord.length(); i++) {
      for (int alpha = 0; alpha < 26; alpha++) {
        char addedChar = (char) ('a' + alpha);
        String sub = inputWord.substring(0, i) + addedChar + inputWord.substring(i + 1);
        firstDegreeWords.add(sub);
      }
    }
    for (int i = 0; i < inputWord.length() + 1; i++) {
      for (int alpha = 0; alpha < 26; alpha++) {
        char addedChar = (char) ('a' + alpha);
        String sub = inputWord.substring(0, i) + addedChar + inputWord.substring(i);
        firstDegreeWords.add(sub);
      }
    }
    for (int i = 0; i < inputWord.length() - 1; i++) {
      String sub = inputWord.substring(0, i) + inputWord.substring(i + 1, i + 2) + inputWord.substring(i, i+ 1) + inputWord.substring(i+2);
      firstDegreeWords.add(sub);
    }
    int totalCount = 0;
    for (String word : firstDegreeWords) {
      if (dictionary.find(word) != null) {
        totalCount++;
      }
    }
    if (totalCount == 0) {
      return secondaryTree(firstDegreeWords);
    } else {
      return firstDegreeWords;
    }
  }

  private TreeSet<String> secondaryTree(TreeSet<String> firstGenWords) {
    TreeSet<String> totalStrings = new TreeSet();
    for (String inputWord : firstGenWords) {
      TreeSet<String> firstDegreeWords = new TreeSet();
      //Create each of the first degree words;
      for (int i = 0; i < inputWord.length(); i++) {
        String sub = inputWord.substring(0, i) + inputWord.substring(i + 1);
        firstDegreeWords.add(sub);
      }
      for (int i = 0; i < inputWord.length(); i++) {
        for (int alpha = 0; alpha < 26; alpha++) {
          char addedChar = (char) ('a' + alpha);
          String sub = inputWord.substring(0, i) + addedChar + inputWord.substring(i + 1);
          firstDegreeWords.add(sub);
        }
      }
      for (int i = 0; i < inputWord.length() + 1; i++) {
        for (int alpha = 0; alpha < 26; alpha++) {
          char addedChar = (char) ('a' + alpha);
          String sub = inputWord.substring(0, i) + addedChar + inputWord.substring(i);
          firstDegreeWords.add(sub);
        }
      }
      for (int i = 0; i < inputWord.length() - 1; i++) {
        String sub = inputWord.substring(0, i) + inputWord.substring(i + 1, i + 2) + inputWord.substring(i, i+ 1) + inputWord.substring(i+2);
        firstDegreeWords.add(sub);
      }
      totalStrings.addAll(firstDegreeWords);
    }
    int total = 0;
    for (String word : totalStrings) {
      if (dictionary.find(word) != null) {
        total++;
      }
    }
    if (total == 0) {
      return null;
    } else {
      return totalStrings;
    }

  }


}