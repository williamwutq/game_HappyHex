package GUI;

import GUI.animation.*;
import Launcher.LaunchEssentials;
import hex.HexEngine;
import game.Queue;
import hexio.HexLogger;
import io.GameTime;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The {@link GameEssentials} class provides essential game utilities, including {@link #generateColor() color generation}
 * and {@link #paintHexagon(Graphics, Color, double, double, double, double) methods} for efficiently rendering hexagons.
 * <p>
 * This class is final and cannot be extended.
 */
public final class GameEssentials {
    /** The sine of 60 degrees, used for hexagonal calculations. For scaling, use {@code GameEssentials.sinOf60 * 2}. */
    public static final double sinOf60 = Math.sqrt(3) / 2;
    /** The delay to a typical action of the game, in ms*/
    private static int actionDelay = 80;
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
    public static Color gameBackgroundColor = GameEssentials.processColor(new Color(213, 236, 230), "GameBackgroundColor");
    public static Color gameOverBackgroundColor = GameEssentials.processColor(new Color(163, 188, 180), "GameOverBackgroundColor");
    public static Color gameBlockDefaultColor = GameEssentials.processColor(Color.BLACK, "GameBlockDefaultColor");
    public static Color gamePiecePanelColor = GameEssentials.processColor(new Color(113, 129, 122), "GamePiecePanelColor");
    public static Color gamePieceSelectedColor = GameEssentials.processColor(new Color(168, 213, 201), "GamePieceSelectedColor");
    public static Color gameDisplayFontColor = GameEssentials.processColor(new Color(5, 34, 24), "GameDisplayFontColor");
    public static Color gameQuitFontColor = GameEssentials.processColor(new Color(136, 7, 7), "GameQuitFontColor");

    /**
     * Sets the typical action delay of the game in ms, must be an integer between 10 and 100000.
     *
     * @param delay the new delay of a typical action in the game; must be between 10 (inclusive) and 100000 (inclusive).
     */
    public static void setDelay(int delay) {
        if (delay <= 100000 && delay >= 10) {
            GameEssentials.actionDelay = delay;
        }
    }
    /**
     * Get the typical action delay of the game in ms
     *
     * @return the delay of a typical action in the game.
     */
    public static int getDelay() {
        return actionDelay;
    }
    /**
     * Generates a random color from a predefined set of 12 distinct colors.
     *
     * @return a randomly selected {@code Color} object.
     */
    public static Color generateColor() {
        return pieceColors[(int) (Math.random() * 12)];
    }
    public static Color getIndexedPieceColor(int index){
        if(index < 12 && index >= 0){
            return pieceColors[index];
        } else return null;
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
        gameDisplayFont = GameEssentials.processFont("Courier", "GameDisplayFont");
    }
    /**
     * Paints a hexagon at the origin (0,0) with a specified color and size.
     *
     * @param g     the {@code Graphics} context used for rendering.
     * @param color the {@code Color} of the hexagon.
     * @param size  the size (radius) of the hexagon.
     * @see #paintHexagon(Graphics, Color, double, double, double, double) Full version
     */
    public static void paintHexagon(Graphics g, Color color, double size) {
        paintHexagon(g, color, 0, 0, size, 0.9);
    }
    /**
     * Paints a hexagon at a specified (x, y) position with a given color, size, and fill ratio.
     *
     * @param g     the {@code Graphics} context used for rendering.
     * @param color the {@code Color} of the hexagon.
     * @param x     the x-coordinate of the hexagon's center.
     * @param y     the y-coordinate of the hexagon's center.
     * @param size  the size (radius) of the hexagon.
     * @param fill  the fill ratio, affecting how much of the hexagon is drawn.
     *              It is usually between 0.0 (exclusive) and 1.0 (inclusive),
     *              but greater values may also be used in special cases.
     * @see #paintHexagon(Graphics, Color, double) Basic version
     */
    public static void paintHexagon(Graphics g, Color color, double x, double y, double size, double fill) {
        // Basic Polygon
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            xPoints[i] = (int) Math.round(size * (x * 2 + sinOf60 + Math.sin(angle) * fill));
            yPoints[i] = (int) Math.round(size * (y * 2 + 1.0 + Math.cos(angle) * fill));
        }
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 6);
        // Highlight
        xPoints = new int[4];
        yPoints = new int[4];
        double r1 = 0.7;
        double r2 = 0.8;
        xPoints[0] = (int) Math.round(size * (x * 2 + sinOf60));
        yPoints[0] = (int) Math.round(size * (y * 2 + 1.0 + r1 * fill));
        xPoints[1] = (int) Math.round(size * (x * 2 + sinOf60 + r1 * sinOf60 * fill));
        yPoints[1] = (int) Math.round(size * (y * 2 + 1.0 + r1 * fill * 0.5));
        xPoints[2] = (int) Math.round(size * (x * 2 + sinOf60 + r2 * sinOf60 * fill));
        yPoints[2] = (int) Math.round(size * (y * 2 + 1.0 + r2 * fill * 0.5));
        xPoints[3] = xPoints[0];
        yPoints[3] = (int) Math.round(size * (y * 2 + 1.0 + r2 * fill));
        g.setColor(gameBackgroundColor);
        g.fillPolygon(xPoints, yPoints, 4);
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
    public static void initialize(int size, int queueSize, int delay, boolean easy, JFrame frame, String player){
        if(easy) {
            game.PieceFactory.setEasy();
        }
        engine = new HexEngine(size, gameBlockDefaultColor, getIndexedPieceColor(0)); // replace getIndexedPieceColor(0) with something else
        queue = new Queue(queueSize);
        window = frame;
        // Logger initialize
        gameLogger = new HexLogger(LaunchEssentials.getCurrentPlayer(), LaunchEssentials.getCurrentPlayerID());
        gameLogger.setEngine(engine);
        gameLogger.setQueue(queue.getPieces());
        // Construct labels
        turnLabel = new GameInfoPanel();
        scoreLabel = new GameInfoPanel();
        playerLabel = new GameInfoPanel();
        quitButton = new GameQuitButton();
        turnLabel.setTitle("TURN");
        scoreLabel.setTitle("SCORE");
        playerLabel.setTitle("PLAYER");
        turnLabel.setInfo("0");
        scoreLabel.setInfo("0");
        playerLabel.setInfo(player);
        turnLabel.setBounds(0, 0, 100, 100);
        scoreLabel.setBounds(300, 0, 100, 100);
        playerLabel.setBounds(300, 300, 100, 100);
        // Calculations
        setDelay(delay);
        calculateButtonSize();
        calculateLabelSize();
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
    public static void checkEnd(){
        // If the game should end, log and reset
        if(gameEnds()){
            System.out.println(io.GameTime.generateSimpleTime() + " GameEssentials: Game ends peacefully.");
            logGame();
            resetGame();
            Launcher.LauncherGUI.toGameOver();
        }
    }
    private static boolean gameEnds(){
        // Helper to check
        for(int i = 0; i < GameEssentials.queue().length(); i ++) {
            if(GameEssentials.engine().checkPositions(GameEssentials.queue().get(i)).size() != 0){
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
        gameLogger = new HexLogger(LaunchEssentials.getCurrentPlayer(), LaunchEssentials.getCurrentPlayerID());
        engine.reset();
        queue.reset();
        gameLogger.setEngine(engine);
        gameLogger.setQueue(queue.getPieces());
        window.repaint();
    }

    // Logging at the end
    public static void logGame(){
        Launcher.LaunchEssentials.log(turn, score);
        try {
            if (gameEnds()) gameLogger.completeGame();
            gameLogger.setEngine(engine);
            gameLogger.setQueue(queue.getPieces());
            gameLogger.write();
        } catch (IOException e) {
            System.err.println(GameTime.generateSimpleTime() + " HexLogger: " + e.getMessage());
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
    public static void move(hex.Hex origin, hex.Piece piece){
        gameLogger.addMove(origin, piece);
    }

    // Setters
    public static void setSelectedPieceIndex(int index){
        if(index == -1 || (index >= 0 && index < queue.length())){
            GameEssentials.selectedPieceIndex = index;
            GameEssentials.selectedBlockIndex = -1;
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
                GameEssentials.selectedBlockIndex = index;
            }
        }
    }
    public static void setHoveredOverIndex(int index) {
        if(index == -1 || (index >= 0 && index < engine.length())){
            GameEssentials.hoveredOverIndex = index;
            window.repaint();
        } else throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + engine.length());
    }
    public static void setClickedOnIndex(int index) {
        if(index == -1 || (index >= 0 && index < engine.length())){
            GameEssentials.clickedOnIndex = index;
            window.repaint();
        } else throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + engine.length());
    }

    // Getters
    public static HexEngine engine(){return engine;}
    public static Queue queue(){return queue;}
    public static JFrame window(){return window;}
    public static int getSelectedPieceIndex(){return selectedPieceIndex;}
    public static int getSelectedBlockIndex(){return selectedBlockIndex;}
    public static int getHoveredOverIndex() {return hoveredOverIndex;}
    public static int getClickedOnIndex() {return clickedOnIndex;}
}
