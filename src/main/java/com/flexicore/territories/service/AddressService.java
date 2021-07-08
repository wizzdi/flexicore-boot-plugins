package com.flexicore.territories.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.*;
import com.flexicore.security.SecurityContextBase;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.metamodel.SingularAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
	@Autowired
	private XmlMapper xmlMapper;



	public <T extends Address> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, Address_.security, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public Address updateAddress(AddressUpdate updateContainer,
								 com.flexicore.security.SecurityContextBase securityContextBase) {
		Address address = updateContainer.getAddress();
		if (updateAddressNoMerge(address, updateContainer)) {
			repository.merge(address);

		}
		return address;
	}

	public void validate(AddressFilter addressFiltering,
						 SecurityContextBase securityContextBase) {
		Set<String> streetIds = addressFiltering.getStreetsIds();
		Map<String, Street> streetMap = streetIds.isEmpty() ? new HashMap<>() : repository.listByIds(Street.class, streetIds, Street_.security, securityContextBase).stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		streetIds.removeAll(streetMap.keySet());
		if (!streetIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Street with ids " + streetIds);
		}
		addressFiltering.setStreets(new ArrayList<>(streetMap.values()));


	}

	public void validate(AddressCreate addressCreationContainer,
						 SecurityContextBase securityContextBase) {
		basicService.validate(addressCreationContainer, securityContextBase);
		String streetId = addressCreationContainer.getStreetId();
		Street street = streetId != null ? getByIdOrNull(streetId,
				Street.class, Street_.security, securityContextBase) : null;
		if (street == null && streetId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Street with id " + streetId);
		}
		addressCreationContainer.setStreet(street);

		String neighbourhoodId = addressCreationContainer.getNeighbourhoodId();
		Neighbourhood neighbourhood = neighbourhoodId != null ? getByIdOrNull(neighbourhoodId,
				Neighbourhood.class, Neighbourhood_.security, securityContextBase) : null;
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

		if (creationContainer.getNumber() != null && !creationContainer.getNumber().equals(address.getNumber())) {
			address.setNumber(creationContainer.getNumber());
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
			SecurityContextBase securityContextBase, AddressFilter filtering) {
		List<Address> list = listAllAddresses(securityContextBase, filtering);
		long count = repository.countAllAddresses(securityContextBase, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}


	public List<Address> listAllAddresses(SecurityContextBase securityContextBase,
										 AddressFilter filtering) {
		return repository.getAllAddresses(securityContextBase, filtering);

	}


	public Address createAddress(AddressCreate creationContainer,
								 SecurityContextBase securityContextBase) {
		Address address = createAddressNoMerge(creationContainer, securityContextBase);
		repository.merge(address);
		return address;
	}


	public Address createAddressNoMerge(
			AddressCreate creationContainer,
			SecurityContextBase securityContextBase) {

		Address address = new Address().setId(Baseclass.getBase64ID());
		BaseclassService.createSecurityObjectNoMerge(address,securityContextBase);
		updateAddressNoMerge(address, creationContainer);
		return address;
	}

	public AddressImportResponse importAddresses(
			SecurityContextBase securityContextBase,
			AddressImportRequest addressImportRequest) {
		AddressImportResponse addressImportResponse = new AddressImportResponse();
		String url = addressImportRequest.getUrl();
		ResponseEntity<Resource> response = restTemplate.getForEntity(url, Resource.class);
		if (response.getStatusCode().is2xxSuccessful()) {
			StreetEntry[] data;
			try {
				InputStream dataS = response.getBody().getInputStream();
				data = xmlMapper.readValue(dataS, StreetEntry[].class);
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"failed parsing external url data",e);
			}
			CountryFilter countryFiltering = new CountryFilter();
			countryFiltering.setBasicPropertiesFilter(new BasicPropertiesFilter().setNameLike("%Israel%"));

			Country israel = countryService.listAllCountries(securityContextBase, countryFiltering).parallelStream().findFirst().orElse(null);
			if (israel == null) {
				israel = countryService.createCountry(new CountryCreate().setName("Israel"), securityContextBase);
			}
			Map<String, City> existingCities = cityService.listAllCities(securityContextBase, new CityFilter()).parallelStream().filter(f -> f.getExternalId() != null).collect(Collectors.toMap(f -> f.getExternalId(), f -> f, (a, b) -> a));
			Map<String, Map<String, Street>> existingStreets = streetService.listAllStreets(securityContextBase, new StreetFilter()).parallelStream().filter(f -> f.getExternalId() != null && f.getCity() != null && f.getCity().getExternalId() != null).collect(Collectors.groupingBy(f -> f.getCity().getExternalId(), Collectors.toMap(f -> f.getExternalId(), f -> f, (a, b) -> a)));
			List<Object> toMerge = new ArrayList<>();

			for (StreetEntry streetEntry : data) {
				String cityId = streetEntry.getCityId() + "";
				City city = existingCities.get(cityId);
				CityCreate cityCreationContainer = new CityCreate().setExternalId(cityId).setCountry(israel).setName(streetEntry.getCityName());
				if (city == null) {
					city = cityService.createCityNoMerge(cityCreationContainer, securityContextBase);
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
					street = streetService.createStreetNoMerge(streetCreationContainer, securityContextBase);
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