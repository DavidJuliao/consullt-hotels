package com.cvc.consullthotels.controller;

import com.cvc.consullthotels.Exception.CheckInDateInvalidException;
import com.cvc.consullthotels.Exception.CheckOutDateInvalidException;
import com.cvc.consullthotels.Exception.ConsultHotelInformationException;
import com.cvc.consullthotels.Exception.NumberOfClientsException;
import com.cvc.consullthotels.Utils.JsonUtils;
import com.cvc.consullthotels.domain.dto.ApiError;
import com.cvc.consullthotels.domain.dto.CategoryDto;
import com.cvc.consullthotels.domain.dto.HotelInfoClientResponseDto;
import com.cvc.consullthotels.domain.dto.HotelInfoResponseDto;
import com.cvc.consullthotels.domain.dto.PriceDetailDto;
import com.cvc.consullthotels.domain.dto.PriceDto;
import com.cvc.consullthotels.domain.dto.RoomClientDto;
import com.cvc.consullthotels.domain.dto.RoomDto;
import com.cvc.consullthotels.enums.ErrorType;
import com.cvc.consullthotels.service.ConsultHotelsService;
import com.cvc.consullthotels.service.client.ConsultHotelInfoClient;
import com.cvc.consullthotels.service.mapper.HotelInfoResponseMapper;
import com.cvc.consullthotels.service.redis.ConsultHotelInformationServiceCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cvc.consullthotels.Utils.DateUtils.dateToString;
import static com.cvc.consullthotels.Utils.JsonUtils.asJsonString;
import static com.cvc.consullthotels.domain.dto.ApiError.crateBodyError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HotelInformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultHotelInfoClient consultHotelInfoClient;

    @MockBean
    private ConsultHotelInformationServiceCache consultHotelInformationServiceCache;

    @MockBean
    private ConsultHotelsService consultHotelsService;

    @MockBean
    private HotelInfoResponseMapper hotelInfoResponseMapper;

    HotelInfoClientResponseDto hotelInfoClientResponseDto;
    private List<HotelInfoResponseDto> hotelInformationClientResponses;
    private  HotelInfoResponseDto hotelInfoResponseDto;
    private Page<HotelInfoClientResponseDto> pageHotelInformation;

    private static final String GET_BY_CITY="/hotel-reservation/city/{cityId}";
    private static final String GET_BY_HOTEL="/hotel-reservation/hotel/{hotelId}";
    private static final String PARAM_CHECK_IN = "checkInDate";
    private static final String PARAM_CHECK_OUT = "checkOutDate";
    private static final String PARAM_NUMBER_OF_ADULTS = "numberOfAdults";
    private static final String PARAM_NUMBER_OF_CHILDREN = "numberOfChildren";

    @BeforeEach
    public void setup(){
        hotelInfoClientResponseDto = new HotelInfoClientResponseDto(1L,"Hotel test1","1032","Porto Seguro",new ArrayList<>());
        hotelInfoResponseDto = new HotelInfoResponseDto(1L,"Hotel test1","Porto Seguro",new ArrayList<>());
        fillHotels();
    }

    @Test
     void findByCity_AllDataInformed_returnOK() throws Exception{
        Page<HotelInfoResponseDto> responseDtoPage = new PageImpl<>(hotelInformationClientResponses);
        when(consultHotelsService.findAllByCity(any(Long.class),any(Pageable.class))).thenReturn(responseDtoPage);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(GET_BY_CITY,"1032")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(JsonUtils.asJsonString(responseDtoPage));
    }

    @Test
    void findByCity_AnErrorOccursWhenQueryingHotels_returnInternalServerError() throws Exception{
        String message = "An error occurred while fetching hotel information";
        ApiError body = ApiError.crateBodyError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorType.CONSULT_HOTEL_INFORMATION.getUri(),
                ErrorType.CONSULT_HOTEL_INFORMATION.getTitle(), message);
        when(consultHotelsService.findAllByCity(any(Long.class),any(Pageable.class))).thenThrow(new ConsultHotelInformationException());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(GET_BY_CITY,"1035")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).isEqualTo(JsonUtils.asJsonString(body));
    }

    @Test
    void findByHotel_AllDataInformed_returnOK() throws Exception{
        LocalDate checkInDate = LocalDate.now().plusDays(1L);
        LocalDate checkOutDate = checkInDate.plusDays(1L);
        Integer numberOfAdults = 1;
        Integer numberOfChild = 1;

        when(consultHotelsService.findByHotel(any(Long.class),any(LocalDate.class), any(LocalDate.class),any(Integer.class),any(Integer.class)))
                .thenReturn(hotelInfoResponseDto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(GET_BY_HOTEL,"1")
                .param(PARAM_CHECK_IN,dateToString(checkInDate))
                .param(PARAM_CHECK_OUT,dateToString(checkOutDate))
                .param(PARAM_NUMBER_OF_ADULTS,asJsonString(numberOfAdults))
                .param(PARAM_NUMBER_OF_CHILDREN,asJsonString(numberOfChild))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(JsonUtils.asJsonString(hotelInfoResponseDto));
    }

    @Test
    void findByHotel_checkInIsLessThanCurrentDate_returnBadRequest() throws Exception{

        String message = "checkIn date is less than current date";
        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.CHECK_IN_DATE_INVALID.getUri(),
                                                            ErrorType.CHECK_IN_DATE_INVALID.getTitle(), message);
        LocalDate checkInDate = LocalDate.now().minusDays(1);
        LocalDate checkOutDate = checkInDate.plusDays(1L);
        Integer numberOfAdults = 1;
        Integer numberOfChild = 1;

        when(consultHotelsService.findByHotel(any(Long.class),any(LocalDate.class), any(LocalDate.class),any(Integer.class),any(Integer.class)))
                .thenThrow(new CheckInDateInvalidException());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(GET_BY_HOTEL,"1")
                .param(PARAM_CHECK_IN,dateToString(checkInDate))
                .param(PARAM_CHECK_OUT,dateToString(checkOutDate))
                .param(PARAM_NUMBER_OF_ADULTS,asJsonString(numberOfAdults))
                .param(PARAM_NUMBER_OF_CHILDREN,asJsonString(numberOfChild))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(JsonUtils.asJsonString(body));
    }

    @Test
    void findByHotel_checkoutIsLessThanOrEqualToCheckIn_returnBadRequest() throws Exception{
        String message = "checkOut date is less than or equal checkIn";
        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.CHECK_OUT_DATE_INVALID.getUri(),
                ErrorType.CHECK_OUT_DATE_INVALID.getTitle(), message);
        LocalDate checkInDate = LocalDate.now().minusDays(1);
        LocalDate checkOutDate = checkInDate.minusDays(1);
        Integer numberOfAdults = 1;
        Integer numberOfChild = 1;

        when(consultHotelsService.findByHotel(any(Long.class),any(LocalDate.class), any(LocalDate.class),any(Integer.class),any(Integer.class)))
                .thenThrow(new CheckOutDateInvalidException());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(GET_BY_HOTEL,"1")
                .param(PARAM_CHECK_IN,dateToString(checkInDate))
                .param(PARAM_CHECK_OUT,dateToString(checkOutDate))
                .param(PARAM_NUMBER_OF_ADULTS,asJsonString(numberOfAdults))
                .param(PARAM_NUMBER_OF_CHILDREN,asJsonString(numberOfChild))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(JsonUtils.asJsonString(body));
    }

    @Test
    void findByHotel_NoMoreThanZerClientsWereProvided_returnBadRequest() throws Exception{
        String message = "Number of Clients must be greater than zero";
        ApiError body = crateBodyError(HttpStatus.BAD_REQUEST.value(), ErrorType.NUMBER_CLIENTS_INVALID.getUri(),
                ErrorType.NUMBER_CLIENTS_INVALID.getTitle(), message);
        LocalDate checkInDate = LocalDate.now().minusDays(1);
        LocalDate checkOutDate = checkInDate.minusDays(1);
        Integer numberOfAdults = 0;
        Integer numberOfChild = 0;

        when(consultHotelsService.findByHotel(any(Long.class),any(LocalDate.class), any(LocalDate.class),any(Integer.class),any(Integer.class)))
                .thenThrow(new NumberOfClientsException());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(GET_BY_HOTEL,"1")
                .param(PARAM_CHECK_IN,dateToString(checkInDate))
                .param(PARAM_CHECK_OUT,dateToString(checkOutDate))
                .param(PARAM_NUMBER_OF_ADULTS,asJsonString(numberOfAdults))
                .param(PARAM_NUMBER_OF_CHILDREN,asJsonString(numberOfChild))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(JsonUtils.asJsonString(body));
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

        hotelInformationClientResponses = new ArrayList<>(Arrays.asList(hotelInfoResponse1,hotelInfoResponse2,hotelInfoResponse3));
    }
}
