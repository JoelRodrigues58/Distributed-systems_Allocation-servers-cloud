package negocio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Servidor {

    private int porta;
    private int nClients;
    ServidoresCloud servidoresCloud;
    Utilizadores utilizadores;

    public Servidor(int porta){
        this.porta = porta;
        this.nClients = 0;
        this.servidoresCloud =new ServidoresCloud();
        this.utilizadores=new Utilizadores();

    }

    public void comecaServidor(){
        
        this.servidoresCloud.registarServidor("server1", 10.0, 5.0);
        this.servidoresCloud.registarServidor("server2", 7.0, 6.0);
        this.servidoresCloud.registarServidor("server3", 20.0, 7.0);
        this.servidoresCloud.registarServidor("server1",10.0,5.0);
        
        try {
            ServerSocket serverSocket = new ServerSocket(porta);

            System.out.println("=================SERVER====================");

            while (true) {

                System.out.println("À espera de conexão....");

                Socket clSocket = serverSocket.accept();

                TratarCliente tratarCliente = new TratarCliente(clSocket,this.servidoresCloud,this.utilizadores);

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
        Servidor servidor = new Servidor(porta);
        servidor.comecaServidor();
    }
}
