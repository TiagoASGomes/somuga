package org.somuga.filters.like;

import org.somuga.entity.Like;
import org.somuga.filters.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LikeSpecificationBuilder {

    private final List<SearchCriteria> params;

    public LikeSpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final LikeSpecificationBuilder with(String key, String value) {
        params.add(new SearchCriteria(key, value));
        return this;
    }

    public final LikeSpecificationBuilder with(SearchCriteria searchCriteria) {
        params.add(searchCriteria);
        return this;
    }

    public Specification<Like> build() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<Like> result =
                new LikeSpecification(params.get(0));
        for (int idx = 1; idx < params.size(); idx++) {
            SearchCriteria criteria = params.get(idx);
            result = Specification.where(result).and(new LikeSpecification(criteria));
        }
        return result;
    }
}
