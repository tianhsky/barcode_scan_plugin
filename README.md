(Deprecated) Cordova PDF417/QRCode Barcode Scan Plugin
==============

PDF417/QRCode Barcode Scan Plugin for Cordova / PhoneGap.

## Supported Platforms

- Android

## How to use

### Install

	cordova plugin add org.pluginporo.barcode_scan_plugin

### Scan

	navigator.barcode_scan_plugin.scan(
		function(scanResult){
			// scanResult contains {barcode, dln, first_name, last_name}
			console.log(scanResult.barcode);
		},
		function(err){
			// error callback
			console.log(err);
		}
	);
