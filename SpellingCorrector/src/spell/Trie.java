package spell;

public class Trie implements ITrie{

  private Node root = new Node();
  private int wordCount = 0;
  private int nodeCount = 1;

  @Override
  public void add(String word) {
    if (find(word) == null) {
      addHelper(root, word);
    } else {
      INode n = find(word);
      n.incrementValue();
    }
  }
  private void addHelper(INode n, String wordToAdd) {

    if (wordToAdd.length() == 0) {
      n.incrementValue();
      wordCount += 1;
      return;
    }
    int index = wordToAdd.charAt(0) - 'a';
    if (n.getChildren()[index] == null) {
      Node addedNode = new Node();
      n.getChildren()[index] = addedNode;
      nodeCount += 1;
      addHelper(n.getChildren()[index], wordToAdd.substring(1, wordToAdd.length()));
    } else {
      addHelper(n.getChildren()[index], wordToAdd.substring(1, wordToAdd.length()));
    }
  }

  @Override
  public INode find(String word) {
    return findHelper(root, word);
  }

  private INode findHelper(INode n, String word) {
    if (n.getValue() == 0 && word.length() <= 0) {
      return null;
    } else if (word.length() <= 0) {
      return n;
    }
    int initialIndex = word.charAt(0) - 'a';
    if (n.getChildren()[initialIndex] == null) {
      return null;
    } else {
      return findHelper(n.getChildren()[initialIndex], word.substring(1,word.length()));
    }
  }


  @Override
  public int getWordCount() {
    return wordCount;
  }

  @Override
  public int getNodeCount() {
    return nodeCount;
  }

  public String toString() {
    StringBuilder currentWord = new StringBuilder();
    StringBuilder output = new StringBuilder();
    toStringHelper(root, currentWord, output);
    return output.toString();
  }

  private void toStringHelper(INode n, StringBuilder currentWord, StringBuilder output) {
    if (n.getValue() > 0) {
      output.append(currentWord.toString());
      output.append("\n");
    }
    for (int i = 0; i < n.getChildren().length; i++) {
      INode child = n.getChildren()[i];
      if (child != null) {
        char childLetter = (char)('a' + i);
        currentWord.append(childLetter);
        toStringHelper(child, currentWord, output);
        currentWord.deleteCharAt(currentWord.length() - 1);
      }
    }
  }

  public boolean equals(Object trie) {
    if (this == trie) {
      //System.out.println("Returning true because of same pointer type");
      return true;
    } else if (trie.getClass() != this.getClass()) {
      //System.out.println("Returning false because of class type");
      return false;
    }

    Trie t = (Trie)trie;
    if (t.getNodeCount() != this.getNodeCount() || t.getWordCount() != this.getWordCount()) {
      //System.out.println("Returning false because of word and node count");
      return false;
    }

    return equalsHelper(this.root, t.root);
  }

  private boolean equalsHelper(INode n1, INode n2) {
    if (n1.getChildren().length != n2.getChildren().length) {
      //System.out.println("Returning false because of children length");
      return false;
    }
    for (int i = 0; i < n1.getChildren().length; i++) {
      if ((n1.getChildren()[i] != null && n2.getChildren()[i] != null) && (n1.getChildren()[i].getValue() != n2.getChildren()[i].getValue())) {
        //System.out.println("Returning false because of difference in children");
        //System.out.println("Here are the differing values: " + n1.getChildren()[i] + ", " + n2.getChildren()[i]);
        return false;
      }
    }
    boolean isNotEqual = false;
    for (int i = 0; i < n1.getChildren().length; i++) {
      if (n1.getChildren()[i] != null) {
        isNotEqual = equalsHelper(n1.getChildren()[i], n2.getChildren()[i]);
        if (isNotEqual == false) {
          return false;
        }
      }
    }
    return true;
  }

  public int hashCode() {
    int totalIndexMultiplier = 1;
    for (int i = 0; i < root.getChildren().length; i++) {
      if (root.getChildren()[i] != null) {
        totalIndexMultiplier = totalIndexMultiplier * i;
      }
    }
    return totalIndexMultiplier * wordCount * nodeCount;
  }


}
