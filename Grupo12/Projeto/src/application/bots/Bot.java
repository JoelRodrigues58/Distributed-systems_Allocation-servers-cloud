package application.bots;

import application.network.ServidorProxy;
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
public static final String ANSI_RESET = "\u001B[0m";


    private String endereco;
    private int porta;
    private ServidorProxy proxy;
    private int id;
    private String cor;
    
    public Bot(String endereco, int porta, int id,String cor){
        this.endereco = endereco;
        this.porta = porta;
        this.id = id;
        this.cor=cor;
    }
    
    @Override
    public void run() {
        
        try {
            System.out.println(cor+cor+"A tentar obter conexão... "+Thread.currentThread().getName()+ANSI_RESET);
            Socket socket = new Socket(endereco, porta);
            System.out.println(cor+cor+ "Conexão obtida com "+Thread.currentThread().getName()+"!"+ ANSI_RESET);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            this.proxy = new ServidorProxy(in,out,socket);
            Random r = new Random();
            
            String email = "bot"+this.id;
            String passw = "bot"+this.id;
            String dadosReg = email+" "+passw;
            String reg="Not Ok";
            while(!reg.equals("Ok")){
                System.out.println(cor+Thread.currentThread().getName()+" a efetuar: Registo."+ ANSI_RESET);
                System.out.println(cor+"\t> Dados inseridos: "+dadosReg);
                reg = this.proxy.efetuarRegisto("1",dadosReg);
                System.out.println(cor+"\n"+ ANSI_RESET);
                //System.out.println(cor+"\t\tServidor respondeu: "+reg);
            }
            
            int escolha;
            String resposta;
            String nomeServidor;
            String licitacao;
            Double saldo;
            String montante;
            String idReserva;
            
            // Depositar dinheiro
            System.out.println(cor+Thread.currentThread().getName()+" a efetuar: Depositar dinheiro."+ ANSI_RESET);
            montante = "50000";
            System.out.println(cor+"\t> Montante depositado: "+montante);
            resposta = this.proxy.depositarMontante("5",montante);
            System.out.println(cor+"\n"+ ANSI_RESET);
            //System.out.println(cor+"\tServidor respondeu: "+resposta);
            
            int []opcoes = new int[]{1,2,8};
            String []tipos_servidor = new String[]{"nano","large","micro","medium"};
            
            while((escolha = opcoes[r.nextInt(opcoes.length)])>0){
                sleep(3000);
                switch(escolha){
                    case 1: 
                        // Reserva a pedido
                        System.out.println(cor+Thread.currentThread().getName()+" a efetuar: Reserva a pedido."+ ANSI_RESET);
                        nomeServidor="t3."+(tipos_servidor[r.nextInt(tipos_servidor.length)]); 
                        System.out.println(cor+"\t> Nome de servidor inserido: "+nomeServidor);
                        resposta = this.proxy.servidorPedido("1",nomeServidor);
                        System.out.println(cor+"\n"+ ANSI_RESET);
                        //System.out.println(cor+"\t\tServidor respondeu: "+resposta);
                        break;
                    case 2:
                        // Reserva a leilão
                        System.out.println(cor+Thread.currentThread().getName()+" a efetuar: Reserva a leilão."+ ANSI_RESET);
                        nomeServidor="t3."+(tipos_servidor[r.nextInt(tipos_servidor.length)]); 
                        licitacao = ""+r.nextInt(15);
                        System.out.println(cor+"\t> Nome de servidor inserido: "+nomeServidor);
                        System.out.println(cor+"\t> Licitação inserida: "+licitacao);
                        resposta = this.proxy.servidorLeilao("2",nomeServidor,licitacao);
                        System.out.println(cor+"\n"+ ANSI_RESET);
                        //System.out.println(cor+"\tServidor respondeu: "+resposta);
                        break;
                    case 8:
                        // Terminar reserva
                        System.out.println(cor+Thread.currentThread().getName()+" a efetuar: Terminar reserva."+ ANSI_RESET);
                        idReserva = ""+r.nextInt(4);
                        System.out.println(cor+"\t> Id de Reserva inserido: "+idReserva);
                        resposta = this.proxy.terminarReserva("8",idReserva);
                        System.out.println(cor+"\n"+ ANSI_RESET);
                        //System.out.println(cor+"\tServidor respondeu: "+resposta);
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
