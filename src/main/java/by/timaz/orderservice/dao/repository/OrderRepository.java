package by.timaz.orderservice.dao.repository;

import by.timaz.orderservice.dao.entity.Order;
import by.timaz.orderservice.dao.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findById(UUID id);
    List<Order> findOrdersByIdIsIn(Collection<UUID> ids);
    List<Order> findOrdersByStatusIn(Collection<OrderStatus> statuses);
    void deleteOrderById(UUID id);

    List<Order> findByUserIdIn(Collection<UUID> userIds);
}
