package aplication.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import aplication.data.ServidoresCloud;
import aplication.data.Utilizadores;

public class DescontaSaldo implements Runnable{
    private Utilizadores utilizadores;
    private ServidoresCloud servidores;
    
    private String email;
    private double taxa;
    private boolean terminado;
    private String nomeServidor;
    private String idReserva;

    public DescontaSaldo(Utilizadores utilizadores, String email, double taxa,ServidoresCloud servidores,String nomeServidor,String idReserva ) {
        this.utilizadores = utilizadores;
        this.email = email;
        this.taxa = taxa;
        this.terminado=false;
        this.servidores=servidores;
        this.nomeServidor=nomeServidor;
        this.idReserva=idReserva;
    }
    
    public void setTerminado(){
        this.terminado=true;
    }
    
    @Override
    public void run() {
        String[] idServidor = idReserva.split(" ");
        try {
            
            while(!terminado){
                sleep(5000);
                String resultado = this.utilizadores.descontarSaldo(email, taxa,idReserva);
                if(resultado.equals("SemSaldo")){
                    terminado=true;
                    this.servidores.desocupaServidor(nomeServidor,Integer.parseInt(idServidor[1]));
                    this.utilizadores.retiraReserva(email,idReserva);
                }else if(resultado.equals("SemReserva")){
                    terminado=true;
                }
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(DescontaSaldo.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

}
