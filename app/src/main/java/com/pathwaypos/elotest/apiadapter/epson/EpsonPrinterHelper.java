package com.pathwaypos.elotest.apiadapter.epson;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.pathwaypos.elotest.R;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

public class EpsonPrinterHelper implements ReceiveListener {

    private Printer mPrinter = null;
    String TAG = "EpsonHelper";

    DeviceInfo mDeviceInfo;
    Context mContext;
    public final int BARCODE_WIDTH = 2;
    public final int BARCODE_HEIGHT = 50;


    public EpsonPrinterHelper(DeviceInfo di, Context ctx) {
        mDeviceInfo = di;
        mContext = ctx;
    }

    public boolean runOpenDrawerSequence() {

        if (!initializeObject(mContext)) {
            return false;
        }

        if (!createDataToOpenDrawer(0)) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }
        disconnectPrinter();
        return true;
    }


    public boolean runPaperStatusSequence() {

        boolean status = false;

        if (!initializeObject(mContext)) {
            Log.d(TAG,"Error initialising printer object");
            return false;
        }

        status = getPrintableStatus();

        finalizeObject();

        return status;
    }

    public boolean runGetCashDrawerStatusSequence() {

        boolean status = false;

        if (!initializeObject(mContext)) {
            Log.d(TAG,"Error initialising printer object");
            return false;
        }

        status = getCashDrawerStatus();

        finalizeObject();

        return status;
    }

    public boolean runPrintBarCodeSequence(String code, boolean withHRI, int widthinDots, int heightInDots) {

        if (!initializeObject(mContext)) {
            return false;
        }

        if (!createReceiptDataToPrintBarcode(code, withHRI, widthinDots, heightInDots)) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }
        disconnectPrinter();
        return true;
    }

    public boolean runPrintImageSequence(Bitmap bmp, int offsetX, int offsetY, int imageHeight, int imageWidth) {

        bmp = createScaledBitmap(bmp,imageHeight,imageWidth);
        if (!initializeObject(mContext)) {
            return false;
        }

        if (!createReceiptDataToPrintImage(bmp, offsetX, offsetY, bmp.getHeight(), bmp.getWidth())) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }
        disconnectPrinter();
        return true;
    }

    public boolean runPrintReceiptSequence(String printdata) {
        if (!initializeObject(mContext)) {
            return false;
        }

        if (!createReceiptData(printdata)) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }
        disconnectPrinter();
        return true;
    }

    public boolean runPrintDemoReceiptSequence() {
        if (!initializeObject(mContext)) {
            return false;
        }

        if (!createDemoReceiptData()) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }
        disconnectPrinter();
        return true;
    }

    private boolean createReceiptData(String printdata) {
        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.addText(printdata);
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            Log.d(TAG,"error = " + e.getStackTrace().toString());
            return false;
        }

        return true;
    }

    private boolean createDemoReceiptData() {
        String method = "";
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            textData.append("THE STORE 123 (555) 555 – 5555\n");
            textData.append("STORE DIRECTOR – John Smith\n");
            textData.append("\n");
            textData.append("7/01/07 16:58 6153 05 0191 134\n");
            textData.append("ST# 21 OP# 001 TE# 01 TR# 747\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("400 OHEIDA 3PK SPRINGF  9.99 R\n");
            textData.append("410 3 CUP BLK TEAPOT    9.99 R\n");
            textData.append("445 EMERIL GRIDDLE/PAN 17.99 R\n");
            textData.append("438 CANDYMAKER ASSORT   4.99 R\n");
            textData.append("474 TRIPOD              8.99 R\n");
            textData.append("433 BLK LOGO PRNTED ZO  7.99 R\n");
            textData.append("458 AQUA MICROTERRY SC  6.99 R\n");
            textData.append("493 30L BLK FF DRESS   16.99 R\n");
            textData.append("407 LEVITATING DESKTOP  7.99 R\n");
            textData.append("441 **Blue Overprint P  2.99 R\n");
            textData.append("476 REPOSE 4PCPM CHOC   5.49 R\n");
            textData.append("461 WESTGATE BLACK 25  59.99 R\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("SUBTOTAL                160.38\n");
            textData.append("TAX                      14.43\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            mPrinter.addText("TOTAL    174.81\n");
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);

            textData.append("CASH                    200.00\n");
            textData.append("CHANGE                   25.19\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("Purchased item total number\n");
            textData.append("Sign Up and Save !\n");
            textData.append("With Preferred Saving Card\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addFeedLine";
            mPrinter.addFeedLine(2);

            method = "addBarcode";
            mPrinter.addBarcode("01209457",
                    Printer.BARCODE_CODE39,
                    Printer.HRI_BELOW,
                    Printer.FONT_A,
                    barcodeWidth,
                    barcodeHeight);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            Log.e(TAG, "error executing " + method, e);
            return false;
        }

        textData = null;

        return true;
    }

    private boolean createReceiptDataToPrintImage(Bitmap bmp, int offsetX, int offsetY, int imageHeight, int imageWidth) {
        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.addImage(bmp, offsetX, offsetY, imageWidth, imageHeight, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            Log.d(TAG, "error = ", e);
            return false;
        }

        return true;
    }

    private boolean createReceiptDataToPrintBarcode(String code, boolean withHRI, int widthInDots, int heightInDots) {

        if (mPrinter == null) {
            return false;
        }
        int printHRI = (withHRI ? Printer.HRI_BELOW : Printer.HRI_NONE);
        try {
            mPrinter.addBarcode(code, Printer.BARCODE_CODE39,
                    printHRI,
                    Printer.FONT_A,
                    widthInDots,
                    heightInDots);
        } catch (Exception e) {
            Log.d(TAG, "error = ", e);
            return false;
        }
        return true;
    }

    private boolean createDataToOpenDrawer(int drawerId) {
        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.addPulse(0,Printer.PULSE_100);
        } catch (Exception e) {
            Log.d(TAG, "error = ", e);
            return false;
        }

        return true;
    }

    private boolean getPrintableStatus() {
        if (mPrinter == null) {
            Log.d(TAG,"Error mPrinter == null");
            return false;
        }

        if (!connectPrinter()) {
            Log.d(TAG,"Error connectPrinter failed");
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(mContext, status);

        boolean result = isPrintable(status);

        Log.d(TAG, "isPrintable result = " + makeErrorMessage(mContext, status));

        disconnectPrinter();

        return result;
    }

    private boolean getCashDrawerStatus() {
        if (mPrinter == null) {
            Log.d(TAG,"Error mPrinter == null");
            return false;
        }

        if (!connectPrinter()) {
            Log.d(TAG,"Error connectPrinter failed");
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(mContext, status);

        boolean result = isDrawerOpen(status);

        disconnectPrinter();

        return result;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(mContext, status);

        if (!isPrintable(status)) {
            Log.d(TAG, "error " + makeErrorMessage(mContext, status));
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            Log.d(TAG, "error " + "sendData");
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean initializeObject(Context ctx) {
        try {
            mPrinter = new Printer(0, Printer.MODEL_ANK, ctx);
        }
        catch (Exception e) {
            Log.d(TAG, "error " + "Printer");
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }
        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        try {
            mPrinter.disconnect();
        } catch (Epos2Exception e) {
            e.printStackTrace();
        }


        mPrinter = null;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect(mDeviceInfo.getTarget(), Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            Log.d(TAG, "error " + "connect");
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            Log.d(TAG, "error " + "beginTransaction");
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {
            Log.d(TAG, "error endTransaction");
        }

        try {
            mPrinter.disconnect();
        }
        catch (final Exception e) {
            Log.d(TAG, "error disconnect");
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }
        else {
            ;//print available
        }

        return true;
    }

    private boolean isDrawerOpen(PrinterStatusInfo status) {
        if (status == null)
            return false;

        if (status.getConnection() == Printer.FALSE)
            return false;

        if (status.getDrawer() == Printer.DRAWER_LOW)
            return false;
        else
            return true;
    }

    private String makeErrorMessage(Context ctx, PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += ctx.getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += ctx.getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += ctx.getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += ctx.getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += ctx.getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += ctx.getString(R.string.handlingmsg_err_autocutter);
            msg += ctx.getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += ctx.getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += ctx.getString(R.string.handlingmsg_err_overheat);
                msg += ctx.getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += ctx.getString(R.string.handlingmsg_err_overheat);
                msg += ctx.getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += ctx.getString(R.string.handlingmsg_err_overheat);
                msg += ctx.getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += ctx.getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += ctx.getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    private void dispPrinterWarnings(Context ctx, PrinterStatusInfo status) {
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += ctx.getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += ctx.getString(R.string.handlingmsg_warn_battery_near_end);
        }

        Log.d(TAG, "warningsMsg = " + warningsMsg);
    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onPtrReceive");
                disconnectPrinter();
            }
        }).start();
    }

    private Bitmap createScaledBitmap(Bitmap bitmap, int desiredHeight, int desiredWidth) {
        return Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, false);
    }

    public void runCutPaperSequence() {
        if (!initializeObject(mContext)) {
            return;
        }

        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.addCut(Printer.CUT_FEED);
        } catch (Epos2Exception e) {
            Log.i(TAG,"error in cut paper");
            e.printStackTrace();
        }

        if(!printData()){
            return;
        }

        disconnectPrinter();
    }

}
