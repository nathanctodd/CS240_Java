package spell;

public class Node implements INode {

  private int counter = 0;
  private String value;
  private Node[] children = new Node[26];

  @Override
  public int getValue() {
    return counter;
  }

  @Override
  public void incrementValue() {
    counter++;
  }

  @Override
  public INode[] getChildren() {
    return children;
  }

}
