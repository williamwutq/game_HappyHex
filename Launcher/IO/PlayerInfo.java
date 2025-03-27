package Launcher.IO;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public final class PlayerInfo implements JsonConvertible{
    private String player;
    private int highTurn;
    private int highScore;
    private int recentTurn;
    private int recentScore;
    private long playerID;

    public PlayerInfo() {
        this.highTurn = 0;
        this.highScore = 0;
        this.recentTurn = 0;
        this.recentScore = 0;
        this.playerID = -1;
        this.player = "Guest";
    }
    public PlayerInfo(int highTurn, int highScore, int recentTurn, int recentScore){
        if(player == "") player = "Guest";
        this.highTurn = highTurn;
        this.highScore = highScore;
        this.recentTurn = recentTurn;
        this.recentScore = recentScore;
        this.playerID = -1;
        this.player = "Guest";
    }
    public PlayerInfo(int highTurn, int highScore, int recentTurn, int recentScore, int playerID, String player){
        if(player == "") player = "Guest";
        this.highTurn = highTurn;
        this.highScore = highScore;
        this.recentTurn = recentTurn;
        this.recentScore = recentScore;
        this.playerID = playerID;
        this.player = player;
    }

    public String getPlayer() {return player;}
    public long getPlayerID(){return playerID;}

    public void setPlayer(String player, long ID) {
        if(player == null || player.equals("") || player.equals("Guest") || ID == 0 || ID == -1){
            this.player = "Guest";
            this.playerID = -1;
        } else {
            this.player = player;
            this.playerID = ID;
        }
    }

    public int getHighTurn() {return highTurn;}
    public int getHighScore() {return highScore;}
    public int getRecentTurn() {return recentTurn;}
    public int getRecentScore() {return recentScore;}

    public void setHighTurn(int turns) {this.highTurn = turns;}
    public void setHighScore(int score) {this.highScore = score;}
    public void setRecentTurn(int turns) {this.recentTurn = turns;}
    public void setRecentScore(int score) {this.recentScore = score;}

    public void updateHigh(){
        if(highTurn < recentTurn){
            highTurn = recentTurn;
        }
        if(highScore < recentScore){
            highScore = recentScore;
        }
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("Player", player);
        builder.add("PlayerID", playerID);
        // builder.add(time) add time to be implemented
        JsonObject scoreElement = Json.createObjectBuilder().add("Score", highScore).add("Turn", highTurn).build();
        builder.add("Highest", scoreElement);
        scoreElement = Json.createObjectBuilder().add("Score", recentScore).add("Turn", recentTurn).build();
        builder.add("Recent", scoreElement);
        return builder.build();
    }
}
