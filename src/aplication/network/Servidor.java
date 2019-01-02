package aplication.network;

import aplication.data.Utilizadores;
import aplication.data.ServidoresCloud;
import aplication.threads.TratarCliente;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Servidor {

    private int porta;
    private int nClients;
    private ServidoresCloud servidoresCloud;
    private Utilizadores utilizadores;
    private ServidorSkeleton servidorSkeleton;

    public Servidor(int porta, ServidoresCloud sC, Utilizadores uT, ServidorSkeleton servidorSkeleton){
        this.porta = porta;
        this.nClients = 0;
        this.servidoresCloud = sC;
        this.utilizadores = uT;
        this.servidorSkeleton=servidorSkeleton;
    }

    public void comecaServidor(){
        
        try {
            ServerSocket serverSocket = new ServerSocket(porta);

            System.out.println("=================SERVER====================");

            while (true) {

                System.out.println("À espera de conexão....");

                Socket clSocket = serverSocket.accept();

                TratarCliente tratarCliente = new TratarCliente(clSocket,servidorSkeleton);

                Thread tratarClienteThread = new Thread(tratarCliente);

                tratarClienteThread.setName("Cliente "+(this.nClients+1));

                this.nClients++; // para fazer print

                tratarClienteThread.start();

            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args){
        int porta = 12345;
        Utilizadores utilizadores = new Utilizadores();
        ServidoresCloud servidoresCloud = new ServidoresCloud(utilizadores);

        ServidorSkeleton servidorSkeleton = new ServidorSkeleton(servidoresCloud,utilizadores);
        
        /*
        QUANDO SE QUISER UTILIZAR!!
        int nServidores = 10;
        
        for(int i=0; i< nServidores; i++){
            servidoresCloud.registarServidor("server1", 10.0, 5.0);
            servidoresCloud.registarServidor("server2", 7.0, 6.0);
            servidoresCloud.registarServidor("server3", 20.0, 7.0);
        }
        
        utilizadores.registar("rafa@gmail.com","rafa");
        utilizadores.registar("joel@gmail.com","joel");
        utilizadores.registar("francisco@gmail.com","francisco");
        utilizadores.registar("fabio@gmail.com","fabio");
        utilizadores.registar("hugo@gmail.com","hugo");
        utilizadores.registar("luis@gmail.com","luis");
        utilizadores.registar("bruno@gmail.com","bruno");
        utilizadores.registar("rui@gmail.com","rui");
        utilizadores.registar("maria@gmail.com","maria");
        */
        
        servidoresCloud.registarServidor("server1", 10.0, 5.0);
        servidoresCloud.registarServidor("server2", 7.0, 6.0);
        servidoresCloud.registarServidor("server3", 20.0, 7.0);
        servidoresCloud.registarServidor("server1", 10.0, 5.0);
        
        Servidor servidor = new Servidor(porta,servidoresCloud,utilizadores,servidorSkeleton);
        
        servidor.comecaServidor();
    }
}
