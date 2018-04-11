/* 
DEMO Script: Evading Emulator Detection using Frida
Platform: Android AOSP ver. 4.4. ARM - SELinux Permissive
Requirements: EmulatorDetector
Called by InstrumentEmuDetectorApp.py
Run: python InstrumentEmuDetectorApp.py

__author__ = "elias.t"
__company__ = "IKARUS Security Software GmbH."
__Country__ = "Austria"
*/

setTimeout(function()
{
    Java.perform(
        function ()
        {
            // AndroidDebug
            var AndroidDebugClass = Java.use("android.os.Debug")
            AndroidDebugClass.isDebuggerConnected.implementation = function()
            {
                send("Called modified - android.os.Debug.isDebuggerConnected()")
                //console.log("[*] Called - android.os.Debug.isDebuggerConnected()");
                return false
            }
            // TelephonyManager
            var TelephonyManagerClass = Java.use("android.telephony.TelephonyManager")
            TelephonyManagerClass.getDeviceId.overload().implementation = function()
            {
                send("Called modified - android.telephony.TelephonyManager.getDeviceId()")
                //console.log("[*] Called modified - android.telephony.TelephonyManager.getDeviceId()")
                return "003366991212155"
            }
            TelephonyManagerClass.getNetworkOperatorName.overload().implementation = function()
            {
                send("Called modified - android.telephony.TelephonyManager.getNetworkOperatorName()")
                //console.log("[*] Called modified - android.telephony.TelephonyManager.getNetworkOperatorName()")
                return "tmobile"
            }
            TelephonyManagerClass.getSimCountryIso.overload().implementation = function()
            {
                send("Called modified - android.telephony.TelephonyManager.getSimCountryIso()")
                //console.log("[*] Called modified - android.telephony.TelephonyManager.getSimCountryIso()")
                return "at"
            }

            var DetectHardware = Java.use("emulatordetector.java.ikarus.at.myapplication.DetectHardware")
            if(typeof DetectHardware !== "undefined") 
            {
                send("DetectHardware has been loaded!")
                //console.log("DetectHardware has been loaded!")
            }
            DetectHardware.isAllowedLocale.implementation = function(arg_ctx)
            {
                send("emulatordetector.java.ikarus.at.myapplication.DetectHardware.isAllowedLocale() modified was called!")
                //console.log("emulatordetector.java.ikarus.at.myapplication.DetectHardware.isAllowedLocale() modified was called!")
                return false
            }
            DetectHardware.IsEmulatedEnvironment.implementation = function(arg_ctx)
            {
                send("emulatordetector.java.ikarus.at.myapplication.DetectHardware.isEmulatedEnvironment() modified was called!")
                //console.log("emulatordetector.java.ikarus.at.myapplication.DetectHardware.isEmulatedEnvironment() modified was called!")
                return false
            }
        }
    );
},0);
