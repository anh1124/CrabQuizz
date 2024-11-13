package com.example.crabquizz.Scripts.Models;

import android.content.Context;
import android.util.Log;

import com.example.crabquizz.Scripts.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

public class AppSetup {
    private static AppSetup instance;
    private AppSetup() {}
    public static synchronized AppSetup getInstance() {
        if (instance == null) {
            instance = new AppSetup();
        }
        return instance;
    }

    public int maxTokenValidityDays;


    public void Setup() {
        // Initialize by fetching from the database
        fetchAndSetMaxTokenValidityDays();
    }

    public int getMaxTokenValidityDays() {
        return maxTokenValidityDays;
    }

    public void setMaxTokenValidityDays(int maxTokenValidityDays) {
        this.maxTokenValidityDays = maxTokenValidityDays;
    }

    private void fetchAndSetMaxTokenValidityDays() {
        DbContext dbContext = DbContext.getInstance();
        dbContext.fetchMaxTokenValidityDays()
                .addOnSuccessListener(new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer value) {
                        setMaxTokenValidityDays(value);
                        Log.d("AppSetup", "maxTokenValidityDays set to " + value);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("AppSetup", "Failed to fetch maxTokenValidityDays", e);
                    }
                });
    }
}
