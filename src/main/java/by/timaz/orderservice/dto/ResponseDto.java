package by.timaz.orderservice.dto;

import by.timaz.orderservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {
    private UserDto user;
    @Builder.Default
    private List<OrderDto> orders = new ArrayList<>();

    public void addOrder(OrderDto order) {
        orders.add(order);
    }

}
