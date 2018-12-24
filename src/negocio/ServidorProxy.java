package negocio;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;


public class ServidorProxy {
     BufferedReader in;
     BufferedWriter out;
     Socket socket;
     
    public ServidorProxy(BufferedReader in, BufferedWriter out,Socket socket){
         this.in=in;
         this.socket=socket;
         this.out=out;
    }
    
    public String efetuarRegisto(String escolha, String dados) throws IOException{
        out.write(escolha + " " + dados);
        out.newLine();
        out.flush();
        String serverM = in.readLine();
        return serverM;
    }
    
     public String efetuarLogin(String escolha, String dados) throws IOException{
        out.write(escolha + " " + dados);
        out.newLine();
        out.flush();
        String serverM = in.readLine();
        return serverM;
    }
     
    public void desligarConexao() throws IOException{
        System.out.println("==========Conexão terminada============");
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
        System.exit(0);
    }
    
    public void tirarAutenticacao() throws IOException{
        out.write("-1 ");
        out.newLine();
        out.flush();
    }
    
    public String servidorPedido(String escolha, String nomeServidor) throws IOException{
        out.write(escolha + " " + nomeServidor);
        out.newLine();
        out.flush();
        String serverM = in.readLine();
        System.out.println("O cliente recebeu: " + serverM);
        return serverM;
    }
    
    public String servidorLeilao(String escolha, String nomeServidor) throws IOException{
        out.write(escolha + " " + nomeServidor);
        out.newLine();
        out.flush();
        String serverM = in.readLine();
        System.out.println("O cliente recebeu: " + serverM);
        return serverM;
    }
    
    public double consultarSaldo(String escolha) throws IOException{
        out.write(""+escolha);
        out.newLine();
        out.flush();

        String serverM = in.readLine();
        System.out.println("O cliente recebeu: " + serverM);
        double saldo = Double.parseDouble(serverM);
        
        return saldo;

    }
    
    public String servidoresDisponiveis(String escolha) throws IOException{
        out.write(""+escolha);
        out.newLine();
        out.flush();

        String disponiveis = in.readLine();
        
        System.out.println("O cliente recebeu:: " + disponiveis);
       
        return disponiveis;
    }
    
    public String consultarPropostas(String escolha) throws IOException{
        out.write(""+escolha);
        out.newLine();
        out.flush();

        String propostas = in.readLine();
        System.out.println("O cliente recebeu: " + propostas);

        return propostas;
    }
    
    public String consultarReservas(String escolha) throws IOException{
        out.write(""+escolha);
        out.newLine();
        out.flush();
        String reservas = in.readLine();
        System.out.println("O cliente recebeu: " + reservas);

        return reservas;
    }
    
    public String depositarMontante(String escolha, String montante) throws IOException{
        out.write(escolha+" "+montante);
        out.newLine();
        out.flush();
        String serverM = in.readLine();
        System.out.println("O cliente recebeu: " + serverM);
        return serverM;
    }
}