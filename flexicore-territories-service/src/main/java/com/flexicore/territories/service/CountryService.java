package com.flexicore.territories.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.Country_;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.data.CountryRepository;
import com.flexicore.territories.reponse.ImportCountriesResponse;
import com.flexicore.territories.reponse.ImportedCountry;
import com.flexicore.territories.request.CountryCreate;
import com.flexicore.territories.request.CountryFilter;
import com.flexicore.territories.request.CountryUpdate;
import com.flexicore.territories.request.ImportCountriesRequest;
import com.wizzdi.flexicore.boot.base.annotations.plugins.PluginInfo;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class CountryService implements Plugin {

	private static final Logger logger = LoggerFactory.getLogger(CountryService.class);

	@PluginInfo(version = 1)
	@Autowired
	private CountryRepository repository;
	@Autowired
	private BasicService basicService;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${flexicore.territories.countriesImportUrl:https://pkgstore.datahub.io/core/country-list/data_json/data/8c458f2d15d9f2119654b29ede6e45b8/data_json.json}")
	private String countriesImportUrl;
	@Autowired
	@Qualifier("territoriesObjectMapper")
	private ObjectMapper territoriesObjectMapper;


	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}




	public void deleteCountry(String countryid, SecurityContext SecurityContext) {
		Country country = getByIdOrNull(countryid, Country.class,  SecurityContext);
		repository.remove(country);
	}


	public List<Country> listAllCountries(SecurityContext SecurityContext,
										  CountryFilter filtering) {

		return repository.listAllCountries(SecurityContext, filtering);
	}


	public PaginationResponse<Country> getAllCountries(
			SecurityContext SecurityContext, CountryFilter filtering) {

		List<Country> list = repository.listAllCountries(SecurityContext,
				filtering);
		long count = repository.countAllCountries(SecurityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}


	public void validate(CountryFilter filtering,
						 SecurityContext SecurityContext) {
		basicService.validate(filtering, SecurityContext);
	}


	public Country updateCountry(CountryUpdate updateContainer,
								 SecurityContext SecurityContext) {
		Country country = updateContainer.getCountry();
		if (updateCountryNoMerge(country, updateContainer)) {
			repository.merge(country);

		}
		return country;
	}


	public Country createCountry(CountryCreate creationContainer,
								 SecurityContext SecurityContext) {
		Country country = createCountryNoMerge(creationContainer, SecurityContext);
		repository.merge(country);
		return country;
	}

	public Country createCountryNoMerge(
			CountryCreate creationContainer,
			SecurityContext SecurityContext) {
		Country country = new Country().setId(UUID.randomUUID().toString());
		BaseclassService.createSecurityObjectNoMerge(country,SecurityContext);
		updateCountryNoMerge(country, creationContainer);
		return country;
	}

	public boolean updateCountryNoMerge(Country country,
										 CountryCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(
				creationContainer, country);
		if (creationContainer.getCountryCode() != null && !creationContainer.getCountryCode().equals(country.getCountryCode())) {
			country.setCountryCode(creationContainer.getCountryCode());
			update = true;
		}
		return update;

	}

	public ImportCountriesResponse importCountries(
			SecurityContext SecurityContext,
			ImportCountriesRequest addressImportRequest) {
		ImportCountriesResponse importCountriesResponse = new ImportCountriesResponse();

		ResponseEntity<String> response = restTemplate.getForEntity(countriesImportUrl, String.class);
		if (response.getStatusCode().is2xxSuccessful()) {
			try {
				String body = response.getBody();
				ImportedCountry[] importedCountries = territoriesObjectMapper.readValue(body, ImportedCountry[].class);
				List<Object> toMerge = new ArrayList<>();
				Map<String, Country> existingCountries = listAllCountries(SecurityContext, new CountryFilter()).parallelStream().filter(f -> f.getCountryCode() != null).collect(Collectors.toMap(f -> f.getCountryCode(), f -> f, (a, b) -> a));
				for (ImportedCountry importedCountry : importedCountries) {
					if (existingCountries.get(importedCountry.getCode()) == null) {
						CountryCreate creationContainer = new CountryCreate().setCountryCode(importedCountry.getCode()).setName(importedCountry.getName());
						Country country = createCountryNoMerge(creationContainer, SecurityContext);
						existingCountries.put(country.getCountryCode(), country);
						toMerge.add(country);
						importCountriesResponse.setCreatedCountries(importCountriesResponse.getCreatedCountries() + 1);

					} else {
						importCountriesResponse.setExistingCountries(importCountriesResponse.getExistingCountries() + 1);
					}
				}
				repository.massMerge(toMerge);
			}
			catch (Exception e){
				logger.error("failed getting countries",e);
			}
		}

		return importCountriesResponse;
	}

}
