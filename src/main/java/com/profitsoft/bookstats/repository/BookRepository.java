package com.profitsoft.bookstats.repository;

import com.profitsoft.bookstats.domain.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

  @Query("select b from Book b join fetch b.author where b.id = :id")
  Optional<Book> fetchByIdWithAuthor(@Param("id") Long id);
}
