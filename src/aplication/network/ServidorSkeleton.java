package aplication.network;

import aplication.data.Utilizadores;
import aplication.data.ServidoresCloud;
import aplication.threads.DescontaSaldo;
import java.io.IOException;

public class ServidorSkeleton {
    private ServidoresCloud servidoresCloud;
    private Utilizadores utilizadores;

    public ServidorSkeleton(ServidoresCloud servidoresCloud, Utilizadores utilizadores) {
        this.servidoresCloud = servidoresCloud;
        this.utilizadores = utilizadores;
    }
    
    public String efetuarRegisto(String[] msg){
        String serverM;
        String email = msg[1];
        String password = msg[2];
        boolean registo = this.utilizadores.registar(email, password);

        if (registo) {
            serverM = "Ok";
            
        } else serverM = "Not Ok";

        if(serverM.equals("Not Ok")) return "Erro";
        else return email;
        
    }
    
    public String efetuarLogin(String[] msg){
        String password, serverM;
        String email = msg[1];
        password = msg[2];

        boolean login = this.utilizadores.autenticar(email, password);

        if (login) {
            serverM = "Ok";
        } else serverM = "Not Ok";

        if(serverM.equals("Not Ok")) return "Erro";
        else return email;

    }
    
    public String reservaPedido(String[] msgAut,String email){
        String serverM, idReserva;
        String nomeServidor = msgAut[1];
        if(this.utilizadores.getSaldoCliente(email)>0){

            idReserva = this.servidoresCloud.reservarPedido(nomeServidor);

            if(idReserva!=null && !idReserva.equals("ServidorInexistente") && !idReserva.equals("TodosServidoresIndisponiveis")) {
                String[] split = idReserva.split("-");
                if (split[0].equals("2")) {
                    String emailRemover = this.utilizadores.verificarReserva(split[1]);

                    this.utilizadores.retiraReserva(emailRemover,split[1].split(" ")[1]);
                    
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
                else {
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

            }
            //MUDAR ISTO
            else if(idReserva!=null && idReserva.equals("TodosServidoresIndisponiveis")){
                serverM= "TodosServidoresIndisponiveis";
            }
            else serverM="ServidorInexistente";
        }
        else serverM="DinheiroInsuficiente";
        
        return serverM;
      
    }
    
    public String reservaLeilao(String[]msgAut,String email){
        String serverM;
        String nome_servidor = msgAut[1];
        String licitacao = msgAut[2];  
        if(this.utilizadores.getSaldoCliente(email)>0){
            String idReserva = this.servidoresCloud.reservarLeilao(nome_servidor, email, Double.parseDouble(licitacao));  
            if(!idReserva.equals("ServidorInexistente") && !idReserva.equals("LicitacaoBaixa") && !idReserva.equals("ServidoresOcupados")){
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
    
    public String consultarDisponiveis(String[]msgAut,String email){
        return this.servidoresCloud.consultarDisponiveis();
    }
    
    public Double consultarConta(String[]msgAut,String email){
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
    
    public String retiraReserva(String email, String msgAut){
        String serverM;
        String res = this.utilizadores.retiraReserva(email, msgAut);
        int id = Integer.parseInt(msgAut);
            if(!res.equals("IdInvalido") && !res.equals("NaoTemReservas")){
                this.servidoresCloud.desocupaServidor(this.servidoresCloud.servidorPorId(id), id);
                serverM="Ok";
            }
            else serverM="IdInvalido";
        return serverM;                   
    }
}
