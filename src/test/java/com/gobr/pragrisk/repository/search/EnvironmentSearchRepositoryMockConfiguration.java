package com.gobr.pragrisk.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link EnvironmentSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class EnvironmentSearchRepositoryMockConfiguration {

    @MockBean
    private EnvironmentSearchRepository mockEnvironmentSearchRepository;
}
