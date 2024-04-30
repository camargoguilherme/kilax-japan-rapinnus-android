package com.kilax.rapinnus.produto;

/**
 * Created by guilherme on 21/06/17.
 */

public class Produto {

    private int id;
    private String shortname;
    private String detalhes;
    private int saldoGondola;
    private int saldoDeposito;
    private int saldoDanificado;
    private boolean createView = false;

    public boolean isCreateView() {
        return createView;
    }

    public void setCreateView(boolean createView) {
        this.createView = createView;
    }

    public Produto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public int getSaldoGondola() {
        return saldoGondola;
    }

    public void setSaldoGondola(int saldoGondola) {
        this.saldoGondola = saldoGondola;
    }

    public int getSaldoDeposito() {
        return saldoDeposito;
    }

    public void setSaldoDeposito(int saldoDeposito) {
        this.saldoDeposito = saldoDeposito;
    }

    public int getSaldoDanificado() {
        return saldoDanificado;
    }

    public void setSaldoDanificado(int saldoDanificado) {
        this.saldoDanificado = saldoDanificado;
    }

    @Override
    public String toString() {
        return id + " - " + shortname;
    }
}
