package by.timaz.orderservice.dao.repository;

import by.timaz.orderservice.dao.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findById(UUID id);
    Optional<Item> findByName(String name);
    List<Item> findItemsByNameIn(Collection<String> names);

    List<Item> findItemsByIdIn(Collection<UUID> ids);

    Collection<UUID> id(UUID id);
}
