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
import game.GameGUIInterface;
import hex.Hex;
import hex.HexEngine;
import game.Queue;
import hex.Piece;
import hexio.HexLogger;
import io.GameTime;
import python.PythonCommandProcessor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The {@link GameEssentials} class provides essential game utilities.
 * <p>
 * This class is final and cannot be extended.
 */
public final class GameEssentials implements GameGUIInterface {
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
    // Info panels and quit button
    private static GameInfoPanel turnLabel;
    private static GameInfoPanel scoreLabel;
    private static GameInfoPanel playerLabel;
    private static GameQuitButton quitButton;

    private static int selectedPieceIndex = -1;
    private static int selectedBlockIndex = -1;
    private static int hoveredOverIndex = -1;
    private static int clickedOnIndex = -1;

    private static int turn = 0;
    private static int score = 0;

    // Autoplay
    private static Thread autoplayThread;

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

    /**
     * Generates a random {@link Color} from a predefined set of 12 distinct colors.
     * @return a randomly selected {@code SolidColor} object.
     */
    public static Color generateColor(int index) {
        if (index == -1 || index < -2){
            return gameBlockDefaultColor;
        } else if (index == -2 || index >= pieceColors.length){
            return getDefaultColor();
        } else return pieceColors[index];
    }
    public static Color getIndexedPieceColor(int index){
        if(index < 12 && index >= 0){
            return pieceColors[index];
        } else return null;
    }
    public static Color getDefaultColor(){
        int r = 0;
        int g = 0;
        int b = 0;
        for (Color color : pieceColors){
            r += color.getRed();
            g += color.getGreen();
            b += color.getBlue();
        }
        return interpolate(gameBackgroundColor, gameBlockDefaultColor, 2);
    }
    @Deprecated
    public static Color whitenColor(Color origin){
        return new Color((origin.getRed() + 255)/2, (origin.getGreen() + 255)/2, (origin.getBlue() + 255)/2);
    }
    @Deprecated
    public static Color darkenColor(Color origin){
        return new Color((origin.getRed())/2, (origin.getGreen())/2, (origin.getBlue())/2);
    }
    public static Color dimColor(Color origin){
        return new Color(origin.getRed(), origin.getGreen(), origin.getBlue(), (int)Math.round(origin.getAlpha() * 0.8));
    }
    public static Color processColor(Color origin){
        return (Color) colorProcessor.process(origin);
    }
    public static Color processColor(Color origin, String hint){
        return (Color) colorProcessor.process(origin, hint);
    }
    public static Color interpolate(Color base, Color target, int degree){
        return new Color((base.getRed()*degree + target.getRed())/(1 + degree),(base.getGreen()*degree + target.getGreen())/(1 + degree),(base.getBlue()*degree + target.getBlue())/(1 + degree));
    }
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
    public static String processFont(String origin, String hint){
        return (String) fontProcessor.process(origin, hint);
    }
    public static void changeFontProcessor(special.SpecialFeature newProcessor){
        fontProcessor = newProcessor;
        gameDisplayFont = processFont("Courier", "GameDisplayFont");
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
    public static void calculateLabelSize() {
        int height = window().getHeight();
        int width = window().getWidth();
        int gamePanelExtension = getGamePanelWidthExtension();

        double minSize = Math.min(height - 33, width - 5);
        int labelWidth = (int) Math.round(minSize / 6.0);
        int labelHeight = (int) Math.round(minSize / 12.0);
        Dimension dimension = new Dimension(labelWidth, labelHeight);
        SimpleButton.setSize((int)Math.round(labelHeight*0.6));

        int margin = 3;
        int piecePanelSize = (int) Math.round(5 * HexButton.getActiveSize());
        int bottomOffset = labelHeight + 28 + margin;

        int right = width - gamePanelExtension - labelWidth - margin;
        int bottom = height - piecePanelSize - bottomOffset;

        turnLabel.setPreferredSize(dimension);
        scoreLabel.setPreferredSize(dimension);
        playerLabel.setPreferredSize(dimension);
        quitButton.setPreferredSize(dimension);
        quitButton.resetSize();

        turnLabel.setBounds(new Rectangle(new Point(margin + gamePanelExtension, margin), dimension));
        scoreLabel.setBounds(new Rectangle(new Point(right, margin), dimension));
        playerLabel.setBounds(new Rectangle(new Point(right, bottom), dimension));
        quitButton.setBounds(new Rectangle(new Point(margin + gamePanelExtension, bottom), dimension));
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
    // Initializing
    public static void initialize(int size, int queueSize, boolean easy, JFrame frame, String player, HexLogger logger){
        System.out.println(GameTime.generateSimpleTime() + " GameEssentials: Game starts.");
        if(easy) {
            game.PieceFactory.setEasy();
        }
        score = 0;
        turn = 0;
        selectedPieceIndex = -1;
        selectedBlockIndex = -1;
        hoveredOverIndex = -1;
        clickedOnIndex = -1;
        engine = new HexEngine(size);
        queue = new Queue(queueSize);
        window = frame;
        // Logger initialize
        gameLogger = logger;
        if(logger.getEngine().getRadius() == size && logger.getQueue().length == queueSize){
            // Copy logger info to game
            score = logger.getScore();
            turn = logger.getTurn();
            for (hex.Block block : logger.getEngine().blocks()){
                if (block != null && block.getState()) {
                    hex.Block cloned = block.clone();
                    engine.setBlock(block.getLineI(), block.getLineK(), cloned);
                }
            }
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
        // Construct labels
        turnLabel = new GameInfoPanel();
        scoreLabel = new GameInfoPanel();
        playerLabel = new GameInfoPanel();
        quitButton = new GameQuitButton();
        turnLabel.setTitle("TURN");
        scoreLabel.setTitle("SCORE");
        playerLabel.setTitle("PLAYER");
        turnLabel.setInfo(turn + "");
        scoreLabel.setInfo(score + "");
        playerLabel.setInfo(player);
        turnLabel.setBounds(0, 0, 100, 100);
        scoreLabel.setBounds(300, 0, 100, 100);
        playerLabel.setBounds(300, 300, 100, 100);
        // Calculations
        calculateButtonSize();
        calculateLabelSize();
        autoplayThread = null;
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
        JPanel panel = new GamePanel();
        panel.add(turnLabel);
        panel.add(scoreLabel);
        panel.add(playerLabel);
        panel.add(quitButton);
        return panel;
    }
    public static JPanel fetchPiecePanel(){
        return new PiecePanel();
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
            GameEssentials.resetGame();
        }
        Launcher.LauncherGUI.returnHome();
    }
    public static void checkEnd(){
        // If the game should end, log and reset
        if(gameEnds()){
            if (autoplayThread != null) autoplayThread.interrupt();
            System.out.println(io.GameTime.generateSimpleTime() + " GameEssentials: Game ends peacefully.");
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
        score = 0;
        turn = 0;
        selectedPieceIndex = -1;
        selectedBlockIndex = -1;
        hoveredOverIndex = -1;
        clickedOnIndex = -1;
        turnLabel.setInfo(turn + "");
        scoreLabel.setInfo(score + "");
        gameLogger = new HexLogger(Launcher.LaunchEssentials.getCurrentPlayer(), Launcher.LaunchEssentials.getCurrentPlayerID());
        engine.reset();
        queue.reset();
        gameLogger.setEngine(engine);
        gameLogger.setQueue(queue.getPieces());
        if (autoplayThread != null) autoplayThread.interrupt();
        window.repaint();
    }

    public static void interruptAutoplay(){
        if (autoplayThread != null) autoplayThread.interrupt();
    }

    // Logging at the end
    public static void logGame(){
        boolean complete = gameEnds();
        gameLogger.setEngine(engine);
        gameLogger.setQueue(queue.getPieces());
        if (complete) gameLogger.completeGame();
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
            LaunchEssentials.log(turn, score);
        } else {
            System.out.println(GameTime.generateSimpleTime() + " LaunchLogger: JSON data not logged in logs.json because player has not completed the game.");
        }
    }

    // Scoring
    public static int getTurn(){
        return turn;
    }
    public static int getScore(){
        return score;
    }
    public static void incrementTurn(){
        turn ++;
        turnLabel.setInfo(turn + "");
    }
    public static void incrementScore(int addedScore){
        score += addedScore;
        scoreLabel.setInfo(score + "");
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
            incrementTurn();
            incrementScore(piece.length());
            gameLogger.addMove(position, selectedPieceIndex, queue.getPieces());
            engine().add(position, queue().fetch(pieceIndex));
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
        hex.Block[] eliminated = engine().eliminate();
        // Add animation
        for(hex.Block block : eliminated){
            addAnimation(createDisappearEffect(block));
            addAnimation(createCenterEffect(new hex.Block(block)));
        }
        // Add score
        incrementScore(5 * eliminated.length);
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
        return engine;
    }
    @Override
    public Piece[] getQueue() {
        return queue.getPieces();
    }
}
