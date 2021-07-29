package com.branch.tech.ppm;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class MainActivity extends AppCompatActivity {
    private static final String DEEP_LINK_TEST = "deep_link_test";
    private static final String OTHER_VALUE = "other";
    private static final String TEST_LINK = "ppm-tech-challenge://open";
    private static final String TAG = "PPM-Tech-Challenge";
    Button shareButton;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initClickListener();
    }

    private void initViews() {
        shareButton = findViewById(R.id.share);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initClickListener() {
        shareButton.setOnClickListener(v -> createAndShareDeepLink());
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();
        if (null != branch) {
            branch.setRetryCount(10);
        }
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            if (error == null) {
                Log.i(TAG, "JSON String :: " + linkProperties.toString());

                try {
                    String value = linkProperties.getString(DEEP_LINK_TEST);
                    if (value.equalsIgnoreCase(OTHER_VALUE)) {
                        Intent intent = new Intent(MainActivity.this, OtherActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "KEY NOT FOUND :: " + DEEP_LINK_TEST);
                }
            } else {
                Log.e(TAG, error.getMessage());
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createAndShareDeepLink() {
        //Create
        BranchUniversalObject buo = getBranchUniversalObject();
        LinkProperties linkProperties = getLinkProperties();
        generateShortUrl(buo, linkProperties);

        //Share
        ShareSheetStyle ss = getShareSheetStyle();
        showShareSheet(buo, linkProperties, ss);
    }

    private BranchUniversalObject getBranchUniversalObject() {
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/1")
                .setTitle("Share Button")
                .setContentDescription("Share Button");
        return buo;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private LinkProperties getLinkProperties() {
        LinkProperties lp = new LinkProperties()
                .setChannel("ShareButton")
                .setFeature("sharing")
                .setCampaign("PPM Tech Challenge")
                .setStage("Main Screen")
                .addControlParameter("$desktop_url", TEST_LINK)
                .addControlParameter(DEEP_LINK_TEST, OTHER_VALUE);
        return lp;

    }

    private void generateShortUrl(BranchUniversalObject buo, LinkProperties linkProperties) {
        buo.generateShortUrl(this, linkProperties, (url, error) -> {
            if (error == null) {
                Log.i(TAG, "got my Branch link to share: " + url);
            } else {
                Log.e(TAG, "Error in generating Short URL :: " + error.getMessage());
            }
        });
    }

    private void showShareSheet(BranchUniversalObject buo, LinkProperties linkProperties, ShareSheetStyle ss) {
        buo.showShareSheet(this, linkProperties, ss, new Branch.BranchLinkShareListener() {
            @Override
            public void onShareLinkDialogLaunched() {
            }

            @Override
            public void onShareLinkDialogDismissed() {
            }

            @Override
            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
            }

            @Override
            public void onChannelSelected(String channelName) {
            }
        });
    }

    private ShareSheetStyle getShareSheetStyle() {
        Resources resources = getResources();
        ShareSheetStyle ss = new ShareSheetStyle(MainActivity.this, resources.getString(R.string.share_message_title), resources.getString(R.string.share_message_body))
                .setCopyUrlStyle(ContextCompat.getDrawable(this, R.drawable.copy), resources.getString(R.string.copy), resources.getString(R.string.added_to_clipboard))
                .setAsFullWidthStyle(true)
                .setSharingTitle(resources.getString(R.string.share_with));
        return ss;
    }


}