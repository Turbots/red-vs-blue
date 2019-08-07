package io.pivotal.workshop.redvsblue.redplayer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OutputChannels {

    @Output("balls")
    MessageChannel output();
}
