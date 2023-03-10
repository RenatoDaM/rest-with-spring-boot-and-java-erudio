package com.example.restwithspringbootandjavaerudio.integrationtests.controller.withyaml;

import com.example.restwithspringbootandjavaerudio.configs.TestConfigs;
import com.example.restwithspringbootandjavaerudio.integrationtests.controller.withyaml.mapper.YamlMapper;
import com.example.restwithspringbootandjavaerudio.integrationtests.testcontainer.AbstractIntegrationTest;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.AccountCredentialsVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.BookVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.TokenVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.wrappers.book.PagedModelBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerYamlTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static YamlMapper objectMapper;
    private static BookVO bookVO;


    @BeforeAll
    public static void setup() {
        objectMapper = new YamlMapper();
        bookVO = new BookVO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var accessToken = given()
                .basePath("/auth/signin")
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(user, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class, objectMapper)
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

        var persistedBook = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(bookVO, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, objectMapper);

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

        var persistedBook = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .body(bookVO, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, objectMapper);

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
        var persistedBook = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .header(TestConfigs.HEADER_PARAMETER_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .pathParam("id", bookVO.getKey())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(BookVO.class, objectMapper);

        bookVO = persistedBook;

        assertNotNull(persistedBook);

        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getAuthor());

        assertTrue(persistedBook.getKey() > 0);

        assertEquals("Piquet Souto Maior", persistedBook.getAuthor());
        assertEquals("Darkness through your heart", persistedBook.getTitle());
        assertEquals(40.00, persistedBook.getPrice());

    }


    @Test
    @Order(4)
    public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .header(TestConfigs.HEADER_PARAMETER_ORIGIN, TestConfigs.ORIGIN_SEMERU)
                .pathParam("id", bookVO.getKey())
                .when()
                .get("{id}")
                .then()
                .statusCode(403)
                .extract()
                .body().asString();

        System.out.println(content);
        assertNotNull(content);
        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(5)
    public void testDelete() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
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
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .queryParam("page", 0, "size", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedModelBook.class, objectMapper);

        List<BookVO> people = content.getContent();

        BookVO dataBaseBook = people.get(0);
        assertNotNull(dataBaseBook);
        assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", dataBaseBook.getAuthor());
        assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informa????o cotidiana", dataBaseBook.getTitle());
        assertEquals(54.0, dataBaseBook.getPrice());


        dataBaseBook = people.get(1);

        assertNotNull(dataBaseBook);
        assertEquals("Robert C. Martin", dataBaseBook.getAuthor());
        assertEquals("Clean Code", dataBaseBook.getTitle());
        assertEquals(77.0, dataBaseBook.getPrice());

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
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .when()
                .get()
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();
    }

    @Test
    @Order(6)
    public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

/*        assertTrue(content.contains("- rel: \"first\"\n" +
                "  href: \"http://localhost:8888/api/book/v1?direction=asc&page=0&size=10&sort=title,asc\""));
        assertTrue(content.contains("- rel: \"self\"\n" +
                "  href: \"http://localhost:8888/api/book/v1?page=0&size=10&direction=asc\""));
        assertTrue(content.contains("- rel: \"next\"\n" +
                "  href: \"http://localhost:8888/api/book/v1?direction=asc&page=1&size=10&sort=title,asc\""));
        assertTrue(content.contains("- rel: \"last\"\n" +
                "  href: \"http://localhost:8888/api/book/v1?direction=asc&page=1&size=10&sort=title,asc\""));
        assertTrue(content.contains("page:\n" +
                "  size: 10\n" +
                "  totalElements: 15\n" +
                "  totalPages: 2\n" +
                "  number: 0"));
        assertTrue(content.contains("content:\n" +
                "- id: 12\n" +
                "  author: \"Viktor Mayer-Schonberger e Kenneth Kukier\"\n" +
                "  launchDate: 1510020000000\n" +
                "  price: 54.0\n" +
                "  title: \"Big Data: como extrair volume, variedade, velocidade e valor da avalanche\\\n" +
                "    \\ de informa????o cotidiana\"\n" +
                "  links:\n" +
                "  - rel: \"self\"\n" +
                "    href: \"http://localhost:8888/api/book/v1/12\"\n" +
                "  links: []"));*/
    }
    private void mockPerson() {

        bookVO.setAuthor("Kandar Anzudere");
        bookVO.setLaunchDate(new Date());
        bookVO.setPrice(40.00);
        bookVO.setTitle("Darkness through your heart");
    }
}
