package com.kilax.rapinnus.rapinnusandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kilax.rapinnus.conexao.ConexaoDB;
import com.kilax.rapinnus.conexao.Servidor;
import com.kilax.rapinnus.produto.Produto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class ConsultaActivity extends AppCompatActivity {

    private Button consultar;
    private TextView descricao;
    private EditText codbarra;
    private TextView gondola;
    private TextView deposito;
    private TextView danificado;
    private TextView loja;
    private ConexaoDB db = null;
    private ProgressDialog dialog;
    private ResultSet resultSet = null;
    private String[] values = new String[6];
    private String barra = "";
    private Servidor servidor;
    private int numServidor = 0;
    private boolean lembrarSenha = false;
    private boolean criandoView = false;
    private Preferencias prefencia;

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        prefencia = new Preferencias( getSharedPreferences("preferencia",
                Context.MODE_PRIVATE));
        numServidor = prefencia.getNumServidor();
        lembrarSenha = prefencia.getLembrarSenha();
        criandoView = prefencia.getCriandoView();

        servidor = new Servidor(numServidor);
        values = servidor.getValues();

        descricao = (TextView) findViewById(R.id.txtDescricao);
        codbarra = (EditText) findViewById(R.id.editTextCodbarra);
        consultar = (Button) findViewById(R.id.btnConsultar);
        gondola = (TextView) findViewById(R.id.txtSaldoGandola);
        deposito = (TextView) findViewById(R.id.txtSaldoDeposito);
        danificado = (TextView) findViewById(R.id.txtSaldoDanificados);
        loja = (TextView) findViewById(R.id.txtLoja);
        loja.setText(values[5]);
        descricao.setText("");
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Produto p;
                p = null;
                barra = codbarra.getText().toString();
                values[0] = barra;
                if(!barra.isEmpty()){
                    try {
                        p = new BuscarProduto().execute(values).get();
                    } catch (InterruptedException e) {
                        Log.e("BuscarProduto()", "InterruptedException\n" + e);
                    } catch (ExecutionException e) {
                        Log.e("BuscarProduto()", "ExecutionException\n" + e);
                    }
                    if(p != null){

                        descricao.setText(p.toString());
                        gondola.setText("" + p.getSaldoGondola());
                        deposito.setText("" + p.getSaldoDeposito());
                        danificado.setText("" + p.getSaldoDanificado());
                    }else{
                        descricao.setText("Produto não Encontrado");
                        gondola.setText("0");
                        deposito.setText("0");
                        danificado.setText("0");
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Insira o Código de Barras do Produto",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void finish() {
        super.finish();
        prefencia.comitPreferencias(numServidor, lembrarSenha, criandoView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefencia.comitPreferencias(numServidor, lembrarSenha, criandoView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch ( id ) {
            case R.id.deslogar:
                lembrarSenha = false;
                prefencia.comitPreferencias(numServidor, lembrarSenha, criandoView);
                Intent i = new Intent(ConsultaActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //product barcode mode
    public void scanBar(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(ConsultaActivity.this, "Leitor não encontrado ", "Baixar Leitor de Codigo de Barras?", "Sim", "Não").show();
        }
    }

    //product qr code mode
    public void scanQR(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(ConsultaActivity.this, "Leitor não encontrado", "Baixar Leitor de Codigo QR?", "Sim", "Não").show();
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final AppCompatActivity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                codbarra.setText(contents);
            }
        }
    }

    class BuscarProduto extends AsyncTask<String, Void, Produto> {

        protected Produto doInBackground(String... codbarra) {
            Produto p = new Produto();
            try {

                db = new ConexaoDB(codbarra[1], codbarra[2], codbarra[3], codbarra[4]);
                db.conectarPostgres();

                if(db._status) {
                    resultSet = db.retornaSaldoProduto(codbarra[0]);
                    if(resultSet != null){
                        p.setShortname(resultSet.getString("shortname"));
                        p.setId(resultSet.getInt("id"));
                        p.setDetalhes(resultSet.getString("detalhes"));
                        p.setSaldoGondola(resultSet.getInt("gondolas"));
                        p.setSaldoDeposito(resultSet.getInt("depositos"));
                        p.setSaldoDanificado(resultSet.getInt("danificados"));
                    }else{
                        p = null;
                    }
                }

            } catch (SQLException e) {
                Log.e("BuscarProduto", "Erro ao conectar\n"+ e);
            }finally {
                try {
                    if(db._status) {
                        db.desconectarPostgres();
                    }else{
                        //Toast.makeText(getApplicationContext(), "Não Foi Possivel Desconectar do Banco da Kilax " , Toast.LENGTH_LONG).show();
                    }
                } catch (SQLException e) {
                    Log.e("BuscarProduto", "Erro ao desconectar\n"+ e);
                }
            }

            return p;
        }



        @Override
        protected void onPreExecute() {
            // Cria a caixa de dialogo em quanto faz a conexão
            dialog = new ProgressDialog(ConsultaActivity.this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage("Pesquisando...");

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Produto produto) {
            // TODO Auto-generated method stub

            dialog.dismiss();
            consultar.setEnabled(true);
            super.onPostExecute(produto);

        }
    }
}
