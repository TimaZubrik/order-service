package by.timaz.orderservice.mapper;

import by.timaz.orderservice.dao.entity.Order;
import by.timaz.orderservice.dto.OrderDto;
import by.timaz.orderservice.dto.OrderUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    Order toOrder(OrderDto orderDto);

    OrderDto toOrderDto(Order order);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order updateOrder(OrderUpdateDto orderDto, @MappingTarget Order order);
}
