package app.totemapps.com.positions;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class StartScreen extends Fragment {

    LoginFragment loginFragment = LoginFragment.instance();

    Button openMapButton, editSettingsButton, logoutButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_screen, container, false);

        openMapButton = (Button) view.findViewById(R.id.openMapButton);
        editSettingsButton = (Button) view.findViewById(R.id.editSettingsButton);
        logoutButton = (Button) view.findViewById(R.id.logoutButton);

        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapFragment();
            }
        });

        editSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!loginFragment.conn1.isConnected()) {
            loginFragment.reconnect();
        }
    }

    public void openMapFragment() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            MainMapFragment mapFragment = new MainMapFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, mapFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("GPS must be enabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void openSettings() {
        SettingsFragment settingsFragment = new SettingsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, settingsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void logout() {
        loginFragment.conn1.disconnect();
        getFragmentManager().popBackStack();
        Toast.makeText(getActivity(), "You have been logged out", Toast.LENGTH_LONG).show();
    }

}
