package com.kilax.rapinnus.conexao;

import java.io.Serializable;

/**
 * Created by guilherme on 11/07/17.
 */

public class Servidor implements Serializable {

    private String[] values = new String[6];

    public Servidor(int i) {
        selecionaServidor(i);
    }

    public String[] getValues() {
        return values;
    }

    public void selecionaServidor(int num){
        switch ( num ) {
            case 0:
                this.values[1] = "192.168.1.105";
                this.values[2] = "";                    // nome do banco de dados
                this.values[3] = "";                    // nome do usuario do banco
                this.values[4] = "";
                this.values[5] = "Loja Centro";
                break;
            case 1:
                this.values[1] = "192.168.25.240";
                this.values[2] = "";                    // nome do banco de dados
                this.values[3] = "";                    // nome do usuario do banco
                this.values[4] = "";
                this.values[5] = "Loja Maringa";
                break;
            case 2:
                this.values[1] = "192.168.1.254";
                this.values[2] = "";                    // nome do banco de dados
                this.values[3] = "";                    // nome do usuario do banco
                this.values[4] = "";
                this.values[5] = "Loja Shopping";
                break;
            case 3:
                this.values[1] = "192.168.1.4";
                this.values[2] = "";                    // nome do banco de dados
                this.values[3] = "";                    // nome do usuario do banco
                this.values[4] = "";
                this.values[5]  = "Conexão de Teste";
                break;
        }
    }
}
