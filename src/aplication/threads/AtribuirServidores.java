package aplication.threads;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import aplication.data.ServidoresCloud;
import aplication.data.Utilizadores;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class AtribuirServidores implements Runnable{
    private ServidoresCloud totalservidores;
    private Utilizadores utilizadores;
    private String nomeServidor;
    
    public AtribuirServidores(String nomeServidor,Utilizadores utilizadores,ServidoresCloud totalservidores ){
        this.nomeServidor = nomeServidor;
        this.utilizadores=utilizadores;
        this.totalservidores=totalservidores;
    }
    
    
    @Override
    public void run() {
        while(true){
            try {
                
                String res = this.totalservidores.atribuirServidoresPropostas(nomeServidor);
                String split[] = res.split("-");
                String email = split[0];
                String reserva = split[1];
                
                String partirReserva[] = reserva.split(" ");
                String idReserva = partirReserva[0] + " " + partirReserva[1];
                String licitacao = partirReserva[2];
                
                this.utilizadores.adicionarReservas(email, idReserva);
                

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                StringBuilder sB = new StringBuilder();
                sB.append("$Foi-lhe atribuído um servidor do tipo "+partirReserva[0]);
                sB.append(", com ID = "+partirReserva[1]);
                sB.append(" ( Data = "+ sdf.format(timestamp) +" )");
                String notificacoes = sB.toString();
                
                this.utilizadores.inserirNotificacoes(email,notificacoes);
                
                 Thread descontarSaldo = new Thread(
                            new DescontaSaldo(
                                    this.utilizadores,
                                    email,
                                    Double.parseDouble(licitacao),
                                    this.totalservidores,
                                    nomeServidor,
                                    idReserva)
                    );

                    descontarSaldo.start();
                
            } catch (InterruptedException ex) {
                Logger.getLogger(AtribuirServidores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
