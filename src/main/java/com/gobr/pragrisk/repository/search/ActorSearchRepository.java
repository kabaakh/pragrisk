package com.gobr.pragrisk.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.gobr.pragrisk.domain.Actor;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Actor} entity.
 */
public interface ActorSearchRepository extends ElasticsearchRepository<Actor, UUID>, ActorSearchRepositoryInternal {}

interface ActorSearchRepositoryInternal {
    Stream<Actor> search(String query);
}

class ActorSearchRepositoryInternalImpl implements ActorSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ActorSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Actor> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Actor.class).map(SearchHit::getContent).stream();
    }
}
