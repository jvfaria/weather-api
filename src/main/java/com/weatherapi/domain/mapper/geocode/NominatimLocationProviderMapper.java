package com.weatherapi.domain.mapper.geocode;

import com.weatherapi.domain.dto.response.GeocodeNominatimResponseDTO;
import com.weatherapi.domain.model.GeocodeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NominatimLocationProviderMapper {
    @Mapping(source = "address.city", target = "address.city")
    @Mapping(source = "address.state", target = "address.state")
    @Mapping(source = "address.postalcode", target = "address.postalcode")
    @Mapping(source = "address.country", target = "address.country")
    GeocodeResponse toGeocodeResponse(GeocodeNominatimResponseDTO dto);
}
