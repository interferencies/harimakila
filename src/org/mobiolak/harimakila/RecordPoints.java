package org.mobiolak.harimakila;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
public class RecordPoints extends Activity {

	WifiManager wifi;
	TelephonyManager tel;
	private Handler customHandler = new Handler();
	private TextView signal;
	private boolean record_position = false; // estamos grabando
	OutputStreamWriter osw;
	
	private WifiWalk ww;
	
	HashMap<String,WifiAP> h = new HashMap<String, WifiAP>();
	
    private void addWifiAP(String e, int l) {
    	if (!h.containsKey(e)) { h.put(e, new WifiAP(e,l)); }
    	else {
    		WifiAP w = (WifiAP) h.get(e);
    		w.updateWifiAP(l);
    		h.put(e, w);
    	}
    }

    private Runnable updateTimerThread = new Runnable() {
    	public void run() {
    			if (wifi.startScan()) {
	    	    	List<ScanResult> results = wifi.getScanResults();

	    	    	if (record_position) {
	    	    		for (ScanResult result : results ) {
			    	    	
			    	    	addWifiAP(result.SSID,result.level);
		    	    	}
	    	    		
	    	    		Collection<WifiAP> c = h.values();
		    	    	Iterator<WifiAP> i = c.iterator();
		    	    	String info = "";
		    	    	while (i.hasNext()) {
		    	    		WifiAP w = (WifiAP) i.next();
		    	    		info = info+"["+w.getEssid()+"]["+w.getLevel()+"]\n";
		    	    	}
		    	    	signal.setText(info);
	    	    	}
	    	    	
	    	    	
	    	    	customHandler.postDelayed(this, 500);
    			}
    	}
    };
	
    // GESTION DE ESTADOS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harimakila_record_points);
        signal = (TextView) findViewById(R.id.signal);
        signal.setText("Sin captura de señales Wifi");
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ww = new WifiWalk();
    }
    
    protected void onResume() {
    	super.onResume();
    	customHandler.postDelayed(updateTimerThread, 100);
    }
    
    protected void onStop() {
		super.onStop();
		customHandler.removeCallbacks(updateTimerThread);
		wifi.disconnect();
	}
    
    // GESTION DE EVENTOS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.harimakila_record_points, menu);
        return true;
    }
    
	public void onClick(View view) {
    	Button b = (Button) view;
    	if (record_position) {
	    	try {
	    		Log.d("HARIMAKILA","Cerrando");
	    		
	    		String position=String.valueOf(SystemClock.uptimeMillis())+",\n";
	    		Collection<WifiAP> c = h.values();
    	    	Iterator<WifiAP> i = c.iterator();

    	    	while (i.hasNext()) {
    	    		WifiAP w = (WifiAP) i.next();
    	    		position = position+"{\"wifi\":{\"essid\":"+w.getEssid()+",\"level:\""+w.getLevel()+",\"max_level:\""+w.getMinLevel()+",\"min_level:\""+w.getMaxLevel()+"}}\n";
    	    	}
    	    	position = "{\n"+position+"}";
    	    	
    	    	try {
	    			osw.write(position);
	            } catch (Exception e) {
	            	Log.e("HARIMAKILA","No se puede escribir:"+e.getMessage());
	            }

	    		osw.flush();
	    		osw.close();
	    		b.setText("Salvar posición");
	    		h.clear();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
    	} else {
    		
    		try {
    			File sdCard = Environment.getExternalStorageDirectory();
    			File directory = new File(sdCard.getAbsolutePath()+Main.HARIMAKILA_DIRECTORY);
    			Log.d("HARIMAKILA",directory.getPath());
    			if (!directory.isDirectory()) {
    				directory.mkdirs();
    			}
    			
    			EditText e = (EditText) findViewById(R.id.editText1); 
    			File file = new File(directory,SystemClock.uptimeMillis()+"_"+e.getText().toString()+"_harimakila.json");
    			
    			FileOutputStream fOut = new FileOutputStream(file);
    			osw = new OutputStreamWriter(fOut);
    			
    			ww.setName(e.getText().toString());
    			
	    		b.setText("Grabando");
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
    	}
    	record_position=!record_position;
    }
    
}
