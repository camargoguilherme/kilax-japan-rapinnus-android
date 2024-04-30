package com.kilax.rapinnus.rapinnusandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.kilax.rapinnus.conexao.ConexaoDB;
import com.kilax.rapinnus.conexao.Servidor;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsuario;
    private EditText editTextSenha;
    private Button btnLogin;
    private Switch switchSenha;

    private String usuario;
    private String senha;

    private String[] values = new String[6];
    private ProgressDialog dialog;
    private ConexaoDB db = null;

    private Servidor servidor;
    private Preferencias prefencia;
    private int numServidor = 0;
    private boolean lembrarSenha = false;
    private boolean criandoView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent i = getIntent();

        prefencia = new Preferencias( getSharedPreferences("preferencia",
                Context.MODE_PRIVATE));
        numServidor = prefencia.getNumServidor();
        lembrarSenha = prefencia.getLembrarSenha();
        criandoView = prefencia.getCriandoView();

        Log.i("LoginActivity", "numServer = " + numServidor + "\n" +
                "lembrarSenha = " + lembrarSenha + "\n" +
                "criandoView = " + criandoView);

        servidor = new Servidor(numServidor);
        values = servidor.getValues();

        editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
        editTextSenha = (EditText) findViewById(R.id.editTextSenha);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        switchSenha = (Switch) findViewById(R.id.switchSenha);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario = editTextUsuario.getText().toString();
                senha = editTextSenha.getText().toString();
                lembrarSenha = switchSenha.isChecked();
                Log.d("lembrarSenha", ""+lembrarSenha);
                try {
                    if( (new buscarUsuario().execute(values).get()) && senha.equals("a")){

                        prefencia.comitPreferencias(numServidor, lembrarSenha, criandoView);

                        Intent i = new Intent(LoginActivity.this, ConsultaActivity.class);
                        startActivity(i);
                        finish();

                    }else{
                        Toast.makeText(getApplicationContext(), "Usuario ou Senha Inválido...", Toast.LENGTH_SHORT).show();
                        editTextSenha.setText("");

                    }

                } catch (InterruptedException e) {
                    Log.e("buscarUsuario()", "InterruptedException\n" + e);
                } catch (ExecutionException e) {
                    Log.e("buscarUsuario()", "ExecutionException\n" + e);
                }
            }
        });
    }

    class buscarUsuario extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... conexao) {
            Boolean view = false;
            try {

                db = new ConexaoDB(conexao[1], conexao[2], conexao[3], conexao[4]);
                db.conectarPostgres();
                if(db._status){
                    view = db.buscarUsuario(usuario);
                }
            } catch (SQLException e) {
                Log.e("buscaUsuario()", "Erro ao conectar" + e.toString());
            }finally {
                try {
                    if(db._status) {
                        db.desconectarPostgres();
                    }else{
                        view = false;
                        //Toast.makeText(getApplicationContext(), "Não Foi Possivel Conetar ao Banco da Kilax " + values[5], Toast.LENGTH_LONG).show();
                    }
                } catch (SQLException e) {
                    Log.e("buscaUsuario()", "Erro ao desconectar" + e.toString());
                }
            }
            return view;
        }

        @Override
        protected void onPreExecute() {
            // Cria a caixa de dialogo em quanto faz a conexão
            dialog = new ProgressDialog(LoginActivity.this);
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
}
