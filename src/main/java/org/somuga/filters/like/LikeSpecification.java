package org.somuga.filters.like;

import jakarta.persistence.criteria.*;
import org.somuga.entity.Like;
import org.somuga.entity.Media;
import org.somuga.entity.User;
import org.somuga.filters.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class LikeSpecification implements Specification<Like> {

    private final SearchCriteria criteria;

    public LikeSpecification(SearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Like> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        String strToSearch = criteria.getValue().toLowerCase();
        return switch (criteria.getKey()) {
            case "userId" -> criteriaBuilder.equal(userJoin(root).get("id"), strToSearch);
            case "mediaId" -> criteriaBuilder.equal(mediaJoin(root).get("id"), Long.parseLong(strToSearch));
            default -> null;
        };
    }

    private Join<Like, User> userJoin(Root<Like> root) {
        return root.join("user", JoinType.INNER);
    }

    private Join<Like, Media> mediaJoin(Root<Like> root) {
        return root.join("media", JoinType.INNER);
    }
}
