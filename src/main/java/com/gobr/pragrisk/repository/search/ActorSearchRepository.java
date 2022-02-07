package com.gobr.pragrisk.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.gobr.pragrisk.domain.Actor;
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
 * Spring Data Elasticsearch repository for the {@link Actor} entity.
 */
public interface ActorSearchRepository extends ElasticsearchRepository<Actor, UUID>, ActorSearchRepositoryInternal {}

interface ActorSearchRepositoryInternal {
    Page<Actor> search(String query, Pageable pageable);
}

class ActorSearchRepositoryInternalImpl implements ActorSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ActorSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Actor> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Actor> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Actor.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
