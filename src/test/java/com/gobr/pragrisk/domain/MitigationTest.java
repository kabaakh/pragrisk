package com.gobr.pragrisk.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gobr.pragrisk.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MitigationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mitigation.class);
        Mitigation mitigation1 = new Mitigation();
        mitigation1.setMitigationID(UUID.randomUUID());
        Mitigation mitigation2 = new Mitigation();
        mitigation2.setMitigationID(mitigation1.getMitigationID());
        assertThat(mitigation1).isEqualTo(mitigation2);
        mitigation2.setMitigationID(UUID.randomUUID());
        assertThat(mitigation1).isNotEqualTo(mitigation2);
        mitigation1.setMitigationID(null);
        assertThat(mitigation1).isNotEqualTo(mitigation2);
    }
}
