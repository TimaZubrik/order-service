package by.timaz.orderservice.service;

import by.timaz.orderservice.dao.entity.Item;
import by.timaz.orderservice.dao.repository.ItemRepository;
import by.timaz.orderservice.dto.ItemDto;
import by.timaz.orderservice.dto.ItemUpdateDto;
import by.timaz.orderservice.mapper.ItemMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    @Test
    public void saveItemTest() {
        BigDecimal price = BigDecimal.valueOf(5.05);
        String name = "Ice-cream";
        UUID uuid = UUID.randomUUID();
        ItemDto itemDto = ItemDto.builder()
                .name(name)
                .price(price)
                .build();
        ItemDto actualDto = ItemDto.builder()
                .id(uuid)
                .name(name)
                .price(price)
                .build();
        Item item = Item.builder()
                .id(uuid)
                .price(price)
                .name(name)
                .build();
        Mockito.when(itemMapper.toItem(itemDto)).thenReturn(item);
        Mockito.when(itemRepository.save(item)).thenReturn(item);
        Mockito.when(itemMapper.toItemDto(item)).thenReturn(actualDto);

        ItemDto result = itemService.saveItem(itemDto);
        assertEquals(actualDto, result);

        verify(itemMapper).toItem(itemDto);
        verify(itemRepository).save(item);
    }

    @Test
    public void findAllItemsTest(){
        BigDecimal price = BigDecimal.valueOf(5.05);
        String name = "Ice-cream";
        UUID uuid = UUID.randomUUID();;
        ItemDto actualDto = ItemDto.builder()
                .id(uuid)
                .name(name)
                .price(price)
                .build();
        Item item = Item.builder()
                .id(uuid)
                .price(price)
                .name(name)
                .build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemDto> actualDtos = new ArrayList<>();
        actualDtos.add(actualDto);

        Mockito.when(itemRepository.findAll()).thenReturn(items);
        Mockito.when(itemMapper.toItemDto(item)).thenReturn(actualDto);

        List<ItemDto> result = itemService.findAllItems();
        assertEquals(actualDtos, result);
        assertEquals(1, result.size());

        verify(itemRepository).findAll();
        verify(itemMapper).toItemDto(item);
    }

    @Test
    public void findItemByIdTest(){
        UUID uuid = UUID.randomUUID();
        String name = "Ice-cream";
        BigDecimal price = BigDecimal.valueOf(5.05);
        ItemDto actualDto = ItemDto.builder()
                .id(uuid)
                .name(name)
                .price(price)
                .build();
        Item item = Item.builder()
                .id(uuid)
                .price(price)
                .name(name)
                .build();

        Mockito.when(itemRepository.findById(uuid)).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.toItemDto(item)).thenReturn(actualDto);

        ItemDto result = itemService.findItemById(uuid);
        assertEquals(actualDto, result);
        verify(itemRepository).findById(uuid);
        verify(itemMapper).toItemDto(item);
    }

    @Test
    public void findItemsByNamesTest(){
        BigDecimal price = BigDecimal.valueOf(5.05);
        String name = "Ice-cream";
        UUID uuid = UUID.randomUUID();
        List<String> names = new ArrayList<>();
        names.add(name);
        ItemDto actualDto = ItemDto.builder()
                .id(uuid)
                .name(name)
                .price(price)
                .build();
        Item item = Item.builder()
                .id(uuid)
                .price(price)
                .name(name)
                .build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemDto> actualDtos = new ArrayList<>();
        actualDtos.add(actualDto);

        Mockito.when(itemRepository.findItemsByNameIn(names)).thenReturn(items);
        Mockito.when(itemMapper.toItemDto(item)).thenReturn(actualDto);

        List<ItemDto> result = itemService.findItemsByNames(names);
        assertEquals(actualDtos, result);
        verify(itemRepository).findItemsByNameIn(names);
        verify(itemMapper).toItemDto(item);
    }

    @Test
    public void updateItemTest(){
        BigDecimal price = BigDecimal.valueOf(5.05);
        String name = "Ice-cream";
        UUID uuid = UUID.randomUUID();
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .price(price)
                .build();
        ItemDto actualDto = ItemDto.builder()
                .id(uuid)
                .name(name)
                .price(price)
                .build();
        Item item = Item.builder()
                .id(uuid)
                .price(price)
                .name(name)
                .build();

        Mockito.when(itemRepository.findById(uuid)).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.updateItem(itemUpdateDto, item)).thenReturn(item);
        Mockito.when(itemMapper.toItemDto(item)).thenReturn(actualDto);

        ItemDto result = itemService.updateItem(itemUpdateDto, uuid);
        assertEquals(actualDto, result);

        verify(itemRepository).findById(uuid);
        verify(itemMapper).updateItem(itemUpdateDto, item);
        verify(itemMapper).toItemDto(item);

    }

    @Test
    public void deleteItemTest(){
        UUID uuid = UUID.randomUUID();
        String name = "Ice-cream";
        BigDecimal price = BigDecimal.valueOf(5.05);
        ItemDto actualDto = ItemDto.builder()
                .id(uuid)
                .name(name)
                .price(price)
                .build();
        Item item = Item.builder()
                .id(uuid)
                .price(price)
                .name(name)
                .build();

        Mockito.when(itemRepository.findById(uuid)).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.toItemDto(item)).thenReturn(actualDto);
        Mockito.doNothing().when(itemRepository).deleteById(uuid);

        itemService.deleteItemById(uuid);

        verify(itemRepository).deleteById(uuid);
        verify(itemMapper).toItemDto(item);


    }
}
