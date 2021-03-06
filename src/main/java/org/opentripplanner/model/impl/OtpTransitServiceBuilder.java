package org.opentripplanner.model.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import org.opentripplanner.model.Agency;
import org.opentripplanner.model.FareAttribute;
import org.opentripplanner.model.FareRule;
import org.opentripplanner.model.FeedInfo;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Frequency;
import org.opentripplanner.model.IdentityBean;
import org.opentripplanner.model.OtpTransitService;
import org.opentripplanner.model.Pathway;
import org.opentripplanner.model.Route;
import org.opentripplanner.model.ServiceCalendar;
import org.opentripplanner.model.ServiceCalendarDate;
import org.opentripplanner.model.ShapePoint;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.StopPattern;
import org.opentripplanner.model.Transfer;
import org.opentripplanner.model.Trip;
import org.opentripplanner.model.TripStopTimes;
import org.opentripplanner.routing.edgetype.TripPattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for building a {@link OtpTransitService}. The instance returned by the
 * {@link #build()} method is read only, and this class provide a mutable collections to construct
 * a {@link OtpTransitService} instance.
 */
public class OtpTransitServiceBuilder {
    private final List<Agency> agencies = new ArrayList<>();

    private final List<ServiceCalendarDate> calendarDates = new ArrayList<>();

    private final List<ServiceCalendar> calendars = new ArrayList<>();

    private final List<FareAttribute> fareAttributes = new ArrayList<>();

    private final List<FareRule> fareRules = new ArrayList<>();

    private final List<FeedInfo> feedInfos = new ArrayList<>();

    private final List<Frequency> frequencies = new ArrayList<>();

    private final List<Pathway> pathways = new ArrayList<>();

    private final EntityById<FeedScopedId, Route> routesById = new EntityById<>();

    private final List<ShapePoint> shapePoints = new ArrayList<>();

    private final EntityById<FeedScopedId, Stop> stopsById = new EntityById<>();

    private final TripStopTimes stopTimesByTrip = new TripStopTimes();

    private final List<Transfer> transfers = new ArrayList<>();

    private final EntityById<FeedScopedId, Trip> trips = new EntityById<>();

    private final ListMultimap<StopPattern, TripPattern> tripPatterns = ArrayListMultimap.create();


    public OtpTransitServiceBuilder() {
    }



    /* Accessors */


    public List<Agency> getAgencies() {
        return agencies;
    }

    public List<ServiceCalendarDate> getCalendarDates() {
        return calendarDates;
    }

    public List<ServiceCalendar> getCalendars() {
        return calendars;
    }

    public List<FareAttribute> getFareAttributes() {
        return fareAttributes;
    }

    public List<FareRule> getFareRules() {
        return fareRules;
    }

    public List<FeedInfo> getFeedInfos() {
        return feedInfos;
    }

    public List<Frequency> getFrequencies() {
        return frequencies;
    }

    public List<Pathway> getPathways() {
        return pathways;
    }

    public EntityById<FeedScopedId, Route> getRoutes() {
        return routesById;
    }

    public List<ShapePoint> getShapePoints() {
        return shapePoints;
    }

    public EntityById<FeedScopedId, Stop> getStops() {
        return stopsById;
    }

    public TripStopTimes getStopTimesSortedByTrip() {
        return stopTimesByTrip;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public EntityById<FeedScopedId, Trip> getTrips() {
        return trips;
    }

    public Multimap<StopPattern, TripPattern> getTripPatterns() {
        return tripPatterns;
    }


    /**
     * Find all serviceIds in both CalendarServices and CalendarServiceDates.
     */
    Set<FeedScopedId> findAllServiceIds() {
        Set<FeedScopedId> serviceIds = new HashSet<>();
        for (ServiceCalendar calendar : getCalendars()) {
            serviceIds.add(calendar.getServiceId());
        }
        for (ServiceCalendarDate date : getCalendarDates()) {
            serviceIds.add(date.getServiceId());
        }
        return serviceIds;
    }

    public OtpTransitService build() {
        createNoneExistentIds();

        return new OtpTransitServiceImpl(this);
    }

    private void createNoneExistentIds() {
        generateNoneExistentIds(feedInfos);
    }

    static <T extends IdentityBean<String>> void generateNoneExistentIds(List<T> entities) {
        int maxId = 0;


        for (T it : entities) {
            try {
                if(it.getId() != null) {
                    maxId = Math.max(maxId, Integer.parseInt(it.getId()));
                }
            } catch (NumberFormatException ignore) {}
        }

        for (T it : entities) {
            try {
                if(it.getId() == null || Integer.parseInt(it.getId()) == 0) {
                    it.setId(Integer.toString(++maxId));
                }
            }
            catch (NumberFormatException ignore) { }
        }
    }

    public void regenerateIndexes() {
        this.trips.reindex();
        this.stopsById.reindex();
        this.routesById.reindex();
        this.stopTimesByTrip.reindex();
    }
}
