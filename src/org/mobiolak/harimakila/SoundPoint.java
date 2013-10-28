package org.mobiolak.harimakila;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.net.wifi.ScanResult;
import android.util.Log;

public class SoundPoint {
	String name;
	HashMap<String,Integer> point;
	HashMap<String,Integer> umbral;
	Integer UMBRAL = 8;
	private double vol;
	static double UP = 0.1;
	static double DOWN = -0.1;
	
	public SoundPoint(String n, HashMap<String,Integer> p, HashMap<String,Integer> u) {
		this.point = p;
		this.name = n;
		this.umbral = u;
		this.vol=0;
	}
	
	public double getVolume() {
		return this.vol;
	}
	
	private void changeVolume(double change) {
		this.vol+=change;
		if (this.vol<0) this.vol=0;
		if (this.vol>1) this.vol=1;
	}

	public boolean checkCollision(List<ScanResult> wf) {
			HashMap<String,Integer> w = new HashMap<String,Integer>();
			for (ScanResult result : wf) {
				w.put(result.SSID, result.level);
			}
			String msg="";
			Set<String> essids = this.point.keySet();
			Iterator<String> i = essids.iterator();
			while (i.hasNext()) {
				String essid = i.next();
				Integer e0 = this.point.get(essid);
				Integer e1 = w.get(essid)-this.umbral.get(essid);
				Integer e2 = w.get(essid)+this.umbral.get(essid);
				msg+=this.name+" E:"+essid+": W:"+w.get(essid)+"dB P:"+this.point.get(essid)+"dB U:"+this.umbral.toString()+"\n";
				Log.d("HARIMAKILA",e1+"<-- "+e0+" -->"+e2);
				if (e0!=null) {
					if (e0<e1 || e0>e2) { PlayWalk.Coberturas.obtainMessage(0,msg.getBytes()).sendToTarget(); changeVolume(DOWN); return false; }
				} else { PlayWalk.Coberturas.obtainMessage(0,msg.getBytes()).sendToTarget(); return false;}
			}
			PlayWalk.Coberturas.obtainMessage(0,msg.getBytes()).sendToTarget();
			changeVolume(UP);
			return true;
	}
}
