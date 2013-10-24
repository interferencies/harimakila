package org.mobiolak.harimakila;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HarimakilaMain extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harimakila_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.harimakila_main, menu);
        return true;
    }
    
}
