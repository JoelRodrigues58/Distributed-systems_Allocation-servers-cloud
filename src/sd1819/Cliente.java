import java.io.*;
import java.net.Socket;

import static com.sun.javafx.util.Utils.split;

public class Cliente {
    private String endereco;
    private int porta;

    public Cliente(String endereco, int porta){
        this.endereco = endereco;
        this.porta = porta;
    }


    public void comecaCliente(){
        System.out.println("=================CLIENT====================");
            try {
            System.out.println("A tenta obter conexão...");

            Socket socket = new Socket(endereco, porta);

            System.out.println("Conexão obtida!");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //Para o cliente poder digitar no terminal
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String clientM, serverM=null, dados=null;
            int escolha;

            System.out.println("Opções: ");
            Menu menu = new Menu();
            menu.menuInicial();

            System.out.print("\nEscolha: ");

            while((clientM = systemIn.readLine()) != null && !clientM.equals("3") || !serverM.equals("Ok")){

                escolha = Integer.parseInt(clientM);

                switch (escolha){
                    case 1:
                        dados = menu.menuRegistar();

                        out.write(escolha + " " + dados);
                        out.newLine();
                        out.flush();

                        serverM = in.readLine();
                        System.out.println("O servidor respondeu: " + serverM);
                        break;

                    case 2:
                        dados = menu.menuLogin();

                        out.write(escolha + " " + dados);
                        out.newLine();
                        out.flush();

                        serverM = in.readLine();
                        System.out.println("O servidor respondeu: " + serverM);
                        break;

                    default:
                        System.out.println("Não representa uma escolha!");
                        System.out.print("Escolha novamente: ");
                        break;
                }

                System.out.println("\nOpções: ");
                menu.menuInicial();
                System.out.print("\nEscolha: ");

            }

            //JÁ ESTÁ AUTENTICADO!

            String[] msg = split(dados," ");
            String email = msg[0], nomeServidor=null;
            menu.menuAutenticado(email);
            System.out.print("\nEscolha: ");

            while((clientM = systemIn.readLine()) != null && !clientM.equals("7") ){
                escolha = Integer.parseInt(clientM);

                switch (escolha){
                    case 1:
                        nomeServidor = menu.menuPedido();

                        out.write(escolha + " " + nomeServidor);
                        out.newLine();
                        out.flush();

                        serverM = in.readLine();
                        System.out.println("O servidor respondeu: " + serverM);
                        break;

                    case 2:
                        nomeServidor = menu.menuLeilao();

                        out.write(escolha + " " + nomeServidor);
                        out.newLine();
                        out.flush();

                        serverM = in.readLine();
                        System.out.println("O servidor respondeu: " + serverM);
                        break;

                    case 3:
                        out.write(""+escolha);
                        out.newLine();
                        out.flush();

                        serverM = in.readLine();
                        System.out.println("O servidor respondeu: " + serverM);
                        int saldo = Integer.parseInt(serverM);

                        System.out.println("Saldo = "+ saldo);

                        break;

                    case 4:
                        out.write(""+escolha);
                        out.newLine();
                        out.flush();

                        serverM = in.readLine();
                        System.out.println("O servidor respondeu: " + serverM);
                        String disponiveis = serverM;

                        System.out.println(disponiveis);

                        break;

                    case 5:
                        out.write(""+escolha);
                        out.newLine();
                        out.flush();

                        serverM = in.readLine();
                        System.out.println("O servidor respondeu: " + serverM);
                        String propostas = serverM;

                        System.out.println(propostas);
                        break;

                    case 6:
                        String montante = menu.menuDepositar();

                        out.write(escolha+" "+montante);
                        out.newLine();
                        out.flush();

                        serverM = in.readLine();
                        System.out.println("O servidor respondeu: " + serverM);
                        break;

                    default:
                        System.out.println("Não representa uma escolha!");
                        System.out.print("Escolha novamente: ");
                        break;
                }
            }

            System.out.println("Conexão terminada!");
            System.out.println("===========================================");

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();

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
