package org.pluginporo.barcodescan;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;

import net.photopay.barcode.BarcodeDetailedData;
import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;

import org.pluginporo.aamva.DriverLicense;

public class BarcodeScanPlugin extends CordovaPlugin {

	private static final String LOG_TAG = "BarcodeScanPlugin";
	private static final int MY_REQUEST_CODE = 1337;

	CallbackContext pluginCallbackContext = null;

	public BarcodeScanPlugin() {
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("scan")) {
			this.pluginCallbackContext = callbackContext;
			try {
				this.doScan();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == Pdf417ScanActivity.RESULT_OK) {
        	Pdf417MobiScanData scanData = data.getParcelableExtra(Pdf417ScanActivity.EXTRAS_RESULT);
            // read scanned barcode type (PDF417 or QR code)
            String barcodeType = scanData.getBarcodeType();
            // read the data contained in barcode
            String barcodeData = scanData.getBarcodeData();
          	sendUpdate(getScannedInfo(barcodeData), false);
        }
    }

	private DriverLicense parseDL(String barcode){
		DriverLicense dl = null;
		try {
			dl = new DriverLicense(barcode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dl;
	}

	private void doScan() {
		Intent intent = new Intent((CordovaActivity)this.cordova.getActivity(), Pdf417ScanActivity.class);
		Pdf417MobiSettings sett = new Pdf417MobiSettings();
		sett.setPdf417Enabled(true);
		sett.setNullQuietZoneAllowed(true);
		sett.setQrCodeEnabled(true);
		sett.setDontShowDialog(true);
		intent.putExtra(Pdf417ScanActivity.EXTRAS_SETTINGS, sett);
		this.cordova.startActivityForResult((CordovaPlugin)this, intent, MY_REQUEST_CODE);
	}

	private JSONObject getScannedInfo(String barcode) {
		DriverLicense dl = parseDL(barcode);
	    JSONObject obj = new JSONObject();
	    try {
	      obj.put("barcode", barcode);
	      if(dl != null){
	      	obj.put("dln", dl.getDriverLicenseNumber());
	      	obj.put("first_name", dl.getFirstName());
	      	obj.put("last_name", dl.getLastName());
	      }
	    } catch (JSONException e) {
	      Log.e(LOG_TAG, e.getMessage(), e);
	    }
	    return obj;
  	}

	private void sendUpdate(JSONObject info, boolean keepCallback) {
	    if (this.pluginCallbackContext != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, info);
			result.setKeepCallback(keepCallback);
			this.pluginCallbackContext.sendPluginResult(result);
		}
	}

}
