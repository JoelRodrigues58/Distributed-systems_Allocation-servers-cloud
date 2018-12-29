package negocio;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizador {
    private String password;
    private String email;
    private double montante;
    private boolean autenticado;
    private ArrayList<String> reservas;
    private ReentrantLock l; 

    public Utilizador(String password, String email, double montante) {
        this.password = password;
        this.email = email;
        this.montante = montante;
        this.reservas=new ArrayList<String>();
        this.autenticado=false;
        this.l = new ReentrantLock();
    }

    public ReentrantLock getL() {
        return l;
    }
    
    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }
    
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public double getMontante() {
        return montante;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getReservas() {
        return reservas;
    }

    public void setReservas(ArrayList<String> reservas) {
        this.reservas = reservas;
    }

    public void setMontante(double montante) {
        this.montante = montante;
    }

    public void adicionarReserva(String idReserva){
        this.reservas.add(idReserva);
    }

}
