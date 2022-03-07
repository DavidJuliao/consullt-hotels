package com.cvc.consullthotels.controller;

import com.cvc.consullthotels.Exception.CheckInDateInvalidException;
import com.cvc.consullthotels.Exception.CheckOutDateInvalidException;
import com.cvc.consullthotels.Exception.ConsultHotelInformationException;
import com.cvc.consullthotels.Exception.HotelInformationNotFoundException;
import com.cvc.consullthotels.Exception.NumberOfClientsException;
import com.cvc.consullthotels.domain.dto.HotelInfoClientResponseDto;
import com.cvc.consullthotels.domain.dto.HotelInfoRequestDto;
import com.cvc.consullthotels.domain.dto.HotelInfoResponseDto;
import com.cvc.consullthotels.service.ConsultHotelsService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotel-reservation")
public class HotelInformationController {

    @Autowired
    private final ConsultHotelsService consultHotelsService;

    @GetMapping("/city/{cityId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<Page<HotelInfoResponseDto>> findByCity(@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                                                 @PathVariable Long cityId) throws ConsultHotelInformationException, HotelInformationNotFoundException {
        return ResponseEntity.ok(consultHotelsService.findAllByCity(cityId,pageable));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<HotelInfoResponseDto>  findByHotel(@PathVariable Long hotelId,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate checkInDate,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate checkOutDate,
                                                             @RequestParam @NotNull Integer numberOfAdults,
                                                             @RequestParam @NotNull Integer numberOfChildren)
            throws CheckOutDateInvalidException, CheckInDateInvalidException, NumberOfClientsException, ConsultHotelInformationException, HotelInformationNotFoundException {

        return ResponseEntity.ok(consultHotelsService.findByHotel(hotelId, checkInDate, checkOutDate ,numberOfAdults, numberOfChildren));
    }
}
