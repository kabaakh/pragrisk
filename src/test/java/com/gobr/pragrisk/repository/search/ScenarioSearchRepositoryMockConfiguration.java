package com.gobr.pragrisk.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ScenarioSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ScenarioSearchRepositoryMockConfiguration {

    @MockBean
    private ScenarioSearchRepository mockScenarioSearchRepository;
}
