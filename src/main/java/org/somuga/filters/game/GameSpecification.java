package org.somuga.filters.game;

import jakarta.persistence.criteria.*;
import org.somuga.entity.Developer;
import org.somuga.entity.Game;
import org.somuga.entity.GameGenre;
import org.somuga.entity.Platform;
import org.somuga.filters.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class GameSpecification implements Specification<Game> {

    private final SearchCriteria criteria;

    public GameSpecification(SearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }


    @Override
    public Predicate toPredicate(Root<Game> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        String strToSearch = criteria.getValue().toLowerCase();
        return switch (criteria.getKey()) {
            case "title" -> criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + strToSearch + "%");
            case "developer" ->
                    criteriaBuilder.like(criteriaBuilder.lower(developerJoin(root).get("developerName")), "%" + strToSearch + "%");
            case "platform" ->
                    criteriaBuilder.like(criteriaBuilder.lower(platformJoin(root).get("platformName")), "%" + strToSearch + "%");
            case "genre" ->
                    criteriaBuilder.like(criteriaBuilder.lower(genreJoin(root).get("genre")), "%" + strToSearch + "%");
            default -> null;
        };
    }

    private Join<Game, Developer> developerJoin(Root<Game> root) {
        return root.join("developer", JoinType.INNER);
    }

    private Join<Game, Platform> platformJoin(Root<Game> root) {
        return root.join("platforms", JoinType.INNER);
    }

    private Join<Game, GameGenre> genreJoin(Root<Game> root) {
        return root.join("genres", JoinType.INNER);
    }
}
