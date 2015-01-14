package com.fdgproject.firedge.speakwithme;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cleverbot.ChatterBot;
import cleverbot.ChatterBotFactory;
import cleverbot.ChatterBotSession;
import cleverbot.ChatterBotType;


public class MainActivity extends Activity implements TextToSpeech.OnInitListener{

    private ChatterBotSession cbs;
    private EditText et;
    private LinearLayout lp;
    private Button btMicro, btSend;
    private boolean reproductor = false;
    private static final int CTTS = 1, CSTT = 2, CSET = 3;
    private TextToSpeech tts;
    private ArrayList<Message> messages = new ArrayList<Message>();
    private Settings st;

    /********************************************************************************************/
    /*                                                                                          */
    /*                                     Métodos on...                                        */
    /*                                                                                          */
    /********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.et_text);
        lp = (LinearLayout) findViewById(R.id.lay_prin);
        btMicro = (Button) findViewById(R.id.bt_capt);
        btSend = (Button) findViewById(R.id.bt_send);
        String loc = getFilesDir().toString()+"/settings.st";
        try{
            InputStream file = new FileInputStream(loc);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            st = (Settings)input.readObject();
        }catch(Exception ex){
            st = new Settings(true, new Locale("es", "ES"), 1, 1);
        }
        ChatterBotFactory factory = new ChatterBotFactory();
        try {
            ChatterBot bot = factory.create(ChatterBotType.CLEVERBOT);
            cbs = bot.createSession();
        } catch (Exception e) {
            Log.v("CBS", e.toString());
        }
        if(st.isSound()) {
            Intent i = new Intent();
            i.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(i, CTTS);
        }
        if(savedInstanceState!= null) {
            messages = (ArrayList<Message>)savedInstanceState.getSerializable("list");
        }
        for(Message msg : messages){
            mensaje(msg);
        }
    }

    //Inicializar los parametros del TTS
    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            //Se puede reproducir
            reproductor = true;
            tts.setLanguage(st.getLanguage());
            tts.setPitch(st.getVoice()); //Tono
            tts.setSpeechRate(st.getSpeed()); //Velocidad
        } else {
            //No se puede reproducir
        }
    }

    //Cerrar el TTS
    @Override
    protected void onStop() {
        super.onStop();
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    //Guardamos los ajustes en un archivo
    @Override
    protected void onDestroy() {
        super.onDestroy();
        String loc = getFilesDir().toString()+"/settings.st";
        try{
            OutputStream file = new FileOutputStream(loc);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);
            output.writeObject(st);
            output.close();
        }
        catch(IOException ex){
            Log.v("WRITE ERROR", ex.toString());
        }
    }

    //Resultado de los Intents
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Intent del TextToSpeech
        if (requestCode == CTTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            } else {
                Intent intent = new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }
        //Intent del Reconocedor de Voz
        else if(requestCode == CSTT){
            if(resultCode == RESULT_OK) {
                ArrayList<String> textos = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                et.setText(textos.get(0));
            }
        }
        //Intent del menú de ajustes
        else if(requestCode == CSET){
            if(resultCode == RESULT_OK)
                st = (Settings)data.getSerializableExtra("resul");
            if(st.isSound()) {
                Intent i = new Intent();
                i.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                startActivityForResult(i, CTTS);
            }
        }
    }

    //Recuperar datos

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("list", messages);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messages = (ArrayList<Message>)savedInstanceState.getSerializable("list");
    }

    /********************************************************************************************/
    /*                                                                                          */
    /*                                     Menu Ajustes                                         */
    /*                                                                                          */
    /********************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("options", st);
            i.putExtras(bundle);
            startActivityForResult(i, CSET);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /********************************************************************************************/
    /*                                                                                          */
    /*                                    Hebra Internet                                        */
    /*                                                                                          */
    /********************************************************************************************/

    class Chat extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deshabilitarBT();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return cbs.think(strings[0]);
            } catch (Exception e) {
                Log.v("Think", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            contestar(s);
            habilitarBT();
        }
    }

    /********************************************************************************************/
    /*                                                                                          */
    /*                                  Métodos auxiliares                                      */
    /*                                                                                          */
    /********************************************************************************************/

    //Crear Message cuando proviene del EditText
    private void escribir(final String s){
        Message msg = new Message(s, false, new Date());
        messages.add(msg);
        mensaje(msg);
    }

    //Crear Message cuando proviene del bot
    private void contestar(final String s){
        Message msg = new Message(s, true, new Date());
        messages.add(msg);
        mensaje(msg);

        if(st.isSound()) {
            if (reproductor) {
                tts.speak(s, TextToSpeech.QUEUE_ADD, null);
            } else {
                Toast.makeText(this, getResources().getString(R.string.error_reproductor), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Método para mostrar y construir el layout de cada mensaje
    private void mensaje(final Message msg){
        int l = msg.isClever() ? R.layout.chat2 : R.layout.chat1;
        View v = LayoutInflater.from(this).inflate(l, null, false);
        lp.addView(v);
        int t = msg.isClever() ? R.id.tv_chat2 : R.id.tv_chat1;
        TextView tv = (TextView)v.findViewById(t);
        tv.setText(msg.getMsg());
        TextView tvd = (TextView)v.findViewById(R.id.tv_date);
        tvd.setText(msg.fecha());
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(st.isSound()) {
                        if (reproductor) {
                            tts.speak(msg.getMsg(), TextToSpeech.QUEUE_ADD, null);
                        } else {
                            Toast.makeText(view.getContext(), getResources().getString(R.string.error_reproductor), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        //ScrollView al final
        final ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    //Habilitar y deshabilitar botones
    private void habilitarBT(){
        btSend.setEnabled(true);
        btSend.setBackground(getResources().getDrawable(R.drawable.send_on));
        btMicro.setEnabled(true);
        btMicro.setBackground(getResources().getDrawable(R.drawable.micro_on));
    }

    private void deshabilitarBT(){
        btSend.setEnabled(false);
        btSend.setBackground(getResources().getDrawable(R.drawable.send_off));
        btMicro.setEnabled(false);
        btMicro.setBackground(getResources().getDrawable(R.drawable.micro_off));
    }

    //Funcionalidad del botón para enviar el mensaje
    public void send(View v){
        String s = et.getText().toString();
        escribir(s);
        new Chat().execute(new String[]{s});
        et.setText("");
    }

    //Funcionalidad del botón para grabar sonido
    public void recive(View v){
        String len = (st.getLanguage().equals(Locale.US)) ? "en-US" : "es-ES";
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, len);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla ahora");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(i, CSTT);
    }

}
