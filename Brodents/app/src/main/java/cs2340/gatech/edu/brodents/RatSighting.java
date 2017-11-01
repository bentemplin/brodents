package cs2340.gatech.edu.brodents;

import java.util.Date;

/**
 * This class will represent a single rat sighting report. It will have all of the information that
 * a full rat app would. This class is an information holder.
 *
 * @author Ben Templin
 * @version 1.0
 */

public class RatSighting {
    // From the sightingInfo table
    private final int key;
    private final Date createdDate;
    private final String agencyCode;
    private final String agencyName; //forced to Brodents
    private final String complaintType; //provided

    // From the sightingStatus table
    private final String status; //provided
    private final Date dueDate;
    private final Date closedDate;
    private final Date resActionUpdated;

    // From the sightingLocation table
    private final String locationType; //Provided
    private final int incidentZip; //Provided
    private final String city; //Provided
    private final String borough; //Provided
    private final double latitude; //Provided
    private final double longitude; //Provided
    private final String address; //Provided

    private final String createdBy;

    /*
        Constructor is super long due to the complicated nature of a single rat sighting
     */
    /**
     * The constructor for a RatSighting object. You can pass stuff in as null, but note that this
     * only makes the information holder object. It does not actually communicate with the database.
     * @param key Unique key for the sighting
     * @param createdDate Date created
     * @param agencyCode Agency code
     * @param agencyName Full agency name
     * @param complaintType Type of complaint
     * @param status Resolution status
     * @param dueDate Due date (often null)
     * @param closedDate Date the issue was closed
     * @param resActionUpdated Last time issue was updated
     * @param locationType Type of location
     * @param incidentZip Zip for the incident
     * @param city City for the incident
     * @param borough Borough for the incident
     * @param latitude Latitude for the incident
     * @param longitude Longitude for the incident
     * @param createdBy Who created it. Defaults to "Not Specified"
     */
    RatSighting(int key, Date createdDate, String agencyCode, String agencyName,
                String complaintType, String status, Date dueDate, Date closedDate,
                Date resActionUpdated, String locationType, int incidentZip, String city,
                String borough, String address, double latitude, double longitude,
                String createdBy) {
        this.key = key;
        this.createdDate = createdDate;
        this.agencyCode = agencyCode;
        this.agencyName = agencyName;
        this.closedDate = closedDate;
        this.complaintType = complaintType;
        this.status = status;
        this.dueDate = dueDate;
        this.locationType = locationType;
        this.incidentZip = incidentZip;
        this.city = city;
        this.borough = borough;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.resActionUpdated = resActionUpdated;
        this.createdBy = createdBy;
    }

    /**
     * The constructor for a RatSighting object. You can pass stuff in as null, but note that this
     * only makes the information holder object. It does not actually communicate with the database.
     * @param key Unique key for the sighting
     * @param createdDate Date created
     * @param agencyCode Agency code
     * @param agencyName Full agency name
     * @param complaintType Type of complaint
     * @param status Resolution status
     * @param dueDate Due date (often null)
     * @param closedDate Date the issue was closed
     * @param resActionUpdated Last time issue was updated
     * @param locationType Type of location
     * @param incidentZip Zip for the incident
     * @param city City for the incident
     * @param borough Borough for the incident
     * @param latitude Latitude for the incident
     * @param longitude Longitude for the incident
     */
    RatSighting(int key, Date createdDate, String agencyCode, String agencyName,
                String complaintType, String status, Date dueDate, Date closedDate,
                Date resActionUpdated, String locationType, int incidentZip, String city,
                String borough, String address, double latitude, double longitude) {
        this.key = key;
        this.createdDate = createdDate;
        this.agencyCode = agencyCode;
        this.agencyName = agencyName;
        this.closedDate = closedDate;
        this.complaintType = complaintType;
        this.status = status;
        this.dueDate = dueDate;
        this.locationType = locationType;
        this.incidentZip = incidentZip;
        this.city = city;
        this.borough = borough;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.resActionUpdated = resActionUpdated;
        this.createdBy = "Not Specified";
    }

    // Here go all of the getters!

    /**
     * Gets the Sighting's key
     * @return The sighting's unique key.
     */
    int getKey() {return key;}

    /**
     * @return The date the sighting was created.
     */
    Date getCreatedDate() {return createdDate;}

    /**
     * @return The agency code for the sighting.
     */
    String getAgencyCode() {return agencyCode;}

    /**
     * @return The agency name for the sighting.
     */
    String getAgencyName() {return agencyName;}

    /**
     * @return The date the sighting was closed.
     */
    Date getClosedDate() {return closedDate;}

    /**
     * @return The complaint type of the sighting.
     */
    String getComplaintType() {return complaintType;}

    /**
     * @return The status of the sighting.
     */
    String getStatus() {return status;}

    /**
     * @return The due date for the sighting.
     */
    Date getDueDate() {return dueDate;}

    /**
     * @return The location type for the sighting.
     */
    CharSequence getLocationType() {return locationType;}

    /**
     * @return The zip code for the sighting.
     */
    int getZip() {return incidentZip;}

    /**
     * @return The city of the sighting.
     */
    CharSequence getCity() {return city;}

    /**
     * @return The borough of the sighting.
     */
    CharSequence getBorough() {return borough;}

    /**
     * @return The latitude of the sighting.
     */
    double getLatitude() {return latitude;}

    /**
     * @return The longitude of the sighting.
     */
    double getLongitude() {return longitude;}

    /**
     * @return The date the sighting last had a resolution action taken.
     */
    Date getDateUpdated() {return resActionUpdated;}

    /**
     * @return The address of the sighting location. Often null.
     */
    CharSequence getAddress() {return address;}

    /**
     * @return The username of the person who created the sighting.
     */
    String getCreatedBy() {return createdBy;}

    /**
     * ToString method for RatSightings
     * @return String with basic info about the sighting.
     */
    @Override
    public String toString() {
        return "Sighting " + Integer.toString(key) + ": Created by " + agencyName + " on "
                + createdDate.toString() + ". Type: " + complaintType;
    }

    /**
     * Standard equals method for RatSightings, compares keys
     * @param o Object to compare with
     * @return Whether the objects are RatSightings with equal keys
     */
    @Override
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof RatSighting)) {
            return false;
        } else {
            RatSighting or = (RatSighting) o;
            return this.key == or.key;
        }
    }
}
