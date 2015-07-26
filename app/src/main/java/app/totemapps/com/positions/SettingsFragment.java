package app.totemapps.com.positions;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.Set;

public class SettingsFragment extends Fragment {

    LoginFragment loginFragment = LoginFragment.instance();

    protected static final String TAG = "SettingsFragment";

    EditText firstNameText, lastNameText, moodText, relationshipText, likesText, facebookText;
    Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        firstNameText = (EditText) view.findViewById(R.id.firstNameText);
        lastNameText = (EditText) view.findViewById(R.id.lastNameText);
        moodText = (EditText) view.findViewById(R.id.moodText);
        relationshipText = (EditText) view.findViewById(R.id.relationshipText);
        likesText = (EditText) view.findViewById(R.id.likesText);
        facebookText = (EditText) view.findViewById(R.id.facebookText);

        saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        return view;
    }

    VCardManager vCardManager;
    VCard loaded;
    @Override
    public void onStart() {
        super.onStart();
        if(loginFragment.conn1.isConnected()) {
            vCardManager = VCardManager.getInstanceFor(loginFragment.conn1);
            loadAndSetDetails();
        } else {
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), "You must be logged in", Toast.LENGTH_LONG).show();
        }
    }

    public void loadAndSetDetails() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    loaded = vCardManager.loadVCard();
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                setDetails();
            }
        }.execute();
    }

    public void setDetails() {
        firstNameText.setText(loaded.getFirstName());
        lastNameText.setText(loaded.getLastName());
        moodText.setText(loaded.getField("mood"));
        relationshipText.setText(loaded.getField("relationship"));
        likesText.setText(loaded.getField("likes"));
        facebookText.setText(loaded.getField("facebook"));
    }

    boolean saved = false;
    public void saveProfile() {
        saved = false;
        String facebookLink = facebookText.getText().toString().replace("http://", "");

        final VCard vCard = new VCard();

        vCard.setFirstName(firstNameText.getText().toString());
        vCard.setLastName(lastNameText.getText().toString());
        vCard.setField("mood", moodText.getText().toString());
        vCard.setField("relationship", relationshipText.getText().toString());
        vCard.setField("likes", likesText.getText().toString());
        vCard.setField("facebook", facebookLink);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    vCardManager.saveVCard(vCard);
                    saved = true;
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(saved) {
                    Toast.makeText(getActivity(), "Profile Saved", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Unable to save profile", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }
}
