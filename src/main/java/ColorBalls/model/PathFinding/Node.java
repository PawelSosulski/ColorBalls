package ColorBalls.model.PathFinding;

public class Node {

    int x;
    int y;
    double hValue;
    int gValue;
    double fValue;
    Node parent;


    public Node(int y, int x) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                /*", hValue=" + hValue +
                ", gValue=" + gValue +
                ", fValue=" + fValue +
                ", parent=" + parent +*/
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (x != node.x) return false;
        return y == node.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}