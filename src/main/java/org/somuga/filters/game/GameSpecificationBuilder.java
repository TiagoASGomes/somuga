package org.somuga.filters.game;

import org.somuga.entity.Game;
import org.somuga.filters.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GameSpecificationBuilder {

    private final List<SearchCriteria> params;

    public GameSpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final GameSpecificationBuilder with(String key, String value) {
        params.add(new SearchCriteria(key, value));
        return this;
    }

    public final GameSpecificationBuilder with(SearchCriteria searchCriteria) {
        params.add(searchCriteria);
        return this;
    }

    public Specification<Game> build() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<Game> result =
                new GameSpecification(params.get(0));
        for (int idx = 1; idx < params.size(); idx++) {
            SearchCriteria criteria = params.get(idx);
            result = Specification.where(result).and(new GameSpecification(criteria));
        }
        return result;
    }
}
