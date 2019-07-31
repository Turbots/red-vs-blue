package io.pivotal.workshop.redvsblue.game.scoring.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface InputChannels {

    @Input
    SubscribableChannel balls();
}
