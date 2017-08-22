package servidorfisql.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import servidorfisql.server.Server;

/**
 *
 * @author jorge
 */
public class Consola extends javax.swing.JFrame {

    
    private String prompt;
    private boolean running;
    
    public Consola() {
        initComponents();
        
        this.running = false;
        this.jbRestart.setEnabled(false);
    }

    
    public static void write(String mensaje){
        consola.append(getDateTime() + "@" + Server.user + ">> " + mensaje + "\n");
        consola.setCaretPosition(consola.getDocument().getLength());
    }
    
    
    private static String getDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        consola = new javax.swing.JTextArea();
        jToolBar1 = new javax.swing.JToolBar();
        jbStart = new javax.swing.JButton();
        jbRestart = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        consola.setEditable(false);
        consola.setBackground(java.awt.Color.darkGray);
        consola.setColumns(20);
        consola.setFont(new java.awt.Font("Dialog", 1, 15)); // NOI18N
        consola.setForeground(java.awt.Color.white);
        consola.setRows(5);
        jScrollPane1.setViewportView(consola);

        jToolBar1.setRollover(true);

        jbStart.setText("Start");
        jbStart.setFocusable(false);
        jbStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbStartActionPerformed(evt);
            }
        });
        jToolBar1.add(jbStart);

        jbRestart.setText("Restart");
        jbRestart.setFocusable(false);
        jbRestart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbRestart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbRestart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRestartActionPerformed(evt);
            }
        });
        jToolBar1.add(jbRestart);

        jButton1.setText("Pruebas");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbStartActionPerformed
        if(running){
            running = false;
            Server.detenerServidor();
            this.jbStart.setText("Start");
            this.jbRestart.setEnabled(false);
        }else{
            running = true;
            Server.iniciarServidor();
            this.jbStart.setText("Stop");
            this.jbRestart.setEnabled(true);
        }
        
    }//GEN-LAST:event_jbStartActionPerformed

    private void jbRestartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRestartActionPerformed
        if(running){
            Server.reiniciarServidor();
        }
    }//GEN-LAST:event_jbRestartActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        java.io.File dir = new java.io.File("src/servidorfisql/interpretes/Analizadores/PlyCS/analizador/ast/");
        if(dir.exists())
            Consola.write("Existe");
        else
            Consola.write("No existe");
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(Consola.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Consola.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Consola.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Consola.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Consola().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextArea consola;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbRestart;
    private javax.swing.JButton jbStart;
    // End of variables declaration//GEN-END:variables
}
