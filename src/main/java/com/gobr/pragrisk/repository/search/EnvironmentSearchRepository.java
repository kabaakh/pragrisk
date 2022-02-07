package com.gobr.pragrisk.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.gobr.pragrisk.domain.Environment;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Environment} entity.
 */
public interface EnvironmentSearchRepository extends ElasticsearchRepository<Environment, Long>, EnvironmentSearchRepositoryInternal {}

interface EnvironmentSearchRepositoryInternal {
    Stream<Environment> search(String query);
}

class EnvironmentSearchRepositoryInternalImpl implements EnvironmentSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    EnvironmentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Environment> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Environment.class).map(SearchHit::getContent).stream();
    }
}
