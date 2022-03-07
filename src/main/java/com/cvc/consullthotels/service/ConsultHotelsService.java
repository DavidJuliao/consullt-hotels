package com.cvc.consullthotels.service;

import com.cvc.consullthotels.Exception.CheckInDateInvalidException;
import com.cvc.consullthotels.Exception.CheckOutDateInvalidException;
import com.cvc.consullthotels.Exception.ConsultHotelInformationException;
import com.cvc.consullthotels.Exception.FeignGeneralException;
import com.cvc.consullthotels.Exception.HotelInformationNotFoundException;
import com.cvc.consullthotels.Exception.NumberOfClientsException;
import com.cvc.consullthotels.domain.dto.HotelInfoClientResponseDto;
import com.cvc.consullthotels.domain.dto.HotelInfoResponseDto;
import com.cvc.consullthotels.service.client.ConsultHotelInformationClient;
import com.cvc.consullthotels.service.mapper.HotelInfoResponseMapper;
import com.cvc.consullthotels.service.redis.ConsultHotelInformationServiceCache;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.cvc.consullthotels.Utils.DateUtils.checkInDateIsMoreThanOrEqualCheckOut;
import static com.cvc.consullthotels.Utils.DateUtils.isTodayOrMoreThanCurrent;

@Service
@RequiredArgsConstructor
public class ConsultHotelsService {

    @Autowired
    private final ConsultHotelInformationClient consultHotelInfoClient;

    @Autowired
    private final ConsultHotelInformationServiceCache consultHotelInformationServiceCache;

    private final HotelInfoResponseMapper hotelInfoResponseMapper;

     public Page<HotelInfoResponseDto> findAllByCity(Long idCity, Pageable pageable) throws FeignGeneralException, HotelInformationNotFoundException {

         List<HotelInfoResponseDto> allHotelsInformation = consultHotelInformationServiceCache.findByIdCity(idCity);

         if(allHotelsInformation.isEmpty()){
             throw new HotelInformationNotFoundException(String.format("idCity: %s",idCity));
         }

         allHotelsInformation = filterByPageable(allHotelsInformation, pageable);

         return new PageImpl<>(allHotelsInformation, pageable ,allHotelsInformation.size());
     }

     public HotelInfoResponseDto findByHotel(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate,
                                             Integer numberOfAdults, Integer numberOfChildren)
             throws CheckOutDateInvalidException, CheckInDateInvalidException, NumberOfClientsException, HotelInformationNotFoundException {

         validateInformation(checkInDate, checkOutDate, numberOfAdults, numberOfChildren);
         HotelInfoClientResponseDto hotelInfoClientResponseDto = findByIdHotel(hotelId);

         HotelInfoResponseDto hotelInfoResponseDto = hotelInfoResponseMapper.toHotelInfoResponseDto(hotelInfoClientResponseDto);
         hotelInfoResponseDto.getRooms()
                 .forEach(room -> room.calculateTotalPrice(checkInDate, checkOutDate, numberOfAdults, numberOfChildren));

         return hotelInfoResponseDto;
     }

     private HotelInfoClientResponseDto findByIdHotel(Long hotelId) throws HotelInformationNotFoundException {
         List<HotelInfoClientResponseDto>  hotelInformationClients;

         try{
             hotelInformationClients= consultHotelInfoClient.findByIdHotel(hotelId);
         }catch (FeignException fex){
            throw new FeignGeneralException(fex.status(), fex.getMessage(), fex.request(), fex.getCause());
        }
         return
                 hotelInformationClients.stream()
                 .findAny()
                 .orElseThrow(() -> new HotelInformationNotFoundException(String.format("hotelId: %s",hotelId)));
     }

     private void validateInformation(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfAdults, Integer numberOfChildren) throws CheckInDateInvalidException, CheckOutDateInvalidException, NumberOfClientsException {
         validateCheckIn(checkInDate);
         validateCheckOut(checkInDate, checkOutDate);
         validateNumberOfPeople(numberOfAdults, numberOfChildren);
     }

     private void validateCheckIn(LocalDate checkIn) throws CheckInDateInvalidException {
         if(!isTodayOrMoreThanCurrent(checkIn))
             throw new CheckInDateInvalidException();
     }

    private void validateCheckOut(LocalDate checkIn, LocalDate checkOut) throws CheckOutDateInvalidException {
        if(checkInDateIsMoreThanOrEqualCheckOut(checkIn, checkOut))
            throw new CheckOutDateInvalidException();
     }

     private void validateNumberOfPeople(Integer numberOfAdults, Integer numberOfChildren) throws NumberOfClientsException {
         if(numberOfAdults.equals(0) && numberOfChildren.equals(0))
             throw new NumberOfClientsException();
     }

    private List<HotelInfoResponseDto> filterByPageable(List<HotelInfoResponseDto> allHotelsInformation, Pageable pageable){
         return pageable.isUnpaged()?
                 allHotelsInformation:
                 allHotelsInformation
                                 .parallelStream()
                                 .skip((long) pageable.getPageSize() * pageable.getPageNumber())
                                 .limit(pageable.getPageSize())
                                 .collect(Collectors.toList());
     }
}
