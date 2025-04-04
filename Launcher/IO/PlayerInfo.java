package Launcher.IO;

import javax.json.*;

public final class PlayerInfo implements JsonConvertible{
    private Username player;
    private int highTurn;
    private int highScore;
    private int recentTurn;
    private int recentScore;
    private long playerID;
    private GameTime time;

    public PlayerInfo() {
        this.highTurn = 0;
        this.highScore = 0;
        this.recentTurn = 0;
        this.recentScore = 0;
        this.playerID = -1;
        this.player = Username.getUsername("Guest");
        this.time = new GameTime();
    }
    public PlayerInfo(int highTurn, int highScore, int recentTurn, int recentScore){
        this.highTurn = highTurn;
        this.highScore = highScore;
        this.recentTurn = recentTurn;
        this.recentScore = recentScore;
        this.playerID = -1;
        this.player = Username.getUsername("Guest");
        this.time = new GameTime();
    }
    public PlayerInfo(int highTurn, int highScore, int recentTurn, int recentScore, long playerID, Username player){
        this.setPlayer(player, playerID);
        this.highTurn = highTurn;
        this.highScore = highScore;
        this.recentTurn = recentTurn;
        this.recentScore = recentScore;
        this.time = new GameTime();
    }
    public PlayerInfo(int highTurn, int highScore, int recentTurn, int recentScore, GameTime time, String playerID, Username player){
        long ID = Long.parseUnsignedLong(playerID, 16);
        this.setPlayer(player, ID);
        this.highTurn = highTurn;
        this.highScore = highScore;
        this.recentTurn = recentTurn;
        this.recentScore = recentScore;
        this.time = time;
    }

    public Username getPlayer() {return player;}
    public long getPlayerID(){return playerID;}

    public void setPlayer(Username player, long ID) {
        if(player == null || player.equals("") || player.equals("Guest") || ID == 0 || ID == -1){
            this.player = Username.getUsername("Guest");
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
        updateTime();
    }

    public void updateTime(){
        this.time = new GameTime();
    }

    @Override
    public String toString() {
        return "PlayerInfo[Player = " + player +
                ", Highest Turn = " + highTurn +
                ", Highest Score = " + highScore +
                ", Recent Turn = " + recentTurn +
                ", Recent Score = " + recentScore +
                ", Player ID = " + playerID +
                ", Time = " + time + "]";
    }

    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("Player", player.toString());
        builder.add("PlayerID", Long.toHexString(playerID));
        builder.add("Time", time.toJsonObject());
        JsonObject scoreElement = Json.createObjectBuilder().add("Score", highScore).add("Turn", highTurn).build();
        builder.add("Highest", scoreElement);
        scoreElement = Json.createObjectBuilder().add("Score", recentScore).add("Turn", recentTurn).build();
        builder.add("Recent", scoreElement);
        return builder;
    }
}
