package aplication.bots;

import aplication.network.ServidorProxy;
import apresentation.MenuInicial;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot implements Runnable{

    private String endereco;
    private int porta;
    private ServidorProxy proxy;
    private int id;
    
    public Bot(String endereco, int porta, int id){
        this.endereco = endereco;
        this.porta = porta;
        this.id = id;
    }
    
    @Override
    public void run() {
        
        try {
            System.out.println("A tentar obter conexão... "+Thread.currentThread().getName());
            Socket socket = new Socket(endereco, porta);
            System.out.println("Conexão obtida com "+Thread.currentThread().getName()+"!");
            
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            this.proxy = new ServidorProxy(in,out,socket);
            Random r = new Random();
            
            String email = "bot"+this.id;
            String passw = "bot"+this.id;
            String dadosReg = email+" "+passw;
            String reg="Not Ok";
            while(!reg.equals("Ok")){
                System.out.println(Thread.currentThread().getName()+" a efetuar: Registo.");
                System.out.println("\t> Dados inseridos: "+dadosReg);
                reg = this.proxy.efetuarRegisto("1",dadosReg);
                System.out.println("\n");
                //System.out.println("\t\tServidor respondeu: "+reg);
            }
            
            int escolha;
            String resposta;
            String nomeServidor;
            String licitacao;
            Double saldo;
            String montante;
            String idReserva;
            
            // Depositar dinheiro
            System.out.println(Thread.currentThread().getName()+" a efetuar: Depositar dinheiro.");
            montante = "50000";
            System.out.println("\t> Montante depositado: "+montante);
            resposta = this.proxy.depositarMontante("5",montante);
            System.out.println("\n");
            //System.out.println("\tServidor respondeu: "+resposta);
            
            int []opcoes = new int[]{1,2,8};
            
            while((escolha = opcoes[r.nextInt(opcoes.length)])>0){
                sleep(3000);
                switch(escolha){
                    case 1: 
                        // Reserva a pedido
                        System.out.println(Thread.currentThread().getName()+" a efetuar: Reserva a pedido.");
                        nomeServidor="server"+(r.nextInt(3)+1); //Mudar isto depois para os servidores que temos
                        System.out.println("\t> Nome de servidor inserido: "+nomeServidor);
                        resposta = this.proxy.servidorPedido("1",nomeServidor);
                        System.out.println("\n");
                        //System.out.println("\t\tServidor respondeu: "+resposta);
                        break;
                    case 2:
                        // Reserva a leilão
                        System.out.println(Thread.currentThread().getName()+" a efetuar: Reserva a leilão.");
                        nomeServidor="server"+(r.nextInt(3)+1);
                        licitacao = ""+r.nextInt(15);
                        System.out.println("\t> Nome de servidor inserido: "+nomeServidor);
                        System.out.println("\t> Licitação inserida: "+licitacao);
                        resposta = this.proxy.servidorLeilao("2",nomeServidor,licitacao);
                        System.out.println("\n");
                        //System.out.println("\tServidor respondeu: "+resposta);
                        break;
                    case 8:
                        // Terminar reserva
                        System.out.println(Thread.currentThread().getName()+" a efetuar: Terminar reserva.");
                        idReserva = ""+r.nextInt(4);
                        System.out.println("\t> Id de Reserva inserido: "+idReserva);
                        resposta = this.proxy.terminarReserva("8",idReserva);
                        System.out.println("\n");
                        //System.out.println("\tServidor respondeu: "+resposta);
                        break;
                    default: break;    
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}