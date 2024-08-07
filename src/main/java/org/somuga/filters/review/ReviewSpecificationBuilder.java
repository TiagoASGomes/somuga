package org.somuga.filters.review;

import org.somuga.entity.Review;
import org.somuga.filters.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ReviewSpecificationBuilder {
    private final List<SearchCriteria> params;

    public ReviewSpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final ReviewSpecificationBuilder with(String key, String value) {
        params.add(new SearchCriteria(key, value));
        return this;
    }

    public final ReviewSpecificationBuilder with(SearchCriteria searchCriteria) {
        params.add(searchCriteria);
        return this;
    }

    public Specification<Review> build() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<Review> result =
                new ReviewSpecification(params.get(0));
        for (int idx = 1; idx < params.size(); idx++) {
            SearchCriteria criteria = params.get(idx);
            result = Specification.where(result).and(new ReviewSpecification(criteria));
        }
        return result;
    }
}
