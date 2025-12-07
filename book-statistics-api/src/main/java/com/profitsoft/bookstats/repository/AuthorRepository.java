package com.profitsoft.bookstats.repository;

import com.profitsoft.bookstats.domain.Author;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

  Optional<Author> findByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCase(String name);
}
