package com.example.restwithspringbootandjavaerudio.repositories;

import com.example.restwithspringbootandjavaerudio.model.Person;
import com.example.restwithspringbootandjavaerudio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PersonRepository extends JpaRepository<Person, Long> {

    @Modifying
    @Query("UPDATE Person p SET p.enabled= false WHERE p.id = :id")
    void disablePerson(@Param("id") Long id);

    // SÓ DE LEITURA NÃO PRECISA DO MODIFYING

}