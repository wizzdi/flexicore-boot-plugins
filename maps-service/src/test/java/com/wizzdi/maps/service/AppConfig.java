package com.wizzdi.maps.service;

import com.flexicore.model.territories.*;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.request.*;
import com.flexicore.territories.service.*;
import com.wizzdi.maps.model.*;
import com.wizzdi.maps.service.request.*;
import com.wizzdi.maps.service.service.*;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.service.MappedPOIService;
import net.bytebuddy.utility.nullability.MaybeNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    public static final String ON = "on";
    public static final String OFF = "off";
    public static final String UNKNOWN = "unknown";
    @Autowired
    private RoomService roomService;

    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    private LayerService layerService;

    @Autowired
    private LayerTypeService layerTypeService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private MapIconService mapIconService;

    @Autowired
    @Qualifier("adminSecurityContext")
    private SecurityContextBase securityContext;


    @Autowired
    private MapGroupService mapGroupService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private CityService cityService;
    @Autowired
    private StreetService streetService;
    @Autowired
    private NeighbourhoodService neighbourhoodService;
    @Autowired
    private StateService stateService;
    @Bean
    public LayerType layerType1(SecurityContextBase securityContext) {
        return layerTypeService.createLayerType(new LayerTypeCreate().setName("Layer type 1"),securityContext);
    }
    @Bean
    public Layer layer1(LayerType layerType1,SecurityContextBase securityContext) {
        return layerService.createLayer(new LayerCreate().setLayerType(layerType1).setName("Layer 1"),securityContext);
    }
    @Bean
    public MappedPOI first() {
        return mappedPOIService.createMappedPOI(new MappedPOICreate().setLat(32.06121634257458).setLon(34.77602769776043).setName("first"), securityContext);
    }

    @Bean
    public MappedPOI second() {
        return mappedPOIService.createMappedPOI(new MappedPOICreate().setLat(32.06207103256403).setLon(34.777958888094304).setName("second"), securityContext);
    }


    @Bean
    @Qualifier("historyIcons")
    public Map<String,MapIcon> historyIcons(){
        String type="test";
        Map<String,MapIcon> map=new HashMap<>();
        for (String statusName : Arrays.asList(ON, OFF, UNKNOWN)) {
            String externalId=type+"_"+statusName;
            map.put(statusName,mapIconService.createMapIcon(new MapIconCreate().setExternalId(externalId).setName(statusName),securityContext));
        }
        return map;
    }

    @Bean
    public Room room() {
        RoomCreate roomCreate = new RoomCreate();
        return roomService.createRoom(roomCreate, securityContext);
    }

    @Bean
    public MappedPOI mappedPOI() {
        MappedPOICreate mappedPOICreate = new MappedPOICreate();
        return mappedPOIService.createMappedPOI(mappedPOICreate, securityContext);
    }

    @Bean
    public MapGroup mapGroup() {
        MapGroupCreate mapGroupCreate = new MapGroupCreate();
        return mapGroupService.createMapGroup(mapGroupCreate, securityContext);
    }

    @Bean
    public Building building() {
        BuildingCreate buildingCreate = new BuildingCreate();
        return buildingService.createBuilding(buildingCreate, securityContext);
    }

    @Bean
    public MapIcon mapIcon() {
        MapIconCreate mapIconCreate = new MapIconCreate();
        return mapIconService.createMapIcon(mapIconCreate, securityContext);
    }

    @Bean
    public Country israel(){
        return countryService.createCountry(new CountryCreate().setCountryCode("IL").setName("Israel"),securityContext);
    }


    @Bean
    public City telAviv(Country israel){
        return cityService.createCity(new CityCreate().setExternalId("tlv").setCountry(israel).setName("Tel Aviv"),securityContext);
    }
    @Bean
    public Street hertzel(City telAviv){
        return streetService.createStreet(new StreetCreate().setExternalId("hertzel").setCity(telAviv).setName("Hertzel"),securityContext);
    }

    @Bean
    public Neighbourhood yadEliyahu(City telAviv){
        return neighbourhoodService.createNeighbourhood(new NeighbourhoodCreate().setCity(telAviv).setExternalId("yadEliayhu").setCity(telAviv).setName("Yad Eliyahu"),securityContext);
    }

    @Bean
    public Address telAvivAddress(Neighbourhood yadEliyahu,Street hertzel){
        return addressService.createAddress(new AddressCreate().setStreet(hertzel).setNeighbourhood(yadEliyahu).setExternalId("tvl1").setName("Tel Aviv Address"),securityContext);
    }




    @Bean
    public Country usa(){
        return countryService.createCountry(new CountryCreate().setCountryCode("US").setName("USA"),securityContext);
    }

    @Bean
    public State newYork(Country usa){
        return stateService.createState(new StateCreate().setCountry(usa).setExternalId("NY").setName("New York"),securityContext);
    }


    @Bean
    public City newYorkCity(Country usa,State newYork){
        return cityService.createCity(new CityCreate().setState(newYork).setExternalId("NYC").setCountry(usa).setName("New York City"),securityContext);
    }
    @Bean
    public Street broadway(City newYorkCity){
        return streetService.createStreet(new StreetCreate().setExternalId("broadway").setCity(newYorkCity).setName("Broadway"),securityContext);
    }

    @Bean
    public Neighbourhood manhattan(City newYorkCity){
        return neighbourhoodService.createNeighbourhood(new NeighbourhoodCreate().setCity(newYorkCity).setExternalId("manhattan").setCity(newYorkCity).setName("Manhattan"),securityContext);
    }

    @Bean
    public Address newyorkAddress(Neighbourhood manhattan,Street broadway){
        return addressService.createAddress(new AddressCreate().setStreet(broadway).setNeighbourhood(manhattan).setExternalId("nyc1").setName("New York Address"),securityContext);
    }


}
