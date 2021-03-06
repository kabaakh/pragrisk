package com.gobr.pragrisk.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.gobr.pragrisk.domain.Mitigation;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Mitigation} entity.
 */
public interface MitigationSearchRepository extends ElasticsearchRepository<Mitigation, Long>, MitigationSearchRepositoryInternal {}

interface MitigationSearchRepositoryInternal {
    Page<Mitigation> search(String query, Pageable pageable);
}

class MitigationSearchRepositoryInternalImpl implements MitigationSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    MitigationSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Mitigation> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Mitigation> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Mitigation.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
