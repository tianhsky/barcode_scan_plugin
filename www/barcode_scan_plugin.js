var cordova = require('cordova');
var exec = require('cordova/exec');

var BarcodeScanPlugin = function() {

  this.scan = function(success_cb, error_cb){
    exec(success_cb, error_cb, "BarcodeScanPlugin", "scan", []);
  };

};

module.exports = new BarcodeScanPlugin();