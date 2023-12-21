package org.example;

import io.javalin.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        PlayerController playerController = new PlayerController();
        MatchController matchController = new MatchController();

        // Players Endpoints
        app.get("/players", playerController::getAllPlayers);
        app.get("/players/{id}", playerController::getPlayer);
        app.post("/players", playerController::createPlayer);
        app.put("/players/{id}", playerController::updatePlayer);
        app.delete("/players/{id}", playerController::deletePlayer);

        // Matches Endpoints
        app.get("/matches", matchController::getAllMatches);
        app.get("/matches/{id}", matchController::getMatch);
        app.post("/matches", matchController::createMatch);
        app.delete("/matches/{id}", matchController::deleteMatch);
    }
}