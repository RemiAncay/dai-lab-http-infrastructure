package org.example;

import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerController {
    private Map<String, Player> players = new HashMap<>();

    PlayerController() {
        players.put("1", new Player("1", "John Doe", "1990-01-01"));
        players.put("2", new Player("2", "Jane Doe", "1990-01-01"));
    }

    // Players CRUD Operations
    public void getAllPlayers(Context ctx) {
        ctx.json(new ArrayList<>(players.values()));
    }

    public void getPlayer(Context ctx) {
        String playerId = ctx.pathParam("id");
        Player player = players.get(playerId);
        if (player != null) {
            ctx.json(player);
        } else {
            ctx.status(404).result("Player not found");
        }
    }

    public void createPlayer(Context ctx) {
        Player player = ctx.bodyAsClass(Player.class);
        players.put(player.getId(), player);
        ctx.status(201);
    }

    public void updatePlayer(Context ctx) {
        String playerId = ctx.pathParam("id");
        Player existingPlayer = players.get(playerId);
        if (existingPlayer != null) {
            Player updatedPlayer = ctx.bodyAsClass(Player.class);
            existingPlayer.setName(updatedPlayer.getName());
            existingPlayer.setDateOfBirth(updatedPlayer.getDateOfBirth());
            ctx.status(204);
        } else {
            ctx.status(404).result("Player not found");
        }
    }

    public void deletePlayer(Context ctx) {
        String playerId = ctx.pathParam("id");
        Player removedPlayer = players.remove(playerId);
        if (removedPlayer != null) {
            ctx.status(204);
        } else {
            ctx.status(404).result("Player not found");
        }
    }
}
