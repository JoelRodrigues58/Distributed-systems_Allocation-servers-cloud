package application.network;

import application.data.Utilizadores;
import application.data.ServidoresCloud;
import application.threads.TratarCliente;
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

                this.nClients++; 

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
        
        
        int nServidores = 3;
        
        for(int i=0; i< nServidores; i++){
            servidoresCloud.registarServidor("t3.nano", 10.0, 5.0);
            servidoresCloud.registarServidor("t3.micro", 7.0, 6.0);
            servidoresCloud.registarServidor("t3.large", 20.0, 7.0);
            servidoresCloud.registarServidor("t3.medium", 15.0, 8.0);
        }
     
        
        Servidor servidor = new Servidor(porta,servidoresCloud,utilizadores,servidorSkeleton);
        
        servidor.comecaServidor();
    }
}
