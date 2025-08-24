package com.weatherapi.domain.mapper.geocode;

import com.weatherapi.domain.dto.response.GeocodeNominatimResponseDTO;
import com.weatherapi.domain.model.Address;
import com.weatherapi.domain.model.GeocodeResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Arrays;

@Mapper(componentModel = "spring")
public interface NominatimLocationProviderMapper {

    @Mapping(source = "address.city", target = "address.city")
    @Mapping(source = "address.state", target = "address.state")
    @Mapping(source = "address.postcode", target = "address.postcode")
    @Mapping(source = "address.country", target = "address.country")
    @Mapping(source = "rawDisplayName", target = "rawDisplayName")
    GeocodeResponse toGeocodeResponse(GeocodeNominatimResponseDTO dto);

    @AfterMapping
    default void fillWhenMissingBuilder(GeocodeNominatimResponseDTO dto,
                                        @MappingTarget GeocodeResponse.GeocodeResponseBuilder out) {
        Address parsed = parseDisplayNamePositional(dto.getRawDisplayName());
        if (parsed != null && !isEmpty(parsed)) {
            out.address(parsed);
        }
    }

    default boolean isEmpty(Address a) {
        return a == null
                || (isBlank(a.getCity()) && isBlank(a.getState())
                && isBlank(a.getPostcode()) && isBlank(a.getCountry()));
    }

    default boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    /**
     * Regra POSICIONAL fixa (padrão definido):
     * 0: CEP
     * 2: Bairro (ignorado se não existir campo)
     * 3: Cidade
     * n-3: Estado
     * n-1: País
     */
    default Address parseDisplayNamePositional(String display) {
        if (isBlank(display)) return null;

        String[] parts = Arrays.stream(display.split(","))
                .map(String::trim)
                .filter(p -> !p.isBlank())
                .toArray(String[]::new);

        if (parts.length == 0) return null;

        Address addr = new Address();

        // CEP (0)
        addr.setPostcode(getAt(parts, 0));

        // Cidade (3)
        addr.setCity(getAt(parts, 3));

        // Estado (penúltimo)
        addr.setState(getFromEnd(parts, 3));

        // País (último)
        addr.setCountry(getFromEnd(parts, 1));

        return addr;
    }

    // pega índice absoluto com segurança
    default String getAt(String[] parts, int idx) {
        return (idx >= 0 && idx < parts.length) ? emptyToNull(parts[idx]) : null;
    }

    // pega do fim: 1 = último, 2 = penúltimo...
    default String getFromEnd(String[] parts, int posFromEnd) {
        int idx = parts.length - posFromEnd;
        return getAt(parts, idx);
    }

    default String emptyToNull(String s) {
        return isBlank(s) ? null : s;
    }
}
