package by.timaz.orderservice.integrationTest.feign;

import by.timaz.orderservice.dao.entity.Item;
import by.timaz.orderservice.dao.entity.Order;
import by.timaz.orderservice.dao.entity.OrderStatus;
import by.timaz.orderservice.dao.repository.ItemRepository;
import by.timaz.orderservice.dao.repository.OrderRepository;
import by.timaz.orderservice.dto.OrderProduct;
import by.timaz.orderservice.dto.OrderUpdateDto;
import by.timaz.orderservice.dto.user.UserDto;
import by.timaz.orderservice.exceptions.ResourceNotFoundException;
import by.timaz.orderservice.service.OrderService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=test"
)
@AutoConfigureWireMock(port = 0)
public class OrderServiceIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.5-alpine3.22"));

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("eureka.client.register-with-eureka", () -> "false");
        registry.add("eureka.client.fetch-registry",       () -> "false");
        registry.add("spring.cloud.discovery.enabled",     () -> "false");
    }

    @Autowired
    private WireMockServer wireMockServer;



    @BeforeEach
    void resetDbAndStubWireMock() {
        orderRepository.deleteAll();

        wireMockServer.resetAll();

        wireMockServer.stubFor(get(urlPathEqualTo("/user/"))
                .withQueryParam("email", equalTo("test@example.com"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "id":"11111111-1111-1111-1111-111111111111",
                      "name":"TestName",
                      "surname":"TestSurname",
                      "email":"test@example.com",
                      "birthday":"2000-01-01",
                      "cards":[]
                    }
                    """)
                )
        );
    }
    @Test
    void whenFindAll_thenReturnsOrdersAndUser() {

        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .creationDate(LocalDate.now())
                .build();
        orderRepository.save(order);

        var response = orderService.findAll("test@example.com");

        UserDto user = response.getUser();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getId()).isEqualTo(userId);

        assertThat(response.getOrders())
                .hasSize(1)
                .first()
                .matches(o -> o.getId().equals(order.getId()));
    }

    @Test
    void whenFindById_thenReturnsOrder() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .creationDate(LocalDate.now())
                .build();
        orderRepository.save(order);

        var response = orderService.findById(order.getId(),"test@example.com");
        UserDto user = response.getUser();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getId()).isEqualTo(userId);

        Order checkOrder = orderRepository.findById(order.getId()).get();
        assertThat(checkOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(checkOrder.getCreationDate()).isEqualTo(order.getCreationDate());
        assertThat(checkOrder.getUserId()).isEqualTo(userId);
    }

    @Test
    void whenFindById_thenReturnsOrderAndUser() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .creationDate(LocalDate.now())
                .build();
        orderRepository.save(order);

        var response = orderService.findById(order.getId(),"test@example.com");

        UserDto user = response.getUser();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getId()).isEqualTo(userId);

        assertThat(response.getOrders())
                .hasSize(1)
                .first()
                .matches(o -> o.getId().equals(order.getId()));
    }

    @Test
    void whenFindByIds_thenReturnsOrdersAndUser() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        List<UUID> users = Collections.singletonList(userId);
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .creationDate(LocalDate.now())
                .build();
        orderRepository.save(order);

        var responseList = orderService.findByUserIds(users);

        assertThat(responseList.size()).isEqualTo(1);
        assertThat(responseList.getFirst().getUserId()).isEqualTo(userId);
        assertThat(responseList.getFirst().getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void whenFindByStatusIn_thenReturnsOrderAndUser() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String status = "CREATED";
        List<String> statusList = Collections.singletonList(status);
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .creationDate(LocalDate.now())
                .build();
        orderRepository.save(order);

        var response = orderService.findByStatusIn(statusList,"test@example.com");

        UserDto user = response.getUser();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getId()).isEqualTo(userId);

        assertThat(response.getOrders())
                .hasSize(1)
                .first()
                .matches(o -> o.getId().equals(order.getId()));
    }

    @Test
    void whenSave_thenReturnsOrderAndUser() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Item item = Item.builder()
                .price(BigDecimal.TEN)
                .name("Coconut")
                .build();
        itemRepository.save(item);
        OrderProduct orderProduct = OrderProduct.builder()
                .itemId(item.getId())
                .quantity(2)
                .build();
        List<OrderProduct> orderProducts = Collections.singletonList(orderProduct);

        var response = orderService.save(orderProducts, "test@example.com");

        UserDto user = response.getUser();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getId()).isEqualTo(userId);

        assertThat(response.getOrders().size()).isEqualTo(1);

        var savedDto = response.getOrders().getFirst();
        assertThat(savedDto.getId()).isNotNull();
        assertThat(savedDto.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(savedDto.getOrderItems().getFirst().getItem().getId())
                .isEqualTo(item.getId());
    }

    @Test
    void whenUpdate_thenReturnsOrderAndUser() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .creationDate(LocalDate.now())
                .build();
        orderRepository.save(order);

        OrderUpdateDto updateDto =OrderUpdateDto.builder()
                .status("CANCELLED")
                .build();

        var response = orderService.update(updateDto,order.getId(),"test@example.com");
        UserDto user = response.getUser();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getId()).isEqualTo(userId);

        assertThat(response.getOrders().size()).isEqualTo(1);
        var savedDto = response.getOrders().getFirst();
        assertThat(savedDto.getId()).isEqualTo(order.getId());
        assertThat(savedDto.getStatus()).isEqualTo(OrderStatus.CANCELLED);

    }

    @Test
    void whenDelete() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .creationDate(LocalDate.now())
                .build();
        orderRepository.save(order);

        orderService.delete(order.getId(),"test@example.com");

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.findById(order.getId(),"test@example.com"));

        assertThat(orderRepository.findAll()).isEmpty();
    }
}
