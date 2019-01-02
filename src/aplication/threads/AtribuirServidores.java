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
                System.out.println("Entrei Thread Atribui servidores:" + this.nomeServidor);
                String idReserva = this.totalservidores.atribuirServidoresPropostas(nomeServidor);
                String split[] = idReserva.split("-");
                String email = split[0];
                String reserva = split[1];
                this.utilizadores.adicionarReservas(email, reserva);
                
                //FALTA DESCONTAR SALDO!!
                 Thread descontarSaldo = new Thread(
                            new DescontaSaldo(
                                    this.utilizadores,
                                    email,
                                    this.totalservidores.taxaServidor(nomeServidor),
                                    this.totalservidores,
                                    nomeServidor,
                                    reserva)
                    );

                    descontarSaldo.start();
                
            } catch (InterruptedException ex) {
                Logger.getLogger(AtribuirServidores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
