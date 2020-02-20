package au.com.tyo.utils;

import java.util.ArrayList;
import java.util.Collections;
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

    public static class ComparableLocationPoint implements Comparable {

        private LocationPoint locationPoint;

        public ComparableLocationPoint(LocationPoint locationPoint) {
            this.locationPoint = locationPoint;
        }

        public ComparableLocationPoint(LocationPoint locationPoint, double distanceToBasePoint) {
            this.locationPoint = locationPoint;
            this.distanceToBasePoint = distanceToBasePoint;
        }

        private double distanceToBasePoint = 0;

        public double getDistanceToBasePoint() {
            return distanceToBasePoint;
        }

        public void setDistanceToBasePoint(double distanceToBasePoint) {
            this.distanceToBasePoint = distanceToBasePoint;
        }

        @Override
        public int compareTo(Object o) {
            ComparableLocationPoint point2 = (ComparableLocationPoint) o;

            double d1 = getDistanceToBasePoint();
            double d2 = point2.getDistanceToBasePoint();
            if (d1 == d2)
                return 0;
            else if (d1 < d2)
                return -1;
            return 1;
        }

        public LocationPoint getLocationPoint() {
            return locationPoint;
        }
    }

    public static double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    /**
     * Calculate the distance between two GPS points
     *
     * @param location1, 0 latitude, 1 longitude
     * @param location2, 0 latitude, 1 longitude
     * @return
     */
    public static double distance(double[] location1, double[] location2) {
        return distance(location1[0], location1[1], location2[0], location2[1]);
    }

    public static double distance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double lat1 = degreeToRadian(latitude1);
        double lat2 = degreeToRadian(latitude2);

        double dfLat = lat2 - lat1;
        double dfLon = degreeToRadian(longitude1 - longitude2);

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

    public static List<ComparableLocationPoint> findNearestComparablePoints(List list, LocationPoint point) {
        List<ComparableLocationPoint> tempList = new ArrayList();

        double[] location1 = new double[] {point.getLatitude(), point.getLongitude()};
        for ( int i = 0; i < list.size(); ++i) {

            LocationPoint p2 = (LocationPoint) list.get(i);

            double[] location2 = new double[] {p2.getLatitude(), p2.getLongitude()};
            double d = distance(location1, location2);

            tempList.add(new ComparableLocationPoint(p2, d));
        }

        Collections.sort(tempList);

        return tempList;
    }

    public static List<LocationPoint> findNearests(List list, LocationPoint point) {
        List<ComparableLocationPoint> tempList = findNearestComparablePoints(list, point);

        List targetList = new ArrayList();
        for (ComparableLocationPoint sortedPoint : tempList) {
            targetList.add(sortedPoint.getLocationPoint());
        }

        return targetList;
    }
}

/**
 * References:
 *
 * 1. http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates
 * 2. http://www.movable-type.co.uk/scripts/latlong.html
 */