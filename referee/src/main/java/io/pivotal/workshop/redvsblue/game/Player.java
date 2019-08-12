package io.pivotal.workshop.redvsblue.game;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private Long score;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Team team;

    public Player() {
    }

    public Player(String name, Team team, Long score) {
        this.name = name;
        this.team = team;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", score=" + score +
                ", team=" + team +
                '}';
    }
}
