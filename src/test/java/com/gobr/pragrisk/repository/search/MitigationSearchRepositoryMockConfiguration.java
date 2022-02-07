package com.gobr.pragrisk.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link MitigationSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class MitigationSearchRepositoryMockConfiguration {

    @MockBean
    private MitigationSearchRepository mockMitigationSearchRepository;
}
