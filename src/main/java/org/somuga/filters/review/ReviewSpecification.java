package org.somuga.filters.review;

import jakarta.persistence.criteria.*;
import org.somuga.entity.Media;
import org.somuga.entity.Review;
import org.somuga.entity.User;
import org.somuga.filters.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class ReviewSpecification implements Specification<Review> {

    private final SearchCriteria criteria;

    public ReviewSpecification(SearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Review> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        String strToSearch = criteria.getValue().toLowerCase();
        return switch (criteria.getKey()) {
            case "userId" -> criteriaBuilder.equal(userJoin(root).get("id"), strToSearch);
            case "mediaId" -> criteriaBuilder.equal(mediaJoin(root).get("id"), Long.parseLong(strToSearch));
            default -> null;
        };
    }

    private Join<Review, User> userJoin(Root<Review> root) {
        return root.join("user", JoinType.INNER);
    }

    private Join<Review, Media> mediaJoin(Root<Review> root) {
        return root.join("media", JoinType.INNER);
    }

}
