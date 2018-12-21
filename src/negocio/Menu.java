package negocio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Menu {

    public Menu(){

    }

    public void menuInicial(){

        System.out.println("---------- MENU INICIAL ----------");
        System.out.println("|  1. Registar.                  |");
        System.out.println("|  2. Login.                     |");
        System.out.println("|  3. Exit.                      |");
        System.out.println("----------------------------------");

    }

    public String menuRegistar() throws IOException {
        System.out.println("\n---------- REGISTAR ----------");
        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("   email = ");
        String email = systemIn.readLine();
        System.out.print("   password = ");
        String password = systemIn.readLine();
        System.out.println("---------------------------------\n");
        return email+" "+password;
    }

    public String menuLogin() throws IOException {
        System.out.println("\n---------- LOGIN ----------");
        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("   email = ");
        String email = systemIn.readLine();
        System.out.print("   password = ");
        String password = systemIn.readLine();
        System.out.println("---------------------------------\n");
        return email+" "+password;
    }

    public void menuAutenticado(String email) throws IOException {
        System.out.println("\n------------- "+ email +" -------------");
        System.out.println("|  1. Reservar a pedido.                 |");
        System.out.println("|  2. Reservar a leilão.                 |");
        System.out.println("|  3. Consultar saldo.                   |");
        System.out.println("|  4. Consultar servidores disponíveis.  |");
        System.out.println("|  5. Consultar propostas.               |");
        System.out.println("|  6. Depositar dinheiro.                |");
        System.out.println("|  7. Exit.                              |");
        System.out.println("------------------------------------------");
    }


    public String menuPedido()  throws IOException{

        System.out.println("\n---------- PEDIDO ----------");
        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("   Nome do servidor = ");
        String nome = systemIn.readLine();
        System.out.println("---------------------------------\n");
        return nome;
    }


    public String menuLeilao()  throws IOException{

        System.out.println("\n---------- LEILAO ----------");
        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("   Nome do servidor = ");
        String nome = systemIn.readLine();
        System.out.println("---------------------------------\n");
        return nome;
    }

    public String menuDepositar() throws IOException{
        System.out.println("\n---------- DEPOSITO ----------");
        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("   Montante = ");
        String montante = systemIn.readLine();
        System.out.println("---------------------------------\n");
        return montante;
    }

}


