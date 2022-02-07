package com.gobr.pragrisk.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gobr.pragrisk.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TechnologyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Technology.class);
        Technology technology1 = new Technology();
        technology1.setTechnologyID(UUID.randomUUID());
        Technology technology2 = new Technology();
        technology2.setTechnologyID(technology1.getTechnologyID());
        assertThat(technology1).isEqualTo(technology2);
        technology2.setTechnologyID(UUID.randomUUID());
        assertThat(technology1).isNotEqualTo(technology2);
        technology1.setTechnologyID(null);
        assertThat(technology1).isNotEqualTo(technology2);
    }
}
