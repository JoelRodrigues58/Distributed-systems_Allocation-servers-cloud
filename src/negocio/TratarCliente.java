package negocio;

import java.io.*;
import java.net.Socket;

public class TratarCliente implements Runnable {
    private Socket clienteSocket;
    private ServidoresCloud servidoresCloud;
    private Utilizadores utilizadores;
    private String email; //para o método consultarConta
    private boolean autenticado;

    public TratarCliente(Socket s, ServidoresCloud sc, Utilizadores ut){
        this.clienteSocket = s;
        this.servidoresCloud = sc;
        this.utilizadores = ut;
        this.autenticado = false;
    }

    @Override
    public void run() {
        try {

            System.out.println("Conexão obtida com " + Thread.currentThread().getName() + "!");
            BufferedReader in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clienteSocket.getOutputStream()));
            String clientM, serverM;

            while ((clientM = in.readLine()) != null) {

                if(!this.autenticado) {

                    String[] msg = clientM.split(" ");
                    System.out.println("Opção do cliente: "+ msg[0]);
                    int escolha = Integer.parseInt(msg[0]);
                    

                    switch (escolha) {
                        case 1:
                            //Efetuar registo
                            this.email = msg[1];
                            String password = msg[2];

                            boolean registo = this.utilizadores.registar(this.email, password);

                            if (registo) {
                                serverM = "Ok";
                                this.autenticado = true;
                            } else serverM = "Not Ok";

                            out.write(serverM);
                            out.newLine();
                            out.flush();
                            System.out.println("O servidor respondeu: " + serverM);
                            break;

                        case 2:
                            //Efetuar login
                            this.email = msg[1];
                            password = msg[2];

                            boolean login = this.utilizadores.autenticar(this.email, password);

                            if (login) {
                                serverM = "Ok";
                                this.autenticado = true;
                            } else serverM = "Not Ok";

                            out.write(serverM);
                            out.newLine();
                            out.flush();
                            System.out.println("O servidor respondeu: " + serverM);
                            break;

                        default:
                            break;
                    }
                }

                else{

                    String[] msgAut = clientM.split(" ");

                    int escolha = Integer.parseInt(msgAut[0]);
                    System.out.println("Opção do cliente: "+ msgAut[0]);
                    String idReserva;

                    switch (escolha){
                        case -1:
                            //Tirar autenticacao
                            this.autenticado=false;
                            break;
                            
                        case 1:
                            //Reserva a pedido
                            String nomeServidor = msgAut[1];

                            idReserva = this.servidoresCloud.reservarPedido(nomeServidor);

                            if(idReserva!=null && !idReserva.equals("ServidorInexistente") && !idReserva.equals("TodosServidoresIndisponiveis")) {
                                this.utilizadores.adicionarReservas(email,idReserva);
                                serverM = "Ok";
                            }else if(idReserva!=null && idReserva.equals("TodosServidoresIndisponiveis")){
                                serverM= "TodosServidoresIndisponiveis";
                            }
                            else serverM="ServidorInexistente";
       
                            out.write(serverM);
                            out.newLine();
                            out.flush();
                            System.out.println("O servidor respondeu: " + serverM);
                            break;

                        case 2:
                            //Reserva Leilão
                            break;
                            
                        case 3:
                            //Consultar servidores disponiveis
                            String disponiveis = this.servidoresCloud.consultarDisponiveis();
                            out.write(disponiveis);
                            out.newLine();
                            out.flush();
                            System.out.println("O servidor respondeu: " + disponiveis);
                            break;
                            
                        case 4:
                            //Consultar Saldo
                            Double saldo = this.utilizadores.consultarConta(email);
                            out.write(saldo.toString());
                            out.newLine();
                            out.flush();
                            System.out.println("O servidor respondeu: " + saldo.toString());
                            break;
                        case 5:
                            //Depositar dinheiro
                            String montante = msgAut[1];
                            Double montante_atual = this.utilizadores.depositarDinheiro(email, Double.parseDouble(montante));
                            out.write(montante_atual.toString());
                            out.newLine();
                            out.flush();
                            System.out.println("O servidor respondeu: "+ montante_atual.toString());
                            break;
                        case 6: 
                            //Consultar propostas
                            break;
                        case 7:
                            //Consultar reservas de um cliente
                            String reservas = this.utilizadores.consultarReservas(email);
                            out.write(reservas);
                            out.newLine();
                            out.flush();
                            System.out.println("O servidor respondeu: "+ reservas);
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
