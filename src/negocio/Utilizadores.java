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
        if(this.utilizadores.isEmpty() || !this.utilizadores.containsKey(email) || this.utilizadores.get(email).isAutenticado()){
            return false;
        }
        else {
            if(this.utilizadores.get(email).getPassword().equals(password)){
                this.utilizadores.get(email).setAutenticado(true);
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
    
    public void tirarAutenticacao(String email){
        this.utilizadores.get(email).setAutenticado(false);
    }
    
    public double descontarSaldo(String email,double taxa){
        double saldo_atual = this.utilizadores.get(email).getMontante();
        double saldo_final = Double.sum(saldo_atual,-taxa);
        if(saldo_final>=0){
            System.out.println("A descontar: " + taxa);
            this.utilizadores.get(email).setMontante(saldo_final);
        }else{
            System.out.println("A descontar: "+ saldo_atual);
            this.utilizadores.get(email).setMontante(0.0);
        }
        return saldo_final;
    } 
    
    public String retiraReserva(String email, String idReserva){
        ArrayList<String> reservas = this.utilizadores.get(email).getReservas();
        
        if(reservas!=null){
            for(String res : reservas){
                String[] id = res.split(" ");
                if(id[1].equals(idReserva)) {
                    reservas.remove(res);
                    return "Ok";
                }
            }
            return "IdInvalido";
            
        }else {
            return "NaoTemReservas";
        }

    }
    
    public double getSaldoCliente(String email){
        return this.utilizadores.get(email).getMontante();
    }
}
