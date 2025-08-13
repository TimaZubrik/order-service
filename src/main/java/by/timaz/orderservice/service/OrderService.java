package by.timaz.orderservice.service;

import by.timaz.orderservice.dao.entity.Order;
import by.timaz.orderservice.dao.entity.OrderStatus;
import by.timaz.orderservice.dao.repository.OrderRepository;
import by.timaz.orderservice.dto.OrderDto;
import by.timaz.orderservice.dto.OrderUpdateDto;
import by.timaz.orderservice.exceptions.ResourceNotFoundException;
import by.timaz.orderservice.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderDto findById(UUID id) {
        return orderMapper.toOrderDto(orderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Order", "id", id.toString())));
    }

    public List<OrderDto> findAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    public List<OrderDto> findByIdsIn(Collection<UUID> ids) {
        List<Order> orders = orderRepository.findOrdersByIdIsIn(ids);
        return orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    public List<OrderDto> findByUserIds(Collection<UUID> userIds) {
        List<Order> orders = orderRepository.findByUserIdIn(userIds);
        return orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    public List<OrderDto> findByStatusIn(Collection<OrderStatus> statusList) {
        List<Order> orders = orderRepository.findOrdersByStatusIn(statusList);
        return orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    public OrderDto save(OrderDto orderDto) {
        Order order = orderMapper.toOrder(orderDto);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }
    @Transactional
    public OrderDto update(OrderUpdateDto orderDto, UUID id) {
        return orderMapper.toOrderDto(
                orderMapper.updateOrder(
                        orderDto,
                        orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id.toString()))
                )
        );
    }

    @Transactional
    public void delete(UUID id) {
        orderMapper.toOrderDto(orderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Order", "id", id.toString())));
        orderRepository.deleteById(id);
    }
}
