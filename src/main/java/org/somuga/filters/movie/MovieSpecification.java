package org.somuga.filters.movie;

import jakarta.persistence.criteria.*;
import org.somuga.entity.Movie;
import org.somuga.entity.MovieCrewRole;
import org.somuga.filters.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecification implements Specification<Movie> {

    private final SearchCriteria criteria;

    public MovieSpecification(SearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Movie> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        String strToSearch = criteria.getValue().toLowerCase();
        return switch (criteria.getKey()) {
            case "title" -> criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + strToSearch + "%");
            case "crewId" ->
                    criteriaBuilder.equal(movieCrewRoleJoin(root).get("id").get("movieCrewId"), Long.parseLong(strToSearch));
            default -> null;
        };
    }

    private Join<Movie, MovieCrewRole> movieCrewRoleJoin(Root<Movie> root) {
        return root.join("movieCrew", JoinType.INNER);
    }
}
