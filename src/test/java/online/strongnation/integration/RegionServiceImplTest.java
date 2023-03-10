package online.strongnation.integration;

import online.strongnation.business.config.NameProperties;
import online.strongnation.business.exception.CountryNotFoundException;
import online.strongnation.business.exception.IllegalRegionException;
import online.strongnation.business.exception.RegionNotFoundException;
import online.strongnation.business.model.entity.Country;
import online.strongnation.business.model.entity.Region;
import online.strongnation.business.repository.CountryRepository;
import online.strongnation.business.repository.RegionRepository;
import online.strongnation.business.service.RegionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class RegionServiceImplTest {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionService service;

    private final String USA_NAME = "United States of America";
    private final String POLAND_NAME = "Poland";
    private final String WASHINGTON_NAME = "Washington D.C.";
    private final String WARSAW_NAME = "Warsaw";

    //write tests for categories
    @Autowired
    private RegionRepository regionRepository;

    @BeforeEach
    void setUp() {
        //usa
        Region washington = new Region(WASHINGTON_NAME);
        Region warsawInUSA = new Region(WARSAW_NAME);
        Country USACountry = new Country(USA_NAME);
        USACountry.setRegions(List.of(washington, warsawInUSA));
        //poland
        Country polandCountry = new Country(POLAND_NAME);
        Region washingtonInPoland = new Region(WASHINGTON_NAME);
        Region warsawInPoland = new Region(WARSAW_NAME);
        polandCountry.setRegions(List.of(washingtonInPoland, warsawInPoland));
        countryRepository.saveAll(List.of(polandCountry, USACountry));
    }

    @AfterEach
    void tearDown() {
        countryRepository.deleteAll();
        regionRepository.deleteAll();
    }

    @Test
    void createSimple() {
        //given
        final String newRegionName = "New Region Name";
        //when
        final var actual = service.create(USA_NAME, newRegionName);
        //then
        assertThat(actual.getId()).isNotNull();
        assertThat(regionRepository.existsRegionByName(newRegionName)).isTrue();
    }

    @Test
    void createAndCutExcessiveWhiteSpaces() {
        //given
        final String newRegionName = "      \nNew   \t\t\t\t                               Region       Name       ";
        final String expectedRegionName = "New Region Name";
        //when
        final var actual = service.create(USA_NAME, newRegionName);
        //then
        assertThat(actual.getId()).isNotNull();
        assertThat(regionRepository.existsRegionByName(expectedRegionName)).isTrue();
        assertThat(regionRepository.existsRegionByName(newRegionName)).isFalse();
    }

    @Test
    void createToSpecificCountry() {
        //given
        final String regionName = "New Region Name";
        //when
        service.create(USA_NAME, regionName);
        //then
        assertThat(regionRepository.existsRegionInCountryByNamesIgnoringCase(USA_NAME, regionName)).isTrue();
        assertThat(regionRepository.existsRegionInCountryByNamesIgnoringCase(POLAND_NAME, regionName)).isFalse();
    }

    @Test
    void createWhenCountryDoesntExist() {
        //given
        final String regionName = "New Region Name";
        //when
        //then
        assertThatThrownBy(() -> service.create("Russia", regionName))
                .isInstanceOf(CountryNotFoundException.class);
    }

    @Test
    void createWhenRegionAlreadyExistsLowerCase() {
        //given
        final String regionName = "New Region Name";
        Country country = countryRepository.findCountryByName(USA_NAME)
                .orElseThrow(CountryNotFoundException::new);
        country.setRegions(List.of(new Region(regionName)));
        countryRepository.save(country);
        //when
        //then
        assertThatThrownBy(() -> service.create(USA_NAME, regionName.toLowerCase()))
                .isInstanceOf(IllegalRegionException.class);
    }

    @Test
    void createWhenRegionAlreadyExistsUpperCase() {
        //given
        final String regionName = "New Region Name";
        Country country = countryRepository.findCountryByName(USA_NAME)
                .orElseThrow(CountryNotFoundException::new);
        country.setRegions(List.of(new Region(regionName)));
        countryRepository.save(country);
        //when
        //then
        assertThatThrownBy(() -> service.create(USA_NAME, regionName.toUpperCase()))
                .isInstanceOf(IllegalRegionException.class);
    }

    @Test
    void createAll() {
        //given
        final List<String> strings = List.of("Some region 1",
                "Some region 2",
                "Some region 3",
                "Some region 4");
        //when
        service.createAll(USA_NAME, strings);
        //then
        for (var i : strings) {
            assertThat(regionRepository
                    .existsRegionInCountryByNamesIgnoringCase(USA_NAME, i)).isTrue();
        }
    }

    @Test
    void createAllWhenRegionAlreadyExists() {
        //given
        final String regionName = "New Region Name";
        final List<String> strings = List.of("Some region 1",
                "Some region 2",
                "Some region 3",
                regionName,
                "Some region 4");
        Country country = countryRepository.findCountryByName(USA_NAME)
                .orElseThrow(CountryNotFoundException::new);
        country.setRegions(List.of(new Region(regionName)));
        countryRepository.save(country);
        //when
        //then
        assertThatThrownBy(() -> service.createAll(USA_NAME, strings))
                .isInstanceOf(IllegalRegionException.class)
                .hasMessage("Region " + regionName + " is already present. Regions are not saved");
        for (var i : strings) {
            if (regionName.equals(i)) {
                continue;
            }
            assertThat(regionRepository
                    .existsRegionInCountryByNamesIgnoringCase(USA_NAME, i)).isFalse();
        }
    }

    @Test
    void rename() {
        //given
        final String newName = "Some new name";
        final var expected = regionRepository
                .findRegionDTOInCountryByNamesIgnoringCase(USA_NAME, WASHINGTON_NAME)
                .orElseThrow(RegionNotFoundException::new)
                .toBuilder()
                .name(newName).build();
        //when
        var actual = service.rename(USA_NAME, WASHINGTON_NAME, newName);
        //then
        assertThat(actual).isEqualTo(expected);
        assertThat(regionRepository
                .existsRegionInCountryByNamesIgnoringCase(POLAND_NAME, WASHINGTON_NAME))
                .isTrue();
        assertThat(regionRepository
                .existsRegionInCountryByNamesIgnoringCase(USA_NAME, WASHINGTON_NAME))
                .isFalse();
    }

    @Test
    void renameWithBlankSymbols() {
        //given
        final String expectedName = "Some new name";
        final String givenNewName = "        Some  \n\t new \t  name    \n";
        final var expected = regionRepository
                .findRegionDTOInCountryByNamesIgnoringCase(USA_NAME, WASHINGTON_NAME)
                .orElseThrow(RegionNotFoundException::new)
                .toBuilder()
                .name(expectedName).build();
        //when
        var actual = service.rename(USA_NAME, WASHINGTON_NAME, givenNewName);
        //then
        assertThat(actual).isEqualTo(expected);
        assertThat(regionRepository
                .existsRegionInCountryByNamesIgnoringCase(POLAND_NAME, WASHINGTON_NAME))
                .isTrue();
        assertThat(regionRepository
                .existsRegionInCountryByNamesIgnoringCase(USA_NAME, WASHINGTON_NAME))
                .isFalse();
        assertThat(regionRepository
                .existsRegionInCountryByNamesIgnoringCase(USA_NAME, givenNewName))
                .isFalse();
        assertThat(regionRepository
                .existsRegionInCountryByNamesIgnoringCase(USA_NAME, expectedName))
                .isTrue();
    }

    @Test
    void renameWithTooLongRegionOld() {
        //given
        byte[] array = new byte[NameProperties.REGION_NAME_LENGTH + 1];
        Arrays.fill(array, (byte) 'A');
        final var input = new String(array, StandardCharsets.UTF_8);
        //when
        //then
        assertThatThrownBy(() -> service.rename(USA_NAME, input, WASHINGTON_NAME))
                .isInstanceOf(IllegalRegionException.class);
    }

    @Test
    void renameWithTooLongCountryNew() {
        //given
        byte[] array = new byte[NameProperties.REGION_NAME_LENGTH + 1];
        Arrays.fill(array, (byte) 'A');
        final var input = new String(array, StandardCharsets.UTF_8);
        //when
        //then
        assertThatThrownBy(() -> service.rename(USA_NAME, WASHINGTON_NAME, input))
                .isInstanceOf(IllegalRegionException.class);
    }

    @Test
    void renameById() {
        //given
        final String expectedName = "Some new name";
        final String givenNewName = "        Some  \n\t new \t  name    \n";
        final var expected = regionRepository
                .findRegionDTOInCountryByNamesIgnoringCase(USA_NAME, WASHINGTON_NAME)
                .orElseThrow(RegionNotFoundException::new)
                .toBuilder()
                .name(expectedName).build();
        //when
        var actual = service.rename(expected.getId(), givenNewName);
        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById() {
        //given
        final var expected = regionRepository
                .findRegionDTOInCountryByNamesIgnoringCase(USA_NAME, WASHINGTON_NAME)
                .orElseThrow(RegionNotFoundException::new);
        //when
        var actual = service.delete(expected.getId());
        //then
        assertThat(actual).isEqualTo(expected);
        assertThat(regionRepository
                .existsRegionInCountryByNamesIgnoringCase(POLAND_NAME, WASHINGTON_NAME))
                .isTrue();
        assertThat(regionRepository
                .existsRegionInCountryByNamesIgnoringCase(USA_NAME, WASHINGTON_NAME))
                .isFalse();
    }
}