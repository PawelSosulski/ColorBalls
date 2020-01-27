package ColorBalls.controllers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;

 public class TimeUpdater implements Runnable {
    private final BlockingQueue<String> timeText;
    private boolean countTime;
    private LocalDateTime time;
    private LocalDateTime startTime;
    private static long deltaTime;
    private final long MAX_TIME = Long.MAX_VALUE;

    public TimeUpdater(BlockingQueue<String> timeQueue, boolean countTime, LocalDateTime startTime) {
        this.timeText = timeQueue;
        this.countTime = countTime;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        try {
            while (true) {
                while (countTime) {
                    time = LocalDateTime.now();
                    deltaTime = ChronoUnit.MILLIS.between(startTime, time);
                    //System.out.println(deltaTime);
                    String timeRunning = GridController.getTimeFormat(deltaTime);
                    timeText.put(timeRunning);
                    if (deltaTime > MAX_TIME * 0.99) {
                        deltaTime = Double.valueOf(MAX_TIME * 0.99).longValue();
                        timeRunning = GridController.getTimeFormat(deltaTime);
                        timeText.put(timeRunning);
                        countTime = false;
                        break;
                    }
                }
            }
        } catch (InterruptedException exc) {
            System.out.println("TimeUpdater interrupted: exiting.");
        }
    }

    public void setCountTime(boolean countTime) {
        this.countTime = countTime;
    }

    public static long getDeltaTime() {
        return deltaTime;
    }
}
