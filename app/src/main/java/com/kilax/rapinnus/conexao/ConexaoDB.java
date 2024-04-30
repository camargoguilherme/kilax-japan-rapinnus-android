package com.kilax.rapinnus.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.widget.TextView;

public class ConexaoDB extends _Default implements Runnable{

    private Connection conn;
    private String url      = ""; // localizacao do servidor
    private String dbName   = "";                    // nome do banco de dados
    private String driver   = "org.postgresql.Driver";   // nome do driver de conexao
    private String userName = "";                    // nome do usuario do banco
    private String password = "";
    private Statement st;
    private ResultSet rs;

    public ConexaoDB(String host, String dbName, String user, String pwd) throws SQLException {
        super();
        this.url = "jdbc:postgresql://" + host + "/";
        this.dbName = dbName;
        this.userName = user;
        this.password = pwd;
        this.conectarPostgres();
    }

    public void conectarPostgres() throws SQLException {
        Thread thread = new Thread(this);
        thread.start();
        try{
            thread.join();
        }catch (Exception e){
            Log.e("conectarPostgres()","Erro ao conectar: "+e.toString());
        }

    }

    public void desconectarPostgres() throws SQLException {
        try {
            conn.close();
            _mensage = "Desconectado";
            _status = false;
        } catch (SQLException e) {
            Log.e("desconectarPostgres()","Erro ao desconectar: "+e.toString());
        }
    }

    public ResultSet retornaSaldoProduto(String codbarra) throws SQLException {
        ResultSet resultSet = null;
        try{
            String sql2 =
                    "SELECT * from retorna_saldo_produto where codbarra like '" + codbarra + "'";
            if(conn != null){
                st = conn.createStatement();
                rs = st.executeQuery(sql2);
                if(rs.next()){
                    resultSet = rs;
                }
            }

        } catch (Exception erro){
            Log.e("retornaSaldoProdut()","Erro pesquisa: "+erro.toString());
            _mensage = "Erro pesquisa: "+erro.toString();
            _status = false;
            resultSet = null;
        }finally {
            try {
                conn.close();
            }catch (SQLException erro){
                Log.e("retornaSaldoProdut()","Erro ao fechar conexao: "+erro.toString());
                _mensage = "Erro ao fechar conexao: : "+erro.toString();
                _status = false;
                resultSet = null;
            }
        }

        return resultSet;
    }

    public boolean createView() throws SQLException {
        boolean view = false;
        try{
            String sql2 =
                    "create view retorna_saldo_produto " +
                            "as select codbarra, cod, id, shortname, detalhes, " +
                            "retorna_estoque_disponivel(cod, (select codigo from fildeposito where cast(codigo as text) like '%030')) as gondolas, " +
                            "retorna_estoque_disponivel(cod, (select codigo from fildeposito where cast(codigo as text) like '%050')) as depositos, " +
                            "retorna_estoque_disponivel(cod, (select codigo from fildeposito where cast(codigo as text) like '%060')) as danificados " +
                            "from (SELECT codbarra, cod, id, shortname, detalhes " +
                            "FROM filprd pr, (select id as cod, idprd, codbarra " +
                            "from filprdcod) prd " +
                            "where pr.id = prd.idprd) as p ";

            st = conn.createStatement();
            view = st.execute(sql2);

        } catch (Exception erro){
            Log.e("createView()","Erro ao criar view: "+erro.toString());
            _mensage = "Erro pesquisa: "+erro.toString();
            _status = false;

        }finally {
            try {
                conn.close();
            }catch (SQLException erro){
                Log.e("createView()","Erro ao fechar conexao: "+erro.toString());
                _mensage = "Erro ao fechar conexao: : "+erro.toString();
                _status = false;
            }
        }

        return view;
    }

    public boolean buscarUsuario(String usuario) throws SQLException {
        boolean view = false;
        try{
            String sql2 =
                    "select guerra from usuarios where guerra like '"+ usuario + "'";

            st = conn.createStatement();
            rs = st.executeQuery(sql2);
            String user = "";
            while(rs.next()){
                user = rs.getString("guerra");
            }
            if(user.equals(usuario)){
                view = true;
            }

        } catch (Exception erro){
            Log.e("Metodo buscarUsuario()","Erro ao buscar usuario: "+erro.toString());
            _mensage = "Erro pesquisa: "+erro.toString();
            _status = false;
        }finally {
            try {
                conn.close();
            }catch (SQLException erro){
                Log.e("Metodo buscarUsuario()","Erro ao fechar conexao: "+erro.toString());
                _mensage = "Erro ao fechar conexao: : "+erro.toString();
                _status = false;

            }
        }

        return view;
    }


    @Override
    public void run() {

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url + dbName, userName, password);
            _mensage = "Conectado com Sucesso";
            _status = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("Metodo run()","Erro ao ao conectar: "+e.toString());
            _mensage = "Erro ao conectar: "+e.toString();
            _status = false;
        }
    }
}