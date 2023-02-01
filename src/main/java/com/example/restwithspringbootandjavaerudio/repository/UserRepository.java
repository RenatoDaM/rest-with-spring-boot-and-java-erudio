package com.example.restwithspringbootandjavaerudio.repository;

import com.example.restwithspringbootandjavaerudio.model.Person;
import com.example.restwithspringbootandjavaerudio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Repare que é uma query de um objeto, e não do banco em si. Uso UserName e não user_name
    //@Query("SELECT u FROM User WHERE u.UserName = :userName")
    @Query("SELECT userName FROM User WHERE userName = :userName")
    User findByUserName(@Param("userName") String userName);
}
