package org.mobiolak.harimakila;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
			rLayout.setBackground(drawable);
			im.setImageResource(R.drawable.icono_cascos);
			break;
		case 3:
			t.setText("¿Musika entzuten duzu? / ¿Escuchas la música?");
			drawable = res.getDrawable(R.drawable.fondo_azul); //new Image that was added to the res folder
			rLayout.setBackground(drawable);
			im.setImageResource(R.drawable.icono_musica);
			break;
		case 2:
			t.setText("Sartu telefonoa poltsatxoan / Mete el teléfono en el bolsito");
			drawable = res.getDrawable(R.drawable.fondo_amarillo); //new Image that was added to the res folder
			rLayout.setBackground(drawable);
			im.setImageResource(R.drawable.icono_bolso);
			break;
		case 1:
			t.setText("Lasai ibili instalazio barruan / Visita la instalación con calma");
			drawable = res.getDrawable(R.drawable.fondo_rosa); //new Image that was added to the res folder
			rLayout.setBackground(drawable);
			im.setImageResource(R.drawable.icono_tortuga);
			break;
		default:
			p.setVisibility(View.VISIBLE);
			t.setText("martxan.. / en marcha..");
			drawable = res.getDrawable(R.drawable.fondo_azul); //new Image that was added to the res folder
			rLayout.setBackground(drawable);
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
