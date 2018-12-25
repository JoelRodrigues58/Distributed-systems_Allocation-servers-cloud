package negocio;

import apresentacao.MenuInicial;
import java.io.*;
import java.net.Socket;

import static com.sun.javafx.util.Utils.split;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class Cliente {
    private String endereco;
    private int porta;
    private ServidorProxy proxy;

    public Cliente(String endereco, int porta){
        this.endereco = endereco;
        this.porta = porta;
    }


    public void comecaCliente(){
        try {
            System.out.println("=================CLIENTE====================");
            System.out.println("A tentar obter conexão...");
            Socket socket = new Socket(endereco, porta);
            System.out.println("Conexão obtida!");
            
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            this.proxy = new ServidorProxy(in,out,socket);
            
            Posicao pos = new Posicao(0,400);
            
            MenuInicial menuInicial = new MenuInicial(proxy,pos);

            menuInicial.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    
    public static void main(String[] args){

        String endereco = "localhost"; //127.0.0.1
        int porta = 12345;

        Cliente cliente = new Cliente(endereco,porta);

        cliente.comecaCliente();

    }
}
