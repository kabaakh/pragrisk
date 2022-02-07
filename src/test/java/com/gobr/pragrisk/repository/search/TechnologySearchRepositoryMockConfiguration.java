package com.gobr.pragrisk.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link TechnologySearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class TechnologySearchRepositoryMockConfiguration {

    @MockBean
    private TechnologySearchRepository mockTechnologySearchRepository;
}
