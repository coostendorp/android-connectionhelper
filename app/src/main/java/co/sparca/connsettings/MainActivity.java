package co.sparca.connsettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    private final PlaceholderFragment main_fragment = new PlaceholderFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, main_fragment)
                    .commit();
        }

        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mConnReceiver);
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            // Update the screen
            main_fragment.updateContent();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        View rootView;

        public PlaceholderFragment() {
        }

        public void updateContent() {
            if (rootView == null) return;

            // Get instance of NetworkUtils class. It needs context to access system services
            NetworkUtils nu = new NetworkUtils(rootView.getContext());

            // Get the states
            NetworkUtils.ConnStates states = nu.getConnStates();

            RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.rgOptions);

            if (!states.isDataEnabled && !states.isWifiEnabled && !states.isWifiApEnabled) {
                rg.check(R.id.rbNoInternet);
            } else if (states.isDataEnabled && !states.isWifiEnabled && !states.isWifiApEnabled) {
                rg.check(R.id.rbInternetOnly);
            } else if (states.isDataEnabled && !states.isWifiEnabled && states.isWifiApEnabled) {
                rg.check(R.id.rbHotspot);
            } else if (!states.isDataEnabled && states.isWifiEnabled && !states.isWifiApEnabled) {
                rg.check(R.id.rbWifi);
            } else if (states.isDataEnabled && states.isWifiEnabled && !states.isWifiApEnabled) {
                rg.check(R.id.rbWifiInternet);
            }
        }

        @Override
        public void onResume() {
            updateContent();
            super.onResume();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.rgOptions);


            View.OnClickListener listener = new View.OnClickListener() {
                int lastClicked = 0;

                @Override
                public void onClick(View view) {
                    // Get instance of the radiobutton that was clicked
                    RadioButton rb = (RadioButton) view;

                    // If the clicked radiobutton was the same as last time, disregard, otherwise update lastone
                    if (rb.getId() == lastClicked) {
                        return;
                    } else {
                        lastClicked = rb.getId();
                    }

                    // Get instance of NetworkUtils class. It needs context to access system services
                    NetworkUtils nu = new NetworkUtils(view.getContext());

                    NetworkUtils.ConnStates connStates = null;

                    // Decide what to do based on which radiobutton was clicked
                    switch (rb.getId()) {
                        // No networking at all: Turn everything off!
                        case R.id.rbNoInternet:
                            connStates = new NetworkUtils.ConnStates(false, false, false);
                            break;

                        // Internet only (not sharing it)
                        case R.id.rbInternetOnly:
                            connStates = new NetworkUtils.ConnStates(true, false, false);
                            break;

                        // Internet + Wifi hotspot
                        case R.id.rbHotspot:
                            connStates = new NetworkUtils.ConnStates(true, false, true);
                            break;

                        // Wifi only
                        case R.id.rbWifi:
                            connStates = new NetworkUtils.ConnStates(false, true, false);
                            break;

                        // Wifi and Internet
                        case R.id.rbWifiInternet:
                            connStates = new NetworkUtils.ConnStates(true, true, false);
                            break;
                    }
                    nu.setConnStates(connStates);
                }
            };

            rg.findViewById(R.id.rbNoInternet).setOnClickListener(listener);
            rg.findViewById(R.id.rbInternetOnly).setOnClickListener(listener);
            rg.findViewById(R.id.rbHotspot).setOnClickListener(listener);
            rg.findViewById(R.id.rbWifi).setOnClickListener(listener);
            rg.findViewById(R.id.rbWifiInternet).setOnClickListener(listener);


            return rootView;
        }
    }

}
