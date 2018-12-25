package negocio;

import java.util.ArrayList;
import java.util.HashMap;

public class ServidoresCloud {
    private HashMap<String, ArrayList<ServidorCloud>> servidores;
    private HashMap<String, ArrayList<Proposta>> propostas;
    private int proxId;

    public ServidoresCloud() {
        this.servidores = new HashMap<String,ArrayList<ServidorCloud>>();
        this.propostas = new HashMap<String, ArrayList<Proposta>>();
        this.proxId=0;
    }

    public HashMap<String, ArrayList<ServidorCloud>> getServidores(){
        return this.servidores;
    }

    public void registarServidor(String nome, double taxa, double licitacaoMinima){
        ServidorCloud servidorCloud = new ServidorCloud(nome,taxa,proxId,false,false,licitacaoMinima);

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

        ArrayList<ServidorCloud> servidorClouds = this.servidores.get(nomeServidor);
        
        if(servidorClouds!=null){
            for(ServidorCloud servidorCloud : servidorClouds){
                if(!servidorCloud.isOcupado()) {
                    servidorCloud.setOcupado(true);
                    return  nomeServidor+" "+servidorCloud.getId();
                }
            }
            return "TodosServidoresIndisponiveis";
        }
        else{
            return "ServidorInexistente";
        }

    }

    public String reservarLeilao(String nomeServidor, String email, double licitacao){

        ArrayList<ServidorCloud> servidorClouds = this.servidores.get(nomeServidor);
        
        if(servidorClouds!=null){
            double licitMin;
            boolean tentouLicitar = false;
            for(ServidorCloud servidorCloud : servidorClouds){
                if(!servidorCloud.isOcupado()) {
                    tentouLicitar = true;
                    licitMin = servidorCloud.getLicitacaoMinima();
                    if(licitacao >= licitMin) {
                        servidorCloud.setTaxaLeiloada(licitacao);
                        servidorCloud.setLeilao(true);
                        servidorCloud.setOcupado(true);
                        return nomeServidor+" "+servidorCloud.getId();
                    }
                }
            }

            if(!tentouLicitar) {
                System.out.println("ENTREI");
                registarProposta(nomeServidor,email,licitacao);
                return "ServidoresOcupados";
            }

            return  "LicitacaoBaixa";
        }
        else return "ServidorInexistente";
    }

    public void registarProposta(String nomeServidor, String email, double licitacao){

        Proposta proposta = new Proposta(licitacao,email);

        if(this.propostas.isEmpty() || !this.propostas.containsKey(nomeServidor)){
            ArrayList<Proposta> propostas = new ArrayList<>();
            propostas.add(proposta);
            this.propostas.put(nomeServidor,propostas);
        }
        else {
            ArrayList<Proposta> p = this.propostas.get(nomeServidor);
            Proposta pr = verficarProposta(email,p);
            if(pr!=null){
                p.remove(pr);
            }

            p.add(proposta);
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
    }
    
    public double taxaServidor(String nomeServidor){
        return this.servidores.get(nomeServidor).get(0).getTaxaFixa();
    }

    public void desocupaServidor(String nomeServidor,int id){
        ArrayList<ServidorCloud> servidores = this.servidores.get(nomeServidor);
        for(ServidorCloud sC : servidores){
            if(sC.isLeilao() && sC.getId()==id){
                sC.setLeilao(false);
                sC.setOcupado(false);
            }else if(sC.getId()==id){
                sC.setOcupado(false);
            }
        }
    }
    
    public String servidorPorId(int id){
        for(ArrayList<ServidorCloud> sC : this.servidores.values()){
            for(ServidorCloud s : sC){
                if(s.getId()==id) return s.getNome();
            }
        }
        return null;
    }
    public String propostasPorServidor(String nomeServidor){
        StringBuilder res = new StringBuilder();
        ArrayList<Proposta> propostas = this.propostas.get(nomeServidor);
        ArrayList<ServidorCloud> servidores = this.servidores.get(nomeServidor);
        
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
