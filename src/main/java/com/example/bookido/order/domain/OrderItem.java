package com.example.bookido.order.domain;

import com.example.bookido.catalog.domain.Book;
import com.example.bookido.jpa.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    private int quantity;
}
