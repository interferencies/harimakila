package org.mobiolak.harimakila;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.wifi.ScanResult;
import android.os.SystemClock;
import android.util.Log;

public class WifiWalk {
	ArrayList<SoundPoint> sp;
	String title;
	int id;
	
	public WifiWalk(String title) {
		 //this.wf = new HashMap<String, WifiAP>();
		 this.sp = new ArrayList<SoundPoint>();
		 this.title = title;
		 this.id = 0;
	}
	
	// Cargar los puntos del JSON al HasMap de Sonido
	public boolean loadSoundPoints(JSONObject json) throws JSONException {
		JSONObject points = json.getJSONObject("points");
		JSONArray features = points.getJSONArray("features");
		for (int i = 0; i<features.length(); i++ ){
			JSONObject feature = features.getJSONObject(i);
			String name = feature.getString("name");
			JSONObject properties = feature.getJSONObject("properties");
			JSONObject wp = properties.getJSONObject("wifi_point");
			JSONArray essids = wp.getJSONArray("essids");
			JSONArray values = wp.getJSONArray("values");
			JSONArray um = wp.getJSONArray("umbral");
			
			HashMap<String,Integer> point = new HashMap<String,Integer>(); 
			HashMap<String,Integer> umbral = new HashMap<String,Integer>(); 

			for (int j = 0; j<essids.length(); j++) {
				point.put(essids.getString(j), values.getInt(j));
				umbral.put(essids.getString(j), um.getInt(j));
			}
			sp.add(new SoundPoint(name,point,umbral));
			Log.d("HARIMAKILA","Punto:"+name);
		}
		return true;
	}

	// Colisiones
	public void checkCollision(List<ScanResult> wf) {
		Iterator<SoundPoint> i = sp.iterator();
		String msg="{time:"+String.valueOf(SystemClock.uptimeMillis())+"\n,result:{\n";
		while (i.hasNext()) {
			SoundPoint s = (SoundPoint) i.next();
			Log.d("HARIMAKILA","CheckPoint: "+s.name); 
			msg+="\""+s.name+"\":";
			if (s.checkCollision(wf)) { playSound(s); msg+="\"COLISIONA\""; }
			else { Log.d("HARIMAKILA","No colisiona"); msg+="\"NO COLISIONA\""; }
			msg+=", "+s.getVolume()+"\n";
		}
		msg+="}}";
		PlayWalk.Colisiones.obtainMessage(0,msg.getBytes()).sendToTarget();
	}
	
	// Gestiona el estado de los audios
	public void playSound(SoundPoint sp) {
		Log.d("HARIMAKILA","Colisiona "+sp.name);
	}
}
