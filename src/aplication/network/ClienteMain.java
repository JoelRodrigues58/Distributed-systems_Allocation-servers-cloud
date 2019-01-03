/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplication.network;

import apresentation.Posicao;
import static java.lang.Thread.sleep;
import java.util.ArrayList;

/**
 *
 * @author raphael
 */
public class ClienteMain {
    
    
    public static void main(String[] args) throws InterruptedException{

            String endereco = "localhost"; //127.0.0.1
            int porta = 12345;

            int nClientes = 2;
            ArrayList<Posicao> posicoes= new ArrayList<Posicao>();
            posicoes.add(new Posicao(0,0));
            posicoes.add(new Posicao(700,0));
            posicoes.add(new Posicao(700,700));
            posicoes.add(new Posicao(0,700));
            
            for(int i=0; i<nClientes; i++){
                sleep(5000);
                Cliente cliente = new Cliente(endereco,porta,posicoes.get(i));
                //Thread clienteThread = new Thread(cliente);
                cliente.comecaCliente();
            }

        }
}
