package aplication.threads;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import aplication.data.ServidoresCloud;
import aplication.data.Utilizadores;

public class AtribuirServidores implements Runnable{
    private ServidoresCloud totalservidores;
    private Utilizadores utilizadores;
    private String nomeServidor;
    
    public AtribuirServidores(String nomeServidor,Utilizadores utilizadores,ServidoresCloud totalservidores ){
        this.nomeServidor = nomeServidor;
        this.utilizadores=utilizadores;
        this.totalservidores=totalservidores;
    }
    
    
    @Override
    public void run() {
        while(true){
            try {
                String idReserva = this.totalservidores.servidorParaProposta(nomeServidor);
                String split[] = idReserva.split("-");
                this.utilizadores.adicionarReservas(split[0], split[1]);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(AtribuirServidores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
