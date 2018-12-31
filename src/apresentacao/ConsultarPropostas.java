package apresentacao;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import negocio.Posicao;
import negocio.ServidorProxy;

public class ConsultarPropostas extends javax.swing.JFrame {
    private static ServidorProxy proxy;
    private static String email;
    private static String propostas;
    private static Posicao pos;

    public ConsultarPropostas(ServidorProxy proxy, String email,Posicao pos) {
        this.proxy=proxy;
        this.email=email;
        this.pos=pos;
        desenhaTabela("");
        initComponents();
        this.setLocation(pos.getX(), pos.getY()); 
    }
    
    public ConsultarPropostas(ServidorProxy proxy, String email,String propostas, Posicao pos) {
        this.proxy=proxy;
        this.email=email;
        this.propostas=propostas;
        this.pos=pos;
        desenhaTabela(propostas);
        initComponents();
        this.setLocation(pos.getX(), pos.getY()); 
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label5 = new javax.swing.JLabel();
        sair = new javax.swing.JButton();
        servidor = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        pesquisar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        label5.setFont(new java.awt.Font("Myanmar Sangam MN", 3, 48)); // NOI18N
        label5.setText("Gestão de Servidores");
        label5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        sair.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        sair.setText("Sair");
        sair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sairActionPerformed(evt);
            }
        });

        jLabel1.setText("Servidor:");

        pesquisar.setText("Pesquisar");
        pesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pesquisarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(sair, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label5)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(servidor, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pesquisar)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(label5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(servidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(pesquisar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 197, Short.MAX_VALUE)
                .addComponent(sair, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sairActionPerformed
        // TODO add your handling code here:
        MenuPrincipal menuprincipal = new MenuPrincipal(proxy,email,pos);
        menuprincipal.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_sairActionPerformed

    private void pesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pesquisarActionPerformed
        // TODO add your handling code here:
        
        String nomeServidor = this.servidor.getText();
        if(nomeServidor.length()!=0){
            try {
                String propostas = proxy.consultarPropostas("6",nomeServidor);
                if(!propostas.equals("Naohapropostas") && !propostas.equals("ServidorInexistente")){
                    this.setVisible(false);
                    ConsultarPropostas consultarproposta = new ConsultarPropostas(proxy,email,propostas,pos);
                    consultarproposta.setVisible(true);
                }
                else if(propostas.equals("Naohapropostas")){
                    JOptionPane.showMessageDialog(null, "Não existem propostas para esse servidor");
                    this.servidor.setText("");
                }else {
                    JOptionPane.showMessageDialog(null, "Insira um tipo de servidor correto.");
                    this.servidor.setText("");
                } 
            } catch (IOException ex) {
                Logger.getLogger(ConsultarPropostas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
       
    }//GEN-LAST:event_pesquisarActionPerformed

    public void desenhaTabela(String propostas){
        String[] nomesColunas = {"Licitação", "Email"};
        String[] email_licitacao  = propostas.split("-");
        String[][] dados= new String[email_licitacao.length][2];  

        for (int i = 0; i < email_licitacao.length-1; i++) {
            String[] linha = email_licitacao[i+1].split(" ");
            dados[i] = linha;
        }

        DefaultTableModel listTableModel = new DefaultTableModel(dados, nomesColunas);
        JTable listTable = new JTable(listTableModel);
        listTable.setEnabled(false);
        
        JScrollPane scrollPane = new JScrollPane(listTable);

        JPanel panel = new JPanel();
        panel.add(scrollPane);
        
        panel.setLocation(20, 180);
        panel.setSize(450, 120);

        this.getContentPane().setLayout(null);
        
        this.getContentPane().add(panel, BorderLayout.CENTER);
        
        this.pack();
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ConsultarPropostas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultarPropostas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultarPropostas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultarPropostas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsultarPropostas(proxy,email,pos).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel label5;
    private javax.swing.JButton pesquisar;
    private javax.swing.JButton sair;
    private javax.swing.JTextField servidor;
    // End of variables declaration//GEN-END:variables
}
