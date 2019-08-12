package io.pivotal.workshop.redvsblue.game;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findFirst5ByTeam(Team team, Sort score);

    long countPlayerByTeam(Team red);
    boolean existsByName(String name);

    @Query("select sum(score) from Player p where p.team = :team")
    Long countScoresForTeam(Team team);

    @Modifying
    @Query("update Player p set p.score = 0")
    void resetScores();

    @Modifying
    @Query("update Player p set p.score = p.score + 1 where p = :thrower")
    void givePointTo(Player thrower);

}
