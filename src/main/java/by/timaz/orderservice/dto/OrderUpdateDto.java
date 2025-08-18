package by.timaz.orderservice.dto;

import by.timaz.orderservice.dao.entity.OrderStatus;
import by.timaz.orderservice.dto.validation.ValueOfEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDto {

    @Past(message = "Creation date should be in the past")
    private LocalDate creationDate;

    @ValueOfEnum(enumClass = OrderStatus.class, message = "Invalid order status")
    private String status;

    @Builder.Default
    private List<OrderItemDto> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItemDto orderItem) {
        orderItems.add(orderItem);
    }
}
