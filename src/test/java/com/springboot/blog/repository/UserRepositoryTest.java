package com.springboot.blog.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import com.springboot.blog.entity.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import javax.sql.DataSource;
import java.util.Optional;
import java.util.List;



import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles({"test"})
@Testcontainers
@Sql(value = "classpath:insert-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "classpath:delete-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withUsername("testUser")
            .withPassword("testSecret")
            .withDatabaseName("testDatabase");

    //Cristian Pulido
    @Test
    void findByEmail() {
        Optional<User> encontradofalse = userRepository.findByEmail("oliva0@europa.eu");
        Optional<User> encontradotrue = userRepository.findByEmail("loliva0@europa.eu");

        assertTrue(encontradofalse.isEmpty(),"No se ha encontrado");
        assertTrue(encontradotrue.isPresent(),"Se ha encontrado");
    }

    //Sebastián Millán
    @Test
    void whenUsernameIsPresentAndEmailIsPresent_thenUserIsPresent() {
        Optional<User> result = userRepository.findByUsernameOrEmail("lcroxton0", "loliva0@europa.eu");
        assertTrue(result.isPresent());
        assertEquals(result.get().getUsername(),"lcroxton0");
        assertEquals(result.get().getEmail(),"loliva0@europa.eu");
    }

    //Marco Pertegal
    @Test
    void WhenExistingUserNameThenReturnUser() {
        String username = "fspincke1";
        Optional<User> user = userRepository.findByUsername(username);
        assertTrue(user.isPresent());
        assertEquals("Faunie", user.get().getName());
    }

    @Test
    void WhenNonExistingUserNameThenReturnEmpty() {
        String username = "a";
        Optional<User> user = userRepository.findByUsername(username);
        assertFalse(user.isPresent());
        assertTrue(user.isEmpty());
    }

    @Test
    void WhenExistingUserNameThenReturnTrue() {
        String username = "fspincke1";
        Boolean user = userRepository.existsByUsername(username);
        assertTrue(user);
    }

    //Marco Pertegal
    @Test
    void WhenNonExistingUserNameThenReturnFalse() {
        String username = "a";
        Boolean user = userRepository.existsByUsername(username);
        assertFalse(user);
    }

    //Alejandro Rubens
    @Test
    void existsByEmailTrueStatement() {
        Assertions.assertThat(dataSource).isNotNull();
        Assertions.assertThat(jdbcTemplate).isNotNull();
        Assertions.assertThat(entityManager).isNotNull();
        Assertions.assertThat(userRepository).isNotNull();

        boolean resultTrue = userRepository.existsByEmail("alejandro@gmail.com");

        Assertions.assertThat(resultTrue).isNotNull();
        Assertions.assertThat(resultTrue).isTrue();

    }

    //Alejandro Rubens
    @Test
    void existsByEmailFalseStatement() {
        Assertions.assertThat(dataSource).isNotNull();
        Assertions.assertThat(jdbcTemplate).isNotNull();
        Assertions.assertThat(entityManager).isNotNull();
        Assertions.assertThat(userRepository).isNotNull();

        boolean resultFalse = userRepository.existsByEmail("nonExistingEmail");

        Assertions.assertThat(resultFalse).isNotNull();
        Assertions.assertThat(resultFalse).isFalse();
    }

}