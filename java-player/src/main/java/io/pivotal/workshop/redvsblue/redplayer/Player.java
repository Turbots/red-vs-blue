package io.pivotal.workshop.redvsblue.redplayer;

public class Player {

    private Long id;
    private String name;
    private Long score;
    private Team team;

    public Player() {
    }

    public Player(Long id, String name, Long score, Team team) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isTeam(Team team) {
        return this.team == team;
    }
}
