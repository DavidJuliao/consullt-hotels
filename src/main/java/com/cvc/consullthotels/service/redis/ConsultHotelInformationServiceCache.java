package com.cvc.consullthotels.service.redis;

import com.cvc.consullthotels.Exception.ConsultHotelInformationException;
import com.cvc.consullthotels.domain.dto.HotelInfoResponseDto;
import com.cvc.consullthotels.service.client.ConsultHotelInformationClient;
import com.cvc.consullthotels.service.mapper.HotelInfoResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultHotelInformationServiceCache {

    @Autowired
    private final ConsultHotelInformationClient consultHotelInfoClient;

    private final HotelInfoResponseMapper hotelInfoResponseMapper;

    @Cacheable(cacheNames = "hotelInformationPerCity",key = "#idCity")
    public List<HotelInfoResponseDto> findByIdCity(Long idCity) throws ConsultHotelInformationException {
        return consultHotelInfoClient.findByIdCity(idCity).stream()
                .map(hotelInfoResponseMapper::toHotelInfoResponseDto)
                .collect(Collectors.toList());
    }

}
