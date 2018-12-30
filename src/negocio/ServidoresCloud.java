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
    //private Condition not_Servidores = lockServidores.newCondition();
    //private Condition not_Propostas = lockPropostas.newCondition();
    
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
                            return nomeServidor+" "+servidorCloud.getId();
                        }
                }
                return "TodosServidoresIndisponiveis";
            }
            else{
                return "ServidorInexistente";
            }
        }
    }

    public String reservarLeilao(String nomeServidor, String email, double licitacao){
        ArrayList<ServidorCloud> servidorClouds =null;
        boolean flag=false;
        String res=null;
        //Um comentário aí pa ver depois. 
        this.lockServidores.lock();
            servidorClouds = this.servidores.get(nomeServidor);
        synchronized (servidorClouds){
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
                    registarProposta(nomeServidor,email,licitacao);
                    return "ServidoresOcupados";
                }
                return  "LicitacaoBaixa";
            }
            else return "ServidorInexistente";
        }
    }

    public void registarProposta(String nomeServidor, String email, double licitacao){

        Proposta proposta = new Proposta(licitacao,email);
        boolean flag=false;
        ArrayList<Proposta> p = null;

        this.lockPropostas.lock();
        if(this.propostas.isEmpty() || !this.propostas.containsKey(nomeServidor)){
            try{
                ArrayList<Proposta> propostas = new ArrayList<>();
                propostas.add(proposta);
                this.propostas.put(nomeServidor,propostas);
                flag=true;
            }finally{
                this.lockPropostas.unlock();
            }
        }

        if(flag==false){
            p = this.propostas.get(nomeServidor);
            synchronized (p){
                this.lockPropostas.unlock();
                
                Proposta pr = verficarProposta(email,p);

                if(pr!=null){  p.remove(pr); }

                p.add(proposta);
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
            this.lockServidores.unlock();
            for(ServidorCloud sC : servidores){
                if(sC.isLeilao() && sC.getId()==id){
                    sC.setLeilao(false);
                    sC.setOcupado(false);
                }else if(sC.getId()==id){
                    sC.setOcupado(false);
                }
            }
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
        
        synchronized (servidores){
            this.lockServidores.unlock();
            synchronized (propostas){
                this.lockPropostas.unlock();
                    if(propostas!=null && servidores!=null){
                        for(Proposta p : propostas){
                            res.append("-"+p.getLicitacao() + " " + p.getEmail());
                        }
                    }else if(servidores!=null){
                    return "Naohapropostas";
            
                    }else return "ServidorInexistente";
        
                    return res.toString();
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
            }
        }
        
        propostas.remove(indiceMelhorProposta);
        
        return servidorEscolhido.getNome() + " " + servidorEscolhido.getId();
        
    }
    
    public String servidorParaProposta(ArrayList<ServidorCloud> servidores) throws InterruptedException{
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


}
