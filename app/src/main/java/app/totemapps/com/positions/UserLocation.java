package app.totemapps.com.positions;

import org.jivesoftware.smackx.pubsub.Item;

public class UserLocation extends Item {

    private String sender;
    private String latitude;
    private String longitude;
    private String details;

    public UserLocation(String sender, String latitude, String longitude) {
        this.sender = sender;
        this.latitude = latitude;
        this.longitude = longitude;
        this.details = details;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDetails() {
        return this.details;
    }

    @Override
    public String toString() {
        return this.sender + " Position: " + this.latitude + ", " + this.longitude;
    }
}
