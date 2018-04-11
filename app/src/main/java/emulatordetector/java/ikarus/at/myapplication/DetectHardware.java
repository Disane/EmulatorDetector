package emulatordetector.java.ikarus.at.myapplication;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by elias.t on 17.10.2017.
 */

public class DetectHardware
{
    /*
    Launches App in stealth mode
    * */
    public static void hideApp(Context context)
    {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK); // 268435456
        context.startActivity(intent);
    }

    public static void printDeviceInfo(Context ctx)
    {
        // we are going to try and manipulate these values at run time
        // by injecting JS into the Zygote using Frida
        Log.d("DetectHardware", "DeviceInfo.isDebuggerConnected: " +  (android.os.Debug.isDebuggerConnected() ? "connected" : "not connected"));
        TelephonyManager telephonyManager = (TelephonyManager)ctx.getSystemService("phone");
        Log.d("IsEmulatedEnvironment", "Build.Model.RELEASE: " + Build.VERSION.RELEASE);
        Log.d("IsEmulatedEnvironment", "TelephonyManager.getDeviceID(): " + telephonyManager.getDeviceId());
        Log.d("IsEmulatedEnvironment", "Build.Model: " + Build.MODEL);
        Log.d("IsEmulatedEnvironment", "Build.Manufacturer: " + Build.MANUFACTURER);
        Log.d("IsEmulatedEnvironment", "Build.Product: " + Build.PRODUCT);
        Log.d("DetectHardware", "telephonyManager.getNetworkOperatorName(): " + telephonyManager.getNetworkOperatorName().toLowerCase());
        Log.d("DetectHardware", "telephonyManager.getSimCountryIso(): " + telephonyManager.getSimCountryIso().toLowerCase());
        String language = Locale.getDefault().getLanguage();
        Log.i("DetectHardware" , "Locale language: " + language);
    }

    public static boolean isAllowedLocale(Context ctx)
    {
        Log.d("isAllowedLocale", "android.os.Debug.isDebuggerConnected(): " + (android.os.Debug.isDebuggerConnected() ? "connected" : "not connected"));
        if(android.os.Debug.isDebuggerConnected())
        {
            return true;
        }
        Log.d("isAllowedLocale", "DetectHardware.IsEmulatedEnvironment(): " + (DetectHardware.IsEmulatedEnvironment(ctx) ? "emulated" : "non-emulated") );
        if(DetectHardware.IsEmulatedEnvironment(ctx))
        {
            return true;
        }
        // create array of used language shorts
        // Region ISOs
        String[] split = "ru|rus|kz|ua|by|az|am|kg|md|tj|tm|uz|us|ca|cs|sk".split("\\|");
        // Network ISOs
        String[] split2 = "ru|uk|be|az|hy|ky|mo|ro|tg|tk|uz|cs|sk".split("\\|");
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService("phone");
        Log.d("isAllowedLocale", "telephonyManager.getNetworkOperatorName(): " + telephonyManager.getNetworkOperatorName().toLowerCase());
        // get getNetworkCountryIso()
        if (Arrays.asList(split).contains(telephonyManager.getNetworkCountryIso().toLowerCase()))
        {
            return true;
        }
        Log.d("isAllowedLocale", "telephonyManager.getSimCountryIso(): " + telephonyManager.getSimCountryIso().toLowerCase());
        // getSimCountryIso()
        if (Arrays.asList(split).contains(telephonyManager.getSimCountryIso().toLowerCase()))
        {
            return true;
        }
        String lowerCase_language;
        String language = Locale.getDefault().getLanguage();
        Class[] cls = new Class[0];
        lowerCase_language = "";
        try
        {
            lowerCase_language = language.toLowerCase();
        }
        catch(Exception e)
        {
            language = lowerCase_language;
        }
        Log.i("isAllowedLocale" , "lowerCase_language: " + lowerCase_language + " language: " + language);
        Log.i("isAllowedLocale", Arrays.asList(split2).contains(lowerCase_language) == true ? "failed" : "passed");
        return Arrays.asList(split2).contains(lowerCase_language);
    }

    public static boolean IsEmulatedEnvironment(Context context)
    {
        String deviceId = ((TelephonyManager)context.getSystemService("phone")).getDeviceId() == null ? "" : ((TelephonyManager)context.getSystemService("phone")).getDeviceId();
        Log.d("IsEmulatedEnvironment", "Build.Model.RELEASE: " + Build.VERSION.RELEASE);
        Log.d("IsEmulatedEnvironment", "TelephonyManager.getDeviceID(): " + deviceId);
        Log.d("IsEmulatedEnvironment", "Build.Model: " + Build.MODEL);
        Log.d("IsEmulatedEnvironment", "Build.Manufacturer: " + Build.MANUFACTURER);
        Log.d("IsEmulatedEnvironment", "Build.Product: " + Build.PRODUCT);
        // check product: Device ID and Model
        if (Build.VERSION.RELEASE.equals("0") ||
                deviceId.equals("000000000000000") ||
                deviceId.equals("012345678912345") ||
                deviceId.equals("004999010640000") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                "google_sdk".equals(Build.PRODUCT) ||
                "sdk".equals(Build.PRODUCT) ||
                "sdk_x86".equals(Build.PRODUCT) ||
                "vbox86p".equals(Build.PRODUCT))
        {
            return true;
        }
        // get SIM card operator name
        String simOperatorName = ((TelephonyManager) context.getSystemService("phone")).getNetworkOperatorName();
        Log.d("DetectHardware", "TelephonyManager.getNetworkOperatorName(): " + simOperatorName);
        for (String carriers : "android|emergency calls only|fakecarrier".split("\\|"))
        {
            if (simOperatorName.toLowerCase().equals(carriers))
            {
                return true;
            }
        }
        return false;
    }
}
