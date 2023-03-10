package online.strongnation.business.service;

import online.strongnation.business.model.dto.RegionDTO;

import java.util.List;

public interface RegionService {
    RegionDTO create(String countryName, String name);

    List<RegionDTO> createAll(String countryName, List<String> names);

    RegionDTO get(String countryName, String name);

    RegionDTO get(Long id);

    List<RegionDTO> all(String countryName);

    RegionDTO rename(String countryName, String oldName, String newName);

    RegionDTO rename(Long id, String newName);

    RegionDTO delete(String countryName, String name);

    RegionDTO delete(Long id);

    List<RegionDTO> deleteAllByCountry(String name);
}
