package negocio;

import java.util.ArrayList;
import java.util.HashMap;

public class Utilizadores {
    private HashMap<String,Utilizador> utilizadores;

    public Utilizadores() {
        this.utilizadores = new HashMap<String,Utilizador>();
    }

    public boolean registar(String email,String password){
        if(this.utilizadores.isEmpty() || !this.utilizadores.containsKey(email)){
            Utilizador utilizador = new Utilizador(password,email,0.0);
            this.utilizadores.put(email, utilizador);
            return true;
        }
        else return false;
    }

    public boolean autenticar(String email, String password){
        if(this.utilizadores.isEmpty() || !this.utilizadores.containsKey(email)){
            return false;
        }
        else {
            if(this.utilizadores.get(email).getPassword().equals(password)){
                return true;
            }
            else return false;
        }
    }

    public void adicionarReservas(String email, String reserva){
        this.utilizadores.get(email).adicionarReserva(reserva);
    }

    public double consultarConta(String email){
        return this.utilizadores.get(email).getMontante();
    }
    
    public Double depositarDinheiro(String email,Double montante_inserido){
        Double saldo_anterior= this.utilizadores.get(email).getMontante();
        Double saldo_atual = Double.sum(saldo_anterior,montante_inserido);
        this.utilizadores.get(email).setMontante(saldo_atual);
        return saldo_atual;
    }
    
    public String consultarReservas(String email){
        StringBuilder res = new StringBuilder();
        ArrayList<String> reservas = this.utilizadores.get(email).getReservas();
        for(String s : reservas){
            res.append("-"+s);
        }
        return res.toString();
    }
}
