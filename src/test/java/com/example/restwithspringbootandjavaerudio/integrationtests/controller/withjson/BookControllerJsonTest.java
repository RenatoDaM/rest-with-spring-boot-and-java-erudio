package com.example.restwithspringbootandjavaerudio.integrationtests.controller.withjson;

import com.example.restwithspringbootandjavaerudio.configs.TestConfigs;
import com.example.restwithspringbootandjavaerudio.integrationtests.testcontainer.AbstractIntegrationTest;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.AccountCredentialsVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.BookVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.PersonVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static BookVO bookVO;


    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        bookVO = new BookVO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var accessToken = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class)
                .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAMETER_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    public void testCreate() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(bookVO)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        bookVO = persistedBook;

        assertNotNull(persistedBook);

        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getAuthor());

        assertTrue(persistedBook.getKey() > 0);

        assertEquals("Kandar Anzudere", persistedBook.getAuthor());
        assertEquals("Darkness through your heart", persistedBook.getTitle());
        assertEquals(40.00, persistedBook.getPrice());


    }

    @Test
    @Order(2)
    public void testUpdate() throws JsonMappingException, JsonProcessingException {
        bookVO.setAuthor("Piquet Souto Maior");

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(bookVO)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        bookVO = persistedBook;

        assertNotNull(persistedBook);

        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getAuthor());

        assertEquals(persistedBook.getKey(), bookVO.getKey());

        assertEquals("Piquet Souto Maior", persistedBook.getAuthor());
        assertEquals("Darkness through your heart", persistedBook.getTitle());
        assertEquals(40.00, persistedBook.getPrice());

    }

    @Test
    @Order(3)
    public void testFindById() throws JsonMappingException, JsonProcessingException {
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAMETER_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .pathParam("id", bookVO.getKey())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO persistedPerson = objectMapper.readValue(content, BookVO.class);
        bookVO = persistedPerson;

        assertNotNull(persistedPerson);

        assertNotNull(persistedPerson.getKey());
        assertNotNull(persistedPerson.getLaunchDate());
        assertNotNull(persistedPerson.getTitle());
        assertNotNull(persistedPerson.getPrice());
        assertNotNull(persistedPerson.getAuthor());

        assertTrue(persistedPerson.getKey() > 0);

        assertEquals("Piquet Souto Maior", persistedPerson.getAuthor());
        assertEquals("Darkness through your heart", persistedPerson.getTitle());
        assertEquals(40.00, persistedPerson.getPrice());

    }


    @Test
    @Order(4)
    public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAMETER_ORIGIN, TestConfigs.ORIGIN_SEMERU)
                .pathParam("id", bookVO.getKey())
                .when()
                .get("{id}")
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();


        assertNotNull(content);
        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(5)
    public void testDelete() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("id", bookVO.getKey())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    public void testFindAll() throws JsonMappingException, JsonProcessingException {
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();


        List<BookVO> people = objectMapper.readValue(content, new TypeReference<List<BookVO>>() {});

        BookVO dataBaseBook = people.get(0);

        assertNotNull(dataBaseBook);
        assertEquals("Michael C. Feathers", dataBaseBook.getAuthor());
        assertEquals("Working effectively with legacy code", dataBaseBook.getTitle());
        assertEquals(49.00, dataBaseBook.getPrice());


        dataBaseBook = people.get(1);

        assertNotNull(dataBaseBook);
        assertEquals("Ralph Johnson, Erich Gamma, John Vlissides e Richard Helm", dataBaseBook.getAuthor());
        assertEquals("Design Patterns", dataBaseBook.getTitle());
        assertEquals(45.00, dataBaseBook.getPrice());


    }

    @Test
    @Order(7)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        given().spec(specificationWithoutToken)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();
    }


    private void mockPerson() {

        bookVO.setAuthor("Kandar Anzudere");
        bookVO.setLaunchDate(new Date());
        bookVO.setPrice(40.00);
        bookVO.setTitle("Darkness through your heart");
    }
}
