/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplication.threads;

import aplication.data.ServidoresCloud;
import aplication.data.Utilizadores;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author franciscoaraujo
 */
public class AtribuirPropostas implements Runnable{
    private ServidoresCloud totalservidores;
    private Utilizadores utilizadores;
    private String nomeServidor;
    
    public AtribuirPropostas(String nomeServidor,Utilizadores utilizadores,ServidoresCloud totalservidores ){
        this.nomeServidor = nomeServidor;
        this.utilizadores=utilizadores;
        this.totalservidores=totalservidores;
    }
    
    @Override
    public void run() {
        while(true){
            try {
                
                System.out.println("Entrei Thread Atribui propostas:" + this.nomeServidor);
                String idReserva = this.totalservidores.procuraPropostas(nomeServidor);
                String split[] = idReserva.split("-");
                this.utilizadores.adicionarReservas(split[0], split[1]);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(AtribuirServidores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
