package sd1819;

public class Utilizador {
    private String password;
    private String email;
    private double montante;

    public Utilizador(String password, String email, double montante) {
        this.password = password;
        this.email = email;
        this.montante = montante;
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

    public void setMontante(double montante) {
        this.montante = montante;
    }
    
    
}
