package aplication.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizadores {
    private HashMap<String,Utilizador> utilizadores;
    private ReentrantLock l;

    public Utilizadores() {
        this.utilizadores = new HashMap<String,Utilizador>();
        this.l = new ReentrantLock();
    }

    public synchronized boolean registar(String email,String password){
        if(this.utilizadores.isEmpty() || !this.utilizadores.containsKey(email)){
            Utilizador utilizador = new Utilizador(password,email,0.0);
            utilizador.setAutenticado(true);
            this.utilizadores.put(email, utilizador);

            return true;
        }
        else return false;
    }

    public boolean autenticar(String email, String password){
        Utilizador utilizador=null;
        l.lock();
        try{
            if(this.utilizadores.isEmpty() || !this.utilizadores.containsKey(email) || this.utilizadores.get(email).isAutenticado()){
                return false;
            }
            utilizador = this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
                
        try{
            if(utilizador.getPassword().equals(password)){
                utilizador.setAutenticado(true);
                return true;
            }
            else return false;
            
        }finally{
            utilizador.getL().unlock();
        } 
    }

    public void adicionarReservas(String email, String reserva){
        Utilizador utilizador = null;
        l.lock();
        try{
            utilizador = this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        try{
            utilizador.adicionarReserva(reserva);
        }finally{
            utilizador.getL().unlock();
        }
    }

    public double consultarConta(String email){
        Utilizador utilizador = null;
        l.lock();
        try{
            utilizador = this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        
        try{
            return utilizador.getMontante();
        }finally{
            utilizador.getL().unlock();
        }
    }
    
    public Double depositarDinheiro(String email,Double montante_inserido){
        Utilizador utilizador = null;
        l.lock();
        try{
            utilizador=this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        
        try{
            Double saldo_anterior= utilizador.getMontante();
            Double saldo_atual = Double.sum(saldo_anterior,montante_inserido);
            utilizador.setMontante(saldo_atual);
            return saldo_atual;
        }finally{
            utilizador.getL().unlock();
        }

    }
    
    public String consultarReservas(String email){
        StringBuilder res = new StringBuilder();
        Utilizador utilizador=null;
        ArrayList<String> reservas = null;
        l.lock();
        try{
            utilizador = this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        
        try{
            reservas = utilizador.getReservas();
        }finally{
           utilizador.getL().unlock();
        }
        
        for(String s : reservas){
            res.append("-"+s);
        }
        return res.toString();
    }
    
    public void tirarAutenticacao(String email){
        Utilizador utilizador=null;
        l.lock();
        try{
            utilizador = this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        
        try{
            utilizador.setAutenticado(false);
        }finally{
            utilizador.getL().unlock();
        }
    }
    
    public boolean existeReserva(Utilizador utilizador,String idReserva){
        for(String reserva : utilizador.getReservas()){
            if(reserva.equals(idReserva)) return true;
        }
        return false;
    }
    

    public String descontarSaldo(String email,double taxa, String idReserva){
        Utilizador utilizador = null;
        l.lock();
        try{
            utilizador = this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        
        try{
            if(!existeReserva(utilizador,idReserva)) return "SemReserva";
            double saldo_atual = utilizador.getMontante();
            double saldo_final = Double.sum(saldo_atual,-taxa);
            if(saldo_final>=0){
                System.out.println("... a descontar: " + taxa + "  -> " + utilizador.getEmail());
                utilizador.setMontante(saldo_final);
                return "ComSaldo";
            }else{
                System.out.println("... a descontar: "+ saldo_atual+ "  -> " + utilizador.getEmail());
                utilizador.setMontante(0.0);
                return "SemSaldo";
            }

        }finally{
            utilizador.getL().unlock();
        }
        
    } 
    
    public String retiraReserva(String email, String idReserva){
        Utilizador utilizador = null;
        l.lock();
        try{
            utilizador=this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        
        try{
            ArrayList<String> reservas = utilizador.getReservas();

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
        }finally{
            utilizador.getL().unlock();
        }

    }
    
    public double getSaldoCliente(String email){
        Utilizador utilizador=null;
        l.lock();
        try{
            utilizador=this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        try{
            return utilizador.getMontante();
        }finally{
            utilizador.getL().unlock();
        }
    }

    public String verificarReserva(String idReserva){
        l.lock();
        try{
            for(Utilizador utilizador : this.utilizadores.values()){
                for(String reserva : utilizador.getReservas()) {
                    if (reserva.equals(idReserva)) return utilizador.getEmail();
                }
            }
            return null;
        }finally{
            l.unlock();
        }
    }
    
    public void inserirNotificacoes(String email, String notificacoes){
        Utilizador utilizador = null;
        StringBuilder sB = new StringBuilder();
        l.lock();
        try{
            utilizador = this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        try{
            String notAtuais = utilizador.getNotificacoes();
            if(notAtuais!=null) sB.append(notAtuais);

            sB.append(notificacoes);

            utilizador.setNotificacoes(sB.toString());
        }finally{
            utilizador.getL().unlock();
        }
    }
    
    public String consultarNotificacoes(String email){
        Utilizador utilizador = null;
        l.lock();
        try{
            utilizador = this.utilizadores.get(email);
            utilizador.getL().lock();
        }finally{
            l.unlock();
        }
        try{
            return utilizador.getNotificacoes();
        }finally{
            utilizador.getL().unlock();
        }
    }
}
