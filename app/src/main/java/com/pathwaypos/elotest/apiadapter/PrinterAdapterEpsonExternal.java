package com.pathwaypos.elotest.apiadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.elo.device.exceptions.UnsupportedPeripheralMethodException;
import com.pathwaypos.elotest.apiadapter.epson.EpsonPrinterHelper;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

import java.io.IOException;

// Printer specific imports


public class PrinterAdapterEpsonExternal implements PrinterAdapter ,CashDrawerAdapter{
    private String TAG = "ELO_EPSON_API";
    private Context ctx;
    private boolean hasPaper = false;

    DeviceInfo mDeviceInfo = null;

    PrinterAdapterEpsonExternal(Context ctx){
        this.ctx=ctx;
        Log.i(TAG,"constructor");

        // search for epson printer
        searchPrinter(ctx);
    }

    private boolean isPrinterConnected() {
        if(mDeviceInfo == null) {
            Log.i(TAG,"Epson printer not connected");
            Toast.makeText(ctx, "Epson printer not connected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void searchPrinter(Context context) {
        FilterOption filterOption = new FilterOption();
        filterOption.setDeviceType(Discovery.TYPE_PRINTER);
        filterOption.setEpsonFilter(Discovery.FILTER_NAME);
        try {
            Discovery.stop();
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            Log.i(TAG, " Discovery.stop()");
        }
        try {
            Discovery.start(context, filterOption, epsonPrinterDiscoveryListener);
        } catch (Exception e) {
            Log.i(TAG,e.toString());
            Log.i(TAG,  "Discovery.start()");
        }
    }

    private DiscoveryListener epsonPrinterDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            Log.i(TAG, "Found Epson Printer at " + deviceInfo.getTarget());
            mDeviceInfo = deviceInfo;
        }
    };


    @Override
    public boolean hasPaper() throws IOException {
        Log.i(TAG,"hasPaper");

        if(isPrinterConnected() == false)
            return false;

        boolean paper = new EpsonPrinterHelper(mDeviceInfo, ctx).runPaperStatusSequence();
        Log.i(TAG,"hasPaper " + paper);
        return paper;
    }

    @Override
    public void print(String printData) throws IOException {
        if(isPrinterConnected() == false)
            return;

        new EpsonPrinterHelper(mDeviceInfo, ctx).runPrintReceiptSequence(printData);
    }

    @Override
    public void print(byte[] printData) throws IOException {
        if(isPrinterConnected() == false)
            return;

        new EpsonPrinterHelper(mDeviceInfo, ctx).runPrintReceiptSequence(printData.toString());
    }

    @Override
    public void cutPaper() throws IOException {
        if(isPrinterConnected() == false)
            return;

        new EpsonPrinterHelper(mDeviceInfo, ctx).runCutPaperSequence();
    }

    // prints demo receipt with native formattings
    @Override
    public void printDemo() throws IOException {
        if(isPrinterConnected() == false)
            return;

        new EpsonPrinterHelper(mDeviceInfo, ctx).runPrintDemoReceiptSequence();  // TODO: print well-formatted receipt
    }

    @Override
    public void printImage(Bitmap bmp, int offsetX, int offsetY, int imageHeight, int imageWidth) throws IOException {
        if (isPrinterConnected() == false)
            return;
        new EpsonPrinterHelper(mDeviceInfo, ctx).runPrintImageSequence(bmp, offsetX, offsetY, imageHeight, imageWidth);
    }

    @Override
    public void printBarcode(String code, boolean withHRI, int widthInDots, int heightInDots) throws IOException {
        if (!isPrinterConnected())
            return;
        new EpsonPrinterHelper(mDeviceInfo, ctx).runPrintBarCodeSequence(code, withHRI, widthInDots, heightInDots);
    }

    @Override
    public void printBarcode(String code) throws IOException {
        EpsonPrinterHelper EpsonPrinterHelperObj = new EpsonPrinterHelper(mDeviceInfo, ctx);
        this.printBarcode(code, true, EpsonPrinterHelperObj.BARCODE_WIDTH, EpsonPrinterHelperObj.BARCODE_HEIGHT);
    }

    @Override
    public void setChineseMode(boolean enable) throws UnsupportedPeripheralMethodException {
        // TODO; Set chinese mode
        throw new UnsupportedPeripheralMethodException("Chinese mode not supported in this printer model", "setChineseMode", "Epson");
    }

    @Override
    public void setBaudRate(int baudRate) throws UnsupportedPeripheralMethodException {
    }

    @Override
    public boolean isCashDrawerOpen() {
        Log.i(TAG,"isCashDrawerOpen");

        if(isPrinterConnected() == false)
            return false;

        boolean drawerOpen = new EpsonPrinterHelper(mDeviceInfo, ctx).runGetCashDrawerStatusSequence();
        Log.i(TAG,"drawerOpen " + drawerOpen);
        return drawerOpen;
    }

    @Override
    public void openCashDrawer() {
        new EpsonPrinterHelper(mDeviceInfo, ctx).runOpenDrawerSequence();
    }
}
