package com.example.restwithspringbootandjavaerudio.repositories;

import com.example.restwithspringbootandjavaerudio.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonRepository extends JpaRepository<Person, Long> {}