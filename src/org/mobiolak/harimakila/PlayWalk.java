package org.mobiolak.harimakila;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class PlayWalk extends Activity {

	WifiManager wifi;
	Vibrator v;
	WifiWalk walk;
	static TextView ic,iw;
	static String PATH_PASEO = "/storage/sdcard0/harimakila/paseo.json";
	
	Handler gScanWifi = new Handler();
	Handler Monitor = new Handler();
	
	static Handler Colisiones = new Handler() {
		@Override
		public void handleMessage (Message msg) {
			byte[] buffer = (byte[]) msg.obj;
			String strReceived = new String(buffer);
			ic.setText(strReceived);
		}
	};
	
	static Handler Coberturas = new Handler() {
		@Override
		public void handleMessage (Message msg) {
			byte[] buffer = (byte[]) msg.obj;
			String strReceived = new String(buffer);
			String log = (String) strReceived+iw.getText();
			if (log.length()>512) log=log.substring(0, 512);
			iw.setText(log);
		}
	};
	
	private Runnable TimerThreadWifi = new Runnable() {
    	public void run() {
    			if (wifi.startScan()) {
	    	    	List<ScanResult> results = wifi.getScanResults();
	    	    	walk.checkCollision(results);
	    	    	gScanWifi.postDelayed(this, 500);
    			}
    	}
    };
	
    private JSONObject loadJSON(String path) throws JSONException, IOException {
    	Log.d("HARIMAKILA","Leyendo paseo");
    	Writer writer = new StringWriter();
    	File jsondata = new File(path);
		char[] buffer = new char[1024];
		Reader reader = new BufferedReader(new FileReader(jsondata));
		int n;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer,0,n);
		}
		reader.close();
    	return new JSONObject(writer.toString());
		
    }
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_walk);
		// Show the Up button in the action bar.
		setupActionBar();
		
		ic = (TextView) findViewById(R.id.info_colisiones);
		iw = (TextView) findViewById(R.id.info_coberturas);
		
		// Iniciamos wifi
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		//Gestion de volumen con audiomanager
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    	v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	
    	// Cargamos los puntos
    	//String JSONPoints = getIntent().getStringExtra("json");
    	String str = "{\"wifi_point\":{\"name\":\"punto0\",\"essids\":[\"tiburon00\",\"TP-LINK_E18C1A\"],\"values\":[-48,-59],\"relations\":[1.674,0.955],\"umbral\":5}}";
    	walk = new WifiWalk("test");
    	JSONObject points;
		try {
			points = loadJSON(PATH_PASEO);
			walk.loadSoundPoints(points);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	
	protected void onResume() {
		super.onResume();
		gScanWifi.postDelayed(TimerThreadWifi, 100);
	}
	
	protected void onStop() {
		super.onStop();
		gScanWifi.removeCallbacks(TimerThreadWifi);
		wifi.disconnect();
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.play_walk, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
