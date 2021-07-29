package com.branch.tech.ppm;

import android.app.Application;

import io.branch.referral.Branch;

public class BranchPPMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Branch.enableLogging();

        Branch.getAutoInstance(this);
    }
}
