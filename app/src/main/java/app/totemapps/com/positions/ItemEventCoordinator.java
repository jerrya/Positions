package app.totemapps.com.positions;

import android.util.Log;

import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

public class ItemEventCoordinator extends UserLocationProvider implements ItemEventListener {

    protected static final String TAG = "HandlePublishedItems";

    LoginFragment loginFragment = LoginFragment.instance();

    @Override
    public void handlePublishedItems(ItemPublishEvent items) {
        PayloadItem<UserLocation> userPayload = (PayloadItem<UserLocation>) items.getItems().get(0);
        UserLocation userLocation = userPayload.getPayload();
        Log.d(TAG, "Got: " + userLocation.getSender() + ", " + userLocation.getLatitude() + ", " + userLocation.getLongitude());

//        if(!loginFragment.conn1.getUser().equals(userLocation.getSender())) {
            if(MainMapFragment.userLocationMap.containsKey(userLocation.getSender())) {
                MainMapFragment.userLocationMap.get(userLocation.getSender()).setLatitude(userLocation.getLatitude());
                MainMapFragment.userLocationMap.get(userLocation.getSender()).setLongitude(userLocation.getLongitude());
                Log.d(TAG, "Does contain it");
            } else {
                MainMapFragment.userLocationMap.put(userLocation.getSender(),
                        new UserLocation(userLocation.getSender(), userLocation.getLatitude(), userLocation.getLongitude()));
                Log.d(TAG, "Does not contain sender");
            }
//        }
    }
}
