public class ServidorCloud {
    private String nome;
    private double taxaFixa;
    private double taxaLeiloada;
    private double licitacaoMinima;
    private int id;
    private boolean ocupado;
    private boolean leilao;


    public ServidorCloud(String nome, double taxaFixa, int id, boolean ocupado, boolean leilao, double licitacaoMinima) {
        this.nome = nome;
        this.taxaFixa = taxaFixa;
        this.id = id;
        this.ocupado = ocupado;
        this.leilao = leilao;
        this.taxaLeiloada=0.0;
        this.licitacaoMinima = licitacaoMinima;
    }

    public double getLicitacaoMinima() {
        return licitacaoMinima;
    }

    public void setLicitacaoMinima(double licitacaoMinima) {
        this.licitacaoMinima = licitacaoMinima;
    }

    public String getNome() {
        return nome;
    }

    public double getTaxaFixa() {
        return taxaFixa;
    }

    public double getTaxaLeiloada() {
        return taxaLeiloada;
    }

    public void setTaxaLeiloada(double taxaLeiloada) {
        this.taxaLeiloada = taxaLeiloada;
    }

    public int getId() {
        return id;
    }

    public boolean isOcupado() {
        return ocupado;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTaxaFixa(double taxaFixa) {
        this.taxaFixa = taxaFixa;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public boolean isLeilao() {
        return leilao;
    }

    public void setLeilao(boolean leilao) {
        this.leilao = leilao;
    }
}
