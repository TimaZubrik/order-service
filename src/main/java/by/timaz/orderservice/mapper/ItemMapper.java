package by.timaz.orderservice.mapper;

import by.timaz.orderservice.dao.entity.Item;
import by.timaz.orderservice.dto.ItemDto;
import by.timaz.orderservice.dto.ItemUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
uses = OrderItemMapper.class)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item updateItem(ItemUpdateDto itemDto, @MappingTarget Item item);
}
