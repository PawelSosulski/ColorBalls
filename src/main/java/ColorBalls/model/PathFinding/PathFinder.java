package ColorBalls.model.PathFinding;

import ColorBalls.model.Board;
import ColorBalls.model.Coordinances;
import ColorBalls.model.FieldStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class PathFinder {

    private PriorityQueue<Node> openList = new PriorityQueue<>(11, new Comparator() {
        @Override
        //Compares 2 Node objects stored in the PriorityQueue and Reorders the Queue according to the object which has the lowest fValue
        public int compare(Object cell1, Object cell2) {
            return ((Node) cell1).fValue < ((Node) cell2).fValue ? -1 :
                    ((Node) cell1).fValue > ((Node) cell2).fValue ? 1 : 0;
        }
    });
    private ArrayList<Node> closedList = new ArrayList<>();
    private ArrayList<Node> pathList = new ArrayList<>();
    boolean[][] matrix;
    private int moveCost = 1;
    private Board board;

    public PathFinder(Board board) {;
    this.board = board;
    }

    public ArrayList<Node> getPathList() {
        return pathList;
    }

    public boolean isPathPossible( Coordinances startPoint, Coordinances endPoint) {
        boolean[][] matrix = fillMatrix();
        generateHValue(matrix, startPoint, endPoint);
        if (isInArray(pathList, endPoint))
            return true;
        else
            return false;
    }


    private boolean[][] fillMatrix() {
        int size = board.getBoardSize();
        matrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.getStatus(j, i) == FieldStatus.EMPTY) {
                    matrix[i][j] = true;
                } else {
                    matrix[i][j] = false;
                }
            }
        }
        return matrix;
    }

    private void generateHValue(boolean matrix[][], Coordinances startPoint, Coordinances endPoint) {
        matrix[startPoint.getY()][startPoint.getX()] = true;
        Node[][] cell = new Node[matrix.length][matrix.length];
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix.length; x++) {
                //Creating a new Node object for each and every Cell of the Grid (Matrix)
                cell[y][x] = new Node(y, x);
                //Checks whether a cell is Blocked or Not by checking the boolean value
                if (matrix[y][x]) {
                    //Assigning the Manhattan Heuristic value by calculating the absolute length (x+y) from the ending point to the starting point
                    cell[y][x].hValue = Math.abs(y - endPoint.getY()) + Math.abs(x - endPoint.getX());
                    //Assigning the Chebyshev Heuristic value
                        /*if (Math.abs(y - Bi) > Math.abs(x - Bj)) {
                            cell[y][x].hValue = Math.abs(y - Bi);
                        } else {
                            cell[y][x].hValue = Math.abs(x - Bj);
                        }*/
                    //Assigning the Euclidean Heuristic value
                    // cell[y][x].hValue = Math.sqrt(Math.pow(y - Bi, 2) + Math.pow(x - Bj, 2));
                } else {
                    //If the boolean value is false, then assigning -1 instead of the absolute length
                    cell[y][x].hValue = -1;
                }
            }
        }
        generatePath(cell, startPoint, endPoint);
    }


    private void generatePath(Node cell[][], Coordinances startPoint, Coordinances endPoint) {
        pathList.clear();
        openList.clear();
        //Adds the Starting cell inside the openList
        openList.add(cell[startPoint.getY()][startPoint.getX()]);
        //Executes the rest if there are objects left inside the PriorityQueue
        while (true) {
            //Gets and removes the objects that's stored on the top of the openList and saves it inside node
            Node node = openList.poll();
            //Checks if whether node is empty and f it is then breaks the while loop
            if (node == null) {
                break;
            }
            //Checks if whether the node returned is having the same node object values of the ending point
            //If it des then stores that inside the closedList and breaks the while loop
            if (node.equals(cell[endPoint.getY()][endPoint.getX()])) {
                closedList.add(node);
                break;
            }
            closedList.add(node);
            //Left Cell
            findWay(cell, node, -1, 0);
            //Right Cell
            findWay(cell, node, 1, 0);
            //Bottom Cell
            findWay(cell, node, 0, 1);
            //Top Cell
            findWay(cell, node, 0, -1);
        }
        //Assigns the last Object in the closedList to the endNode variable
        Node endNode = closedList.get(closedList.size() - 1);
        //Checks if whether the endNode variable currently has a parent Node. if it doesn't then stops moving forward.
        //Stores each parent Node to the PathList so it is easier to trace back the final path
        while (endNode.parent != null) {
            Node currentNode = endNode;
            pathList.add(currentNode);
            endNode = endNode.parent;
        }
        pathList.add(cell[startPoint.getY()][startPoint.getX()]);
        //Clears the openList
        openList.clear();
        closedList.clear();
    }


    private void findWay(Node[][] cell, Node node, int dx, int dy) {
        try {
            if (cell[node.y + dy][node.x + dx].hValue != -1
                    && !openList.contains(cell[node.y + dy][node.x + dx])
                    && !closedList.contains(cell[node.y + dy][node.x + dx])) {
                double tCost = node.fValue + moveCost;
                cell[node.y + dy][node.x + dx].gValue = moveCost;
                double cost = cell[node.y + dy][node.x + dx].hValue + tCost;
                if (cell[node.y + dy][node.x + dx].fValue > cost || !openList.contains(cell[node.y + dy][node.x + dx]))
                    cell[node.y + dy][node.x + dx].fValue = cost;
                openList.add(cell[node.y + dy][node.x + dx]);
                cell[node.y + dy][node.x + dx].parent = node;
            }
        } catch (IndexOutOfBoundsException e) {
        }
    }

    private boolean isInArray(List<Node> list, Coordinances point) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).x == point.getX() && list.get(i).y == point.getY())
                return true;
        }
        return false;
    }

}
