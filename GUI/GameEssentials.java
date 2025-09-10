/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package GUI;

import GUI.animation.*;
import Launcher.LaunchEssentials;
import game.AutoplayHandler;
import game.GameGUIInterface;
import hex.GameState;
import hex.Hex;
import hex.HexEngine;
import game.Queue;
import hex.Piece;
import hexio.HexLogger;
import io.GameTime;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The {@link GameEssentials} class provides essential game utilities.
 * <p>
 * This class is final and cannot be extended.
 */
public final class GameEssentials implements GameGUIInterface {
    private GameEssentials() {
        // Private constructor to prevent instantiation outside of this class
    }
    /** The sine of 60 degrees, used for hexagonal calculations. For scaling, use {@code GameEssentials.sinOf60 * 2}. */
    public static final double sinOf60 = Math.sqrt(3) / 2;
    /** The delay to a typical action of the game, in ms*/
    private static final int actionDelay = 250;
    /** The main game engine object. */
    private static HexEngine engine;
    /** The main game queue of pieces, which contain a number of {@link hex.Piece}. */
    private static Queue queue;
    /** The main window of the game. */
    private static JFrame window;
    /** The game logger used for data recording. */
    private static HexLogger gameLogger;
    private static PiecePanel piecePanel;
    private static GamePanel gamePanel;

    private static final Object moveLock = new Object(); // Lock to modify the engine and queue

    private static final AutoplayHandler autoplayHandler = new AutoplayHandler(new GameEssentials());

    private static int selectedPieceIndex = -1;
    private static int selectedBlockIndex = -1;
    private static int hoveredOverIndex = -1;
    private static int clickedOnIndex = -1;

    // Special Features
    private static special.SpecialFeature colorProcessor = special.FeatureFactory.createFeature(Color.class.getName());
    private static special.SpecialFeature fontProcessor = special.FeatureFactory.createFeature(Font.class.getName());
    private static special.SpecialFeature effectProcessor = special.FeatureFactory.createFeature(Animation.class.getName());

    // Random Piece
    private static Color[] getRawPieceColors(){
        return new Color[]{
                new Color(0, 0, 240),
                new Color(0, 100, 190),
                new Color(0, 180, 180),
                new Color(0, 180, 120),
                new Color(0, 210, 0),
                new Color(100, 180, 0),
                new Color(180, 180, 0),
                new Color(200, 90, 0),
                new Color(210, 0, 0),
                new Color(200, 0, 120),
                new Color(180, 0, 180),
                new Color(100, 0, 200),
        };
    }
    private static Color[] pieceColors = (Color[]) colorProcessor.process(getRawPieceColors());

    public static String gameDisplayFont = GameEssentials.processFont("Courier", "GameDisplayFont");
    public static Color gameBackgroundColor = GameEssentials.processColor(new Color(213, 236, 230), "GamePanelBackgroundColor");
    public static Color gameOverBackgroundColor = GameEssentials.processColor(new Color(163, 188, 180), "GameOverBackgroundColor");
    public static Color gameBlockDefaultColor = GameEssentials.processColor(Color.BLACK, "GameBlockDefaultColor");
    public static Color gamePiecePanelColor = GameEssentials.processColor(new Color(113, 129, 122), "GamePiecePanelColor");
    public static Color gamePieceSelectedColor = GameEssentials.processColor(new Color(168, 213, 201), "GamePieceSelectedColor");
    public static Color gameDisplayFontColor = GameEssentials.processColor(new Color(5, 34, 24), "GameDisplayFontColor");
    public static Color gameQuitFontColor = GameEssentials.processColor(new Color(136, 7, 7), "GameQuitFontColor");
    public static Color gameDynamicIslandTransitionStartColor = GameEssentials.processColor(new Color(200, 49, 214), "GameDynamicStartBackgroundColor");
    public static Color gameDynamicIslandTransitionEndColor = GameEssentials.processColor(new Color(56, 216, 64), "GameDynamicEndBackgroundColor");

    /** The global color animator rarely used. */
    private static ColorAnimator colorAnimator;

    /**
     * Generates a random {@link Color} from a predefined set of 12 distinct colors by index.
     * @param index the index of the color to choose from, -1 and -2 represent default colors.
     * @return a randomly selected {@code SolidColor} object.
     */
    public static Color generateColor(int index) {
        if (index == -1 || index < -2){
            return gameBlockDefaultColor;
        } else if (index == -2 || index >= pieceColors.length){
            return getDefaultColor();
        } else if (colorAnimator != null){
            return colorAnimator.get((double)index / pieceColors.length);
        } else return pieceColors[index];
    }
    /**
     * Return whether the block colors are animated using colorAnimator.
     * @return whether the block colors are animated
     */
    public static boolean isColorAnimated(){
        return colorAnimator != null;
    }
    /**
     * Generates the default color by interpolating the background and the default block color.
     * @return the default color resulting from interpolation.
     */
    private static Color getDefaultColor(){
        return interpolate(gameBackgroundColor, gameBlockDefaultColor, 2);
    }
    /**
     * Dim a color, namely lower the alpha value of that color by 80%.
     * @param origin the original color.
     * @return the dimmed color.
     */
    public static Color dimColor(Color origin){
        return new Color(origin.getRed(), origin.getGreen(), origin.getBlue(), (int)Math.round(origin.getAlpha() * 0.8));
    }
    /**
     * Process a color with special color processor providing hints to the processor.
     * @param origin the original color.
     * @return the processed color.
     */
    public static Color processColor(Color origin, String hint){
        return (Color) colorProcessor.process(origin, hint);
    }
    /**
     * Interpolating a color base with a color target with a given degree. Degree will result in color closer to the base.
     * @param base the base color.
     * @param target the target color
     * @param degree the interpolation constant used by the operation, higher value result in color closer to base.
     * @return the interpolation of the colors as defined by the degree.
     */
    public static Color interpolate(Color base, Color target, int degree){
        return new Color((base.getRed()*degree + target.getRed())/(1 + degree),(base.getGreen()*degree + target.getGreen())/(1 + degree),(base.getBlue()*degree + target.getBlue())/(1 + degree));
    }
    /**
     * Change the special color processor to a given processor and update all static colors.
     * @param newProcessor the new color processor to be used.
     */
    public static void changeColorProcessor(special.SpecialFeature newProcessor){
        colorProcessor = newProcessor;
        pieceColors = (Color[]) colorProcessor.process(getRawPieceColors());
        gameBackgroundColor = processColor(new Color(213, 236, 230), "GamePanelBackgroundColor");
        gameOverBackgroundColor = processColor(new Color(163, 188, 180), "GameOverBackgroundColor");
        gameBlockDefaultColor = processColor(Color.BLACK, "GameBlockDefaultColor");
        gamePiecePanelColor = processColor(new Color(113, 129, 122), "GamePiecePanelBackgroundColor");
        gamePieceSelectedColor = processColor(new Color(168, 213, 201), "GamePieceSelectedColor");
        gameDisplayFontColor = processColor(new Color(5, 34, 24), "GameDisplayFontColor");
        gameQuitFontColor = processColor(new Color(136, 7, 7), "GameQuitFontColor");
    }
    /**
     * Process a font with special font processor providing hints to the processor.
     * @param origin the original font.
     * @return the processed font.
     */
    public static String processFont(String origin, String hint){
        return (String) fontProcessor.process(origin, hint);
    }
    /**
     * Change the special font processor to a given processor and update all static fonts.
     * @param newProcessor the new font processor to be used.
     */
    public static void changeFontProcessor(special.SpecialFeature newProcessor){
        fontProcessor = newProcessor;
        gameDisplayFont = processFont("Courier", "GameDisplayFont");
        if (colorAnimator != null) colorAnimator.setColors(pieceColors);
    }

    // Resizing
    public static void calculateButtonSize(){
        if(engine == null || window == null){
            throw new NullPointerException("Critical game components cannot be null");
        }
        // Calculate minimum size
        double horizontalCount = engine().getRadius() * 4 - 2;
        double verticalCount = engine().getRadius() * 3 + 4;
        double minSize = Math.min((window().getHeight()-33) / verticalCount, (window().getWidth()-5) / horizontalCount / GameEssentials.sinOf60);
        HexButton.setSize(minSize);
    }
    public static int getPiecePanelWidthExtension(){
        int half = window.getWidth()/2;
        int length = queue.length() * 3;
        return half - (int)Math.round(length * HexButton.getActiveSize() * sinOf60);
    }
    public static int getGamePanelWidthExtension(){
        int half = window.getWidth()/2;
        int length = engine.getRadius() * 2 - 1;
        return half - (int)Math.round(length * HexButton.getActiveSize() * sinOf60);
    }
    public static int getGamePanelHeightExtension(){
        int half = (int)Math.round((window.getHeight() - 33.0)/2);
        return half - (int)Math.round((engine.getRadius() * 1.5 + 2) * HexButton.getActiveSize());
    }
    public static void setAnimator(Runnable guiUpdater){
        colorAnimator = new ColorAnimator(pieceColors, 16000, guiUpdater);
        colorAnimator.start();
    }
    // Initializing
    public static void initialize(int size, int queueSize, boolean easy, JFrame frame, String player, HexLogger logger){
        System.out.println(GameTime.generateSimpleTime() + " GameEssentials: Game starts.");
        if (colorAnimator != null) {
            colorAnimator.setColors(pieceColors);
        }
        if(easy) {
            game.PieceFactory.setEasy();
        }
        selectedPieceIndex = -1;
        selectedBlockIndex = -1;
        hoveredOverIndex = -1;
        clickedOnIndex = -1;
        window = frame;
        // Construct engine, queue, logger
        synchronized (moveLock) {
            engine = null;
            queue = null;
            engine = new HexEngine(size);
            queue = new Queue(queueSize);
            gameLogger = logger;
            // Logger initialize
            if (logger.getEngine().getRadius() == size && logger.getQueue().length == queueSize) {
                engine = logger.getEngine().clone();
                Piece[] loggerQueue = logger.getQueue();
                for (int i = 0; i < loggerQueue.length; i++) {
                    Piece piece = loggerQueue[i];
                    if (piece != null) {
                        queue.inject(piece, i);
                    }
                }
            } else {
                // Copy info to logger
                gameLogger.setEngine(engine);
                gameLogger.setQueue(queue.getPieces());
                gameLogger.setScore(0);
                gameLogger.setTurn(0);
            }
        }
        // Autoplay
        autoplayHandler.useMLIfAvailable();
        // Construct GUI
        HexButton.setSize(1);
        piecePanel = new PiecePanel();
        gamePanel = new GamePanel(engine.length(), getTurn(), getScore(), player);
        // Calculations
        piecePanel.doLayout();
        gamePanel.doLayout();
    }
    public static void closeAnimator(){
        if (colorAnimator != null) colorAnimator.stop();
    }
    public static void startAutoplay(){
        autoplayHandler.run();
    }
    public static void interruptAutoplayExternal(){
        autoplayHandler.genericClose();
        gamePanel.quitAuto();
    }
    public static void interruptAutoplay(){
        autoplayHandler.genericClose();
    }
    public static void terminateAutoplay(){
        autoplayHandler.hardClose();
        if (gamePanel!= null) gamePanel.quitAutoImmediately();
    }
    public static Void setFastAutoplay(){
        if (!autoplayHandler.changeDelay(250)){
            throw new IllegalStateException("Autoplay setting could not be performed");
        }
        return null;
    }
    public static Void setSlowAutoplay(){
        if (!autoplayHandler.changeDelay(1200)){
            throw new IllegalStateException("Autoplay setting could not be performed");
        }
        return null;
    }
    public static Animation createCenterEffect(hex.Block block){
        Animation animation = (Animation) effectProcessor.process(new Object[]{new CenteringEffect(block), block})[0];
        animation.start();
        return animation;
    }
    public static Animation createDisappearEffect(hex.Block block){
        Animation animation = (Animation) effectProcessor.process(new Object[]{new DisappearEffect(block), block})[0];
        animation.start();
        return animation;
    }
    public static void addAnimation(Animation animation){
        Component component = window().getContentPane().getComponent(0);
        if(component instanceof JPanel){
            ((JPanel)component).add(animation, 0);
        }
    }
    public static JPanel fetchGamePanel(){
        return gamePanel;
    }
    public static JPanel fetchPiecePanel(){
        return piecePanel;
    }

    // End checking
    public static void quitGame(){
        if(Launcher.LaunchEssentials.isGameStarted()) {
            // Log if it has score and reset
            if (GameEssentials.getTurn() != 0) {
                System.out.println(io.GameTime.generateSimpleTime() + " GameEssentials: Game ends by force quitting.");
                GameEssentials.logGame();
            } else {
                System.out.println(io.GameTime.generateSimpleTime() + " GameEssentials: Game quits without change.");
            }
            autoplayHandler.genericClose();
            GameEssentials.resetGame();
        }
        Launcher.LauncherGUI.returnHome();
    }
    public static void checkEnd(){
        // If the game should end, log and reset
        if(gameEnds()){
            System.out.println(io.GameTime.generateSimpleTime() + " GameEssentials: Game ends peacefully.");
            autoplayHandler.genericClose();
            logGame();
            resetGame();
            Launcher.LauncherGUI.toGameOver();
        }
    }
    static boolean gameEnds(){
        // Helper to check
        HexEngine clonedEngine = engine().clone();
        clonedEngine.eliminate();
        for(int i = 0; i < queue().length(); i ++) {
            if(!clonedEngine.checkPositions(queue().get(i)).isEmpty()){
                return false;
            }
        }
        return true;
    }
    public static void resetGame(){
        selectedPieceIndex = -1;
        selectedBlockIndex = -1;
        hoveredOverIndex = -1;
        clickedOnIndex = -1;
        gameLogger = new HexLogger(Launcher.LaunchEssentials.getCurrentPlayer(), Launcher.LaunchEssentials.getCurrentPlayerID());
        synchronized (moveLock){
            engine.reset();
            queue.reset();
            gameLogger.setEngine(engine);
            gameLogger.setQueue(queue.getPieces());
        }
        window.repaint();
    }

    // Logging at the end
    public static void logGame(){
        boolean complete = gameEnds();
        synchronized (moveLock) {
            gameLogger.setEngine(engine);
            gameLogger.setQueue(queue.getPieces());
            if (complete) gameLogger.completeGame();
        }
        final HexLogger writeOnLogger = gameLogger; // This logger will be used for writing
        gameLogger = gameLogger.clone(); // Create cloned logger
        new Thread(() -> {
            try {
                writeOnLogger.write("hex.coloredbinary");
                System.out.println(GameTime.generateSimpleTime() + " HexLogger: file written to " + writeOnLogger.getDataFileName() + ".hpyhex");
            } catch (IOException e) {
                System.err.println(GameTime.generateSimpleTime() + " HexLogger: " + e.getMessage());
        }}).start();
        if (LaunchEssentials.getCurrentPlayerID() == -1 || complete){
            // Log if the game is complete or the player did not log in, in which the game cannot be restarted.
            LaunchEssentials.log(getTurn(), getScore());
        } else {
            System.out.println(GameTime.generateSimpleTime() + " LaunchLogger: JSON data not logged in logs.json because player has not completed the game.");
        }
    }

    // Scoring
    public static int getTurn(){
        return gameLogger.getTurn();
    }
    public static int getScore(){
        return gameLogger.getScore();
    }

    // Setters
    public static void setSelectedPieceIndex(int index){
        if(index == -1 || (index >= 0 && index < queue.length())){
            selectedPieceIndex = index;
            selectedBlockIndex = -1;
            window.repaint();
        } else throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + queue.length());
    }
    public static void setSelectedBlockIndex(int index){
        if(selectedPieceIndex == -1){
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length 0");
        } else {
            // Try to get the piece
            int pieceLength = queue.get(selectedPieceIndex).length();
            if(index < -1 || index >= pieceLength){
                throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + pieceLength);
            } else {
                window.repaint();
                selectedBlockIndex = index;
            }
        }
    }
    public static void setDualIndexesWithoutRepaint(int pieceIndex, int blockIndex){
        if(pieceIndex >= 0 && pieceIndex < queue.length()){
            selectedPieceIndex = pieceIndex;
            int pieceLength = queue.get(selectedPieceIndex).length();
            if(blockIndex < -1 || blockIndex >= pieceLength){
                throw new IndexOutOfBoundsException("Index " + blockIndex + " out of bounds for length " + pieceLength);
            } else {
                selectedBlockIndex = blockIndex;
            }
        } else if (pieceIndex == -1){
            throw new IndexOutOfBoundsException("Index " + blockIndex + " out of bounds for length 0");
        } else throw new IndexOutOfBoundsException("Index " + pieceIndex + " out of bounds for length " + queue.length());
    }
    public static void setHoveredOverIndex(int index) {
        if(index == -1 || (index >= 0 && index < engine.length())){
            hoveredOverIndex = index;
            window.repaint();
        } else throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + engine.length());
    }
    public static void setClickedOnIndex(int index) {
        if(index == -1 || (index >= 0 && index < engine.length())){
            clickedOnIndex = index;
            window.repaint();
        } else throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + engine.length());
    }

    public static void addMove(Hex position){
        int pieceIndex = getSelectedPieceIndex();
        int blockIndex = getSelectedBlockIndex();
        // Get piece
        Piece piece = queue().get(pieceIndex);
        // Modify position relative to selected block
        position = position.subtract(piece.getBlock(blockIndex));
        // Check this position, if good then add
        if (engine().checkAdd(position, piece)) {
            synchronized (moveLock) {
                gameLogger.addMove(position, selectedPieceIndex, queue.getPieces());
                gamePanel.updateDisplayedInfo(getTurn(), getScore());
                engine().add(position, queue().fetch(pieceIndex));
            }
            // Generate animation
            for (int i = 0; i < piece.length(); i ++){
                addAnimation(createCenterEffect(piece.getBlock(i).add(position)));
            }
        }
        // Reset index
        setSelectedPieceIndex(-1);
        setClickedOnIndex(-1);
        // Paint and eliminate
        window().repaint();
        if (engine().checkEliminate()) {
            Timer gameTimer = new Timer(actionDelay, null);
            gameTimer.setRepeats(false);
            gameTimer.addActionListener(e -> GameEssentials.eliminate());
            gameTimer.start();
        } else checkEnd();
    }
    public static void eliminate(){
        // Run elimination
        hex.Block[] eliminated;
        synchronized (moveLock) {
            eliminated = engine().eliminate();
        }
        // Add animation
        for(hex.Block block : eliminated){
            addAnimation(createDisappearEffect(block));
            addAnimation(createCenterEffect(new hex.Block(block)));
        }
        // Check end after eliminate
        checkEnd();
        window().repaint();
    }

    // Getters
    public static HexEngine engine(){return engine;}
    public static Queue queue(){return queue;}
    public static JFrame window(){return window;}
    public static int getSelectedPieceIndex(){return selectedPieceIndex;}
    public static int getSelectedBlockIndex(){return selectedBlockIndex;}
    public static int getHoveredOverIndex() {return hoveredOverIndex;}
    public static int getClickedOnIndex() {return clickedOnIndex;}
    /**
     * Get a snapshot of the current game state.
     * This method returns a {@link GameState} object that provides a read-only view of the current game state,
     * including the game engine, piece queue, score, and turn number. The returned object is a snapshot and
     * will not reflect future changes to the game state.
     *
     * @return a {@code GameState} object representing the current state of the game
     */
    public static GameState getGameState(){
        return new GameState() {
            @Override
            public HexEngine getEngine() {
                synchronized (moveLock) {
                    return engine().clone();
                }
            }
            @Override
            public Piece[] getQueue() {
                synchronized (moveLock) {
                    return queue().getPieces().clone();
                }
            }
            @Override
            public int getScore() {
                return GameEssentials.getScore();
            }
            @Override
            public int getTurn() {
                return GameEssentials.getTurn();
            }
        };
    }

    public static AutoplayHandler getAutoplayHandler() {
        return autoplayHandler;
    }
    @Override
    public void setEngine(HexEngine newEngine) {
        engine = newEngine;
    }
    @Override
    public void setQueue(Piece[] pieces) {
        queue = Queue.fromPieces(pieces);
    }
    @Override
    public HexEngine getEngine() {
        synchronized (moveLock) {
            return gameLogger.getEngine();
        }
    }
    @Override
    public Piece[] getQueue() {
        return queue.getPieces();
    }
    @Override
    public boolean move(int originIndex, int pieceIndex) {
        // Get piece
        Piece piece; Hex position;
        try {
            piece = queue().get(pieceIndex);
            position = engine().getBlock(originIndex);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        // Check this position, if good then add
        if (engine().checkAdd(position, piece)) {
            synchronized (moveLock) {
                if (!gameLogger.addMove(position, pieceIndex, queue.getPieces())) {
                    // If the move cannot be added, there must be a desync issue. Manually sync the game with the logger.
                    // See the other part for explanation of desync. In fact, the desync detected here is a bit more
                    // complicated, but the solution is the same.
                    engine = gameLogger.getEngine().clone();
                    queue = Queue.fromPieces(gameLogger.getQueue().clone());
                    gamePanel.updateDisplayedInfo(getTurn(), getScore());
                    // Shutdown autoplay
                    autoplayHandler.genericClose();
                    // Update GUI
                    window.repaint();
                    return false;
                }
                gamePanel.updateDisplayedInfo(getTurn(), getScore());
                engine().add(position, queue().fetch(pieceIndex));
            }
            // Generate animation
            for (int i = 0; i < piece.length(); i++) {
                addAnimation(createCenterEffect(piece.getBlock(i).add(position)));
            }
        } else {
            // If the move cannot be added, there must be a desync issue. Manually sync the game with the logger.
            // Desync happen not because we are not syncing, but because graphic elimination is lagging behind the real elimination.
            // When the autoplay plays faster than elimination, the game will desync. This is to be expected and handled here.
            // The ony symptom of desync is that the game cannot add a piece, but the logger can.
            synchronized (moveLock) {
                engine = gameLogger.getEngine().clone();
                queue = Queue.fromPieces(gameLogger.getQueue().clone());
                gamePanel.updateDisplayedInfo(getTurn(), getScore());
                // Update GUI
                window.repaint();
            }
        }
        // Reset index
        setSelectedPieceIndex(-1);
        setClickedOnIndex(-1);
        // Paint and eliminate
        window().repaint();
        if (engine().checkEliminate()) {
            Timer gameTimer = new Timer(actionDelay, null);
            gameTimer.setRepeats(false);
            gameTimer.addActionListener(e -> GameEssentials.eliminate());
            gameTimer.start();
        } else checkEnd();
        return true;
    }
}
