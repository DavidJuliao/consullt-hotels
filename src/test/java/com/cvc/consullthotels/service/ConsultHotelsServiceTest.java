package com.cvc.consullthotels.service;

import com.cvc.consullthotels.Exception.CheckInDateInvalidException;
import com.cvc.consullthotels.Exception.CheckOutDateInvalidException;
import com.cvc.consullthotels.Exception.ConsultHotelInformationException;
import com.cvc.consullthotels.Exception.NumberOfClientsException;
import com.cvc.consullthotels.domain.dto.CategoryDto;
import com.cvc.consullthotels.domain.dto.HotelInfoClientResponseDto;
import com.cvc.consullthotels.domain.dto.HotelInfoResponseDto;
import com.cvc.consullthotels.domain.dto.PriceDetailDto;
import com.cvc.consullthotels.domain.dto.PriceDto;
import com.cvc.consullthotels.domain.dto.RoomClientDto;
import com.cvc.consullthotels.domain.dto.RoomDto;
import com.cvc.consullthotels.service.client.ConsultHotelInformationClient;
import com.cvc.consullthotels.service.mapper.HotelInfoResponseMapper;
import com.cvc.consullthotels.service.redis.ConsultHotelInformationServiceCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ConsultHotelsServiceTest {

    @MockBean
    private ConsultHotelInformationClient consultHotelInfoClient;

    private List<HotelInfoResponseDto> hotelInformationResponses;
    HotelInfoResponseDto hotelInfoResponseDto;

    private List<HotelInfoClientResponseDto> hotelInfoClientResponsesByCity;
    private List<HotelInfoClientResponseDto> hotelInfoClientResponsesByHotel;

    @MockBean
    private ConsultHotelInformationServiceCache consultHotelInformationServiceCache;

    @MockBean
    private HotelInfoResponseMapper hotelInfoResponseMapper;

    private ConsultHotelsService consultHotelsService;

    @BeforeEach
    public void setup(){
        fillHotels();
        fillHotels2();
        consultHotelsService = new ConsultHotelsService(consultHotelInfoClient, consultHotelInformationServiceCache, hotelInfoResponseMapper);
    }

    @Test
    void findByCity_AllDataInformed_workFine() throws Exception{
        Page<HotelInfoResponseDto> responseDtoPage = new PageImpl<>(hotelInformationResponses);
        when(consultHotelInformationServiceCache.findByIdCity(any(Long.class))).thenReturn(hotelInformationResponses);

        Page<HotelInfoResponseDto> responseDtoPageResult = consultHotelsService.findAllByCity(1032L,Pageable.unpaged());
        assertThat(responseDtoPageResult).isEqualTo(responseDtoPage);

    }

    @Test
    void findByCity_AnErrorOccursWhenQueryingHotels_returnConsultHotelInformationException() throws Exception{
        when(consultHotelInformationServiceCache.findByIdCity(any(Long.class))).thenThrow(new ConsultHotelInformationException());

        assertThrows(ConsultHotelInformationException.class, () -> {
            consultHotelsService.findAllByCity(1032L,Pageable.unpaged());
        });
    }

    @Test
    void findByHotel_AllDataInformed_workFine() throws Exception{
        LocalDate checkInDate = LocalDate.now().plusDays(1L);
        LocalDate checkOutDate = checkInDate.plusDays(1L);
        Integer numberOfAdults = 1;
        Integer numberOfChild = 1;
        hotelInfoResponseDto = hotelInformationResponses.get(0);

        when(consultHotelInfoClient.findByIdHotel(any(Long.class))).thenReturn(hotelInfoClientResponsesByHotel);
        when(hotelInfoResponseMapper.toHotelInfoResponseDto(any(HotelInfoClientResponseDto.class))).thenReturn(hotelInfoResponseDto);

        HotelInfoResponseDto responseDtoPageResult = consultHotelsService.findByHotel(1L, checkInDate, checkOutDate,
                                                                                            numberOfAdults, numberOfChild);
        assertThat(responseDtoPageResult).isEqualTo(hotelInfoResponseDto);
    }

    @Test
    void findByHotel_checkInIsLessThanCurrentDate_returnCheckInDateInvalidException(){
        LocalDate checkInDate = LocalDate.now().minusDays(1);
        LocalDate checkOutDate = checkInDate.plusDays(1L);
        Integer numberOfAdults = 1;
        Integer numberOfChild = 1;

        assertThrows(CheckInDateInvalidException.class, () -> {
            consultHotelsService.findByHotel(1L, checkInDate, checkOutDate,
                                                    numberOfAdults, numberOfChild);
        });
    }

    @Test
    void findByHotel_checkoutIsLessThanOrEqualToCheckIn_returnCheckOutDateInvalidException(){
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.minusDays(1);
        Integer numberOfAdults = 1;
        Integer numberOfChild = 1;

        assertThrows(CheckOutDateInvalidException.class, () -> {
            consultHotelsService.findByHotel(1L, checkInDate, checkOutDate,
                    numberOfAdults, numberOfChild);
        });
    }

    @Test
    void findByHotel_NoMoreThanZerClientsWereProvided_returnNumberOfClientsException(){
        LocalDate checkInDate = LocalDate.now().plusDays(1L);
        LocalDate checkOutDate = checkInDate.plusDays(1L);
        Integer numberOfAdults = 0;
        Integer numberOfChild = 0;

        assertThrows(NumberOfClientsException.class, () -> {
            consultHotelsService.findByHotel(1L, checkInDate, checkOutDate,
                    numberOfAdults, numberOfChild);
        });
    }

    private void fillHotels(){
        PriceDetailDto priceDetail1 = new PriceDetailDto(BigDecimal.valueOf(132.45),BigDecimal.valueOf(592.69));
        PriceDetailDto priceDetail2 = new PriceDetailDto(BigDecimal.valueOf(132.54),BigDecimal.valueOf(592.69));
        PriceDetailDto priceDetail3 = new PriceDetailDto(BigDecimal.valueOf(152.45),BigDecimal.valueOf(552.67));
        PriceDetailDto priceDetail4 = new PriceDetailDto(BigDecimal.valueOf(142.47),BigDecimal.valueOf(542.68));
        PriceDetailDto priceDetail5 = new PriceDetailDto(BigDecimal.valueOf(162.48),BigDecimal.valueOf(517.67));
        PriceDetailDto priceDetail6 = new PriceDetailDto(BigDecimal.valueOf(192.47),BigDecimal.valueOf(538.63));

        RoomDto room1 = new RoomDto(1L, new CategoryDto("test1"),BigDecimal.valueOf(725.14),priceDetail1);
        RoomDto room2 = new RoomDto(2L, new CategoryDto("test2"),BigDecimal.valueOf(725.23),priceDetail2);
        RoomDto room3 = new RoomDto(3L, new CategoryDto("test3"),BigDecimal.valueOf(705.12),priceDetail3);
        RoomDto room4 = new RoomDto(4L, new CategoryDto("test4"),BigDecimal.valueOf(685.15),priceDetail4);
        RoomDto room5 = new RoomDto(5L, new CategoryDto("test5"),BigDecimal.valueOf(680.15),priceDetail5);
        RoomDto room6 = new RoomDto(6L, new CategoryDto("test6"),BigDecimal.valueOf(731.10),priceDetail6);

        List<RoomDto> roomList1 = new ArrayList<>(Arrays.asList(room1, room2));
        List<RoomDto> roomList2 = new ArrayList<>(Arrays.asList(room3, room4));
        List<RoomDto> roomList3 = new ArrayList<>(Arrays.asList(room5, room6));

        HotelInfoResponseDto hotelInfoResponse1 = new HotelInfoResponseDto(1L,"Hotel test1","Porto Seguro",roomList1);
        HotelInfoResponseDto hotelInfoResponse2 = new HotelInfoResponseDto(2L,"Hotel test2","Fortaleza",roomList2);
        HotelInfoResponseDto hotelInfoResponse3 = new HotelInfoResponseDto(3L,"Hotel test3","Porto Alegre",roomList3);
        hotelInformationResponses = new ArrayList<>(Arrays.asList(hotelInfoResponse1,hotelInfoResponse2,hotelInfoResponse3));
    }

    private void fillHotels2(){
        PriceDto price1 = new PriceDto(BigDecimal.valueOf(132.45),BigDecimal.valueOf(592.69));
        PriceDto price2 = new PriceDto(BigDecimal.valueOf(132.54),BigDecimal.valueOf(592.69));
        PriceDto price3 = new PriceDto(BigDecimal.valueOf(152.45),BigDecimal.valueOf(552.67));
        PriceDto price4 = new PriceDto(BigDecimal.valueOf(142.47),BigDecimal.valueOf(542.68));
        PriceDto price5 = new PriceDto(BigDecimal.valueOf(162.48),BigDecimal.valueOf(517.67));
        PriceDto price6 = new PriceDto(BigDecimal.valueOf(192.47),BigDecimal.valueOf(538.63));

        RoomClientDto room1 = new RoomClientDto(1L, "test1",price1);
        RoomClientDto room2 = new RoomClientDto(2L, "test2",price2);
        RoomClientDto room3 = new RoomClientDto(3L, "test3",price3);
        RoomClientDto room4 = new RoomClientDto(4L, "test4",price4);
        RoomClientDto room5 = new RoomClientDto(5L,"test5",price5);
        RoomClientDto room6 = new RoomClientDto(6L, "test6",price6);

        List<RoomClientDto> roomList1 = new ArrayList<>(Arrays.asList(room1, room2));
        List<RoomClientDto> roomList2 = new ArrayList<>(Arrays.asList(room3, room4));
        List<RoomClientDto> roomList3 = new ArrayList<>(Arrays.asList(room5, room6));

        HotelInfoClientResponseDto hotelInfoClient1 = new HotelInfoClientResponseDto(1L,"Hotel test1","123","Porto Seguro",roomList1);
        HotelInfoClientResponseDto hotelInfoClient2 = new HotelInfoClientResponseDto(2L,"Hotel test2","321","Fortaleza",roomList2);
        HotelInfoClientResponseDto hotelInfoClient3 = new HotelInfoClientResponseDto(3L,"Hotel test3","132","Porto Alegre",roomList3);

        hotelInfoClientResponsesByCity = new ArrayList<>(Arrays.asList(hotelInfoClient1, hotelInfoClient2, hotelInfoClient3));
        hotelInfoClientResponsesByHotel = new ArrayList<>(List.of(hotelInfoClient1));
    }
}
