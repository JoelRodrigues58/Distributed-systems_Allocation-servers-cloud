package aplication.data;

import aplication.threads.AtribuirServidores;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServidoresCloud {

    private class Informacao{
        public ArrayList<ServidorCloud> servidores;
        public ArrayList<Proposta> propostas;
        public int servidores_disponiveis;
        public ReentrantLock lockInformacao = new ReentrantLock();
        public Condition not_Servidores_or_Propostas = lockInformacao.newCondition();
        
        public Informacao(ArrayList<ServidorCloud> servidores){
            this.servidores= servidores;
            this.servidores_disponiveis=1;
            this.propostas= new ArrayList<Proposta>();
        }
        
    }
        
    private HashMap<String,Informacao> informacao;
    private ReentrantLock lock = new ReentrantLock();
    private int proxId;
    Utilizadores utilizadores;

    
    public ServidoresCloud(Utilizadores utilizadores) {
        this.utilizadores= utilizadores;
        this.informacao = new HashMap<String,Informacao>();
        this.proxId=0;
    }

    /*
        Regista o nº de servidores fixos: nomeServidor, taxa a pagar, licitação mínima
    */
    public synchronized void registarServidor(String nome, double taxa, double licitacaoMinima){

        ServidorCloud servidorCloud = new ServidorCloud(nome,taxa,proxId,licitacaoMinima);
        Informacao informacao = null;
        if(this.informacao.isEmpty() || !this.informacao.containsKey(nome)){
            ArrayList<ServidorCloud> servidores= new ArrayList<ServidorCloud>();
            servidores.add(servidorCloud);
            Informacao info = new Informacao(servidores);
            this.informacao.put(nome, info);
            informacao = info;
            
        //Iniciar thread de atribuir o servidor às propostas
            AtribuirServidores atribuirservidores = new AtribuirServidores(nome,this.utilizadores,this);
            Thread atribuirservidoresThread = new Thread(atribuirservidores);
            atribuirservidoresThread.start();
        }
        else{
            informacao = this.informacao.get(nome);
            informacao.lockInformacao.lock();
            informacao.servidores.add(servidorCloud);
            informacao.servidores_disponiveis++;
            informacao.lockInformacao.unlock();
        }
        proxId++;
        
    }

    /*
        Recebe uma lista de servidores, de um determinado tipo
            Procura servidores (ocupados) e reservados por leilão.
                Se encontrar algum, retorna o que reservou com menor licitação, 
                para lhe desalocar o servidor: "2-nomeServidor idServidor"
                Else retorna null, ou seja, nao ha servidores reservados por leilão. 
    */
    
    public String leilaoParaPedido(ArrayList<ServidorCloud> servidorClouds){
        double taxaMinima = Double.MAX_VALUE;
        int id = -1;
        for(int i = 0; i < servidorClouds.size();i++){
            if(servidorClouds.get(i).isOcupado() && servidorClouds.get(i).isLeilao()){
                if(servidorClouds.get(i).getTaxaLeiloada() < taxaMinima){
                    taxaMinima = servidorClouds.get(i).getTaxaLeiloada();
                    id = i;
                }
            }
        }

        if (taxaMinima != Double.MAX_VALUE){
            servidorClouds.get(id).setLeilao(false);
            return "2-"+servidorClouds.get(id).getNome()+" "+servidorClouds.get(id).getId();
        }
        else return null;
    }

    /*
        Efetuar reserva a pedido, para um determinado servidor:
            Se encontrar algum desocupado, retorna "1-nomeServidor idServidor"
            Senao encontrar nenhum, efetua a função leilaoParaPedido (para verificar 
            se existe um de leilao para lhe retirar o servidor) 
                Se for !=null retorna o resultado desta função
                Else retorna TodosServidoresIndisponiveis
            Se esse servidor nao existir, retorna ServidorInexistente
    */
    public String reservarPedido(String nomeServidor){
        ArrayList<ServidorCloud> servidorClouds=null;

        this.lock.lock();
        try{
            Informacao informacao = this.informacao.get(nomeServidor);
                servidorClouds = informacao.servidores;
                informacao.lockInformacao.lock();
                this.lock.unlock();
                try{
                    for(ServidorCloud servidorCloud : servidorClouds){
                            if(!servidorCloud.isOcupado()) {
                                servidorCloud.setOcupado(true);
                                informacao.servidores_disponiveis--;
                                System.out.println("(reservaPedido - "+ nomeServidor+") servidores_disponiveis--");
                                return "1-"+nomeServidor+" "+servidorCloud.getId();
                            }
                    }
                    String leilaoParaPedido = leilaoParaPedido(servidorClouds);
                    if (leilaoParaPedido != null) {
                        return leilaoParaPedido;
                    }

                    return "TodosServidoresIndisponiveis";
                }finally{
                    informacao.lockInformacao.unlock();
                }
                
        }
        catch(NullPointerException e){
            this.lock.unlock();
            return "ServidorInexistente";
        }
        
    }
    
    /*
        Efetuar reserva a leilão, para um determinado servidor, com uma licitação
            Procura por um servidor desocupado: 
                Se encontrar um desocupado e a sua licitação for maior que a licitação mínima
                retorna "nomeServidor idServidor"
                Se não encontrar um desocupado e a sua licitação for maior que a licitação mínima
                efetua a função registarProposta, e retorna ServidoresOcupados
                Se encontrou servidores desocupados e a sua licitação for menor que a licitação mínima
                retorna LicitacaoBaixa
                
                Se esse servidor nao existir, retorna ServidorInexistente    
    */

    public String reservarLeilao(String nomeServidor, String email, double licitacao){
        ArrayList<ServidorCloud> servidorClouds =null;

        this.lock.lock();
        try{
            Informacao informacao = this.informacao.get(nomeServidor);
            servidorClouds = informacao.servidores;
            
            informacao.lockInformacao.lock();
            try{
                double licitMin;
                
                licitMin = servidorClouds.get(0).getLicitacaoMinima();
                if(licitacao < licitMin) return "LicitacaoBaixa "+licitMin;
                
                for(ServidorCloud servidorCloud : servidorClouds){
                    if(!servidorCloud.isOcupado()) {
                        servidorCloud.setTaxaLeiloada(licitacao); 
                        servidorCloud.setLeilao(true);
                        servidorCloud.setOcupado(true);
                        informacao.servidores_disponiveis--;
                        //System.out.println("(reservaLeilao - "+ nomeServidor+") servidores_disponiveis--");
                        return nomeServidor+" "+servidorCloud.getId();
                    }
                }
                registarProposta(nomeServidor,email,licitacao);
                return "ServidoresOcupados";
            }finally{
                informacao.lockInformacao.unlock();
            }
        }catch(NullPointerException e){
            return "ServidorInexistente";
        }
        finally{
              this.lock.unlock();
        } 
    }
    
    /*
        Efetua o registo da proposta de um determinado utilizador, para um tipo de servidor (através da função anterior)
            Cria uma proposta com uma licitação e um email.
                Verifica se o HashMap de propostas está vazio ou se ainda não existem 
                propostas para esse tipo de servidor:
                    - Caso se verifique, adiciona a 1º proposta para esse servidor;
                        Aqui, é iniciada a Thread AtribuirServidores para esse tipo de servidor 
                        (thread para tentar alocar servidores disponíveis a propostas existentes)
                    - Caso existam propostas, verifica se já existe uma proposta
                    daquele utilizador para aquele tipo de servidor:
                        -Se já fez uma proposta, tal é removida e inserida a nova.
                        -Senão, apenas é inserida a atual proposta.
                        
                    - No fim, notifica a condição c1 (foi inserida uma proposta)
    */
 
    public void registarProposta(String nomeServidor, String email, double licitacao){
        Proposta proposta = new Proposta(licitacao,email);
        Informacao informacao =  this.informacao.get(nomeServidor);
        ArrayList<Proposta> propostas = informacao.propostas;
        Proposta pr = verficarProposta(email,propostas);
        if(pr!=null){  
           propostas.remove(pr); 
        }
        propostas.add(proposta);
        informacao.not_Servidores_or_Propostas.signal();
        //System.out.println("(registarProposta) proposta.size++");//VERIFICAR ALL OU NAO
    } 
    
    /*
        Verifica se uma determinada proposta existe num conjunto de propostas, 
        registada por um certo utilizador, com o email dado (utilizada na função anterior)
    */
    public Proposta verficarProposta(String email, ArrayList<Proposta> propostas){
        Proposta res = null;

        for(Proposta p: propostas) {
            if (p.getEmail().equals(email)) {
                res = p;
                return res;
            }
        }
        return res;
    }

    /*
        Consultar servidores disponíveis:
            Procura em todo o HashMap de Servidores e retorna para todos os tipos de servidor:
                "-nomeServidor numeroDisponiveis numeroLeiloes taxaFixa licitacaoMinima-..."
    */
    public String consultarDisponiveis(){
        StringBuilder stringBuilder = new StringBuilder();
        int nDisponiveis, nLeiloes;
        this.lock.lock();
        try{
            for (Informacao informacao : this.informacao.values()) {
                nDisponiveis = 0;
                nLeiloes = 0;
                String nome = null;
                double taxaFixa=0;
                double licitacaoMinima=0;
                informacao.lockInformacao.lock();
                try{
                    for(ServidorCloud servidorCloud: informacao.servidores){
                        nome = servidorCloud.getNome();
                        taxaFixa = servidorCloud.getTaxaFixa();
                        licitacaoMinima = servidorCloud.getLicitacaoMinima();
                        if(!servidorCloud.isOcupado()) nDisponiveis++;
                        else {
                            if(servidorCloud.isLeilao()) nLeiloes++;
                        }
                    }

                    stringBuilder.append("-"+nome +" "+ nDisponiveis +" " + nLeiloes + " " +taxaFixa + " "+ licitacaoMinima );
                }finally{
                    informacao.lockInformacao.unlock();
                }
            }
            return stringBuilder.toString();
            
        }finally{
            this.lock.unlock();
        }
    }
    
    /*
        Retorna a taxa Fixa para um determinado tipo de servidor (utilizada na 
        Thread DescontarSaldo, para se saber quanto descontar ao saldo do cliente 
        por "cada hora".
    */
    public double taxaServidor(String nomeServidor){
        this.lock.lock();
        
        Informacao informacao =  this.informacao.get(nomeServidor);
        ArrayList<ServidorCloud> servidores = informacao.servidores;
        informacao.lockInformacao.lock();
        
        try{
            this.lock.unlock();
            return servidores.get(0).getTaxaFixa();
        }
        finally{
            informacao.lockInformacao.unlock();
        }
    }

    /*
        Desocupa um certo tipo de servidor, dando-lhe o seu id como argumento 
        (chamada na thread TratarCliente, quando a reserva de um determinado servidor
        é retirada a um utilizador)
           - Quando encontra o servidor com o id:
                - Se foi anteriormente reservado como leilão, põe essa variável a false
                  e a variável Ocupado a false
                - Senão só põe a variável Ocupado a false
           - No fim, notifica a condição c1 (servidor disponível)     
    */
    public void desocupaServidor(String nomeServidor,int id){
        this.lock.lock();
        Informacao informacao =  this.informacao.get(nomeServidor);
        ArrayList<ServidorCloud> servidores = informacao.servidores;
        informacao.lockInformacao.lock();
        try{ 
            this.lock.unlock();
            for(ServidorCloud sC : servidores){
                if(sC.isLeilao() && sC.getId()==id){
                    sC.setLeilao(false);
                    sC.setOcupado(false);
                }else if(sC.getId()==id){
                    sC.setOcupado(false);
                }
            }
            informacao.servidores_disponiveis++;
            //System.out.println("(desocupaServidor -"+ nomeServidor+") servidores_disponiveis++");
            informacao.not_Servidores_or_Propostas.signal(); // NOTIFICAR QUANDO HA SERVERES DIPONVEIS
        }finally{
            informacao.lockInformacao.unlock();
        }
    }
    
    
    /*
      Função utilizada como argumento na função desocupaServidor, pois, dado um
        id de reserva, retorna o seu tipo de servidor (argumento na função anterior)
    */
    //TIRAR PARTIDO DO ID do servidor (mudar interface??)
    public String servidorPorId(int id){
        this.lock.lock();
        try{
            for(Informacao informacao : this.informacao.values()){
                informacao.lockInformacao.lock();
                try{
                    for(ServidorCloud s : informacao.servidores){
                    if(s.getId()==id) return s.getNome();
                    }
                }finally{
                    informacao.lockInformacao.unlock();
                }    
            }
           return null;
        }
        finally{
            this.lock.unlock();
        }
    }
    
    /*
        Função de consultar propostas existentes para um determinado tipo de servidor:
            - Tipo de servidor inexistente, retorna: ServidorInexistente
            - Não há propostas para esse tipo de servidor, retorna: Naohapropostas
            - Senao retorna: "-Licitacao Email-..."
    */
    public String propostasPorServidor(String nomeServidor){
        StringBuilder res = new StringBuilder();
        
        this.lock.lock();
        Informacao informacao =  this.informacao.get(nomeServidor);
        try{
            informacao.lockInformacao.lock();
            ArrayList<ServidorCloud> servidores = informacao.servidores;
            ArrayList<Proposta> propostas = informacao.propostas;
            this.lock.unlock();
            if(propostas.size()==0) {
                informacao.lockInformacao.unlock();
                return "Naohapropostas";
            }
            else{
                try{
                    for(Proposta p : propostas){
                    res.append("-"+p.getLicitacao() + " " + p.getEmail());
                    }
                    return res.toString();
                }finally{    
                    informacao.lockInformacao.unlock();
                }   
            }
        }
        catch(NullPointerException e){
            this.lock.unlock();
            return "ServidorInexistente";
        }
    }
    
    /*
    public int servidoresDisponiveis(ArrayList<ServidorCloud> servidores){
        int s_disponiveis=0;
        for(ServidorCloud sC : servidores){
            if(!sC.isOcupado()) s_disponiveis++;
        }
        return s_disponiveis;
    }*/
    
    // Servidor -> ocupado; Eliminar Proposta escolhida; Adicionar reserva a cliente. 
    public String atualizaInformacao(Informacao informacao){
        double licitacao=0;
        int indiceMelhorProposta=0;
        String email=null;
        ServidorCloud servidorEscolhido=null;
        
        for(ServidorCloud sC : informacao.servidores){
            if(!sC.isOcupado()) {
                sC.setOcupado(true);
                sC.setLeilao(true);
                informacao.servidores_disponiveis--; //ESTAVA A FALTAR ISTO!!
                servidorEscolhido=sC;
            }
        }
        for(int i=0; i<informacao.propostas.size();i++){
            Proposta proposta = informacao.propostas.get(i);
            if(proposta.getLicitacao()>licitacao) {
                licitacao=proposta.getLicitacao();
                indiceMelhorProposta=i;
                email=proposta.getEmail();
            }
        }
        
        informacao.propostas.remove(indiceMelhorProposta);
        
        return email + "-"+ servidorEscolhido.getNome() + " " + servidorEscolhido.getId() + " " + licitacao;
        
    }
    
   
    
    /*
      Se houver servidores disponiveis, segundo um tipo de servidor, e propostas
        existentes para esse tipo de servidor, efetua a função atualizaInformação
        (pôr o servidor a ocupado, eliminar a proposta e fazer a reserva para um cliente)
        Caso contrario, adormece
    */
    
    
    public String atribuirServidoresPropostas(String nomeServidor) throws InterruptedException{
        String res=null;
        this.lock.lock();
        Informacao informacao = this.informacao.get(nomeServidor);
        informacao.lockInformacao.lock();

        this.lock.unlock();
        try{ 
             while((informacao.servidores_disponiveis==0) || (informacao.propostas.size()==0)){
                System.out.println("À espera servidores ou propostas...");
                informacao.not_Servidores_or_Propostas.await(); 
            }
                System.out.println("A atribuir servidores disponíveis a propostas existentes...");
            
            res = atualizaInformacao(informacao);
            return res;
        }finally{
            informacao.lockInformacao.unlock();
        }
    }
    
}
