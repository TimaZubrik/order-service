package by.timaz.orderservice.dao.repository;

import by.timaz.orderservice.dao.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
