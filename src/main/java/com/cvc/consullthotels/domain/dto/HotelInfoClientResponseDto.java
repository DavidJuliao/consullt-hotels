package com.cvc.consullthotels.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelInfoClientResponseDto implements Serializable {

    private Long id;
    private String name;
    private String cityCode;
    private String cityName;
    private List<RoomClientDto> rooms;

}
