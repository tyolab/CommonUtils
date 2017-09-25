package au.com.tyo.utils;

import java.util.List;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 23/9/17.
 */

public class LocationUtils {

    public static final int EARTH_RADIUS = 6371; // radius R = 6371 km,  the distance from Earth's center to its surface, about 6,371 km
    public static final int EEARTH_DIAMETER = 12742; // kms

    public interface LocationPoint {
        double getLongitude();
        double getLatitude();
    }

    public static double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    /**
     * Calculate the distance between two GPS points
     *
     * @param location1, 0 latitude, 1 longitude
     * @param location2
     * @return
     */
    public static double distance(double[] location1, double[] location2) {
        double lat1 = degreeToRadian(location1[0]);
        double lat2 = degreeToRadian(location2[0]);

        double dfLat = lat2 - lat1;
        double dfLon = degreeToRadian(location2[1] - location1[1]);
        double lon1 = degreeToRadian(location1[1]);
        double lon2 = degreeToRadian(location2[1]);

        /**
         * rough
         */
        double x = dfLon * Math.cos((lat1 + lat2) / 2);
        double y = dfLat;
        double d = Math.sqrt(x * x + y * y) * EARTH_RADIUS;
        return d;
    }

    /**
     * Calculate the distance between two GPS points
     *
     * @param location1, 0 latitude, 1 longitude
     * @param location2
     * @return
     */
    public static double distanceMoreAccurate(double[] location1, double[] location2) {
        double lat1 = degreeToRadian(location1[0]);
        double lat2 = degreeToRadian(location2[0]);

        double dfLat = lat2 - lat1;
        double dfLon = degreeToRadian(location2[1] - location1[1]);

        /**
         * more accurate
         */
        double a = Math.sin(dfLat/2) * Math.sin(dfLat/2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dfLon/2) * Math.sin(dfLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static LocationPoint findNearest(List list, LocationPoint point) {
        double closest = EEARTH_DIAMETER;
        LocationPoint found = null;
        double[] location1 = new double[] {point.getLatitude(), point.getLongitude()};
        for ( int i = 0; i < list.size(); ++i) {

            LocationPoint p2 = (LocationPoint) list.get(i);

            double[] location2 = new double[] {p2.getLatitude(), p2.getLongitude()};
            double d = distance(location1, location2);
            if (d < closest) {
                closest = d;
                found = p2;
            }
        }
        return found;
    }
}

/**
 * References:
 *
 * 1. http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates
 * 2. http://www.movable-type.co.uk/scripts/latlong.html
 */