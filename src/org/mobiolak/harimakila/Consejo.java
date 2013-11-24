package org.mobiolak.harimakila;

import java.io.File;
import java.io.IOException;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Consejo extends Fragment {
	public int num_consejo = 0;
	File sdCard = Environment.getExternalStorageDirectory();
	String soundfile=sdCard.getAbsolutePath()+Main.HARIMAKILA_DIRECTORY+"/test00.mp3";
	MediaPlayer m;
	Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
				return inflater.inflate(R.layout.fragment_consejo_harimakila, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	public int getPosition() {
		return this.num_consejo;
	}
	
	public void putPosition(int p) {
		this.num_consejo=p;
		update_info();
	}
	
	public void startAudio() {
		File file = new File(this.soundfile);
		int resourceId = R.raw.audio_test;
		Log.d("CONSEJO",this.soundfile);
		if(!file.exists()) return; 
		
		try {
			//this.m = new MediaPlayer();
			this.m = MediaPlayer.create(getActivity(), resourceId);
			//this.m.setDataSource(this.soundfile);
			//this.m.prepare();
			this.m.setLooping(false);
			this.m.setVolume(1,1);
			this.m.setOnCompletionListener( new OnCompletionListener()
			{
			    @Override
			    public void onCompletion( MediaPlayer mp )
			    {
			        mp.release();
			    }
			} );
			this.m.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopAudio() {
		try {
			this.m.stop();
			this.m.release();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void update_info() {
		Drawable drawable;
		TextView t = (TextView) getActivity().findViewById(R.id.textView1);
		TextView n = (TextView) getActivity().findViewById(R.id.num_consejo);
		ImageView im = (ImageView) getActivity().findViewById(R.id.icono);
		ProgressBar p = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
		RelativeLayout rLayout = (RelativeLayout) getActivity().findViewById(R.id.layout_consejo);
		Resources res = getResources(); //resource handle
		
		
		n.setText(String.valueOf(num_consejo));
		
		switch(num_consejo) {
		case 4:
			t.setText("Entzungailua jarri / Conecta los auriculares");
			drawable = res.getDrawable(R.drawable.fondo_rosa); //new Image that was added to the res folder
			rLayout.setBackgroundDrawable(drawable);
			im.setImageResource(R.drawable.icono_cascos);
			break;
		case 3:
			startAudio();
			t.setText("¿Musika entzuten duzu? / ¿Escuchas la música?");
			drawable = res.getDrawable(R.drawable.fondo_azul); //new Image that was added to the res folder
			rLayout.setBackgroundDrawable(drawable);
			im.setImageResource(R.drawable.icono_musica);
			break;
		case 2:
			stopAudio();
			t.setText("Sartu telefonoa poltsatxoan / Mete el teléfono en el bolsito");
			drawable = res.getDrawable(R.drawable.fondo_amarillo); //new Image that was added to the res folder
			rLayout.setBackgroundDrawable(drawable);
			im.setImageResource(R.drawable.icono_bolso);
			break;
		case 1:
			t.setText("Lasai ibili instalazio barruan / Visita la instalación con calma");
			drawable = res.getDrawable(R.drawable.fondo_rosa); //new Image that was added to the res folder
			rLayout.setBackgroundDrawable(drawable);
			im.setImageResource(R.drawable.icono_tortuga);
			break;
		default:
			p.setVisibility(View.VISIBLE);
			t.setText("martxan.. / en marcha..");
			drawable = res.getDrawable(R.drawable.fondo_azul); //new Image that was added to the res folder
			rLayout.setBackgroundDrawable(drawable);
			im.setImageResource(R.drawable.harima_logo_base);
		}
	}
	
	public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("num_consejo", num_consejo);
    }
	
	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		if (savedInstanceState!=null) {
			this.num_consejo = savedInstanceState.getInt("num_consejo");
			update_info();
		} else {
			this.num_consejo=4;
			update_info();
		}
		
		ImageView i = (ImageView) getActivity().findViewById(R.id.icono);
		i.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (num_consejo>0) num_consejo=num_consejo-1;
				update_info();
			}
		});	
	}
}
