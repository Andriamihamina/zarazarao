package com.sebastienyannis.zarazarao.ui.WelcomePage;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sebastienyannis.zarazarao.data.model.WelcomeViewModel;
import com.sebastienyannis.zarazarao.data.repository.ServerInfoRepository;
import com.sebastienyannis.zarazarao.databinding.FragmentWelcomeBinding;
import com.sebastienyannis.zarazarao.backend.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FragmentWelcomeBinding binding;
    private ServerListAdapter serverListAdapter;
    private HttpServer server;
    private volatile JmDNS jmdns;
    private static final String TAG = "WelcomeFragment";
    private WelcomeViewModel model;



    public WelcomeFragment() {}


    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.model = new ViewModelProvider(this).get(WelcomeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.serverList;
        serverListAdapter = new ServerListAdapter(); // Create once
        TextView connectionStateStateText = binding.connectionState;

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(serverListAdapter); // Use that same instance

        model.getState().observe(getViewLifecycleOwner(), updatedState -> {
            serverListAdapter.submitList(updatedState.getServiceInfos());
            boolean isConnected = (server != null) && server.isConnected();
            if (!isConnected) { connectionStateStateText.setText("Disconnected"); } //TODO use StringRes to internationalize
            else {
                ServerInfoRepository serverInfoRepository = ServerInfoRepository.getInstance();
                InetAddress ipAddress = serverInfoRepository.getLocalIpAddress();
                if ( ipAddress == null ) {
                    connectionStateStateText.setText("Not connected to Wifi");
                } else {
                    connectionStateStateText.setText("Connected to: "+ ipAddress.toString());
                }

            }

        });
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        Button startButton = binding.startButton;
        startButton.setOnClickListener( v -> {
            try {
                this.start();//TODO rename function
                //TODO make the port dynamic
                server = new HttpServer(requireContext().getApplicationContext());
                server.start();
                Toast.makeText(requireContext(), "Server started", Toast.LENGTH_SHORT).show();
                startButton.setEnabled(false);
            } catch (IOException e) {
                System.err.println("Couldn't start server");
                Log.e("WelcomeFragment", "Error while starting server", e);
                Toast.makeText(requireContext(), "Failed to start server", Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    //TODO handle race conditions on jmdns uses
    public void registerService(WifiManager wifi) {
        synchronized (this) {
            try {
                WifiManager.MulticastLock multicastLock = wifi.createMulticastLock("multicastLock");
                multicastLock.setReferenceCounted(true);
                multicastLock.acquire();

                // Register service
                ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "zarazaraoHttpServer", 8080, "path=index.html");
                jmdns.registerService(serviceInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createListener() {
        jmdns.addServiceListener("_http._tcp.local.", new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent event) {
                jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
            }

            @Override
            public void serviceRemoved(ServiceEvent event) {
                Log.d(TAG, "Service removed: " + event.getName());
            }

            @Override
            public void serviceResolved(ServiceEvent event) {
                ServiceInfo info = event.getInfo();
                String host = info.getInetAddresses()[0].getHostAddress();
                int port = info.getPort();
                Log.d(TAG, "Discovered server at " + host + ":" + port);

                //String url = "http://" + host + ":" + port + "/";

                WelcomeFragment.this.model.addServer(info);
                //TODO handle connection
            }
        });
    }

    //TODO i think it's better to move those jmdns functions into a dedicated service
    public void start() {
        Executors.newSingleThreadExecutor().execute(()-> {
            try {
                ServerInfoRepository serverInfoRepository = ServerInfoRepository.getInstance();
                InetAddress addr = serverInfoRepository.getLocalIpAddress();
                jmdns = JmDNS.create(addr);
                System.out.println("Eto");
            } catch (IOException e) {
                Log.e(TAG, "Error when creating the jmdns instance", e);
            }
            this.createListener();
            WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            this.registerService(wifiManager);
        });
    }

    //TODO implement this
    /*public void stop() {
        synchronized (this) {
            if (multicastLock != null && multicastLock.isHeld()) {
                multicastLock.release();
                multicastLock = null;
            }
        }
    }*/

}