package com.cvc.consullthotels.service.redis;

import com.cvc.consullthotels.domain.dto.HotelInfoClientResponseDto;
import com.cvc.consullthotels.service.client.ConsultHotelInfoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultHotelInformationServiceCache {

    @Autowired
    private final ConsultHotelInfoClient consultHotelInfoClient;

    @Cacheable(cacheNames = "hotelInformationPerCity",key = "#idCity")
    public List<HotelInfoClientResponseDto> findByIdCity(Long idCity){
        return consultHotelInfoClient.findByIdCity(idCity);
    }

}
