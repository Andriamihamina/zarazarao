package com.sebastienyannis.zarazarao.data.model;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.jmdns.ServiceInfo;

public class WelcomeViewModel extends ViewModel {
    public static class WelcomePageState {
        private final List<ServiceInfo> serviceInfos;
        private final InetAddress localServerAddress;
        public WelcomePageState(List<ServiceInfo> serviceInfos, InetAddress localServerAddress) {
            this.serviceInfos = serviceInfos;
            this.localServerAddress = localServerAddress;
        }

        public List<ServiceInfo> getServiceInfos(){
            return this.serviceInfos;
        }
    }
    private final MutableLiveData<WelcomePageState> state = new MutableLiveData<>(
            new WelcomePageState(new ArrayList<>(), null)
    );

    private static final String TAG = "WelcomeViewModel";

    public WelcomeViewModel() {
    }





    public LiveData<WelcomePageState> getState() {
        return this.state;
    }

    public void addServer(ServiceInfo newServer) {
        List<ServiceInfo> currentList = (state.getValue() != null) ? state.getValue().serviceInfos : new ArrayList<>();

        boolean exists = currentList.stream().anyMatch(info ->
                info.getName().equals(newServer.getName()) &&
                        info.getPort() == newServer.getPort());

        if (!exists) {
            currentList = new ArrayList<>(currentList); // Copy to avoid modifying old reference
            currentList.add(newServer);
            state.postValue(new WelcomePageState(currentList, state.getValue().localServerAddress));
        }
    }

}
