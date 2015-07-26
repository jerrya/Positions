package app.totemapps.com.positions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends Activity implements MainMapFragment.OnCreateChatListener {

    private DrawerLayout mDrawerLayout;
    private ListView mLeftDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    // The following is for the right drawer
    private ListView mRightDrawerList;

    ArrayAdapter<Friends> friendsAdapter;

    static ArrayList<NavChats> navChatList = new ArrayList<>();
    static ArrayList<Friends> friendsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNavDrawers();

        if (findViewById(R.id.content_frame) != null) {
            if (savedInstanceState != null) {
                return;
            }
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.content_frame, loginFragment).commit();
        }
    }

    NavChatsAdapter navChatAdapter;
    public void createNavDrawers() {
        navChatAdapter = new NavChatsAdapter(this, navChatList);

        friendsList.add(new Friends("Sammy", "Hey this is sammy", 2));
        friendsList.add(new Friends("Robert", "This is Robert", 3));
        friendsList.add(new Friends("Tim", "This is matti", 0));

        friendsAdapter = new FriendsNavAdapter(this, friendsList);

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mLeftDrawerList = (ListView) findViewById(R.id.left_drawer);
        mRightDrawerList = (ListView) findViewById(R.id.right_drawer);

        TextView chatHeader = new TextView(this);
        chatHeader.setText("Chats");
        chatHeader.setTextSize(20.0f);
        chatHeader.setClickable(false);
        mLeftDrawerList.addHeaderView(chatHeader, null, false);

        TextView friendsHeader = new TextView(this);
        friendsHeader.setText("Friends");
        friendsHeader.setTextSize(20.0f);
        mRightDrawerList.addHeaderView(friendsHeader, null, false);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mLeftDrawerList.setAdapter(navChatAdapter);
        mRightDrawerList.setAdapter(friendsAdapter);

        mLeftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mRightDrawerList.setOnItemClickListener(new RightDrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(LoginFragment.instance().conn1 == null || !LoginFragment.instance().conn1.isConnected()) {
                Toast.makeText(getApplicationContext(), "You need to be connected", Toast.LENGTH_SHORT).show();
            } else {
                selectItemChats(position);
            }
        }
    }

    private class RightDrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(LoginFragment.instance().conn1 == null || !LoginFragment.instance().conn1.isConnected()) {
                Toast.makeText(getApplicationContext(), "You need to be connected", Toast.LENGTH_SHORT).show();
            } else {
                selectItemFriends(position);
            }
        }
    }

    int mNavPosition = 0;
    private void selectItemChats(int position) {
        Fragment newChatFragment = new NewChatFragment();
        Bundle args = new Bundle();

        mRightDrawerList.setItemChecked(mNavPosition, false);

        mNavPosition = position-1;

        String sender = navChatAdapter.getItem(mNavPosition).getSender();
        String firstName = navChatAdapter.getItem(mNavPosition).getFirstName();
        navChatAdapter.getItem(mNavPosition).setCount(0);

        args.putString("user", sender);
        args.putString("firstname", firstName);
        newChatFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, newChatFragment, "newchatfragment");
        transaction.addToBackStack(null);
        transaction.commit();

        mLeftDrawerList.setItemChecked(mNavPosition, true);

//        mNavPosition = position-1;

        mDrawerLayout.closeDrawer(mLeftDrawerList);
        setTitle(firstName);
    }

    private void selectItemFriends(int position) {
        Fragment newChatFragment = new NewChatFragment();
        Bundle args = new Bundle();

        String message = friendsAdapter.getItem(position).getMessage();
        String sender = friendsAdapter.getItem(position).getSender();
        friendsAdapter.getItem(position).setCount(0);

        args.putString("displaytext", message);
        newChatFragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, newChatFragment, "newchatfragment").commit();

        mRightDrawerList.setItemChecked(position, true);

        mLeftDrawerList.setItemChecked(mNavPosition, false);

        mNavPosition = position;

        setTitle(sender);
        mDrawerLayout.closeDrawer(mRightDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNewChatListener(String user, String fname) {
        NewChatFragment newChatFragment = new NewChatFragment();
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("firstname", fname);
        newChatFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, newChatFragment, "newchatfragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private static MainActivity inst;
    public static MainActivity instance() {
        return inst;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!LoginFragment.instance().conn1.isConnected()) {
            LoginFragment.instance().disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
