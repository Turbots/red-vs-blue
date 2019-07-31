package io.pivotal.workshop.redvsblue.game.scoring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redvsblue.scoring")
public class ScoringProperties {

    /**
     * Default odds are 50%
     **/
    private int odds = 50;

    public int getOdds() {
        return odds;
    }

    public void setOdds(int odds) {
        this.odds = odds;
    }
}
