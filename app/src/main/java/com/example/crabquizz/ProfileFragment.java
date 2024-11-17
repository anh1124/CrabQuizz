package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.example.crabquizz.Scripts.Controller.TransitionFragemt;
import com.example.crabquizz.Scripts.Controller.UserController;
import com.example.crabquizz.Scripts.Controller.SessionManager;

public class ProfileFragment extends Fragment {
    private Button buttonlogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TransitionFragemt.initializeMenuNavigation(requireContext(), getParentFragmentManager(), view);

        initViews(view);
        showLogoutBtn();

        return view;
    }

    private void showLogoutBtn() {
        if (getContext() == null) return;

        SessionManager.UserTEMPSession userSession = SessionManager.getInstance(getContext()).getUserSession();

        if (userSession != null && userSession.getUser() != null) {
            String role = userSession.getUser().getRole();
            Log.d("ShowLogoutBtn", "Role: " + role);

            // Chỉ hiện nút nếu role là "teacher" hoặc "student"
            if (role.equals("teacher") || role.equals("student")) {
                setbuttonlogoutVisibility(true);
            } else {
                setbuttonlogoutVisibility(false);
            }
        } else {
            setbuttonlogoutVisibility(false);
        }
    }

    private void setbuttonlogoutVisibility(boolean isVisible) {
        if (buttonlogout != null) {
            buttonlogout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void initViews(View view) {
        buttonlogout = view.findViewById(R.id.buttonlogout);
        buttonlogout.setOnClickListener(v -> {
            if (getContext() == null || getActivity() == null) return;

            Log.d("buttonlogout", "CALLER: ");
            UserController.getInstance(SessionManager.getInstance(getContext())).logout(new UserController.CallLogOut() {
                @Override
                public void GoLoginMenu() {
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                    getActivity().finish(); // Close current activity after logout
                }
            });
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up any references if needed
        buttonlogout = null;
    }
}