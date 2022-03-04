package com.cvc.consullthotels.controller;

import com.cvc.consullthotels.domain.dto.HotelInfoClientResponseDto;
import com.cvc.consullthotels.domain.dto.HotelInfoResponseDto;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.cvc.consullthotels.Utils.DateUtils.dateToString;
import static com.cvc.consullthotels.Utils.JsonUtils.asJsonString;
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

    private ConsultHotelsService consultHotelsService;

    @MockBean
    private HotelInfoResponseMapper hotelInfoResponseMapper;

    private final List<HotelInfoClientResponseDto> hotelInfoClientResponses = new ArrayList<>();
    HotelInfoClientResponseDto hotelInfoClientResponseDto;
    private final List<HotelInfoResponseDto> hotelInformationClientResponses = new ArrayList<>();
    private  HotelInfoResponseDto hotelInfoResponseDto;
    private Pageable pageable;
    private Page<HotelInfoClientResponseDto> pageHotelInformation;

    private static final String GET_BY_CITY="/hotel-reservation/city/{cityId}";
    private static final String GET_BY_HOTEL="/hotel-reservation/hotel/{hotelId}";
    private static final String PARAM_CHECK_IN = "checkInDate";
    private static final String PARAM_CHECK_OUT = "checkOutDate";
    private static final String PARAM_NUMBER_OF_ADULTS = "numberOfAdults";
    private static final String PARAM_NUMBER_OF_CHILDREN = "numberOfChildren";

    @BeforeEach
    public void setup(){
        HotelInfoClientResponseDto hotelInfo = new HotelInfoClientResponseDto(1L,"Hotel test1","1032","Porto Seguro",new ArrayList<>());
        hotelInfoClientResponseDto = hotelInfo;
        HotelInfoResponseDto hotelInfoResponseDto = new HotelInfoResponseDto(1L,"Hotel test1","Porto Seguro",new ArrayList<>());
        this.hotelInfoResponseDto = hotelInfoResponseDto;
        hotelInfoClientResponses.add(hotelInfo);
        hotelInformationClientResponses.add(hotelInfoResponseDto);
        consultHotelInformationServiceCache = new ConsultHotelInformationServiceCache(consultHotelInfoClient);
        consultHotelsService = new ConsultHotelsService(consultHotelInfoClient, consultHotelInformationServiceCache, hotelInfoResponseMapper);
        pageable = Pageable.unpaged();
        pageHotelInformation = new PageImpl<>(hotelInfoClientResponses);
    }

    @Test
     void findByCity_AllDataInformed_returnOK() throws Exception{
        when(consultHotelInformationServiceCache.findByIdCity(any(Long.class))).thenReturn(hotelInfoClientResponses);
//        when(hotelInfoResponseMapper.toHotelInfoResponseDto(any(HotelInfoClientResponseDto.class))).thenReturn(hotelInformationClientResponses);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(GET_BY_CITY,"1032")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void findByHotel_AllDataInformed_returnOK() throws Exception{
        LocalDate checkInDate = LocalDate.now().plusDays(1L);
        LocalDate checkOutDate = checkInDate.plusDays(1L);
        Integer numberOfAdults = 1;
        Integer numberOfChild = 1;

        when(consultHotelInfoClient.findByIdHotel(any(Long.class))).thenReturn(hotelInfoClientResponses);
        when(hotelInfoResponseMapper.toHotelInfoResponseDto(any(HotelInfoClientResponseDto.class))).thenReturn(hotelInfoResponseDto);
//        when(consultHotelsService.findByHotel(1L,checkInDate, checkOutDate,numberOfAdults,numberOfChild)).thenReturn(hotelInfoResponseDto);

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
    }
}
