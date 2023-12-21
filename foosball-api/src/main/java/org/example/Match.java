package org.example;

public class Match {
    private String id;
    private String date;
    private String place;
    private String player1Name;
    private String player2Name;
    private String winner;

    Match(String id, String date, String place, String player1Name, String player2Name, String winner) {
        this.id = id;
        this.date = date;
        this.place = place;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.winner = winner;
    }

    //Getters
    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public String getWinner() {
        return winner;
    }
}
