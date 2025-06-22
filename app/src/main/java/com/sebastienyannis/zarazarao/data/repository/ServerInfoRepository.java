package com.sebastienyannis.zarazarao.data.repository;

import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerInfoRepository {
    private static ServerInfoRepository instance;

    public static ServerInfoRepository getInstance() {
        if (instance == null) {
            instance = new ServerInfoRepository();
        }
        return instance;
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
            Log.e("ServerInfoRepository", "Error when getting the local ip ", e);
        }
        return null;
    }

}
