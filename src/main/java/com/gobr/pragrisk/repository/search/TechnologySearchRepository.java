package com.gobr.pragrisk.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.gobr.pragrisk.domain.Technology;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Technology} entity.
 */
public interface TechnologySearchRepository extends ElasticsearchRepository<Technology, UUID>, TechnologySearchRepositoryInternal {}

interface TechnologySearchRepositoryInternal {
    Stream<Technology> search(String query);
}

class TechnologySearchRepositoryInternalImpl implements TechnologySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    TechnologySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Technology> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Technology.class).map(SearchHit::getContent).stream();
    }
}
