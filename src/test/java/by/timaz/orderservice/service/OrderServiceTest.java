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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserServiceInterface userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private OrderService orderService;

    private final String email = "user@example.com";
    private final UUID userId = UUID.randomUUID();
    private final UserDto userDto = UserDto.builder()
            .id(userId)
            .email(email)
            .build();

    private void mockUserLookup() {
        when(userService.getUserByEmail(email))
                .thenReturn(ResponseEntity.ok(userDto));
    }

    @Test
    public void findById_Success() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.CREATED)
                .build();
        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.CREATED.toString())
                .build();

        mockUserLookup();
        when(orderRepository.findOrdersByUserId(userId))
                .thenReturn(List.of(order));
        when(orderMapper.toOrderDto(order))
                .thenReturn(orderDto);

        ResponseDto response = orderService.findById(orderId, email);

        assertEquals(userDto, response.getUser());
        assertEquals(1, response.getOrders().size());
        assertEquals(orderDto, response.getOrders().get(0));

        verify(orderRepository).findOrdersByUserId(userId);
        verify(orderMapper).toOrderDto(order);
    }

    @Test
    public void findById_NotFound() {
        UUID missingId = UUID.randomUUID();

        mockUserLookup();
        when(orderRepository.findOrdersByUserId(userId))
                .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.findById(missingId, email));

        verify(orderRepository).findOrdersByUserId(userId);
    }

    @Test
    public void findAll_ReturnsAllOrders() {
        Order o1 = Order.builder().id(UUID.randomUUID()).userId(userId).build();
        Order o2 = Order.builder().id(UUID.randomUUID()).userId(userId).build();
        OrderDto d1 = OrderDto.builder().id(o1.getId()).userId(userId).build();
        OrderDto d2 = OrderDto.builder().id(o2.getId()).userId(userId).build();

        mockUserLookup();
        when(orderRepository.findOrdersByUserId(userId))
                .thenReturn(List.of(o1, o2));
        when(orderMapper.toOrderDto(o1)).thenReturn(d1);
        when(orderMapper.toOrderDto(o2)).thenReturn(d2);

        ResponseDto response = orderService.findAll(email);

        assertEquals(userDto, response.getUser());
        assertEquals(List.of(d1, d2), response.getOrders());
        verify(orderRepository).findOrdersByUserId(userId);
    }

    @Test
    public void findByIdsIn_FiltersByUser() {
        UUID allowed = UUID.randomUUID();
        UUID blocked = UUID.randomUUID();
        Order allowedOrder = Order.builder()
                .id(allowed)
                .userId(userId)
                .build();
        Order blockedOrder = Order.builder()
                .id(blocked)
                .userId(UUID.randomUUID())
                .build();
        OrderDto allowedDto = OrderDto.builder()
                .id(allowed)
                .userId(userId)
                .build();

        mockUserLookup();
        when(orderRepository.findOrdersByIdIsIn(List.of(allowed, blocked)))
                .thenReturn(List.of(allowedOrder, blockedOrder));
        when(orderMapper.toOrderDto(allowedOrder)).thenReturn(allowedDto);

        ResponseDto response = orderService.findByIdsIn(List.of(allowed, blocked), email);

        assertEquals(userDto, response.getUser());
        assertEquals(1, response.getOrders().size());
        assertEquals(allowedDto, response.getOrders().get(0));
    }

    @Test
    public void findByUserIds_ReturnsMappedOrders() {
        UUID otherUser = UUID.randomUUID();
        Order o1 = Order.builder().id(UUID.randomUUID()).userId(userId).build();
        Order o2 = Order.builder().id(UUID.randomUUID()).userId(otherUser).build();
        OrderDto d1 = OrderDto.builder().id(o1.getId()).userId(userId).build();
        OrderDto d2 = OrderDto.builder().id(o2.getId()).userId(otherUser).build();

        when(orderRepository.findByUserIdIn(List.of(userId, otherUser)))
                .thenReturn(List.of(o1, o2));
        when(orderMapper.toOrderDto(o1)).thenReturn(d1);
        when(orderMapper.toOrderDto(o2)).thenReturn(d2);

        List<OrderDto> result = orderService.findByUserIds(List.of(userId, otherUser));

        assertEquals(List.of(d1, d2), result);
        verify(orderRepository).findByUserIdIn(List.of(userId, otherUser));
    }

    @Test
    public void save_CreatesOrderCorrectly() {
        UUID itemId = UUID.randomUUID();
        OrderProduct prod = new OrderProduct(itemId, 2);
        ItemDto itemDto = ItemDto.builder()
                .id(itemId).name("A").price(BigDecimal.TEN).build();
        OrderItemDto oiDto = OrderItemDto.builder()
                .item(itemDto).quantity(2).build();
        OrderDto mappedOrderDto = OrderDto.builder()
                .userId(userId)
                .status(OrderStatus.CREATED.toString())
                .creationDate(LocalDate.now())
                .orderItems(List.of(oiDto))
                .build();
        Order savedOrder = Order.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .status(OrderStatus.CREATED)
                .build();
        OrderDto savedDto = OrderDto.builder()
                .id(savedOrder.getId())
                .userId(userId)
                .status(savedOrder.getStatus().toString())
                .build();

        mockUserLookup();
        when(itemService.findItemsByIdIn(List.of(itemId)))
                .thenReturn(List.of(itemDto));
        when(orderMapper.toOrder(mappedOrderDto)).thenReturn(savedOrder);
        when(orderMapper.toOrderDto(savedOrder)).thenReturn(savedDto);
        when(orderRepository.save(savedOrder)).thenReturn(savedOrder);

        ResponseDto response = orderService.save(List.of(prod), email);

        assertEquals(userDto, response.getUser());
        assertEquals(1, response.getOrders().size());
        assertEquals(savedDto, response.getOrders().get(0));

        verify(itemService).findItemsByIdIn(List.of(itemId));
        verify(orderRepository).save(savedOrder);
    }

    @Test
    public void update_Success() {
        UUID orderId = UUID.randomUUID();
        Order existing = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.CREATED)
                .build();
        OrderUpdateDto upd = new OrderUpdateDto();
        Order updated = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.COMPLETED)
                .build();
        OrderDto updatedDto = OrderDto.builder()
                .id(orderId)
                .userId(userId)
                .status(updated.getStatus().toString())
                .build();

        mockUserLookup();
        when(orderRepository.findOrdersByUserId(userId))
                .thenReturn(List.of(existing));
        when(orderMapper.updateOrder(upd, existing)).thenReturn(updated);
        when(orderMapper.toOrderDto(updated)).thenReturn(updatedDto);

        ResponseDto response = orderService.update(upd, orderId, email);

        assertEquals(userDto, response.getUser());
        assertEquals(updatedDto, response.getOrders().get(0));

        verify(orderMapper).updateOrder(upd, existing);
    }

    @Test
    public void delete_CallsRepository() {
        UUID orderId = UUID.randomUUID();

        mockUserLookup();
        Order o = Order.builder()
                .id(orderId)
                .userId(userId)
                .build();

        when(orderRepository.findOrdersByUserId(userId))
                .thenReturn(List.of(o));
        OrderDto dummyDto = OrderDto.builder()
                .id(orderId)
                .userId(userId)
                .build();
        when(orderMapper.toOrderDto(o)).thenReturn(dummyDto);
        orderService.delete(orderId, email);

        verify(orderRepository).deleteById(orderId);
    }

    @Test
    public void findByStatus_CallsRepository() {
        OrderStatus created = OrderStatus.CREATED;
        OrderStatus completed = OrderStatus.COMPLETED;
        String s1 = created.toString();
        String s2 = completed.toString();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Order allowedOrder = Order.builder()
                .id(uuid1)
                .userId(userId)
                .status(created)
                .build();
        Order blockedOrder = Order.builder()
                .id(uuid2)
                .userId(UUID.randomUUID())
                .status(completed)
                .build();
        OrderDto allowedDto = OrderDto.builder()
                .id(uuid1)
                .userId(userId)
                .status(created.toString())
                .build();

        mockUserLookup();
        when(orderRepository.findOrdersByStatusIn(List.of(created, completed)))
                .thenReturn(List.of(allowedOrder, blockedOrder));
        when(orderMapper.toOrderDto(allowedOrder)).thenReturn(allowedDto);

        ResponseDto response = orderService.findByStatusIn(List.of(s1,s2), email);

        assertEquals(userDto, response.getUser());
        assertEquals(1, response.getOrders().size());
        assertEquals(allowedDto, response.getOrders().getFirst());

    }
}
