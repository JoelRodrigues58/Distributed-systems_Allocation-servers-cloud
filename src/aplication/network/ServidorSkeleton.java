package aplication.network;

import aplication.data.Utilizadores;
import aplication.data.ServidoresCloud;
import aplication.threads.DescontaSaldo;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ServidorSkeleton {
    private ServidoresCloud servidoresCloud;
    private Utilizadores utilizadores;

    public ServidorSkeleton(ServidoresCloud servidoresCloud, Utilizadores utilizadores) {
        this.servidoresCloud = servidoresCloud;
        this.utilizadores = utilizadores;
    }
    
    public String efetuarRegisto(String email, String password){
        String serverM;
        boolean registo = this.utilizadores.registar(email, password);

        if (registo) {
            serverM = "Ok";
            
        } else serverM = "Not Ok";

       return serverM;
        
    }
    
    public String efetuarLogin(String email, String password){
        String serverM;

        boolean login = this.utilizadores.autenticar(email, password);

        if (login) {
            serverM = "Ok";
        } else serverM = "Not Ok";
        
        return serverM;

    }
    
    public String reservaPedido(String nomeServidor,String email){
        String serverM, idReserva;
        
        if(this.utilizadores.getSaldoCliente(email)>0){

            idReserva = this.servidoresCloud.reservarPedido(nomeServidor);

            if(idReserva!=null && !idReserva.equals("ServidorInexistente") && !idReserva.equals("TodosServidoresIndisponiveis")) {
                String[] split = idReserva.split("-"); 
                if (split[0].equals("2")) {
                    String emailRemover = this.utilizadores.verificarReserva(split[1]);
                    //MANDAR NOTIFICAÇÃO
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    
                    String parteSplit[] = split[1].split(" ");
                    String tipoServidor = parteSplit[0];
                    String id = parteSplit[1];
                    
                    StringBuilder sB = new StringBuilder();
                    sB.append("$Foi lhe retirado um servidor do tipo "+tipoServidor);
                    sB.append(", com ID = "+id);
                    sB.append(" ( Data = "+ sdf.format(timestamp) +" )");
                    String notificacoes = sB.toString();
                    
                    this.utilizadores.inserirNotificacoes(emailRemover,notificacoes);
                    
                    
                    this.utilizadores.retiraReserva(emailRemover,split[1].split(" ")[1]);
                }    
                    this.utilizadores.adicionarReservas(email, split[1]);
                    Thread descontarSaldo = new Thread(
                            new DescontaSaldo(
                                    this.utilizadores,
                                    email,
                                    this.servidoresCloud.taxaServidor(nomeServidor),
                                    servidoresCloud,
                                    nomeServidor,
                                    split[1])
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
        
        return serverM;
      
    }
    
    public String reservaLeilao(String nome_servidor, String licitacao,String email){
        String serverM;
 
        if(this.utilizadores.getSaldoCliente(email)>0){
            String idReserva = this.servidoresCloud.reservarLeilao(nome_servidor, email, Double.parseDouble(licitacao));  
            if(!idReserva.equals("ServidorInexistente") && !idReserva.contains("LicitacaoBaixa") && !idReserva.equals("ServidoresOcupados")){
                this.utilizadores.adicionarReservas(email, idReserva);
                
                Thread descontarSaldo = new Thread(
                        new DescontaSaldo(
                        this.utilizadores,
                        email,
                        Double.parseDouble(licitacao),
                        servidoresCloud,
                        nome_servidor,
                        idReserva)
                );
                
                descontarSaldo.start();
                serverM="Ok";
            }
            else{
                serverM=idReserva;
            }
        }
        else{
            serverM="SaldoInsuficiente";
        }  
        return serverM;
    }
    
    public String consultarDisponiveis(String email){
        return this.servidoresCloud.consultarDisponiveis();
    }
    
    public Double consultarConta(String email){
        return this.utilizadores.consultarConta(email);
    }
    
    public Double depositarDinheiro(String email,String montante){
        return this.utilizadores.depositarDinheiro(email, Double.parseDouble(montante));
    }
    
    public String propostasPorServidor(String servidor){
        return this.servidoresCloud.propostasPorServidor(servidor);
    }
    
    public void tirarAutenticacao(String email){
        this.utilizadores.tirarAutenticacao(email);
    }
    
    public String consultarReservas(String email){
        return this.utilizadores.consultarReservas(email);
    }
    
    public String retiraReserva(String email, String idReserva){
        String serverM;
        String res = this.utilizadores.retiraReserva(email, idReserva);
        int id = Integer.parseInt(idReserva);
            if(!res.equals("IdInvalido") && !res.equals("NaoTemReservas")){
                this.servidoresCloud.desocupaServidor(this.servidoresCloud.servidorPorId(id), id);
                serverM="Ok";
            }
            else serverM="IdInvalido";
        return serverM;                   
    }
    
    public String consultarNotificacoes(String email){
        return this.utilizadores.consultarNotificacoes(email);
    }
}
