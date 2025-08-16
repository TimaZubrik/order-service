package by.timaz.orderservice.mapper;

import by.timaz.orderservice.dao.entity.OrderItem;
import by.timaz.orderservice.dto.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
uses = {ItemMapper.class})
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "item", source = "item")
    OrderItem toOrderItem(OrderItemDto order);

    OrderItemDto toOrderItemDto(OrderItem order);
}
