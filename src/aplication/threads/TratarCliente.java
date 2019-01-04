package aplication.threads;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import aplication.network.ServidorSkeleton;

public class TratarCliente implements Runnable {
    private Socket clienteSocket;
    private String email; 
    private boolean autenticado;
    private BufferedReader in;
    private BufferedWriter out;
    private ServidorSkeleton servidorSkeleton;

    public TratarCliente(Socket s,ServidorSkeleton servidorSkeleton) throws IOException{
        this.clienteSocket = s;
        this.in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(clienteSocket.getOutputStream()));        
        this.autenticado = false;
        this.servidorSkeleton=servidorSkeleton;
    }


    @Override
    public void run() {
        try {

            System.out.println("Conexão obtida com " + Thread.currentThread().getName() + "!");
            String clientM, serverM, resultado, email_inserido, password,
                    nomeServidor, licitacao, montante, idReserva;

            while ((clientM = in.readLine()) != null) {

                if(!this.autenticado) {

                    String[] msg = clientM.split(" ");
                    System.out.println("Opção do cliente: "+ msg[0]);
                    int escolha = Integer.parseInt(msg[0]);
                    
                    switch (escolha) {
                        case 1:
                            //Efetuar registo
                            email_inserido = msg[1];
                            password = msg[2];
                            resultado = servidorSkeleton.efetuarRegisto(email_inserido,password);
                            if(resultado.equals("Erro")) serverM="Not Ok";
                            else {
                                serverM="Ok";
                                this.email=resultado;
                                this.autenticado = true;
                            }
                            out.write(serverM);
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: " + serverM);
                            break;

                        case 2:
                            //Efetuar login
                            email_inserido = msg[1];
                            password = msg[2];
                            resultado = servidorSkeleton.efetuarLogin(email_inserido,password);
                            if(resultado.equals("Erro")) serverM="Not Ok";
                            else {
                                serverM="Ok";
                                this.email=resultado;
                                this.autenticado = true;
                            }
                            out.write(serverM);
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: " + serverM);
                            break;

                        default:
                            break;
                    }
                }

                else{
                    String[] msgAut = clientM.split(" ");

                    int escolha = Integer.parseInt(msgAut[0]);
                    System.out.println("Opção do cliente: "+ msgAut[0]);

                    switch (escolha){
                        case -1:
                            //Tirar autenticacao
                            this.autenticado=false;
                            servidorSkeleton.tirarAutenticacao(email);
                            break;
                            
                        case 1:
                            //Reserva a pedido
                            nomeServidor = msgAut[1];
                            resultado = servidorSkeleton.reservaPedido(nomeServidor,email);
                            out.write(resultado);
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: " + resultado);
                            break;

                        case 2:
                            //Reserva Leilão
                            nomeServidor = msgAut[1];
                            licitacao = msgAut[2]; 
                            resultado = servidorSkeleton.reservaLeilao(nomeServidor,licitacao,email);
                            out.write(resultado);
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: " + resultado);
                            break;
                            
                        case 3:
                            //Consultar servidores disponiveis
                            resultado = servidorSkeleton.consultarDisponiveis(email);
                            out.write(resultado);
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: " + resultado);
                            break;
                            
                        case 4:
                            //Consultar Saldo
                            Double saldo = servidorSkeleton.consultarConta(email);
                            out.write(saldo.toString());
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: " + saldo.toString());
                            break;
                        case 5:
                            //Depositar dinheiro
                            montante = msgAut[1];
                            Double montante_atual = servidorSkeleton.depositarDinheiro(email, montante);
                            out.write(montante_atual.toString());
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: "+ montante_atual.toString());
                            break;
                        case 6: 
                            //Consultar propostas
                            nomeServidor = msgAut[1];
                            resultado = servidorSkeleton.propostasPorServidor(nomeServidor);
                            out.write(resultado);
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: "+ resultado);
                            break;
                        case 7:
                            //Consultar reservas de um cliente
                            resultado = servidorSkeleton.consultarReservas(email);
                            out.write(resultado);
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: "+ resultado);
                            break;
                        case 8:
                            //Terminar reserva
                            idReserva = msgAut[1];
                            resultado = servidorSkeleton.retiraReserva(email, idReserva);
                            out.write(resultado);
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: "+ resultado);
                            break;
                        case 9:
                            //Consultar notificacoes
                            resultado = servidorSkeleton.consultarNotificacoes(email);
                            if(resultado==null) resultado = "NaoHaNotificacoes";
                            out.write(resultado);
                            out.newLine();
                            out.flush();
                            System.out.println("\t>O servidor respondeu: "+ resultado);
                            break;
                            
                        default:
                            break;
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
