package by.timaz.orderservice.integrationTest.repository;

import by.timaz.orderservice.dao.entity.Order;
import by.timaz.orderservice.dao.entity.OrderStatus;
import by.timaz.orderservice.dao.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
public class OrderRepositoryIT {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.5-alpine3.22"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void saveAndFindById() {
        UUID id = UUID.randomUUID();
        Order order = Order.builder()
                .userId(id)
                .status(OrderStatus.SHIPPED)
                .creationDate(LocalDate.of(2025,8,8))
                .build();
        orderRepository.save(order);
        Optional<Order> found = orderRepository.findById(order.getId());

        assertTrue(found.isPresent());
        assertEquals(order.getId(), found.get().getId());
    }

    @Test
    void findAll() {
        UUID id = UUID.randomUUID();
        Order order1 = Order.builder()
                .userId(id)
                .status(OrderStatus.SHIPPED)
                .creationDate(LocalDate.of(2025,8,8))
                .build();
        Order order2 = Order.builder()
                .userId(id)
                .status(OrderStatus.CANCELLED)
                .creationDate(LocalDate.of(2025,8,8))
                .build();
        orderRepository.save(order1);
        orderRepository.save(order2);
        List<Order> orders = orderRepository.findAll();
        assertEquals(2, orders.size());
        assertEquals(order1.getId(), orders.get(0).getId());
        assertEquals(order2.getId(), orders.get(1).getId());

    }

    @Test
    void update() {
        UUID id = UUID.randomUUID();
        Order order1 = Order.builder()
                .userId(id)
                .status(OrderStatus.SHIPPED)
                .creationDate(LocalDate.of(2025,8,8))
                .build();
        orderRepository.save(order1);
        order1.setCreationDate(LocalDate.of(2025,7,8));
        order1.setStatus(OrderStatus.CANCELLED);
        orderRepository.saveAndFlush(order1);
        Order updOrder = orderRepository.findById(order1.getId()).get();

        assertEquals(order1.getStatus(), updOrder.getStatus());
        assertEquals(order1.getCreationDate(), updOrder.getCreationDate());
        assertEquals(order1.getUserId(), updOrder.getUserId());
    }

    @Test
    void deleteById() {
        UUID id = UUID.randomUUID();
        Order order = Order.builder()
                .userId(id)
                .status(OrderStatus.SHIPPED)
                .creationDate(LocalDate.of(2025,8,8))
                .build();
        orderRepository.save(order);
        orderRepository.deleteById(id);

        assertEquals(Optional.empty(), orderRepository.findById(id));
    }

    @Test
    void notNullValidation(){
        UUID id = UUID.randomUUID();
        Order order1 = Order.builder()
                .status(OrderStatus.SHIPPED)
                .creationDate(LocalDate.of(2025,8,8))
                .build();
        Order order2 = Order.builder()
                .userId(id)
                .creationDate(LocalDate.of(2025,8,8))
                .build();
        Order order3 = Order.builder()
                .userId(id)
                .status(OrderStatus.SHIPPED)
                .build();

        assertThrows(DataIntegrityViolationException.class, ()->orderRepository.save(order1));
        assertThrows(DataIntegrityViolationException.class, ()->orderRepository.save(order2));
        assertThrows(DataIntegrityViolationException.class, ()->orderRepository.save(order3));

    }

}
