package Launcher;

import Launcher.IO.LaunchLogger;

public class GameInfo {
    private String player;
    private int turn;
    private int score;
    private long playerID;
    private long gameID;

    public GameInfo() {
        this.turn = 0;
        this.score = 0;
        this.playerID = -1;
        this.player = "Guest";
        this.gameID = LaunchLogger.generateHash(0);
    }
    public GameInfo(int turn, int score){
        if(player == "") player = "Guest";
        this.turn = turn;
        this.score = score;
        this.playerID = -1;
        this.player = "Guest";
        this.gameID = LaunchLogger.generateHash(~(turn * score));
    }
    public GameInfo(int turn, int score, int playerID, String player){
        if(player == "") player = "Guest";
        this.turn = turn;
        this.score = score;
        this.playerID = playerID;
        this.player = player;
        this.gameID = LaunchLogger.generateHash((turn * score) ^ playerID);
    }

    public String getPlayer() {return player;}
    public long getPlayerID(){return playerID;}
    public long getGameID(){return gameID;}

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
