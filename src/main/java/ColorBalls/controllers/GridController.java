package ColorBalls.controllers;

import ColorBalls.model.Board;
import ColorBalls.model.Coordinances;
import ColorBalls.model.FieldStatus;
import ColorBalls.model.PathFinding.PathFinder;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.GridPane;


import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class GridController {

    @FXML
    private Button newGameButton;
    @FXML
    private TextField timeField;
    @FXML
    private TextField recordField;
    @FXML
    private TextField scoreField;
    @FXML
    private GridPane grid;
    @FXML
    private GridPane nextBallsGrid;

    private int score;
    private long record = 0;
    private final BlockingQueue<String> timeQueue = new ArrayBlockingQueue<>(1);
    private TimeUpdater timeUpdater;
    private final LongProperty lastUpdate = new SimpleLongProperty();
    private final long minUpdateInterval = 1000; // nanoseconds. Set to higher number to slow output.
    private Board gameLogic;
    private List<FieldStatus> nextMarks = new ArrayList<>();
    private Coordinances ballToMove = null;
    private Coordinances placeToReplaceBall = null;
    private boolean isUpdateNeeded = true;
    private PathFinder pathFinder;
    private Image pathImage = new Image(getClass().getClassLoader().getResource("view/path_move.jpg").toExternalForm());
    private GuiUpdate refreshGui;
    private Thread updateThread;

    @FXML
    public void initialize() {

        gameLogic = new Board();
        pathFinder = new PathFinder(gameLogic);
        prepareInitialGame();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate.get() > minUpdateInterval) {
                    final String timeMessage = timeQueue.poll();
                    if (timeMessage != null) {
                        timeField.setText(timeMessage);
                    }
                    lastUpdate.set(now);
                }
            }
        };
        timer.start();
        launchTime();
    }

    private void prepareInitialGame() {
        refreshGuiContinusly();
        grid.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gridClickHandler(event);
            }
        });
        score = 0;
        isUpdateNeeded = true;
        nextMarks = gameLogic.getNextThreeMarks();
        gameLogic.putMarksInRandomPlace(nextMarks);
        //updateThread.notify();
        nextMarks = gameLogic.getNextThreeMarks();
        gameLogic.putMarksInRandomPlace(nextMarks);
        nextMarks = gameLogic.getNextThreeMarks();
        updateGuiOfNextMarks();
        newGameButton.setDisable(true);
        setScoreField();
        isUpdateNeeded = true;
    }

    void imageClickHandler(MouseEvent event) {
        Node pickResult = event.getPickResult().getIntersectedNode();
        if (pickResult instanceof ImageView) {
            String[] s = pickResult.getId().split("_");
            // System.out.println("ballToMove");
            ballToMove = new Coordinances(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
            //pickResult.setStyle("-fx-border-width: 10; -fx-border-color: #AAAAAA");
        }
    }

    private void gridClickHandler(MouseEvent event) {
        PickResult result = event.getPickResult();
        if (result.getIntersectedNode() instanceof ImageView)
            return;
        if (ballToMove != null) {
            int row = (int) (result.getIntersectedPoint().getX() - 4) / 50;
            int column = (int) (result.getIntersectedPoint().getY() - 4) / 50;
            placeToReplaceBall = new Coordinances(row, column);
            if (pathFinder.isPathPossible(ballToMove, placeToReplaceBall)) {
                // System.out.println("Jest miejsce " + pathFinder.getPathList().size());
                moveBall();
            } else {
                System.out.println("Nie można przesunąć");
            }
        }
    }


    private void showPathBeforeMove(ArrayList<ColorBalls.model.PathFinding.Node> pathList) {
        for (int i = 0; i < pathList.size() - 1; i++) {
            int row = pathList.get(i).getY();
            int column = pathList.get(i).getX();
            System.out.println(i + ", Y: " + row + "X: " + column);
            /*for (Node node : grid.getChildren()) {
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                    break;
                }
            }*/
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = new ImageView(pathImage);
                    imageView.setFitWidth(48);
                    imageView.setFitHeight(48);
                    grid.add(imageView, column, row);
                    sleep(50);
                }
            });

        }
        sleep(100);
        /*for (int i =0;i<pathList.size()-1;i++) {
            int row = pathList.get(i).getY();
            int column = pathList.get(i).getX();
            System.out.println(i + ", Y: " + row + "X: " + column);
            for (Node node : grid.getChildren()) {
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            grid.getChildren().remove(node);
                        }
                    });
                }
            }
        }*/
        sleep(50);
        System.out.println("Zakończone");
    }

    private void moveBall() {
        gameLogic.putMark(gameLogic.getStatus(ballToMove), placeToReplaceBall);
        gameLogic.removeMark(ballToMove);
        //updateThread.notify();
        ballToMove = null;
        isUpdateNeeded = true;
        if (gameLogic.checkIfInRowAndGetCompletMarks()) {
            checkIfScore();
        } else {
            handleWithNextMarks();
        }

    }

    private void handleWithNextMarks() {
        gameLogic.putMarksInRandomPlace(nextMarks);
        // updateThread.notify();
        if (gameLogic.AmountOfEmptyFields() == 0) {
            showFinalAlert();
            finishGame();
        }
        isUpdateNeeded = true;
        nextMarks = gameLogic.getNextThreeMarks();
        updateGuiOfNextMarks();
        checkIfScore();
    }

    private void finishGame() {
        //todo
    }

    private void showFinalAlert() {
        winAlertShow();
    }

    private void checkIfScore() {
        sleep(50);
        while (gameLogic.checkIfInRowAndGetCompletMarks()) {
            updateScore();
            // System.out.println(gameLogic.getMatchedCoordinates().size());
            List<Coordinances> nodeToRemove = gameLogic.getMatchedCoordinates();
            //nodeToRemove.stream().forEach(e -> System.out.println(e));
            nodeToRemove.forEach(e -> gameLogic.removeMark(e));
            // updateThread.notify();
            isUpdateNeeded = true;
            sleep(25);
        }
        isUpdateNeeded = true;
    }

    private void updateScore() {
        switch (gameLogic.getMatchedCoordinates().size()) {
            case 5:
                score += 5;
                break;
            case 6:
                score += 7;
                break;
            case 7:
                score += 10;
                break;
            case 8:
                score += 14;
                break;
            case 9:
                score += 20;
                break;
            case 10:
                score += 50;
                break;
        }
        setScoreField();
    }

    private void sleep(int timeToWait) {
        try {
            Thread.sleep(timeToWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void launchTime() {
        LocalDateTime startTime = LocalDateTime.now();
        timeUpdater = new TimeUpdater(timeQueue, true, startTime);
        Thread t = new Thread(timeUpdater);
        t.setDaemon(true);
        t.start();
    }

    private void refreshGuiContinusly() {
        refreshGui = new GuiUpdate(this);
        updateThread = new Thread(refreshGui);
        updateThread.setDaemon(true);
        updateThread.start();
    }


    private void winAlertShow() {
        timeUpdater.setCountTime(false);
        long deltaTime = TimeUpdater.getDeltaTime();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game over");
        alert.setHeaderText("Game is over, your score: " + score +
                "\nYour play time: " + getTimeFormat(deltaTime));
        alert.showAndWait();
        newGameButton.setDisable(false);
        updateRecord();
    }

    private void updateRecord() {
        if (score > record)
            record = score;
        recordField.setText(String.valueOf(record));
    }


    public static String getTimeFormat(long value) {
        long amountOfSeconds = value / 1000;
        long amountOfMinutes = amountOfSeconds / 60;
        int minutes = Long.valueOf(amountOfMinutes % 60).intValue();
        int sec = Long.valueOf(amountOfSeconds % 60).intValue();
        int hours = Long.valueOf(amountOfMinutes / 60).intValue();
        return String.format("%02d:%02d:%02d", hours, minutes, sec);
    }


    private void setScoreField() {
        scoreField.setText(String.valueOf(score));
    }


    @FXML
    private void handleNewGame() {
        timeField.clear();
        refreshGui.clearGrid();
        updateThread.interrupt();
        initialize();
    }

    private void updateGuiOfNextMarks() {
        for (int i = 0; i < nextMarks.size(); i++) {
            ImageView imageView = new ImageView(new Image(
                    getClass().getClassLoader()
                            .getResource(nextMarks.get(i).getImagePath()).toExternalForm()));
            imageView.setFitHeight(45);
            imageView.setFitWidth(45);
            nextBallsGrid.add(imageView, i, 0);
        }
    }

    public GridPane getGrid() {
        return grid;
    }

    public Board getGameLogic() {
        return gameLogic;
    }

    public boolean getIsUpdateNeeded() {
        return isUpdateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        isUpdateNeeded = updateNeeded;
    }


}