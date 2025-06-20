package com.sebastienyannis.zarazarao.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sebastienyannis.zarazarao.databinding.FragmentWelcomeBinding;
import com.sebastienyannis.zarazarao.backend.HttpServer;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentWelcomeBinding binding;
    private HttpServer server;


    public WelcomeFragment() {
        // Required empty public constructor
    }


    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button startButton = binding.startButton;


        startButton.setOnClickListener( v -> {
            try {
                server = new HttpServer();
                Toast.makeText(requireContext(), "Server started", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                System.err.println("Couldn't start server");
                e.printStackTrace();
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

}