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
	private ArrayList<SoundPoint> sp;
	private String name, descripcion;
	private JSONObject points;
	private int id;
	
	public WifiWalk(String title) throws JSONException {
		 //this.wf = new HashMap<String, WifiAP>();
		 this.sp = new ArrayList<SoundPoint>();
		 this.id = 0;
		 this.points = new JSONObject();
		 this.points.put("type", "FeatureCollection");
		 this.points.put("features", new JSONArray());
	}
	
	public WifiWalk() {
		this.sp = new ArrayList<SoundPoint>();
		this.id = 0;
	}
	
	//Modificadoras
	public void setName(String n) { this.name=n;}
	public void setDescription(String d) {this.descripcion=d; }
	public void setId(int i) {this.id=i; }
	public void setPoints(JSONObject p) {this.points=p; }
	public void addPoint(String name, String file, boolean autofade, int layer) {}
	
	// Consultoras
	public String getName() { return this.name; }
	public String getDescription() { return this.descripcion; }
	public String getPointsString() {return this.points.toString(); }
	public JSONObject getPoints() { return this.points; }
	public int getId() {return this.id; }
	
	// Convierte los puntos del JSON a datos en el hashMap para su trabajo.
	private void processPoints() throws JSONException {
		JSONArray features = this.points.getJSONArray("features");
		for (int i = 0; i<features.length(); i++ ){
			JSONObject feature = features.getJSONObject(i);
			String name = feature.getString("name");
			
			JSONObject properties = feature.getJSONObject("properties");
			String sound = properties.getString("file");
			int type = properties.getInt("type");
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
			sp.add(new SoundPoint(name,point,umbral,sound,type));
			Log.d("HARIMAKILA","Punto:"+name);
		}
	}
	
	// Cargar los puntos del JSON al HasMap de Sonido
	public boolean loadWalk(JSONObject json) throws JSONException {
		setName(json.getString("name"));
		setDescription(json.getString("description"));
		setId(json.getInt("id"));
		JSONObject points = json.getJSONObject("points");
		setPoints(points);
		processPoints();
		return true;
	}
	
	public JSONObject saveWalk() throws JSONException {
		JSONObject walk = new JSONObject();
		walk.put("name", this.getName());
		walk.put("description", this.getDescription());
		walk.put("id", this.getId());
		walk.put("points", this.getPoints());
		return walk;
	}

	// Colisiones
	public void checkCollision(List<ScanResult> wf) {
		Iterator<SoundPoint> i = this.sp.iterator();
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
	
	public void stopSounds() {
		Iterator<SoundPoint> s = this.sp.iterator();
		while (s.hasNext()) {
			((SoundPoint) s.next()).stopSound();
		}
	}
}
