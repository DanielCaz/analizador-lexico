package analizadorlexico;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class IDE extends javax.swing.JFrame {

    /**
     * Creates new form IDE
     */
    public IDE() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooserAbrir = new javax.swing.JFileChooser();
        jFileChooserGuardar = new javax.swing.JFileChooser();
        jScrollPaneCodigo = new javax.swing.JScrollPane();
        jTextAreaCodigo = new javax.swing.JTextArea();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuArchivo = new javax.swing.JMenu();
        jMenuItemAbrir = new javax.swing.JMenuItem();
        jMenuItemGuardar = new javax.swing.JMenuItem();
        jMenuItemArchivoEjecutar = new javax.swing.JMenuItem();

        jFileChooserAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooserAbrirActionPerformed(evt);
            }
        });

        jFileChooserGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooserGuardarActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IDE");
        setMinimumSize(new java.awt.Dimension(200, 200));

        jScrollPaneCodigo.setMinimumSize(new java.awt.Dimension(0, 0));

        jTextAreaCodigo.setColumns(20);
        jTextAreaCodigo.setRows(5);
        jTextAreaCodigo.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPaneCodigo.setViewportView(jTextAreaCodigo);

        jMenuArchivo.setMnemonic(KeyEvent.VK_A);
        jMenuArchivo.setText("Archivo");

        jMenuItemAbrir.setMnemonic(KeyEvent.VK_O);
        jMenuItemAbrir.setText("Abrir");
        jMenuItemAbrir.setToolTipText("Alt + O");
        jMenuItemAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAbrirActionPerformed(evt);
            }
        });
        jMenuArchivo.add(jMenuItemAbrir);

        jMenuItemGuardar.setMnemonic(KeyEvent.VK_S);
        jMenuItemGuardar.setText("Guardar");
        jMenuItemGuardar.setToolTipText("Alt + G");
        jMenuItemGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuardarActionPerformed(evt);
            }
        });
        jMenuArchivo.add(jMenuItemGuardar);

        jMenuItemArchivoEjecutar.setMnemonic(KeyEvent.VK_R);
        jMenuItemArchivoEjecutar.setText("Ejecutar");
        jMenuItemArchivoEjecutar.setToolTipText("Alt + R");
        jMenuItemArchivoEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemArchivoEjecutarActionPerformed(evt);
            }
        });
        jMenuArchivo.add(jMenuItemArchivoEjecutar);

        jMenuBar.add(jMenuArchivo);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemArchivoEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemArchivoEjecutarActionPerformed
        String codigoFuente = jTextAreaCodigo.getText();
        StringTokenizer tokens = new StringTokenizer(codigoFuente);
        LinkedList<String> listaTokens = new LinkedList<>();

        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.startsWith("'")) {
                String texto = token;
                String siguienteToken = "";
                while (!siguienteToken.endsWith("'") && tokens.hasMoreTokens()) {
                    siguienteToken = tokens.nextToken();
                    texto += " " + siguienteToken;
                }
                listaTokens.add(texto);
            } else if (token.startsWith("¿")) {
                String comentario = token;
                String siguienteToken = "";
                while (!siguienteToken.endsWith("?") && tokens.hasMoreTokens()) {
                    siguienteToken = tokens.nextToken();
                    comentario += " " + siguienteToken;
                }
                listaTokens.add(comentario);
            } else if (token.compareTo("-") == 0) {
                String guion = token;
                if (tokens.hasMoreTokens()) {
                    String siguienteToken = tokens.nextToken();
                    if (siguienteToken.compareTo(">") == 0) {
                        listaTokens.add(guion + siguienteToken);
                    } else {
                        listaTokens.add(guion);
                        listaTokens.add(siguienteToken);
                    }
                }
            } else if (token.compareTo("<") == 0) {
                String menorQue = token;
                if (tokens.hasMoreTokens()) {
                    String siguienteToken = tokens.nextToken();
                    if (siguienteToken.compareTo(">") == 0) {
                        listaTokens.add(menorQue + siguienteToken);
                    } else {
                        listaTokens.add(menorQue);
                        listaTokens.add(siguienteToken);
                    }
                }
            } else if (token.compareTo("?") == 0) {
                String interrogacion = token;
                if (tokens.hasMoreTokens()) {
                    String siguienteToken = tokens.nextToken();
                    if (siguienteToken.compareTo(">") == 0) {
                        listaTokens.add(interrogacion + siguienteToken);
                    } else {
                        listaTokens.add(interrogacion);
                        listaTokens.add(siguienteToken);
                    }
                }
            } else if (token.contains(":")) {
                StringTokenizer subToken = new StringTokenizer(token, ":", true);
                while (subToken.hasMoreTokens()) {
                    listaTokens.add(subToken.nextToken());
                }
            } else {
                listaTokens.add(token);
            }
        }

        Resultados resultados = new Resultados(listaTokens);
        resultados.setLocationRelativeTo(null);
        resultados.setVisible(true);
    }//GEN-LAST:event_jMenuItemArchivoEjecutarActionPerformed

    private String abrirArchivo(String ubicacion) {
        String codigo = "";
        try {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(ubicacion))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    codigo += line + "\n";
                }
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error: " + e);
        }
        return codigo;
    }


    private void jMenuItemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAbrirActionPerformed
        jFileChooserAbrir.showOpenDialog(null);
    }//GEN-LAST:event_jMenuItemAbrirActionPerformed

    private void jFileChooserAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooserAbrirActionPerformed
        if (evt.getActionCommand().compareTo("ApproveSelection") == 0) {
            String ubicacion = jFileChooserAbrir.getSelectedFile().getPath();
            String codigo = abrirArchivo(ubicacion);
            jTextAreaCodigo.setText(codigo);
        }
    }//GEN-LAST:event_jFileChooserAbrirActionPerformed

    private void jMenuItemGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGuardarActionPerformed
        jFileChooserGuardar.showSaveDialog(null);
    }//GEN-LAST:event_jMenuItemGuardarActionPerformed

    private void jFileChooserGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooserGuardarActionPerformed
        if (evt.getActionCommand().compareTo("ApproveSelection") == 0) {
            String ubicacion = jFileChooserGuardar.getSelectedFile().getPath();
            guardarArchivo(jTextAreaCodigo.getText(), ubicacion);
        }
    }//GEN-LAST:event_jFileChooserGuardarActionPerformed

    private void guardarArchivo(String texto, String ubicacion) {
        try {
            FileWriter file = new FileWriter(ubicacion);

            try (BufferedWriter output = new BufferedWriter(file)) {
                output.write(texto);
                output.flush();
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

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
            java.util.logging.Logger.getLogger(IDE.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IDE.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IDE.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IDE.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IDE().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooserAbrir;
    private javax.swing.JFileChooser jFileChooserGuardar;
    private javax.swing.JMenu jMenuArchivo;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuItemAbrir;
    private javax.swing.JMenuItem jMenuItemArchivoEjecutar;
    private javax.swing.JMenuItem jMenuItemGuardar;
    private javax.swing.JScrollPane jScrollPaneCodigo;
    private javax.swing.JTextArea jTextAreaCodigo;
    // End of variables declaration//GEN-END:variables
}
