package com.example.bookido.catalog.db;

import com.example.bookido.catalog.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

}
