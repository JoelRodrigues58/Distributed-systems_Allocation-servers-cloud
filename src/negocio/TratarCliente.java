package negocio;

import java.io.*;
import java.net.Socket;

public class TratarCliente implements Runnable {
    private Socket clienteSocket;
    private ServidoresCloud servidoresCloud;
    private Utilizadores utilizadores;
    private String email; //para o método consultarConta
    private boolean autenticado;
    private BufferedReader in;
    private BufferedWriter out;

    public TratarCliente(Socket s, ServidoresCloud sc, Utilizadores ut) throws IOException{
        this.clienteSocket = s;
        this.servidoresCloud = sc;
        this.utilizadores = ut;
        this.in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(clienteSocket.getOutputStream()));        
        this.autenticado = false;
    }

    public void efetuarRegisto(String[] msg) throws IOException{
        String serverM;
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
    }
    
    public void efetuarLogin(String[] msg) throws IOException{
        String password, serverM;
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
    }
    
    public void reservaPedido(String[] msgAut) throws IOException{
        String serverM, idReserva;
        String nomeServidor = msgAut[1];
        
        if(this.utilizadores.getSaldoCliente(email)>0){
            idReserva = this.servidoresCloud.reservarPedido(nomeServidor);

            if(idReserva!=null && !idReserva.equals("ServidorInexistente") && !idReserva.equals("TodosServidoresIndisponiveis")) {
                this.utilizadores.adicionarReservas(email,idReserva);

                Thread descontarSaldo = new Thread(
                                            new DescontaSaldo(
                                            this.utilizadores,
                                            email,
                                            this.servidoresCloud.taxaServidor(nomeServidor),
                                            servidoresCloud,
                                            nomeServidor,
                                            idReserva)
                                  );
                
                descontarSaldo.start();
                serverM = "Ok";

            }
            else if(idReserva!=null && idReserva.equals("TodosServidoresIndisponiveis")){
                serverM= "TodosServidoresIndisponiveis";
            }
            else serverM="ServidorInexistente";
        }
        else serverM="DinheiroInsuficiente";
        
        out.write(serverM);
        out.newLine();
        out.flush();
        System.out.println("O servidor respondeu: " + serverM);
    }
    
    @Override
    public void run() {
        try {

            System.out.println("Conexão obtida com " + Thread.currentThread().getName() + "!");
            String clientM, serverM;

            while ((clientM = in.readLine()) != null) {

                if(!this.autenticado) {

                    String[] msg = clientM.split(" ");
                    System.out.println("Opção do cliente: "+ msg[0]);
                    int escolha = Integer.parseInt(msg[0]);
                    
                    switch (escolha) {
                        case 1:
                            //Efetuar registo
                            efetuarRegisto(msg);
                            break;

                        case 2:
                            //Efetuar login
                            efetuarLogin(msg);
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
                            this.utilizadores.tirarAutenticacao(email);
                            break;
                            
                        case 1:
                            //Reserva a pedido
                            reservaPedido(msgAut);
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
                        case 8:
                            //Cancelar reserva
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
