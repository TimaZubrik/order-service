package by.timaz.orderservice.service;

import by.timaz.orderservice.dao.entity.Order;
import by.timaz.orderservice.dao.entity.OrderStatus;
import by.timaz.orderservice.dao.repository.OrderRepository;
import by.timaz.orderservice.dto.ItemDto;
import by.timaz.orderservice.dto.OrderDto;
import by.timaz.orderservice.dto.OrderItemDto;
import by.timaz.orderservice.dto.OrderProduct;
import by.timaz.orderservice.dto.OrderUpdateDto;
import by.timaz.orderservice.dto.ResponseDto;
import by.timaz.orderservice.dto.user.UserDto;
import by.timaz.orderservice.exceptions.ResourceNotFoundException;
import by.timaz.orderservice.feign.UserServiceInterface;
import by.timaz.orderservice.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserServiceInterface userService;
    private final ItemService itemService;

    @Transactional
    public ResponseDto findById(UUID id, String email) {
       UserDto user =  userService.getUserByEmail(email).getBody();
       UUID userId = Objects.requireNonNull(user).getId();
       List<Order> orders =  orderRepository.findOrdersByUserId(userId);
       OrderDto orderDto = orders.stream()
               .filter(order -> order.getId().equals(id))
               .map(orderMapper::toOrderDto)
               .findFirst()
               .orElseThrow(()-> new ResourceNotFoundException("Order","id",id.toString()));

       ResponseDto responseDto = ResponseDto.builder()
               .user(user)
               .build();
       responseDto.addOrder(orderDto);
        return responseDto;
    }
    @Transactional(readOnly = true)
    public ResponseDto findAll(String email) {
        UserDto user =  userService.getUserByEmail(email).getBody();
        UUID userId = Objects.requireNonNull(user).getId();
        List<Order> orders = orderRepository.findOrdersByUserId(userId);
        List<OrderDto> orderDtos = orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
        return ResponseDto.builder()
                .user(user)
                .orders(orderDtos)
                .build();
    }
    @Transactional
    public ResponseDto findByIdsIn(Collection<UUID> ids, String email) {
        UserDto user =  userService.getUserByEmail(email).getBody();
        UUID userId = Objects.requireNonNull(user).getId();
        List<Order> orders =  orderRepository.findOrdersByIdIsIn(ids);
        List<OrderDto> orderDtos = orders.stream()
                .filter(order -> order.getUserId().equals(userId))
                .map(orderMapper::toOrderDto)
                .toList();
        return ResponseDto.builder()
                .user(user)
                .orders(orderDtos)
                .build();
    }
    @Transactional
    public List<OrderDto> findByUserIds(Collection<UUID> userIds) {
        List<Order> orders = orderRepository.findByUserIdIn(userIds);
        return orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }
    @Transactional
    public ResponseDto findByStatusIn(Collection<String> statuses, String email) {
        List<OrderStatus> statusList = statuses.stream()
                .map(OrderStatus::valueOf)
                .toList();
        UserDto user =  userService.getUserByEmail(email).getBody();
        UUID userId = Objects.requireNonNull(user).getId();

        List<Order> orders = orderRepository.findOrdersByStatusIn(statusList);
        List<OrderDto> orderDtos = orders.stream()
                .filter(order -> order.getUserId().equals(userId))
                .map(orderMapper::toOrderDto)
                .toList();

        return ResponseDto.builder()
                .user(user)
                .orders(orderDtos)
                .build();
    }
    @Transactional
    public ResponseDto save(List<OrderProduct> createOrder, String email) {
        UserDto user =  userService.getUserByEmail(email).getBody();
        UUID userId = Objects.requireNonNull(user).getId();

        List<ItemDto> items = itemService.findItemsByIdIn(
                createOrder.stream()
                        .map(OrderProduct::getItemId)
                        .collect(Collectors.toList())
        );
        List<OrderItemDto> orderItems = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            var orderItemDto = OrderItemDto.builder()
                    .item(items.get(i))
                    .quantity(createOrder.get(i).getQuantity())
                    .build();
            orderItems.add(orderItemDto);
            System.out.println(orderItemDto);
        }

        OrderDto orderDto = OrderDto.builder()
                        .userId(userId)
                        .status(OrderStatus.CREATED.toString())
                        .creationDate(LocalDate.now())
                        .orderItems(orderItems)
                        .build();
        System.out.println(orderDto);

        Order order = orderMapper.toOrder(orderDto);
        orderRepository.save(order);
        orderDto = orderMapper.toOrderDto(order);

        ResponseDto response = ResponseDto.builder()
                .user(user)
                .build();
        response.addOrder(orderDto);

        return response;
    }
    @Transactional
    public ResponseDto update(OrderUpdateDto orderUpdate, UUID id, String email) {
        UserDto user =  userService.getUserByEmail(email).getBody();
        UUID userId = Objects.requireNonNull(user).getId();

        List<Order> orders = orderRepository.findOrdersByUserId(userId);
        Order order = orders.stream()
                .filter(order1 -> order1.getId().equals(id))
                .findFirst()
                .orElseThrow(()-> new ResourceNotFoundException("Order","id",id.toString()));

       OrderDto orderDto=  orderMapper.toOrderDto(
                orderMapper.updateOrder(orderUpdate, order)
        );
       ResponseDto response = ResponseDto.builder()
               .user(user)
               .build();
       response.addOrder(orderDto);
        return response;
    }

    @Transactional
    public void delete(UUID id, String email) {
        findById(id, email);
        orderRepository.deleteById(id);
    }

}
