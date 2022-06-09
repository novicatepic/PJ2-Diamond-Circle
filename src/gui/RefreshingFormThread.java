package gui;
import game.Game;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RefreshingFormThread extends Thread {

    private final long start;
    private long accumulatedSeconds;
    private long accumulatedSecondsHelper;
    private static Handler handler;

    static {
        try {
            handler = new FileHandler("refreshing.log");
            Logger.getLogger(RefreshingFormThread.class.getName()).addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RefreshingFormThread() {
        super();
        start = System.currentTimeMillis();
        accumulatedSeconds = 0;
        accumulatedSecondsHelper = 0;

    }

    @Override
    public void run() {
        MainFrame.setTimePlayedLabel("0");
        while (true) {
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
                        processException(exc);
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exc) {
                processException(exc);
            }
        }
    }

    private void processException(InterruptedException exc) {
        Logger.getLogger(RefreshingFormThread.class.getName()).log(Level.WARNING, exc.fillInStackTrace().toString());
    }

}
