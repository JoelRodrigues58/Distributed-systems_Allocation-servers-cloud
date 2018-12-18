package sd1819;

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
    
    public double consultarConta(String email){
        return this.utilizadores.get(email).getMontante();
    }
    
}
