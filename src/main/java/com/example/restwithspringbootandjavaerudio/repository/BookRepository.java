package com.example.restwithspringbootandjavaerudio.repository;

import com.example.restwithspringbootandjavaerudio.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {}