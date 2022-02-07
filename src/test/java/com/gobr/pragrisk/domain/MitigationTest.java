package com.gobr.pragrisk.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gobr.pragrisk.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MitigationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mitigation.class);
        Mitigation mitigation1 = new Mitigation();
        mitigation1.setId(1L);
        Mitigation mitigation2 = new Mitigation();
        mitigation2.setId(mitigation1.getId());
        assertThat(mitigation1).isEqualTo(mitigation2);
        mitigation2.setId(2L);
        assertThat(mitigation1).isNotEqualTo(mitigation2);
        mitigation1.setId(null);
        assertThat(mitigation1).isNotEqualTo(mitigation2);
    }
}
