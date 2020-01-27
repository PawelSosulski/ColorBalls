package ColorBalls.controllers;

import ColorBalls.model.Board;
import ColorBalls.model.Coordinances;
import ColorBalls.model.FieldStatus;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class GuiUpdate implements Runnable {

    private GridController gridController;
    private GridPane grid;
    private Board gameLogic;
    private List<Coordinances> toAdd = new ArrayList<>();
    private List<Coordinances> toRemove = new ArrayList<>();
    private FieldStatus[][] previousBoard;


    public GuiUpdate(GridController gridController) {
        this.gridController = gridController;
        this.grid = gridController.getGrid();
        this.gameLogic = gridController.getGameLogic();
        previousBoard = new FieldStatus[gameLogic.getBoardSize()][gameLogic.getBoardSize()];
        fillPreviusBoard();

    }

    public void fillPreviusBoard() {
        for (int i = 0; i < gameLogic.getBoardSize(); i++) {
            for (int j = 0; j < gameLogic.getBoardSize(); j++) {
                previousBoard[i][j] = gameLogic.getStatus(j, i);
            }
        }
    }

    @Override
    public void run() {
        boolean isUpdateNeeded;
        // fillPreviusBoard();
        try {
            while (true) {
                Thread.sleep(100);
                isUpdateNeeded = gridController.getIsUpdateNeeded();
                if (isUpdateNeeded) {
                    //working
                    //clearGrid();
                    //updateGuiOfGrid();

                    //check
                    findDifference();
                    fillPreviusBoard();
                    update();
                    isUpdateNeeded = !isUpdateNeeded;
                    gridController.setUpdateNeeded(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findDifference() {
        toAdd.clear();
        toRemove.clear();
        gameLogic.showBoard();
        showBoard();
        for (int i = 0; i < gameLogic.getBoardSize(); i++) {
            for (int j = 0; j < gameLogic.getBoardSize(); j++) {
                if (previousBoard[i][j] != gameLogic.getStatus(j, i)) {
                    if (gameLogic.getStatus(j, i) == FieldStatus.EMPTY) {
                        toRemove.add(new Coordinances(j, i));
                        System.out.println("Do usunięcia " + j + "," + i);
                    } else {
                        System.out.println("Do dodania " + j + "," + i);
                        toAdd.add(new Coordinances(j, i));
                    }
                }
            }
        }
    }


    private void update() {
        if (toAdd.size() > 0) {
            toAdd.stream().forEach(e -> {
                ImageView imageView = new ImageView(new Image(
                        getClass().getClassLoader()
                                .getResource(gameLogic.getStatus(e.getX(), e.getY()).getImagePath()).toExternalForm()));
                imageView.setFitHeight(45);
                imageView.setFitWidth(45);
                imageView.setId(e.getX() + "_" + e.getY());
                imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        gridController.imageClickHandler(event);
                    }
                });
                final int row = e.getY();
                final int column = e.getX();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        grid.add(imageView, column, row);
                    }
                });
            });
        }

        if (toRemove.size() > 0) {
            List<Node> nodeToRemove = new ArrayList<>();
            //System.out.println("do usunięia size"+toRemove.size());
            for (int i = toRemove.size() - 1; i >= 0; i--) {
                ObservableList<Node> children = grid.getChildren();
                //if (!children.isEmpty()) {
                try {
                    for (Node node : children) {
                        if (node instanceof ImageView
                                && GridPane.getRowIndex(node) == toRemove.get(i).getY()
                                && GridPane.getColumnIndex(node) == toRemove.get(i).getX()) {
                            nodeToRemove.add(node);
                            System.out.println(node);
                        }
                    }
                    //}
                } catch (ConcurrentModificationException e) {
                    e.printStackTrace();
                    clearGrid();
                    updateGuiOfGrid();
                }
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                 /*   for (int i = nodeToRemove.size()-1;i>=0;i--) {
                        System.out.println(nodeToRemove.get(i));
                        try {
                            grid.getChildren().remove(nodeToRemove.get(i));
                        } catch (ConcurrentModificationException e) {
                            e.printStackTrace();
                            //System.out.println(nodeToRemove.get(i));
                        }
                        System.out.println("Usunięto");
                    }*/
                    nodeToRemove.forEach(node -> {
                        System.out.println(node);
                        grid.getChildren().remove(node);
                        System.out.println("Usunięto");
                    }
                    );
                }
            });
        }
    }


    private void updateGuiOfGrid() {
        for (int i = 0; i < gameLogic.getBoardSize(); i++) {
            for (int j = 0; j < gameLogic.getBoardSize(); j++) {
                if (gameLogic.getStatus(j, i) != FieldStatus.EMPTY) {
                    ImageView imageView = new ImageView(new Image(
                            getClass().getClassLoader()
                                    .getResource(gameLogic.getStatus(j, i).getImagePath()).toExternalForm()));
                    imageView.setFitHeight(45);
                    imageView.setFitWidth(45);
                    imageView.setId(j + "_" + i);
                    imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            gridController.imageClickHandler(event);
                        }
                    });
                    final int row = j;
                    final int column = i;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            grid.add(imageView, row, column);
                        }
                    });

                }
            }
        }
    }


    public void clearGrid() {
        ObservableList<Node> children = grid.getChildren();
        List<Node> nodeToRemove = new ArrayList<>();
        if (!children.isEmpty()) {
            for (Node node : children) {
                if (node instanceof ImageView)
                    nodeToRemove.add(node);
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                nodeToRemove.stream().forEach(e -> grid.getChildren().remove(e));
            }
        });
    }


    public void showBoard() {
        System.out.println("Previous board");
        for (int i = 0; i < previousBoard.length; i++) {
            for (int j = 0; j < previousBoard.length; j++) {
                System.out.print(previousBoard[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }


}
