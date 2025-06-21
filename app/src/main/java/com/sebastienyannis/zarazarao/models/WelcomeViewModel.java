package com.sebastienyannis.zarazarao.models;

import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class WelcomeViewModel extends ViewModel {
    private final MutableLiveData<List<ServiceInfo>> servers = new MutableLiveData<>(new ArrayList<>());

    private static final String TAG = "DiscoveryService";

    public WelcomeViewModel() {
    }



    public InetAddress getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "Error when getting the local ip ", e);
        }
        return null;
    }

    public LiveData<List<ServiceInfo>> getServers() {
        return this.servers;
    }

    public void addServer(ServiceInfo newServer) {
        List<ServiceInfo> currentList = servers.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        // Avoid duplicates (based on name, host, and port for example)
        boolean exists = currentList.stream().anyMatch(info ->
                info.getName().equals(newServer.getName()) &&
                        info.getPort() == newServer.getPort());

        if (!exists) {
            currentList = new ArrayList<>(currentList); // Copy to avoid modifying old reference
            currentList.add(newServer);
            servers.postValue(currentList); // Use postValue because this is from a background thread
        }
    }

}
