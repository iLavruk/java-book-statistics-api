package com.profitsoft.bookstats.service;

import com.profitsoft.bookstats.domain.Author;
import com.profitsoft.bookstats.dto.AuthorRequest;
import com.profitsoft.bookstats.repository.AuthorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthorService {

  private final AuthorRepository repository;

  public AuthorService(AuthorRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<Author> findAll() {
    return repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
  }

  public Author create(AuthorRequest request) {
    String name = normalizedName(request.getName());
    if (repository.existsByNameIgnoreCase(name)) {
      throw new ConflictException("Author with name '" + name + "' already exists");
    }
    Author author = new Author(name);
    return repository.save(author);
  }

  public Author update(Long id, AuthorRequest request) {
    Author author = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Author not found: " + id));
    String name = normalizedName(request.getName());
    Optional<Author> existing = repository.findByNameIgnoreCase(name);
    if (existing.isPresent() && !existing.get().getId().equals(id)) {
      throw new ConflictException("Author with name '" + name + "' already exists");
    }
    author.setName(name);
    return repository.save(author);
  }

  public void delete(Long id) {
    Author author = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Author not found: " + id));
    try {
      repository.delete(author);
    } catch (DataIntegrityViolationException ex) {
      throw new ConflictException("Cannot delete author with related books");
    }
  }

  private String normalizedName(String name) {
    return name == null ? null : name.trim();
  }
}
