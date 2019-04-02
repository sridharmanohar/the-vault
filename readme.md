# Technology Stack 

## Core
. Java 1.8
  . core java api.
  . collections used: Set(HashSet) and List(ArrayList).
  . streams: used Collectors class of strem api to convert List to Set.
. Spring Boot 2.1
  . @SpringBootApplication

## Persitence Layer
. Spring Boot Data JPA 2.1
  . used Repository interface of Spring Data (not JpaRepository).
. Hibernate 5.3
  . implicitly used by spring boot as a persistance implementation layer (orm mapping tool).
  . used @Fetch and FetchMode.

## Persitence Store
. MySQl 8.0
  . as persistence store.
  . db components: tables with pk and fk as and where required.

## UI
. Thymeleaf 2.1
  . template engine.
  . thymeleaf tags used: th:href, th:object, th:action, th:if, th:text, th:each, th:field, th:value, th:errors, th:src
. Bootstrap 4.3
  . used for page and it's component styling.
  . displaying success/error messages.

## Field Validation
. Spring Validator (spring boot 2.1)
  . all field-level validations are performed by implementing spring validator.

## Test(ing)
. Junit 4.12
  . used @RunWith and @Test.
. AssertJ 3.11
  . used assertThat to validate.	
. Mockito 2.23
  . used when and thenReturn to set expectations.
. Spring Boot test
  . used @MockBean to mock beans.
  . used @WebMvcTest to define the class for which the mvc test was being carried out.
  . used @DataJpaTest to perform integration testing (service layer).
  . used @AutoConfigureTestDatabase to define to not to replace the mentioned TEST db in application.properties file in /test.
. Spring Test
  . used MockMvc to perform web request actions and used it's ResultActions methods like andExpect to verify the results. 

## Logging
. SLf4J 1.7
  . used as the core log api and logback as an extension to this.
. Logback 1.2
  . used ConsoleAppender for stdout log output.
  . used RollingFileAppender for defining a file based log output.
  . also implemented SizeAndTimeBasedRollingPolicy to define the log archival process. 	

## Dev Utilities
. Spring Boot DevTools 2.1
  . used the Automatic Restart feature of dev tools. (clean -> build ->  build project)

## Other Springframework annotations and classes used
  . @Controller
  . @Component
  . @Autowired
  . @InitBinder
  . @GetMapping
  . @PathVariable
  . @PostMapping
  . @Validated
  . @ModelAttribute
  . @Repository
  . @Trasactional
  . @Modifying
  . WebDataBinder
  . Model
  . BindingResult
  . Repository
  . Validator
  . Errors 

## Java Persistence API annotations used
  . @Entity
  . @Table
  . @Id
  . @GeneratedValue
  . @OneToMany
  . @ManyToOne
  . @JoinColumn
