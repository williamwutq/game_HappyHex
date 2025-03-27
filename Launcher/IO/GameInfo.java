package Launcher.IO;

public class GameInfo {
    private String player;
    private int turn;
    private int score;
    private long playerID;
    private final long gameID;
    private final GameMode gameMode;

    public GameInfo(GameMode mode) {
        this.turn = 0;
        this.score = 0;
        this.playerID = -1;
        this.player = "Guest";
        this.gameID = LaunchLogger.generateHash(0);
        this.gameMode = mode;
    }
    public GameInfo(int turn, int score, GameMode mode){
        if(player == "") player = "Guest";
        this.turn = turn;
        this.score = score;
        this.playerID = -1;
        this.player = "Guest";
        this.gameID = LaunchLogger.generateHash(~(turn * score));
        this.gameMode = mode;
    }
    public GameInfo(int turn, int score, int playerID, String player, GameMode mode){
        if(player == "") player = "Guest";
        this.turn = turn;
        this.score = score;
        this.playerID = playerID;
        this.player = player;
        this.gameID = LaunchLogger.generateHash((turn * score) ^ playerID);
        this.gameMode = mode;
    }

    public String getPlayer() {return player;}
    public long getPlayerID(){return playerID;}
    public long getGameID(){return gameID;}
    public GameMode getGameMode(){return gameMode;}

    public void setPlayer(String player, long ID) {
        if(player == null || player.equals("") || player.equals("Guest") || ID == 0 || ID == -1){
            this.player = "Guest";
            this.playerID = -1;
        } else {
            this.player = player;
            this.playerID = ID;
        }
    }

    public int getTurn() {return turn;}
    public int getScore() {return score;}

    public void setTurn(int turns) {this.turn = turns;}
    public void setScore(int score) {this.score = score;}
}
