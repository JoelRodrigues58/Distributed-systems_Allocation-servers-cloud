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

            while ((clientM = in.readLine()) != null && !clientM.equals("quit")) {

                if(!this.autenticado) {

                    String[] msg = clientM.split(" ");
                    System.out.println(msg[0]);
                    int escolha = Integer.parseInt(msg[0]);
                    

                    switch (escolha) {
                        case 1:
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

                    String idReserva;

                    switch (escolha){
                        case -1:
                            System.out.println("Tirou autenticacao.");
                            this.autenticado=false;
                            break;
                            
                        case 1:
                            String nomeServidor = msgAut[1];

                            idReserva = this.servidoresCloud.reservarPedido(nomeServidor);

                            if(idReserva!=null) {
                                this.utilizadores.adicionarReservas(email,idReserva);
                                serverM = "Ok";
                            }
                            else serverM = "Not Ok";

                            out.write(serverM);
                            out.newLine();
                            out.flush();
                            System.out.println("O servidor respondeu: " + serverM);
                            break;

                        case 2:
                            nomeServidor = msgAut[1];
                            idReserva = this.servidoresCloud.reservarPedido(nomeServidor);
                            
                            break;
                            
                        case 3:
                            String disponiveis = this.servidoresCloud.consultarDisponiveis();
                            out.write(disponiveis);
                            out.newLine();
                            out.flush();
                            System.out.println("O servidor respondeu: " + disponiveis);
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
