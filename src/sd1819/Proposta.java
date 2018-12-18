public class Proposta {

    private double licitacao;
    private String email;


    public Proposta(double licitacao,String email) {
        this.licitacao = licitacao;
        this.email=email;
    }

    public double getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(double licitacao) {
        this.licitacao = licitacao;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
