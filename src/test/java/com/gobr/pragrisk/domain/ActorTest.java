package com.gobr.pragrisk.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gobr.pragrisk.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ActorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Actor.class);
        Actor actor1 = new Actor();
        actor1.setActorID(UUID.randomUUID());
        Actor actor2 = new Actor();
        actor2.setActorID(actor1.getActorID());
        assertThat(actor1).isEqualTo(actor2);
        actor2.setActorID(UUID.randomUUID());
        assertThat(actor1).isNotEqualTo(actor2);
        actor1.setActorID(null);
        assertThat(actor1).isNotEqualTo(actor2);
    }
}
