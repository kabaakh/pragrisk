package com.gobr.pragrisk.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.gobr.pragrisk.domain.Scenario;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Scenario} entity.
 */
public interface ScenarioSearchRepository extends ElasticsearchRepository<Scenario, UUID>, ScenarioSearchRepositoryInternal {}

interface ScenarioSearchRepositoryInternal {
    Stream<Scenario> search(String query);
}

class ScenarioSearchRepositoryInternalImpl implements ScenarioSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ScenarioSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Scenario> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Scenario.class).map(SearchHit::getContent).stream();
    }
}
