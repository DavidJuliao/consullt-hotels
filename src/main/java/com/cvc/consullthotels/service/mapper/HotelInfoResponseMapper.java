package com.cvc.consullthotels.service.mapper;

import com.cvc.consullthotels.domain.dto.CategoryDto;
import com.cvc.consullthotels.domain.dto.HotelInfoClientResponseDto;
import com.cvc.consullthotels.domain.dto.HotelInfoResponseDto;
import com.cvc.consullthotels.domain.dto.PriceDetailDto;
import com.cvc.consullthotels.domain.dto.RoomDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HotelInfoResponseMapper {

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "city", source = "dto.cityName")
    HotelInfoResponseDto toHotelInfoResponseDto(HotelInfoClientResponseDto dto);

    @AfterMapping
    default  void fillRooms(HotelInfoClientResponseDto dto, @MappingTarget HotelInfoResponseDto hotelInfoResponseDto){
        List<RoomDto> rooms = dto.getRooms()
                                    .stream()
                                    .map(room -> RoomDto.builder().id(room.getRoomId())
                                            .category(new CategoryDto(room.getCategoryName()))
                                            .priceDetail(new PriceDetailDto(room.getPrice().getAdult(),room.getPrice().getChild()))
                                            .build())
                                    .collect(Collectors.toList());
        hotelInfoResponseDto.setRooms(rooms);
    }
}
