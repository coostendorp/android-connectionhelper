package co.sparca.connsettings;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";
    Context context;


    public static class ConnStates {
        public boolean isDataEnabled = false;
        public boolean isWifiEnabled = false;
        public boolean isWifiApEnabled = false;

        ConnStates(boolean isDataEnabled, boolean isWifiEnabled, boolean isWifiApEnabled) {
            this.isDataEnabled = isDataEnabled;
            this.isWifiEnabled = isWifiEnabled;
            this.isWifiApEnabled = isWifiApEnabled;
        }
    }

    public NetworkUtils(Context context) {
        this.context = context;
    }

    public ConnStates getConnStates() {
        return new ConnStates(isMobileDataEnabled(), isWifiEnabled(), isWifiApEnabled());
    }

    public void setConnStates(ConnStates connStates) {
        setWifiAp(connStates.isWifiApEnabled);
        setWifi(connStates.isWifiEnabled);
        setMobileData(connStates.isDataEnabled);
    }


    public boolean isMobileDataEnabled() {
        boolean ret = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        try {
            Method method = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            ret = (Boolean) method.invoke(connectivityManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public boolean isWifiApEnabled() {
        boolean ret = false;
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        try {
            Method method = WifiManager.class.getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            ret = (Boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return ret;
    }


    public void setMobileData(boolean enabled) {
        ConnectivityManager dataManager;

        dataManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Method method = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
            method.setAccessible(true);
            method.invoke(dataManager, enabled);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setWifi(boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }


    public void setWifiAp(boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(wifiManager, null, enabled);
                } catch (Exception ex) {
                    Log.e(TAG, "" + ex.getMessage());
                }
                break;
            }
        }
    }
}
