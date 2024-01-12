package org.example;

import io.javalin.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        PlayerController playerController = new PlayerController();
        MatchController matchController = new MatchController();

        // Players Endpoints
        app.get("/api/players", playerController::getAllPlayers);
        app.get("/api/players/{id}", playerController::getPlayer);
        app.post("/api/players", playerController::createPlayer);
        app.put("/api/players/{id}", playerController::updatePlayer);
        app.delete("/api/players/{id}", playerController::deletePlayer);

        // Matches Endpoints
        app.get("/api/matches", matchController::getAllMatches);
        app.get("/api/matches/{id}", matchController::getMatch);
        app.post("/api/matches", matchController::createMatch);
        app.delete("/api/matches/{id}", matchController::deleteMatch);
    }
}