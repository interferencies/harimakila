package org.mobiolak.harimakila;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
public class Main extends Activity {

	WifiManager wifi;
	Intent i;
	static String HARIMAKILA_DIRECTORY = "/harimakila";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harimakila_main);
    }
    
    protected void onResume() {
    	super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.harimakila_main, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
	    switch (item.getItemId()) {
	    	case R.id.action_record_points:
	    		i = new Intent("org.mobiolak.harimakila.RecordPoints");
	    		startActivity(i);
	    		return true;
	    	case R.id.action_play_walk:
	    		i = new Intent("org.mobiolak.harimakila.PlayWalk");
	    		startActivity(i);
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
}
