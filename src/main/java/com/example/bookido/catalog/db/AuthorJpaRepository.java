package com.example.bookido.catalog.db;

import com.example.bookido.catalog.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {

}
