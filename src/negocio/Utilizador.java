package negocio;

import java.util.ArrayList;

public class Utilizador {
    private String password;
    private String email;
    private double montante;
    private ArrayList<String> reservas;

    public Utilizador(String password, String email, double montante) {
        this.password = password;
        this.email = email;
        this.montante = montante;
        this.reservas=new ArrayList<String>();
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
