package negocio;

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
    ServidoresCloud servidoresCloud;
    Utilizadores utilizadores;

    public Servidor(int porta, ServidoresCloud sC, Utilizadores uT){
        this.porta = porta;
        this.nClients = 0;
        this.servidoresCloud = sC;
        this.utilizadores = uT;
    }

    public void comecaServidor(){

        HashMap<String, ArrayList<ServidorCloud>> servidores = servidoresCloud.getServidores();
        for ( ArrayList<ServidorCloud> sc : servidores.values()) {
            AtribuirServidores atribuirservidores = new AtribuirServidores(sc,utilizadores,servidoresCloud);
            Thread atribuirservidoresThread = new Thread(atribuirservidores);
            atribuirservidoresThread.run();
        }
        
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
        ServidoresCloud servidoresCloud = new ServidoresCloud();
        Utilizadores utilizadores = new Utilizadores();
        
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
        
        Servidor servidor = new Servidor(porta,servidoresCloud,utilizadores);
        
        servidor.comecaServidor();
    }
}
