package com.cvc.consullthotels.service.client;

import com.cvc.consullthotels.Exception.ConsultHotelInformationException;
import com.cvc.consullthotels.domain.dto.HotelInfoClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
@FeignClient(url = "https://cvcbackendhotel.herokuapp.com/hotels/", value = "consultHotelInfo")
public interface ConsultHotelInfoClient {

    @GetMapping("avail/{ID_da_Cidade}")
    List<HotelInfoClientResponseDto> findByIdCity(@PathVariable("ID_da_Cidade") Long idCity) throws ConsultHotelInformationException;

    @GetMapping("/{ID_Do_Hotel}")
    List<HotelInfoClientResponseDto> findByIdHotel(@PathVariable("ID_Do_Hotel") Long idHotel)throws ConsultHotelInformationException;

}
