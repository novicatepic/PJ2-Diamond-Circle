package gui;
import game.Game;

public class RefreshingForm extends Thread {

    private final long start;
    private long accumulatedSeconds;
    private long accumulatedSecondsHelper;
    private static boolean isOver = false;
    private static long seconds = 0;

    public RefreshingForm() {
        super();
        start = System.currentTimeMillis();
        accumulatedSeconds = 0;
        accumulatedSecondsHelper = 0;
    }

    public static void setIsOver() {
        isOver = true;
    }

    public static long getSeconds() { return seconds * 1000; }

    @Override
    public void run() {
        MainFrame.setTimePlayedLabel("0");
        while (!isOver) {
            accumulatedSeconds += accumulatedSecondsHelper;
            accumulatedSecondsHelper = 0;
            long time = System.currentTimeMillis() - start - (accumulatedSeconds * 1000);
            seconds = time / 1000;
            String tmp = ".";
            MainFrame.getCurrCardLabel().setText(MainFrame.getCurrCardLabel().getText() + tmp);
            MainFrame.setTimePlayedLabel("Vrijeme: " + seconds);

            pauseCheck();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException exc) {
                Game.log(exc);
            }

            pauseCheck();
        }
    }

    private void pauseCheck() {
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
    }
}
