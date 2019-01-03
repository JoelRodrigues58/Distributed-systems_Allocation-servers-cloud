package aplication.network;

import apresentation.MenuInicial;
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
import apresentation.Posicao;
import java.util.ArrayList;

public class Cliente {
    private String endereco;
    private int porta;
    private ServidorProxy proxy;
    private Posicao posicao;

    public Cliente(String endereco, int porta, Posicao posicao){
        this.endereco = endereco;
        this.porta = porta;
        this.posicao = posicao;
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
            
            // (0,0) -> (700,0) -> (700,700) -> (0,700)
            // (400,200) ~~ posicao central
            //Posicao pos = new Posicao(700,700);
            
            MenuInicial menuInicial = new MenuInicial(proxy,posicao);
            
            menuInicial.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

        public static void main(String[] args) throws InterruptedException{

            String endereco = "localhost"; //127.0.0.1
            int porta = 12345;

            int nClientes = 2;
            ArrayList<Posicao> posicoes= new ArrayList<Posicao>();
            posicoes.add(new Posicao(0,0));
            posicoes.add(new Posicao(700,0));
            posicoes.add(new Posicao(700,700));
            posicoes.add(new Posicao(0,700));

            Cliente cliente = new Cliente(endereco,porta,posicoes.get(1));
            cliente.comecaCliente();
            
        }


}