package com.example.restwithspringbootandjavaerudio.repositories;

import com.example.restwithspringbootandjavaerudio.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {}