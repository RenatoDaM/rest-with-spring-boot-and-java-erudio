package com.example.restwithspringbootandjavaerudio.integrationtests.repositories;

import com.example.restwithspringbootandjavaerudio.integrationtests.testcontainer.AbstractIntegrationTest;
import com.example.restwithspringbootandjavaerudio.model.Person;
import com.example.restwithspringbootandjavaerudio.repositories.PersonRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setup() {
        person = new Person();
    }

    @Test
    @Order(1)
    public void testFindPersonsByName() {
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "firstName"));

        // O REPOSITORY RETORNA UMA PAGE, ENTÃO USAMOS O MÉTODO getContent() e dps .get(0) pra pegar
        // a primeira person, ou seja, a person no índice 0

        person = repository.findPersonsByName("dgard", pageable).getContent().get(0);
        assertNotNull(person);
        assertTrue(person.getId() > 0);
        assertTrue(person.getEnabled());
        assertEquals("Edgard", person.getFirstName());
        assertEquals("Detoc", person.getLastName());
        assertEquals("Male", person.getGender());
        assertEquals("5859 Di Loreto Alley", person.getAddress());
    }

    @Test
    @Order(2)
    public void disablePerson() {
        repository.disablePerson(person.getId());

        // pego dnv a pessoa no banco de dados pra atualizar seu estado, já que o disable person
        // mexe no banco de dados e não é uma espécie de "set" AQUI NO OBJETO JAVA
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonsByName("dgard", pageable).getContent().get(0);

        assertNotNull(person);
        assertTrue(person.getId() > 0);
        assertFalse(person.getEnabled());
        assertEquals("Edgard", person.getFirstName());
        assertEquals("Detoc", person.getLastName());
        assertEquals("Male", person.getGender());
        assertEquals("5859 Di Loreto Alley", person.getAddress());


    }
}
