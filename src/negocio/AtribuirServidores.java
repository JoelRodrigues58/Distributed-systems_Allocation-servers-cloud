package negocio;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AtribuirServidores implements Runnable{
    private ArrayList<ServidorCloud> servidores;
    private ServidoresCloud totalservidores;
    private Utilizadores utilizadores;
    
    public AtribuirServidores(ArrayList<ServidorCloud> servidores,Utilizadores utilizadores,ServidoresCloud totalservidores ){
        this.servidores=servidores;
        this.utilizadores=utilizadores;
        this.totalservidores=totalservidores;
    }
    
    
    @Override
    public void run() {
        while(true){
            try {
                String idReserva = this.totalservidores.servidorParaProposta(servidores);
                String split[] = idReserva.split("-");
                this.utilizadores.adicionarReservas(split[0], split[1]);
                System.out.println("ATRIBUIR SERVER: " + split[1]);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(AtribuirServidores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
