package by.timaz.orderservice.dto;

import by.timaz.orderservice.dao.entity.OrderStatus;
import by.timaz.orderservice.dto.validation.ValueOfEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {

    private UUID id;
    @Null
    private UUID userId;

    @NotNull(message = "Creation date cannot be empty")
    @Past(message = "Creation date should be in the past")
    private LocalDate creationDate;

    @ValueOfEnum(enumClass = OrderStatus.class,
            nullable = false,
            message = "Invalid order status")
    private String status;

    @Builder.Default
    private List<OrderItemDto> orderItems = new ArrayList<>();

    public OrderStatus getStatus() {
        return OrderStatus.valueOf(status);
    }

}
