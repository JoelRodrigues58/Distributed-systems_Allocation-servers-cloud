package sd1819;

public class Servidor {
    private String nome;
    private double taxa;
    private int id;
    private boolean ocupado;

    public Servidor(String nome, double taxa, int id, boolean ocupado) {
        this.nome = nome;
        this.taxa = taxa;
        this.id = id;
        this.ocupado = ocupado;
    }

    public String getNome() {
        return nome;
    }

    public double getTaxa() {
        return taxa;
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

    public void setTaxa(double taxa) {
        this.taxa = taxa;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }
    
    
    
}
