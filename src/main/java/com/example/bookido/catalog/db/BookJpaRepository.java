package com.example.bookido.catalog.db;

import com.example.bookido.catalog.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.authors")
    List<Book> findAllEager();

    List<Book> findByAuthors_nameContainsIgnoreCase(String name);

    List<Book> findAll();

    Optional<Book> findById(Long id);

    List<Book> findByTitleContainsIgnoreCase(String title);

    Optional<Book> findDistinctFirstByTitleContainsIgnoreCase(String title);

    @Query("SELECT b FROM Book b JOIN b.authors a" +
            " WHERE " +
            "lower(a.name) LIKE lower(concat('%', :name, '%')) "

    )
    List<Book> findByAuthor(@Param("name") String name);

    @Query("SELECT b FROM Book b JOIN b.authors a " +
            " WHERE " +
            " lower(b.title) LIKE lower(concat('%', :title, '%')) " +
            " AND lower(a.name) LIKE lower(concat('%', :author, '%')) "
    )
    List<Book> findByTitleAndAuthor(@Param("title") String title,
                                    @Param("author") String author);


}
