package com.wizzdi.basic.iot.service;


import com.wizzdi.basic.iot.service.utils.DistanceUtils;
import org.junit.jupiter.api.Test;

public class DistanceCalculatorTest {

    @Test
    public void testDistance() {
        //32.062452, 34.794820
        //32.061670, 34.794780
        double lat1 = 32.062452;
        double lon1 = 34.794820;
        double lat2 = 32.061670;
        double lon2 = 34.794780;
        double distance = DistanceUtils.haversineDistance(lat1, lon1, lat2, lon2);
        System.out.println("Distance: " + distance + " Meters");
    }
}
