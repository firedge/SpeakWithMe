package com.fdgproject.firedge.speakwithme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;

import java.util.Locale;


public class SettingsActivity extends Activity {

    private Switch swSound;
    private SeekBar swSpeed;
    private RadioButton rbAgudo;
    private Button btSp, btEn;
    private float speed;
    private Locale language;
    private Settings st;

    //Iniciar componentes y cargar configuración anterior
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Bundle b = getIntent().getExtras();
        st = (Settings)b.getSerializable("options");
        swSound = (Switch)findViewById(R.id.sw_sonido);
        swSpeed = (SeekBar)findViewById(R.id.sb_speed);
        rbAgudo = (RadioButton)findViewById(R.id.rb_agudo);
        btSp = (Button)findViewById(R.id.bt_sp);
        btEn = (Button)findViewById(R.id.bt_en);
        swSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double nm = (10+i)/10.0;
                speed = (float) nm;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(st.isSound())
            swSound.setChecked(true);
        else
            swSound.setChecked(false);

        if(st.getVoice()==8)
            rbAgudo.setChecked(true);

        swSpeed.setProgress((int)(st.getSpeed()*10-10));

        if(st.getLanguage().equals(Locale.US)){
            language = Locale.US;
            btEn.setEnabled(false);
            btEn.setBackground(getResources().getDrawable(R.drawable.flag_eeuu_sel));
        } else {
            language = new Locale("es", "ES");
            btSp.setEnabled(false);
            btSp.setBackground(getResources().getDrawable(R.drawable.flag_spain_sel));
        }
    }

    /***********************************************************************************/
    /*                                                                                 */
    /*                         Funcionalidad de los botones                            */
    /*                                                                                 */
    /***********************************************************************************/

    //Botón Aceptar para guardar los nuevos ajustes
    public void aceptar_bt(View v){
        boolean sound;
        float voice;

        sound = swSound.isChecked() ? true : false;
        voice = rbAgudo.isChecked() ? 8 : 1;

        st.setSound(sound);
        st.setVoice(voice);
        st.setSpeed(speed);
        st.setLanguage(language);

        Intent i = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("resul", st);
        i.putExtras(bundle);
        setResult(Activity.RESULT_OK, i);
        finish();

    }

    //Botón Cancelar, para cerrar el intent
    public void cancelar_bt(View v){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    //Cambiar idioma a español
    public void sp(View v){
        language = new Locale("es", "ES");
        btSp.setEnabled(false);
        btSp.setBackground(getResources().getDrawable(R.drawable.flag_spain_sel));
        btEn.setEnabled(true);
        btEn.setBackground(getResources().getDrawable(R.drawable.flag_eeuu));
    }

    //Cambiar idioma a ingles
    public void en(View v){
        language = Locale.US;
        btSp.setEnabled(true);
        btSp.setBackground(getResources().getDrawable(R.drawable.flag_spain));
        btEn.setEnabled(false);
        btEn.setBackground(getResources().getDrawable(R.drawable.flag_eeuu_sel));
    }
}
