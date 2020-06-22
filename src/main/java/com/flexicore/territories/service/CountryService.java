package com.flexicore.territories.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.Country;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.territories.config.Config;
import com.flexicore.territories.data.CountryRepository;
import com.flexicore.territories.interfaces.ICountryService;
import com.flexicore.territories.reponse.ImportCountriesResponse;
import com.flexicore.territories.reponse.ImportedCountry;
import com.flexicore.territories.request.CountryCreationContainer;
import com.flexicore.territories.request.CountryFiltering;
import com.flexicore.territories.request.CountryUpdateContainer;
import com.flexicore.territories.request.ImportCountriesRequest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class CountryService implements ICountryService {

	@PluginInfo(version = 1)
	@Autowired
	private CountryRepository repository;
	@Autowired
	private BaseclassNewService baseclassNewService;

	@Autowired
	private Logger logger;

	@Override
	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
			Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	@Override
	public void deleteCountry(String countryid, SecurityContext securityContext) {
		Country country = getByIdOrNull(countryid, Country.class, null,
				securityContext);
		repository.remove(country);
	}

	@Override
	public List<Country> listAllCountries(SecurityContext securityContext,
			CountryFiltering filtering) {

		return repository.listAllCountries(securityContext, filtering);
	}

	@Override
	public PaginationResponse<Country> getAllCountries(
			SecurityContext securityContext, CountryFiltering filtering) {

		List<Country> list = repository.listAllCountries(securityContext,
				filtering);
		long count = repository.countAllCountries(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	@Override
	public void validate(CountryFiltering filtering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(filtering, securityContext);
	}

	@Override
	public Country updateCountry(CountryUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Country country = updateContainer.getCountry();
		if (updateCountryNoMerge(country, updateContainer)) {
			repository.merge(country);

		}
		return country;
	}

	@Override
	public Country createCountry(CountryCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Country country = createCountryNoMerge(creationContainer,
				securityContext);
		repository.merge(country);
		return country;
	}

	private Country createCountryNoMerge(
			CountryCreationContainer creationContainer,
			SecurityContext securityContext) {
		Country country = new Country(creationContainer.getName(),
				securityContext);
		updateCountryNoMerge(country, creationContainer);
		return country;
	}

	private boolean updateCountryNoMerge(Country country,
			CountryCreationContainer creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(
				creationContainer, country);
		if (creationContainer.getCountryCode() != null
				&& !creationContainer.getCountryCode().equals(
						country.getCountryCode())) {
			country.setCountryCode(creationContainer.getCountryCode());
			update = true;
		}
		return update;

	}

	public ImportCountriesResponse importCountries(
			SecurityContext securityContext,
			ImportCountriesRequest addressImportRequest) {
		ImportCountriesResponse importCountriesResponse = new ImportCountriesResponse();
		try {

			HttpResponse<ImportedCountry[]> response = Unirest.get(
					Config.getCountriesImportUrl()).asObject(
					ImportedCountry[].class);
			if (response.getStatus() == 200) {
				ImportedCountry[] data = response.getBody();
				List<Object> toMerge = new ArrayList<>();
				Map<String, Country> existingCountries = listAllCountries(
						securityContext, new CountryFiltering())
						.parallelStream()
						.filter(f -> f.getCountryCode() != null)
						.collect(
								Collectors.toMap(f -> f.getCountryCode(),
										f -> f, (a, b) -> a));
				for (ImportedCountry datum : data) {
					if (existingCountries.get(datum.getCode()) == null) {
						CountryCreationContainer creationContainer = new CountryCreationContainer()
								.setCountryCode(datum.getCode()).setName(
										datum.getName());
						Country country = createCountryNoMerge(
								creationContainer, securityContext);
						existingCountries
								.put(country.getCountryCode(), country);
						toMerge.add(country);
						importCountriesResponse
								.setCreatedCountries(importCountriesResponse
										.getCreatedCountries() + 1);

					} else {
						importCountriesResponse
								.setExistingCountries(importCountriesResponse
										.getExistingCountries() + 1);
					}
				}
				repository.massMerge(toMerge);
			}
		} catch (UnirestException e) {
			logger.log(Level.SEVERE, "unable to get countries", e);
		}
		return importCountriesResponse;
	}

	static {
		com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		Unirest.setObjectMapper(new ObjectMapper() {
			@Override
			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return objectMapper.readValue(value, valueType);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public String writeValue(Object value) {
				try {
					return objectMapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
}