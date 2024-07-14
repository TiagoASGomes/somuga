package org.somuga.filters.movie;

import org.somuga.entity.Movie;
import org.somuga.filters.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MovieSpecificationBuilder {

    private final List<SearchCriteria> params;

    public MovieSpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final MovieSpecificationBuilder with(String key, String value) {
        params.add(new SearchCriteria(key, value));
        return this;
    }

    public final MovieSpecificationBuilder with(SearchCriteria searchCriteria) {
        params.add(searchCriteria);
        return this;
    }

    public Specification<Movie> build() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<Movie> result =
                new MovieSpecification(params.get(0));
        for (int idx = 1; idx < params.size(); idx++) {
            SearchCriteria criteria = params.get(idx);
            result = Specification.where(result).and(new MovieSpecification(criteria));
        }
        return result;
    }
}
