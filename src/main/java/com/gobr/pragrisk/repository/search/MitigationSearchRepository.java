package com.gobr.pragrisk.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.gobr.pragrisk.domain.Mitigation;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Mitigation} entity.
 */
public interface MitigationSearchRepository extends ElasticsearchRepository<Mitigation, UUID>, MitigationSearchRepositoryInternal {}

interface MitigationSearchRepositoryInternal {
    Stream<Mitigation> search(String query);
}

class MitigationSearchRepositoryInternalImpl implements MitigationSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    MitigationSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Mitigation> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Mitigation.class).map(SearchHit::getContent).stream();
    }
}
