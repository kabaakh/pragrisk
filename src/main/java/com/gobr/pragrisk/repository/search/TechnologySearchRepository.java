package com.gobr.pragrisk.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.gobr.pragrisk.domain.Technology;
import java.util.List;
import java.util.List;
import java.util.UUID;
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
 * Spring Data Elasticsearch repository for the {@link Technology} entity.
 */
public interface TechnologySearchRepository extends ElasticsearchRepository<Technology, UUID>, TechnologySearchRepositoryInternal {}

interface TechnologySearchRepositoryInternal {
    Page<Technology> search(String query, Pageable pageable);
}

class TechnologySearchRepositoryInternalImpl implements TechnologySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    TechnologySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Technology> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Technology> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Technology.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
