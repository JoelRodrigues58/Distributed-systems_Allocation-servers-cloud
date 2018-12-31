package negocio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServidoresCloud {

    private HashMap<String, ArrayList<ServidorCloud>> servidores;
    private HashMap<String, ArrayList<Proposta>> propostas;
    private int proxId;
    private ReentrantLock lockServidores;
    private ReentrantLock lockPropostas;
    private ReentrantLock lock_servidores_propostas = new ReentrantLock();
    private Condition c1 = this.lock_servidores_propostas.newCondition();

    
    public ServidoresCloud() {
        this.servidores = new HashMap<String,ArrayList<ServidorCloud>>();
        this.propostas = new HashMap<String, ArrayList<Proposta>>();
        this.proxId=0;
        this.lockServidores = new ReentrantLock();
        this.lockPropostas = new ReentrantLock();
    }

    public HashMap<String, ArrayList<ServidorCloud>> getServidores(){
        return this.servidores;
    }

    public synchronized void registarServidor(String nome, double taxa, double licitacaoMinima){

        ServidorCloud servidorCloud = new ServidorCloud(nome,taxa,proxId,licitacaoMinima);

        if(this.servidores.isEmpty() || !this.servidores.containsKey(nome)){
            ArrayList<ServidorCloud> servidores= new ArrayList<ServidorCloud>();
            servidores.add(servidorCloud);
            this.servidores.put(nome, servidores);
        }
        else{
            this.servidores.get(nome).add(servidorCloud);
        }

        proxId++;

    }

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

    public String reservarPedido(String nomeServidor){
        ArrayList<ServidorCloud> servidorClouds=null;
        String res=null;


        this.lockServidores.lock();
            servidorClouds = this.servidores.get(nomeServidor);
        synchronized (servidorClouds){
            this.lockServidores.unlock();
        
            if(servidorClouds!=null){
                for(ServidorCloud servidorCloud : servidorClouds){
                        if(!servidorCloud.isOcupado()) {
                            servidorCloud.setOcupado(true);
                            return "1-"+nomeServidor+" "+servidorCloud.getId();
                        }
                }
                String leilaoParaPedido = leilaoParaPedido(servidorClouds);
                if (leilaoParaPedido != null) return leilaoParaPedido;

                return "TodosServidoresIndisponiveis";
            }
            else{
                return "ServidorInexistente";
            }
        }
    }

    public String reservarLeilao(Utilizadores utilizadores, String nomeServidor, String email, double licitacao){
        ArrayList<ServidorCloud> servidorClouds =null;
        boolean flag=false;
        //Um comentário aí pa ver depois. 

        this.lockServidores.lock();

        this.lockServidores.lock();
            servidorClouds = this.servidores.get(nomeServidor);

        synchronized (servidorClouds){
            this.lockServidores.unlock();

            this.lockServidores.unlock();
            if(servidorClouds!=null){

                double licitMin;
                boolean tentouLicitar = false;
                for(ServidorCloud servidorCloud : servidorClouds){
                    if(!servidorCloud.isOcupado() && flag==false) {

                    tentouLicitar = true;
                    licitMin = servidorCloud.getLicitacaoMinima();
                        if(licitacao >= licitMin) {

                            flag=true;
                            servidorCloud.setTaxaLeiloada(licitacao); //TIRAR ISTO, SE RETIRARMOS ESTA VARIÁVEL
                            servidorCloud.setLeilao(true);
                            servidorCloud.setOcupado(true);
                            return nomeServidor+" "+servidorCloud.getId();
                        }
                    }
                }
                if(!tentouLicitar) {

                    registarProposta(utilizadores,nomeServidor,email,licitacao);
                    return "ServidoresOcupados";
                }
                return  "LicitacaoBaixa";
            }
            else return "ServidorInexistente";
        }
    }
 
    public void registarProposta(Utilizadores utilizadores,String nomeServidor, String email, double licitacao){
        System.out.println("ENTREI NO REGISTAR PROPOSTAS");
        Proposta proposta = new Proposta(licitacao,email);
        boolean flag=false;
        ArrayList<Proposta> p = null;

        this.lockPropostas.lock();


        if(this.propostas.isEmpty() || !this.propostas.containsKey(nomeServidor)){

            try{
                ArrayList<Proposta> propostas = new ArrayList<>();
                propostas.add(proposta);
                this.propostas.put(nomeServidor,propostas);
                
                //Iniciar thread de atribuir o servidor às propostas
                AtribuirServidores atribuirservidores = new AtribuirServidores(nomeServidor,utilizadores,this);
                Thread atribuirservidoresThread = new Thread(atribuirservidores);
                atribuirservidoresThread.start();

                flag=true;
            }finally{
                this.lockPropostas.unlock();
            }

        }

        if(flag==false){
            
            p = this.propostas.get(nomeServidor);

            synchronized (p){
                this.lock_servidores_propostas.lock();
                this.lockPropostas.unlock();
                
                Proposta pr = verficarProposta(email,p);

                if(pr!=null){  p.remove(pr); }

                p.add(proposta);
                
                this.c1.signalAll(); //NOTIFICAR QUANDO HA UMA PROPOSTA
                this.lock_servidores_propostas.unlock();
            }
        }
   
    }

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

    public String consultarDisponiveis(){
        StringBuilder stringBuilder = new StringBuilder();
        int nDisponiveis, nLeiloes;
        this.lockServidores.lock();
        try{
            for (ArrayList<ServidorCloud> sC : this.servidores.values()) {
                nDisponiveis = 0;
                nLeiloes = 0;
                String nome = null;
                double taxaFixa=0;
                double licitacaoMinima=0;
                for(ServidorCloud servidorCloud: sC){
                    nome = servidorCloud.getNome();
                    taxaFixa = servidorCloud.getTaxaFixa();
                    licitacaoMinima = servidorCloud.getLicitacaoMinima();
                    if(!servidorCloud.isOcupado()) nDisponiveis++;
                    else {
                        if(servidorCloud.isLeilao()) nLeiloes++;
                    }
                }
                stringBuilder.append("-"+nome +" "+ nDisponiveis +" " + nLeiloes + " " +taxaFixa + " "+ licitacaoMinima );

            }
            return stringBuilder.toString();
        }finally{
            this.lockServidores.unlock();
        }
    }
    
    public double taxaServidor(String nomeServidor){
        this.lockServidores.lock();
        ArrayList<ServidorCloud> servidores = this.servidores.get(nomeServidor);
        synchronized (servidores) {
            this.lockServidores.unlock();
            return servidores.get(0).getTaxaFixa();
        }
    }

    public void desocupaServidor(String nomeServidor,int id){
        this.lockServidores.lock();
        ArrayList<ServidorCloud> servidores = this.servidores.get(nomeServidor);
        synchronized (servidores){ 
            this.lock_servidores_propostas.lock();
            this.lockServidores.unlock();
            for(ServidorCloud sC : servidores){
                if(sC.isLeilao() && sC.getId()==id){
                    sC.setLeilao(false);
                    sC.setOcupado(false);
                }else if(sC.getId()==id){
                    sC.setOcupado(false);
                }
            }
            this.c1.signalAll(); // NOTIFICAR QUANDO HA SERVERES DIPONVEIS
            this.lock_servidores_propostas.unlock();
        }
    }
    
    //TIRAR PARTIDO DO ID do servidor (mudar interface??)
    public String servidorPorId(int id){
        this.lockServidores.lock();
        try{
            for(ArrayList<ServidorCloud> sC : this.servidores.values()){
                for(ServidorCloud s : sC){
                    if(s.getId()==id) return s.getNome();
                }
            }
           return null;
        }
        finally{
            this.lockServidores.unlock();
        }
    }
    
    public String propostasPorServidor(String nomeServidor){
        StringBuilder res = new StringBuilder();
        this.lockPropostas.lock();
        this.lockServidores.lock();
        ArrayList<Proposta> propostas = this.propostas.get(nomeServidor);
        ArrayList<ServidorCloud> servidores = this.servidores.get(nomeServidor);
        
        if(servidores==null) {
            this.lockServidores.unlock();
            this.lockPropostas.unlock();
            return "ServidorInexistente";
        }
        else if(propostas==null) {
            this.lockServidores.unlock();
            this.lockPropostas.unlock();
            return "Naohapropostas";
        }
        else{
            synchronized (servidores){
                this.lockServidores.unlock();
                synchronized (propostas){
                    this.lockPropostas.unlock();
                    for(Proposta p : propostas){
                        res.append("-"+p.getLicitacao() + " " + p.getEmail());
                    }
                    return res.toString();
                }
            }
        }
    }
    
    
    ///*****************************************************//
    public int servidoresDisponiveis(ArrayList<ServidorCloud> servidores){
        int s_disponiveis=0;
        for(ServidorCloud sC : servidores){
            if(!sC.isOcupado()) s_disponiveis++;
        }
        return s_disponiveis;
    }
    
    // Servidor -> ocupado; Eliminar Proposta escolhida; Adicionar reserva a cliente. 
    public String atualizaInformacao(ArrayList<ServidorCloud> servidores,ArrayList<Proposta> propostas){
        double licitacao=0;
        int indiceMelhorProposta=0;
        String email=null;
        ServidorCloud servidorEscolhido=null;
        
        for(ServidorCloud sC : servidores){
            if(!sC.isOcupado()) {
                sC.setOcupado(true);
                servidorEscolhido=sC;
            }
        }
        for(int i=0; i<propostas.size();i++){
            Proposta proposta = propostas.get(i);
            if(proposta.getLicitacao()>licitacao) {
                licitacao=proposta.getLicitacao();
                indiceMelhorProposta=i;
                email=proposta.getEmail();
            }
        }
        
        propostas.remove(indiceMelhorProposta);
        
        return email + "-"+ servidorEscolhido.getNome() + " " + servidorEscolhido.getId();
        
    }
    
   /* public String servidorParaProposta(ArrayList<ServidorCloud> servidores) throws InterruptedException{
        String res=null;
        
        synchronized (servidores){
            while((servidoresDisponiveis(servidores))==0){
                servidores.wait();
            }

            this.lockPropostas.lock();

            ArrayList<Proposta> propostas = this.propostas.get(servidores.get(0).getNome());
            synchronized (propostas) {
                this.lockPropostas.unlock();
                while (propostas == null || propostas.size() == 0) {
                    propostas.wait();
                }
                res = atualizaInformacao(servidores, propostas);
                
            }
        }
        
        return res;
    }
*/

    public String servidorParaProposta(String nomeServidor) throws InterruptedException{
        String res=null;
        this.lockServidores.lock();
        this.lockPropostas.lock();
        ArrayList<ServidorCloud> servidores = this.servidores.get(nomeServidor);
        ArrayList<Proposta> propostas = this.propostas.get(nomeServidor);
            
        this.lock_servidores_propostas.lock();
           try{ 
                this.lockServidores.unlock();
                this.lockPropostas.unlock();
                while((servidores==null || servidoresDisponiveis(servidores)==0 ) || (propostas==null || propostas.size()==0)){
                    System.out.println("Fiquei a dormir!");
                    this.c1.await();
                }
                System.out.println("Acordei");
                res = atualizaInformacao(servidores,propostas);
           }finally{
            this.lock_servidores_propostas.unlock();
           }
        
        return res;
    }
}
