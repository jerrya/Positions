package app.totemapps.com.positions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnInfoWindowClickListener {

    GoogleApiClient mGoogleApiClient;

    static MapFragment mapFragment;

    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;

    private boolean focusMapOnce = true;

    protected static final String TAG = "MainManFragmentTAG";

    public static Map<String, UserLocation> userLocationMap = new HashMap<>();

    LoginFragment loginFragment = LoginFragment.instance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_map_fragment, container, false);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapFragment.getMap().setOnInfoWindowClickListener(this);

        mapFragment.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                Log.e(TAG, "Marker clicked");
                return false;
            }
        });

        buildGoogleApiClient();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragment f = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if(f != null) {
            getFragmentManager().beginTransaction().remove(f).commit();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        getProfile(marker.getTitle());
    }

    public String checkInfoValues(String value) {
        if(value == null) {
            value = "";
        }
        return value;
    }

    String firstName = "";
    String lastName = "";
    String mood = "";
    String relationship = "";
    String likes = "";
    String facebook = "";

    public void getProfile(final String username) {
        final VCardManager vCardManager = VCardManager.getInstanceFor(loginFragment.conn1);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    VCard loaded = vCardManager.loadVCard(username);
                    firstName = loaded.getFirstName();
                    lastName = loaded.getLastName();
                    mood = loaded.getField("mood");
                    relationship = loaded.getField("relationship");
                    likes = loaded.getField("likes");
                    facebook = loaded.getField("facebook");
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e);
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                createPopupWindow(username);
            }
        }.execute();
    }

    public void createPopupWindow(final String user) {
        Log.e(TAG, "Got: " + firstName + ", " + lastName + ", " + facebook);
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.popup_info_fragment, null);

        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // TODO: Do something with this button
        Button sayHelloButton = (Button) popupView.findViewById(R.id.sayHello);
        Button startChatButton = (Button) popupView.findViewById(R.id.startChatButton);
        TextView firstNameView = (TextView) popupView.findViewById(R.id.firstNameView);
        TextView moodView = (TextView) popupView.findViewById(R.id.moodView);
        TextView relationshipView = (TextView) popupView.findViewById(R.id.relationshipView);
        TextView likesView = (TextView) popupView.findViewById(R.id.likesView);
        TextView facebookView = (TextView) popupView.findViewById(R.id.facebookView);

        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onNewChatListener(user, firstName);
            }
        });

        firstNameView.setText(checkInfoValues(firstName) + " " + checkInfoValues(lastName));
        moodView.setText("Mood: " + checkInfoValues(mood));
        relationshipView.setText("Relationship: " + checkInfoValues(relationship));
        likesView.setText("Interests: " + checkInfoValues(likes));
        facebookView.setText("Facebook: " + checkInfoValues(facebook));

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        int location[] = new int[2];

        View anchorView = mapFragment.getView();
        int height = anchorView.getHeight()/2;
        int width = anchorView.getWidth()/2;

        anchorView.getLocationOnScreen(location);

        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0], location[1] + anchorView.getHeight());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private static MainMapFragment inst;
    public static MainMapFragment instance() {
        return inst;
    }
    @Override
    public void onStart() {
        super.onStart();
        inst = this;
        focusMapOnce = true;
        mGoogleApiClient.connect();
        if(!loginFragment.conn1.isConnected()) {
            Log.e(TAG, "Not connected in Map");
            loginFragment.reconnect();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    double latCheck = 0;
    double lonCheck = 0;
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

        if(focusMapOnce) {
            if(mapFragment.getMap() != null) {
                mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f));
            }
            focusMapOnce = false;
        }

        Log.i(TAG, "Accuracy is: " + location.getAccuracy());
        Log.i(TAG, "Position is: " + loc.toString());
        Log.i(TAG, "Postal Code: " + getPostalCode(location.getLatitude(), location.getLongitude()));

        postalCodeUpdate(location.getLatitude(), location.getLongitude());

        if(loginFragment.connectedToNode) {
            if(latCheck != location.getLatitude() || lonCheck != location.getLongitude()) {
                loginFragment.publishData(location.getLatitude(), location.getLongitude());
                latCheck = location.getLatitude();
                lonCheck = location.getLongitude();
            }
        }

        updateMap();
    }

    public void updateMap() {
        mapFragment.getMap().clear();
        for(String sender : userLocationMap.keySet()) {
            addMarker(userLocationMap.get(sender).getLatitude(),
                    userLocationMap.get(sender).getLongitude(),
                    userLocationMap.get(sender).getSender());
            Log.e(TAG, "Looped: " + userLocationMap.get(sender).getLatitude() +
                    ", " + userLocationMap.get(sender).getLongitude());
        }
        Log.e(TAG, "Redrawn");
    }

    String mPostalCode = "";
    String lastPostalCode = "";
    public void postalCodeUpdate(final Double lat, final Double lon) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mPostalCode = getPostalCode(lat, lon);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(mPostalCode != null) {
                    if(!mPostalCode.equals(lastPostalCode)) {
                        // TODO: Should Hashmap be cleared here?
                        loginFragment.connectedToNode = false;
                        loginFragment.connectToNode(mPostalCode);
                        lastPostalCode = mPostalCode;
                        Log.e(TAG, "Connected to: " + mPostalCode);
                    }
                }
            }
        }.execute();
    }

    public String getPostalCode(Double lat, Double lon) {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String postalCode = addressList.get(0).getPostalCode();
        return postalCode;
    }

    public void addMarker(String lat, String lon, String sender) {
        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lon);
        
        mapFragment.getMap().addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.abc_btn_switch_to_on_mtrl_00001))
                .title(sender)
                .snippet("Click for more info")
                .position(new LatLng(latitude, longitude)));
    }

    OnCreateChatListener mCallBack;
    public interface OnCreateChatListener {
        public void onNewChatListener(String user, String fname);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (OnCreateChatListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCreateChatListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
}