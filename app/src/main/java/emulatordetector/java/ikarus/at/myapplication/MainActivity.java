package emulatordetector.java.ikarus.at.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

import static emulatordetector.java.ikarus.at.myapplication.DetectHardware.hideApp;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = MainActivity.class.getName();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //DetectHardware.printDeviceInfo(getApplicationContext());
        // we are going to try and manipulate these values at run time
        // by injecting JS into the Zygote using Frida
        Log.d("MainActivity", "DeviceInfo.isDebuggerConnected: " +  (android.os.Debug.isDebuggerConnected() ? "connected" : "not connected"));
        TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService("phone");
        Log.d("MainActivity", "Build.Model.RELEASE: " + Build.VERSION.RELEASE);
        Log.d("MainActivity", "TelephonyManager.getDeviceID(): " + telephonyManager.getDeviceId());
        Log.d("MainActivity", "Build.Model: " + Build.MODEL);
        Log.d("MainActivity", "Build.Manufacturer: " + Build.MANUFACTURER);
        Log.d("MainActivity", "Build.Product: " + Build.PRODUCT);
        Log.d("MainActivity", "telephonyManager.getNetworkOperatorName(): " + telephonyManager.getNetworkOperatorName().toLowerCase());
        Log.d("MainActivity", "telephonyManager.getSimCountryIso(): " + telephonyManager.getSimCountryIso().toLowerCase());
        String language = Locale.getDefault().getLanguage();
        Log.i("MainActivity" , "Locale language: " + language);

        // Launches App in Stealth Mode
        // hideApp(getApplicationContext());
        String strIsEmulator = "@ App Start: " + (DetectHardware.isAllowedLocale(getApplicationContext()) ? "Emulator" : "Real Device");
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(strIsEmulator);
        Log.d(TAG, "onCreate: " + strIsEmulator);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String strEmulatorMessage = "";
                if (DetectHardware.isAllowedLocale(getApplicationContext()))
                {
                    strEmulatorMessage = "You are now running this in an emulated environment!";
                }
                else
                {
                    strEmulatorMessage = "You are now running this on a physical device!";
                }
                Snackbar.make(view, strEmulatorMessage, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // DetectHardware.isAllowedLocale(getApplicationContext());

        // exiting App
        /*if(DetectHardware.isAllowedLocale(getApplicationContext()))
        {
            Log.d(TAG, "onCreate: " + strIsEmulator + " detected! Exiting App!");
            finish();
            return;
        }*/
        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
