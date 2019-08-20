package io.pivotal.workshop.redvsblue.redplayer;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("red")
public class RedPlayerProperties {

    private String refereeUrl = "http://localhost:8080";

    public String getRefereeUrl() {
        return refereeUrl;
    }

    public void setRefereeUrl(String refereeUrl) {
        this.refereeUrl = refereeUrl;
    }
}
