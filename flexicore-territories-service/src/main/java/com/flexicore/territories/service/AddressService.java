package com.flexicore.territories.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.*;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.data.AddressRepository;
import com.flexicore.territories.reponse.AddressImportResponse;
import com.flexicore.territories.request.*;
import com.wizzdi.flexicore.boot.base.annotations.plugins.PluginInfo;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PluginInfo(version = 1)
@Extension
@Component
public class AddressService implements Plugin {

	private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

	@PluginInfo(version = 1)
	@Autowired
	private AddressRepository repository;

	@PluginInfo(version = 1)
	@Autowired
	private CityService cityService;
	@PluginInfo(version = 1)
	@Autowired
	private StreetService streetService;

	@PluginInfo(version = 1)
	@Autowired
	private CountryService countryService;

	@Autowired
	private BasicService basicService;



	@Autowired
	private RestTemplate restTemplate;
//	private static final XmlMapper xmlMapper= XmlMapper.builder().build();




	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}


	public Address updateAddress(AddressUpdate updateContainer,
								 SecurityContext SecurityContext) {
		Address address = updateContainer.getAddress();
		if (updateAddressNoMerge(address, updateContainer)) {
			repository.merge(address);

		}
		return address;
	}

	public void validate(AddressFilter addressFiltering,
						 SecurityContext SecurityContext) {
		Set<String> streetIds = addressFiltering.getStreetsIds();
		Map<String, Street> streetMap = streetIds.isEmpty() ? new HashMap<>() : repository.listByIds(Street.class, streetIds,SecurityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		streetIds.removeAll(streetMap.keySet());
		if (!streetIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Street with ids " + streetIds);
		}
		addressFiltering.setStreets(new ArrayList<>(streetMap.values()));

		Set<String> neighbourhoodIds = addressFiltering.getNeighbourhoodIds();
		Map<String, Neighbourhood> neighbourhoodMap = neighbourhoodIds.isEmpty() ? new HashMap<>() : repository.listByIds(Neighbourhood.class, neighbourhoodIds,SecurityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		neighbourhoodIds.removeAll(neighbourhoodMap.keySet());
		if (!neighbourhoodIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Neighbourhoods with ids " + neighbourhoodIds);
		}
		addressFiltering.setNeighbourhoods(new ArrayList<>(neighbourhoodMap.values()));


		Set<String> stateIds = addressFiltering.getStateIds();
		Map<String, State> stateMap = stateIds.isEmpty() ? new HashMap<>() : repository.listByIds(State.class, stateIds,SecurityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		stateIds.removeAll(stateMap.keySet());
		if (!stateIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No States with ids " + stateIds);
		}
		addressFiltering.setStates(new ArrayList<>(stateMap.values()));

		Set<String> countryIds = addressFiltering.getCountryIds();
		Map<String, Country> countryMap = countryIds.isEmpty() ? new HashMap<>() : repository.listByIds(Country.class, countryIds,SecurityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		countryIds.removeAll(countryMap.keySet());
		if (!countryIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Countries with ids " + countryIds);
		}
		addressFiltering.setCountries(new ArrayList<>(countryMap.values()));

		Set<String> citiesIds = addressFiltering.getCitiesIds();
		Map<String, City> cityMap = citiesIds.isEmpty() ? new HashMap<>() : repository.listByIds(City.class, citiesIds,SecurityContext).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		citiesIds.removeAll(cityMap.keySet());
		if (!citiesIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Cities with ids " + citiesIds);
		}
		addressFiltering.setCities(new ArrayList<>(cityMap.values()));


	}

	public void validate(AddressCreate addressCreationContainer,
						 SecurityContext SecurityContext) {
		basicService.validate(addressCreationContainer, SecurityContext);
		String streetId = addressCreationContainer.getStreetId();
		Street street = streetId != null ? getByIdOrNull(streetId,
				Street.class,  SecurityContext) : null;
		if (street == null && streetId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Street with id " + streetId);
		}
		addressCreationContainer.setStreet(street);

		String neighbourhoodId = addressCreationContainer.getNeighbourhoodId();
		Neighbourhood neighbourhood = neighbourhoodId != null ? getByIdOrNull(neighbourhoodId,
				Neighbourhood.class,  SecurityContext) : null;
		if (neighbourhood == null && neighbourhoodId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Neighbourhood with id " + neighbourhoodId);
		}
		addressCreationContainer.setNeighbourhood(neighbourhood);

	}


	public boolean updateAddressNoMerge(Address address,
										AddressCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, address);

		if (creationContainer.getFloor() != null && !creationContainer.getFloor().equals(address.getFloorForAddress())) {
			address.setFloorForAddress(creationContainer.getFloor());
			update = true;
		}

		if (creationContainer.getHouseNumber() != null && !creationContainer.getHouseNumber().equals(address.getHouseNumber())) {
			address.setHouseNumber(creationContainer.getHouseNumber());
			update = true;
		}
		if (creationContainer.getNeighbourhood() != null && (address.getNeighbourhood() == null || !creationContainer.getNeighbourhood().getId().equals(address.getNeighbourhood().getId()))) {
			address.setNeighbourhood(creationContainer.getNeighbourhood());
			update = true;
		}
		if (creationContainer.getStreet() != null && (address.getStreet() == null || !creationContainer.getStreet().getId().equals(address.getStreet().getId()))) {
			address.setStreet(creationContainer.getStreet());
			update = true;
		}

		if (creationContainer.getZipCode() != null && !creationContainer.getZipCode().equals(address.getZipCode())) {
			address.setZipCode(creationContainer.getZipCode());
			update = true;
		}

		if (creationContainer.getExternalId() != null && !creationContainer.getExternalId().equals(address.getExternalId())) {
			address.setExternalId(creationContainer.getExternalId());
			update = true;
		}
		return update;
	}


	public PaginationResponse<Address> getAllAddresses(
			SecurityContext SecurityContext, AddressFilter filtering) {
		List<Address> list = listAllAddresses(SecurityContext, filtering);
		long count = repository.countAllAddresses(SecurityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}


	public List<Address> listAllAddresses(SecurityContext SecurityContext,
										 AddressFilter filtering) {
		return repository.getAllAddresses(SecurityContext, filtering);

	}


	public Address createAddress(AddressCreate creationContainer,
								 SecurityContext SecurityContext) {
		Address address = createAddressNoMerge(creationContainer, SecurityContext);
		repository.merge(address);
		return address;
	}


	public Address createAddressNoMerge(
			AddressCreate creationContainer,
			SecurityContext SecurityContext) {

		Address address = new Address().setId(UUID.randomUUID().toString());
		BaseclassService.createSecurityObjectNoMerge(address,SecurityContext);
		updateAddressNoMerge(address, creationContainer);
		return address;
	}

	public AddressImportResponse importAddresses(
			SecurityContext SecurityContext,
			AddressImportRequest addressImportRequest) {
		AddressImportResponse addressImportResponse = new AddressImportResponse();
		String url = addressImportRequest.getUrl();
		ResponseEntity<Resource> response = restTemplate.getForEntity(url, Resource.class);
		if (response.getStatusCode().is2xxSuccessful()) {
			StreetEntry[] data;
			try {
				InputStream dataS = response.getBody().getInputStream();
				data = new StreetEntry[0];//xmlMapper.readValue(dataS, StreetEntry[].class);
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"failed parsing external url data",e);
			}
			CountryFilter countryFiltering = new CountryFilter();
			countryFiltering.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike("%Israel%"));

			Country israel = countryService.listAllCountries(SecurityContext, countryFiltering).parallelStream().findFirst().orElse(null);
			if (israel == null) {
				israel = countryService.createCountry(new CountryCreate().setName("Israel"), SecurityContext);
			}
			Map<String, City> existingCities = cityService.listAllCities(SecurityContext, new CityFilter()).parallelStream().filter(f -> f.getExternalId() != null).collect(Collectors.toMap(f -> f.getExternalId(), f -> f, (a, b) -> a));
			Map<String, Map<String, Street>> existingStreets = streetService.listAllStreets(SecurityContext, new StreetFilter()).parallelStream().filter(f -> f.getExternalId() != null && f.getCity() != null && f.getCity().getExternalId() != null).collect(Collectors.groupingBy(f -> f.getCity().getExternalId(), Collectors.toMap(f -> f.getExternalId(), f -> f, (a, b) -> a)));
			List<Object> toMerge = new ArrayList<>();

			for (StreetEntry streetEntry : data) {
				String cityId = streetEntry.getCityId() + "";
				City city = existingCities.get(cityId);
				CityCreate cityCreationContainer = new CityCreate().setExternalId(cityId).setCountry(israel).setName(streetEntry.getCityName());
				if (city == null) {
					city = cityService.createCityNoMerge(cityCreationContainer, SecurityContext);
					toMerge.add(city);
					existingCities.put(cityId, city);
					addressImportResponse.setCreatedCities(addressImportResponse.getCreatedCities() + 1);
				} else {
					if (cityService.updateCityNoMerge(cityCreationContainer, city)) {
						toMerge.add(city);
						addressImportResponse.setUpdatedCities(addressImportResponse.getUpdatedCities() + 1);
					} else {
						addressImportResponse.setUnchangedCities(addressImportResponse.getUnchangedCities() + 1);

					}
				}

				String streetId = streetEntry.getStreetId() + "";
				Street street = existingStreets.computeIfAbsent(cityId, f -> new HashMap<>()).get(streetId);
				StreetCreate streetCreationContainer = new StreetCreate().setExternalId(streetId).setCity(city).setName(streetEntry.getStreetName());
				if (street == null) {
					street = streetService.createStreetNoMerge(streetCreationContainer, SecurityContext);
					toMerge.add(street);
					existingStreets.computeIfAbsent(cityId, f -> new HashMap<>()).put(streetId, street);
					addressImportResponse.setCreatedStreet(addressImportResponse.getCreatedStreet() + 1);
				} else {
					if (streetService.updateStreetNoMerge(street, streetCreationContainer)) {
						toMerge.add(street);
						addressImportResponse.setUpdatedStreet(addressImportResponse.getUpdatedStreet() + 1);
					} else {
						addressImportResponse.setUnchangedStreet(addressImportResponse.getUnchangedStreet() + 1);

					}
				}
			}

			Set<String> cities = Stream.of(data).map(f -> f.getCityId() + "").collect(Collectors.toSet());
			Map<String, Set<String>> streets = Stream.of(data).collect(Collectors.groupingBy(f -> f.getCityId() + "", Collectors.mapping(f -> f.getStreetId() + "", Collectors.toSet())));

			List<City> citiesToDel = existingCities.values().parallelStream().filter(f -> !cities.contains(f.getExternalId())).collect(Collectors.toList());
			List<Street> streetsToDel = existingStreets.values().parallelStream().flatMap(f -> f.values().stream()).filter(f -> deleteStreet(f, streets)).collect(Collectors.toList());
			for (City city : citiesToDel) {
				city.setSoftDelete(true);
				toMerge.add(city);
				addressImportResponse.setDeletedCities(addressImportResponse.getDeletedCities() + 1);
			}

			for (Street street : streetsToDel) {
				street.setSoftDelete(true);
				toMerge.add(street);
				addressImportResponse.setDeletedStreet(addressImportResponse.getDeletedStreet() + 1);

			}
			repository.massMerge(toMerge);
			addressImportResponse.setOk(true);

		} else {
			logger.error("Request for db file from " + url + " failed with status " + response.getStatusCode());
		}

		return addressImportResponse;
	}

	private boolean deleteStreet(Street f, Map<String, Set<String>> streets) {
		Set<String> cityStreets = streets.get(f.getCity().getExternalId());
		return cityStreets == null || !cityStreets.contains(f.getExternalId());
	}
}
