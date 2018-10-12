package com.pathwaypos.elotest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.elo.device.DeviceManager;
import com.elo.device.ProductInfo;
import com.elo.device.enums.EloPlatform;
import com.elo.device.inventory.Inventory;
import com.pathwaypos.elotest.apiadapter.ApiAdapter;
import com.pathwaypos.elotest.apiadapter.ApiAdapterFactory;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static ProductInfo productInfo;
    private ApiAdapter apiAdapter;
    private Inventory inventory;
    private static String TAG = "ELOtest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inventory = DeviceManager.getInventory(this);
        if (!inventory.isEloSdkSupported()) {
            Toast.makeText(this, "Platform not recognized or supported, sorry", Toast.LENGTH_LONG).show();
            return;

        }

        productInfo = DeviceManager.getPlatformInfo();
        EloPlatform platform = productInfo.eloPlatform;

        apiAdapter = ApiAdapterFactory.getInstance(this).getApiAdapter(inventory);
        if (apiAdapter == null) {
            // We're not running on a supported platform.  This is a common customer scenario where
            // the APK may be written for multiple vendor platforms.  In our case, we can't do much
            Log.d(TAG, "Cannot find support for this platform");
            Toast.makeText(this, "Cannot find support for this platform", Toast.LENGTH_LONG).show();
            return;
        }

        if (!inventory.hasPrinter()) {
            Toast.makeText(this, "Printer not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            apiAdapter.print("TEST\nTEST\n");
        } catch (RuntimeException | IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}
