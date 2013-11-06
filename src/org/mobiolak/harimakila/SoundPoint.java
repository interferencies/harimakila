package org.mobiolak.harimakila;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.util.Log;

public class SoundPoint {
	String name;
	HashMap<String,Integer> point;
	HashMap<String,Integer> umbral;
	Integer UMBRAL = 8;
	private double vol;
	static double UP = 0.01;
	static double DOWN = -0.01;
	static double MAX = 1.0;
	MediaPlayer m;
	File sdCard = Environment.getExternalStorageDirectory();
	String soundfile=sdCard.getAbsolutePath()+Main.HARIMAKILA_DIRECTORY+"/test00.mp3";
	int type; 
	
	
	// Tipos de puntos
	public static final int WIFI_COORD=0;
	public static final int WIFI_RSSI=1;
	
	public SoundPoint(String n, HashMap<String,Integer> p, HashMap<String,Integer> u, String soundfile, int t) {
		this.point = p;
		this.name = n;
		this.umbral = u;
		this.vol=0;
		this.m = new MediaPlayer();
		this.m.setAudioStreamType(AudioManager.STREAM_MUSIC);
		this.soundfile=this.sdCard.getAbsolutePath()+Main.HARIMAKILA_DIRECTORY+"/"+soundfile;
		this.type = t;
	}
	
	public double getVolume() {
		return this.vol;
	}
	
	public void stopSound() {
		try {
			this.m.release();
		} catch (IllegalStateException e) {
			Log.e("HARIMAKILA","No se pudo hacer release del reproductor. "+this.name);
		}
	}
	
	private void changeVolume(double change, double max) {
		Log.d("HARIMAKILA","Change Volume de "+this.name+" a "+this.vol);
		this.vol+=change;
		if (this.vol<0) this.vol=0;
		if (this.vol>max) this.vol=max;
		if (this.m != null) {
			try {
				if (this.m.isPlaying()) {
					this.m.setVolume((float) this.vol, (float) this.vol);
				}
			} catch (IllegalStateException e) {
				Log.e("HARIMAKILA","Estado erroneo");
				this.m = new MediaPlayer();
			}
		}
	}

	public boolean checkCollision(List<ScanResult> wf) {
			HashMap<String,Integer> w = new HashMap<String,Integer>();
			for (ScanResult result : wf) {
				w.put(result.SSID, result.level);
			}
			String msg="";
			Set<String> essids = this.point.keySet();
			Iterator<String> i = essids.iterator();
			switch (this.type) {
				case SoundPoint.WIFI_COORD: 
				while (i.hasNext()) {
					Log.d("HARIMAKILA","Tipo coordenada");
					String essid = i.next();
					Integer e0 = this.point.get(essid);
					if (w.get(essid)==null) {changeVolume(DOWN,MAX);  return false; } // El essid no está disponible (fuera de rango..)
					Integer e1 = w.get(essid)-this.umbral.get(essid);
					Integer e2 = w.get(essid)+this.umbral.get(essid);
					msg+=this.name+" E:"+essid+": W:"+w.get(essid)+"dB P:"+this.point.get(essid)+"dB U:"+this.umbral.toString()+"\n";
					Log.d("HARIMAKILA",e1+"<-- "+e0+" -->"+e2);
					if (e0!=null) {
						if (e0<e1 || e0>e2) { 
							PlayWalk.Coberturas.obtainMessage(0,msg.getBytes()).sendToTarget(); 
							changeVolume(DOWN,MAX); 
							return false; 
						}
					} else { 
						PlayWalk.Coberturas.obtainMessage(0,msg.getBytes()).sendToTarget();
						changeVolume(DOWN,MAX);
						return false;
					}
				}
				break;
				case SoundPoint.WIFI_RSSI:
					Log.d("HARIMAKILA","Tipo nivel señal");
					// Solo un essid y solo un umbral
					while (i.hasNext()) {
						String essid = i.next();
						//Integer e0 = this.point.get(essid);
						if (w.get(essid)==null) {changeVolume(DOWN,MAX);  return false; } // El essid no está disponible (fuera de rango..)
						//this.umbral.get(essid)
						Log.e("HARIMAKILA","[**]W:"+w.get(essid)+", P:"+this.point.get(essid)+" P+U"+(this.point.get(essid)-this.umbral.get(essid)));
						if (w.get(essid)>=this.point.get(essid)) {
							// Zona nivel completo
							changeVolume(UP,MAX);
						} else if ( w.get(essid) > (this.point.get(essid)-this.umbral.get(essid)) ) {
							// Zona nivel variable
							float max = (float) (95+w.get(essid)) / 100;
							changeVolume(UP,max);
						} else {
							// Zona fuera de rango
							//changeVolume(DOWN);
							changeVolume(DOWN,MAX);
						}
						//float level=(float) (100+w.get(essid)) / 100;
						
						//this.vol=level;
					}
					break;
				default:
					return false;
			}
			PlayWalk.Coberturas.obtainMessage(0,msg.getBytes()).sendToTarget();
			changeVolume(UP,MAX);
				
			if (!this.m.isPlaying()) {
				try {
					this.m.setDataSource(this.soundfile);
					this.m.prepare();
					this.m.setLooping(false);
					this.m.setVolume((float) this.vol, (float) this.vol);
					
					this.m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						public void onCompletion(MediaPlayer arg0) {
							arg0.release();
//							arg0.reset();
						}
					});
					
					this.m.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
				return true;
	}
}
