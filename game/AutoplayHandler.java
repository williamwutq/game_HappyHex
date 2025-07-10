package game;

import python.PythonCommandProcessor;

import java.io.IOException;

public class AutoplayHandler implements Runnable{
    private PythonCommandProcessor pythonProcessor;
    private final GameCommandProcessor gameProcessor;
    private static long hardCloseDelay = 600000;
    private long lastCloseTime;
    public AutoplayHandler(GameGUIInterface gameGUI){
        lastCloseTime = System.currentTimeMillis();
        this.gameProcessor = new GameCommandProcessor(gameGUI);
        setupPython();
    }
    private void setupPython() throws IllegalStateException {
        try {
            this.pythonProcessor = new PythonCommandProcessor("python/comm.py");
        } catch (IOException e) {
            throw new IllegalStateException("Python initialization failed");
        }
        gameProcessor.setCallBackProcessor(pythonProcessor);
        pythonProcessor.setCallBackProcessor(gameProcessor);
    }
    public void run(){
        if (pythonProcessor == null) setupPython();
        try {
            gameProcessor.run();
            gameProcessor.query();
        } catch (InterruptedException e) {
            close();
        }
    }
    public void hardClose() {
        if (pythonProcessor != null) {
            pythonProcessor.close();
            pythonProcessor = null;
        }
        if (gameProcessor != null) {
            gameProcessor.close();
            gameProcessor.setCallBackProcessor(null);
        }
    }
    public void softClose() {
        if (gameProcessor != null) {
            gameProcessor.close();
        }
    }
    public void close(){
        if (System.currentTimeMillis() - lastCloseTime < hardCloseDelay){
            hardClose();
        } else {
            softClose();
        }
        lastCloseTime = System.currentTimeMillis();
    }
}
