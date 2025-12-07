package com.profitsoft.bookstats.repository.specification;

import com.profitsoft.bookstats.domain.Book;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecifications {

  private BookSpecifications() {
  }

  public static Specification<Book> filter(Long authorId, Integer yearPublished, String title) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (authorId != null) {
        predicates.add(cb.equal(root.get("author").get("id"), authorId));
      }
      if (yearPublished != null) {
        predicates.add(cb.equal(root.get("yearPublished"), yearPublished));
      }
      if (title != null && !title.isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
