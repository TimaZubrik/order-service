package by.timaz.orderservice.integrationTest.repository;

import by.timaz.orderservice.dao.entity.Item;
import by.timaz.orderservice.dao.repository.ItemRepository;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
public class ItemRepositoryIT {
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
    private ItemRepository itemRepository;

    @Test
    void saveAndFind() {
        Item item = Item.builder()
                .name("Milk")
                .price(BigDecimal.valueOf(12))
                .build();

        itemRepository.save(item);
        Optional<Item> found = itemRepository.findById(item.getId());
        assertTrue(found.isPresent());
        assertEquals("Milk", found.get().getName());
    }

    @Test
    void update() {
        saveAndFind();

        Item item = itemRepository.findByName("Milk").get();
        item.setName("New Milk");
        item.setPrice(BigDecimal.valueOf(15));
        itemRepository.saveAndFlush(item);
        Item updatedItem = itemRepository.findById(item.getId()).get();

        assertEquals("New Milk", updatedItem.getName());
        assertEquals("15.00", updatedItem.getPrice().toString());
        assertEquals(Optional.empty(), itemRepository.findByName("Milk"));

    }

    @Test
    void delete() {

        Item item = Item.builder()
                .name("Bread")
                .price(BigDecimal.valueOf(2))
                .build();
        itemRepository.save(item);
        UUID itemId = item.getId();
        itemRepository.deleteById(itemId);
        assertEquals(Optional.empty(), itemRepository.findById(itemId));
    }

    @Test
    void notNullValidation(){
        Item item = Item.builder()
                .name("Coffee")
                .build();
        Item item2 = Item.builder()
                .price(BigDecimal.valueOf(2))
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> itemRepository.saveAndFlush(item));
        assertThrows(DataIntegrityViolationException.class, () -> itemRepository.saveAndFlush(item2));

    }
}
