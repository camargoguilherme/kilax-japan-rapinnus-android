package com.kilax.rapinnus.rapinnusandroid;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by guilherme on 13/07/17.
 */

public class Preferencias{

    private SharedPreferences save;
    private SharedPreferences.Editor editor;
    private int numServidor = 0;
    private boolean lembrarSenha = false;
    private boolean criandoView = false;

    public Preferencias(SharedPreferences save) {
        this.save = save;
        this.numServidor = save.getInt("numServidor", numServidor);
        this.lembrarSenha = save.getBoolean("lembrarSenha", lembrarSenha);
        this.criandoView = save.getBoolean("criandoView", criandoView);
    }

    public int getNumServidor() {
        return numServidor;
    }

    public boolean getLembrarSenha() {
        return lembrarSenha;
    }

    public boolean getCriandoView() {
        return criandoView;
    }

    public void comitPreferencias(int numServidor, boolean lembrarSenha, boolean criandoView){
        editor = save.edit();
        editor.putInt("numServidor", numServidor );//seta o par de chave("valor") e valor(t)
        editor.putBoolean("lembrarSenha", lembrarSenha);
        editor.putBoolean("criandoView", criandoView);
        editor.commit();//grava a preferencia
    }
}
