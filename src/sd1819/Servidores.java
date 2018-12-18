package sd1819;

import java.util.ArrayList;
import java.util.HashMap;


public class Servidores {
    private HashMap<String,ArrayList<Servidor>> servidores;
    private int proxId;

    public Servidores() {
        this.servidores = new HashMap<String,ArrayList<Servidor>>();
        this.proxId=0;
    }
    
    public void registar(String nome, double taxa){
        Servidor servidor = new Servidor(nome,taxa,proxId,false);
        
        if(this.servidores.isEmpty() || !this.servidores.containsKey(nome)){
            ArrayList<Servidor> servidores= new ArrayList<Servidor>();
            servidores.add(servidor);
            this.servidores.put(nome, servidores);
        }
        else{
            this.servidores.get(nome).add(servidor);
        }
        
        proxId++;
    }
     
}
