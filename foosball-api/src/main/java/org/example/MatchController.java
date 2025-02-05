package org.example;

import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class MatchController {
    private List<Match> matches = new ArrayList<>();

    MatchController() {
        matches.add(new Match("1", "2020-01-01", "Las Vegas", "Tony Spredeman", "Ryan Moore", "Tony Spredeman"));
        matches.add(new Match("2", "2024-01-11", "Chillout, HEIG-VD", "Lucas Charbonnier", "Rémi Ançay", "Rémi Ançay"));
    }

    // Matches CRUD Operations
    public void getAllMatches(Context ctx) {
        ctx.json(matches);
    }

    public void getMatch(Context ctx) {
        String matchId = ctx.pathParam("id");
        Match match = findMatchById(matchId);
        if (match != null) {
            ctx.json(match);
        } else {
            ctx.status(404).result("Match not found");
        }
    }

    public void createMatch(Context ctx) {
        Match match = ctx.bodyAsClass(Match.class);
        matches.add(match);
        ctx.status(201);
    }

    public void deleteMatch(Context ctx) {
        String matchId = ctx.pathParam("id");
        Match match = findMatchById(matchId);
        if (match != null) {
            matches.remove(match);
            ctx.status(204);
        } else {
            ctx.status(404).result("Match not found");
        }
    }

    public Match findMatchById(String matchId) {
        return matches.stream()
                .filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElse(null);
    }
}
