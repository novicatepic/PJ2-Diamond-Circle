package gui;
import game.Game;

public class RefreshingFormThread extends Thread {

    private final long start;
    private long accumulatedSeconds;
    private long accumulatedSecondsHelper;
    private static boolean isOver = false;

    public RefreshingFormThread() {
        super();
        start = System.currentTimeMillis();
        accumulatedSeconds = 0;
        accumulatedSecondsHelper = 0;

    }

    public static void setIsOver() {
        isOver = true;
    }

    @Override
    public void run() {
        MainFrame.setTimePlayedLabel("0");
        while (!isOver) {
            accumulatedSeconds += accumulatedSecondsHelper;
            accumulatedSecondsHelper = 0;
            long time = System.currentTimeMillis() - start - (accumulatedSeconds * 1000);
            long seconds = time / 1000;
            String tmp = ".";
            MainFrame.getCurrCardLabel().setText(MainFrame.getCurrCardLabel().getText() + tmp);
            MainFrame.setTimePlayedLabel("Vrijeme: " + seconds);

            if (Game.pause) {
                long start2 = System.currentTimeMillis();
                while (Game.pause) {
                    long time2 = System.currentTimeMillis() - start2;
                    accumulatedSecondsHelper = time2 / 1000;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException exc) {
                        Game.log(exc);
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException exc) {
                Game.log(exc);
            }
        }
    }

}
