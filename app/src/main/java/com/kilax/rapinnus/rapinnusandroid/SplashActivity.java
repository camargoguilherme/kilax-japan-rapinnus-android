package com.kilax.rapinnus.rapinnusandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kilax.rapinnus.conexao.ConexaoDB;
import com.kilax.rapinnus.conexao.Servidor;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;


public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 5000;
    private String[] values = new String[6];
    private ProgressDialog dialog;
    private ConexaoDB db = null;
    private int numServidor = 0;
    private boolean lembrarSenha = false;
    private boolean criandoView = false;
    private Preferencias prefencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Metodo para deixar a Splash em FullScreen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        prefencia = new Preferencias( getSharedPreferences("preferencia",
                Context.MODE_PRIVATE));
        numServidor = prefencia.getNumServidor();
        lembrarSenha = prefencia.getLembrarSenha();
        criandoView = prefencia.getCriandoView();
        String rede = netWorkdisponibilidade(this);

        boolean podeconectar = false;
        if(rede.equals("WIFI"))
            podeconectar = true;


        final boolean finalPodeconectar = podeconectar;
        new Handler().postDelayed(new Runnable() {

             //Exibindo splash com um timer.

            @Override
            public void run() {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                TextView texto = (TextView) findViewById(R.id.texto);

                Servidor servidor = new Servidor(numServidor);
                values = servidor.getValues();

                // Esse método será executado sempre que o timer acabar
                // E inicia a activity principal
                if(finalPodeconectar){
                    try {

                        if(new BuscandoServidor().execute(values).get()){
                            texto.setText("Conexão Estabelecida...");
                            if(!criandoView)
                                criandoView = new CriandoView().execute(values).get();
                            if(!lembrarSenha){
                                prefencia.comitPreferencias(numServidor, lembrarSenha, criandoView);
                                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(i);
                            }else{
                                prefencia.comitPreferencias(numServidor, lembrarSenha, criandoView);
                                Intent i = new Intent(SplashActivity.this, ConsultaActivity.class);
                                startActivity(i);
                            }

                        }
                    } catch (InterruptedException e) {
                        Log.e("Handler()", "InterruptedException\n" + e.toString());
                    } catch (ExecutionException e) {
                        Log.e("Handler()", "ExecutionException\n" + e.toString());
                    }
                    // Fecha esta activity
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Conecte-se à rede WIFI", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    class BuscandoServidor extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... codbarra) {
            boolean conect = false;
            try {
                int i = 0;

                db = new ConexaoDB(codbarra[1], codbarra[2], codbarra[3], codbarra[4]);
                db.conectarPostgres();

                while(!db._status) {

                    if ( i < 3 ){
                        i++;

                    }else{
                        i = 0;
                    }
                    codbarra = new Servidor(i).getValues();

                    db = new ConexaoDB(codbarra[1], codbarra[2], codbarra[3], codbarra[4]);
                    db.conectarPostgres();

                    Log.i("Testando conexao", "" + i  + " " +  codbarra[1] + " " + codbarra[2] + " " + codbarra[3] + " " + codbarra[4]);
                    Log.i("Status da Conexão", "" + db._mensage);
                    numServidor = i;
                }

                if(db._status){
                    values = codbarra;
                    conect = true;
                }
            } catch (SQLException e) {
                Log.e("BuscandoServidor()", "Erro ao conectar\n" + e);
            }finally {
                try {
                    if(db._status) {
                        db.desconectarPostgres();
                    }else{
                        //Toast.makeText(getApplicationContext(), "Não Foi Possivel Desconetar ao Banco da Kilax " , Toast.LENGTH_LONG).show();
                    }
                } catch (SQLException e) {
                    Log.e("BuscandoServidor()", "Erro ao desconectar\n" + e);
                }
            }

            return conect;
        }



        @Override
        protected void onPreExecute() {
            // Cria a caixa de dialogo em quanto faz a conexão
            dialog = new ProgressDialog(SplashActivity.this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("Pesquisando...");

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean conect) {
            // TODO Auto-generated method stub

            dialog.dismiss();
            super.onPostExecute(conect);

        }
    }

    class CriandoView extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... conexao) {
            Boolean view = false;
            try {

                db = new ConexaoDB(conexao[1], conexao[2], conexao[3], conexao[4]);
                db.conectarPostgres();
                if(db._status){
                    view = db.createView();
                    Log.i("CriandoView()", ""+db._mensage);
                }
            } catch (SQLException e) {
                Log.e("CriandoView()", "Erro ao conectar\n"+e);
            }finally {
                try {
                    if(db._status) {
                        db.desconectarPostgres();
                    }else{
                        view = false;
                        //Toast.makeText(getApplicationContext(), "Não Foi Possivel Conetar ao Banco da Kilax " + values[5], Toast.LENGTH_LONG).show();
                    }
                } catch (SQLException e) {
                    Log.e("CriandoView()", "Erro ao desconectar\n"+e);
                }
            }

            return view;
        }

        @Override
        protected void onPreExecute() {
            // Cria a caixa de dialogo em quanto faz a conexão
            dialog = new ProgressDialog(SplashActivity.this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("Pesquisando...");

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean criado) {
            // TODO Auto-generated method stub

            dialog.dismiss();
            super.onPostExecute(criado);

        }
    }

    /**
     *Verifica a disponibilidade da rede  de dados<br>
     *Tanto WIFI quanto 3G
     *@return  true ou false
     *@see android.net.ConnectivityManager
     */
    public String netWorkdisponibilidade(Context cont){
        String conectado = "";
        ConnectivityManager conmag;
        conmag = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);
        conmag.getActiveNetworkInfo();
        //Verifica o WIFI
        if(conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
            conectado = "WIFI";
        }
        //Verifica o 3G
        else if(conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()){
            conectado = "MOBILE";
        }
        else{
            conectado = "DESCONECTADO";
        }
        return conectado;
    }
}
