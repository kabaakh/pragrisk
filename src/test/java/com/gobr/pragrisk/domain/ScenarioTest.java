package com.gobr.pragrisk.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gobr.pragrisk.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ScenarioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Scenario.class);
        Scenario scenario1 = new Scenario();
        scenario1.setScenarioID(UUID.randomUUID());
        Scenario scenario2 = new Scenario();
        scenario2.setScenarioID(scenario1.getScenarioID());
        assertThat(scenario1).isEqualTo(scenario2);
        scenario2.setScenarioID(UUID.randomUUID());
        assertThat(scenario1).isNotEqualTo(scenario2);
        scenario1.setScenarioID(null);
        assertThat(scenario1).isNotEqualTo(scenario2);
    }
}
