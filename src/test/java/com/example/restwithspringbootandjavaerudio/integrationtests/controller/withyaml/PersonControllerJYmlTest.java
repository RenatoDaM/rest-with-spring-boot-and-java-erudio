package com.example.restwithspringbootandjavaerudio.integrationtests.controller.withyaml;

import com.example.restwithspringbootandjavaerudio.configs.TestConfigs;
import com.example.restwithspringbootandjavaerudio.integrationtests.controller.withyaml.mapper.YamlMapper;
import com.example.restwithspringbootandjavaerudio.integrationtests.testcontainer.AbstractIntegrationTest;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.AccountCredentialsVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.PersonVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.TokenVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.wrappers.person.PagedModelPerson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJYmlTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static YamlMapper objectMapper;
    private static PersonVO person;


    @BeforeAll
    public static void setup() {
        objectMapper = new YamlMapper();
        person = new PersonVO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var accessToken = given()
                .basePath("/auth/signin")
                .config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
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
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    public void testCreate() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        var persistedPerson = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .body(person, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, objectMapper);

        person = persistedPerson;

        assertNotNull(persistedPerson);

        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertTrue(persistedPerson.getEnabled());
        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Nelson", persistedPerson.getFirstName());
        assertEquals("Piquet", persistedPerson.getLastName());
        assertEquals("Brasilia - DF - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(2)
    public void testUpdate() throws JsonMappingException, JsonProcessingException {
        person.setLastName("Piquet Souto Maior");

        var persistedPerson = given().spec(specification)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(person, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, objectMapper);

        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertTrue(persistedPerson.getEnabled());

        assertEquals(persistedPerson.getId(), person.getId());
        assertEquals("Nelson", persistedPerson.getFirstName());
        assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
        assertEquals("Brasilia - DF - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(3)
    public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
        var persistedPerson = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .header(TestConfigs.HEADER_PARAMETER_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .pathParam("id", person.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, objectMapper);

        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertFalse(persistedPerson.getEnabled());
        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Nelson", persistedPerson.getFirstName());
        assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
        assertEquals("Brasilia - DF - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(4)
    public void testFindById() throws JsonMappingException, JsonProcessingException {
        var persistedPerson = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .header(TestConfigs.HEADER_PARAMETER_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, objectMapper);

        person = persistedPerson;

        assertNotNull(persistedPerson);

        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertFalse(persistedPerson.getEnabled());
        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Nelson", persistedPerson.getFirstName());
        assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
        assertEquals("Brasilia - DF - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }


    @Test
    @Order(5)
    public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
        var persistedPerson = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .header(TestConfigs.HEADER_PARAMETER_ORIGIN, TestConfigs.ORIGIN_SEMERU)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();


        assertNotNull(persistedPerson);
        assertEquals("Invalid CORS request", persistedPerson);
    }

    @Test
    @Order(6)
    public void testDelete() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .pathParam("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(7)
    public void testFindAll() throws JsonMappingException, JsonProcessingException {
        var wrapper = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .queryParam("page", 3, "size", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(/*PersonVO[].class*/PagedModelPerson.class, objectMapper);


        /*List<PersonVO> people = Arrays.asList(content);*/
        var people = wrapper.getContent();
        PersonVO dataBasePerson = people.get(0);

        assertNotNull(dataBasePerson);
        assertTrue(dataBasePerson.getId() > 0);
        assertEquals("Alla", dataBasePerson.getFirstName());
        assertEquals("Astall", dataBasePerson.getLastName());
        assertEquals("Female", dataBasePerson.getGender());
        assertEquals("72525 Emmet Alley", dataBasePerson.getAddress());

        dataBasePerson = people.get(1);

        assertEquals("Allegra", dataBasePerson.getFirstName());
        assertEquals("Dome", dataBasePerson.getLastName());
        assertEquals("Female", dataBasePerson.getGender());
        assertEquals("57 Roxbury Pass", dataBasePerson.getAddress());

    }



    @Test
    @Order(8)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
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
                .as(TokenVO.class, objectMapper);
    }

    @Test
    @Order(9)
    public void testPersonsByName() throws JsonMappingException, JsonProcessingException {
        var wrapper = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .queryParam("page", 0, "size", 6, "direction", "asc")
                .pathParam("firstName", "dgard")
                .when()
                .get("/findPersonsByName/{firstName}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(/*PersonVO[].class*/PagedModelPerson.class, objectMapper);


        /*List<PersonVO> people = Arrays.asList(content);*/
        var people = wrapper.getContent();
        PersonVO dataBasePerson = people.get(0);

        assertNotNull(dataBasePerson);
        assertTrue(dataBasePerson.getId() > 0);
        assertTrue(dataBasePerson.getEnabled());
        assertEquals("Edgard", dataBasePerson.getFirstName());
        assertEquals("Detoc", dataBasePerson.getLastName());
        assertEquals("Male", dataBasePerson.getGender());
        assertEquals("5859 Di Loreto Alley", dataBasePerson.getAddress());

    }

    @Test
    @Order(10)
    public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
        var untreatedContent = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .config(RestAssuredConfig.config().encoderConfig(encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .queryParams("page", 3, "size", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        var content = untreatedContent.replace("\n", "").replace("\r", "");
        // ESTE TESTE SE EXECUTADO JUNTO COM TODOS OS OUTROS VAI DAR ERRO, POIS totalElements DA 1006, ALGUM
        // TESTE EXECUTADO EM OUTRA CLASSE ESTÁ CRIANDO UMA PERSON MAS NÃO ESTÁ DELETANDO.
        assertTrue(content.contains("page:" +
                "  size: 10" +
                "  totalElements: 1005" +
                "  totalPages: 101" +
                "  number: 3"));
        assertTrue(content.contains("rel: \"first\"" +
                "  href: \"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\""));
        assertTrue(content.contains("rel: \"prev\"" +
                "  href: \"http://localhost:8888/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\""));
        assertTrue(content.contains("rel: \"self\"" +
                "  href: \"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\""));
        assertTrue(content.contains("rel: \"next\"" +
                "  href: \"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\""));
        assertTrue(content.contains("rel: \"last\"" +
                "  href: \"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\""));
        assertTrue(content.contains("- id: 833" +
                "  firstName: \"Alexis\"" +
                "  lastName: \"Mullally\"" +
                "  address: \"0098 Rigney Center\"" +
                "  gender: \"Male\"" +
                "  enabled: true" +
                "  links:" +
                "  - rel: \"self\"" +
                "    href: \"http://localhost:8888/api/person/v1/833\"" +
                "  links: []"));


    }

    private void mockPerson() {
        person.setFirstName("Nelson");
        person.setLastName("Piquet");
        person.setAddress("Brasilia - DF - Brasil");
        person.setGender("Male");
        person.setEnabled(true);
    }
}
