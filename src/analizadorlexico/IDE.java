package analizadorlexico;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class IDE extends javax.swing.JFrame {

    /**
     * Creates new form IDE
     */
    public IDE() {
        initComponents();

        jFileChooserAbrir.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".upl");
            }

            @Override
            public String getDescription() {
                return "Archivo Úpsilon (*.upl)";
            }
        });
        jFileChooserGuardar.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".upl");
            }

            @Override
            public String getDescription() {
                return "Archivo Úpsilon (*.upl)";
            }
        });
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
        StringTokenizer lineas = new StringTokenizer(codigoFuente, "\n");
        LinkedList<String> listaTokens = new LinkedList<>();
        while (lineas.hasMoreTokens()) {
            String linea = lineas.nextToken();
            StringTokenizer tokens = new StringTokenizer(linea, "=+*/~:()", true);
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                StringTokenizer masTokens = new StringTokenizer(token);
                while (masTokens.hasMoreTokens()) {
                    String ultimoToken = masTokens.nextToken();
                    if (ultimoToken.startsWith("'")) {
                        String texto = ultimoToken;
                        String siguienteToken = "";
                        while (!siguienteToken.endsWith("'") && masTokens.hasMoreTokens()) {
                            siguienteToken = masTokens.nextToken();
                            texto += " " + siguienteToken;
                        }
                        listaTokens.add(texto);
                    } else if (ultimoToken.startsWith("¿")) {
                        while (masTokens.hasMoreTokens() && !masTokens.nextToken().endsWith("?")) { //Descartamos todos los comentarios
                        }
                    } else if (ultimoToken.contains("<>")) {
                        StringTokenizer tokenizer = new StringTokenizer(ultimoToken, "<>", true);
                        String tokenizerSiguiente = "";
                        while (tokenizerSiguiente.compareTo("<") != 0 && tokenizer.hasMoreTokens()) {
                            tokenizerSiguiente = tokenizer.nextToken();
                            listaTokens.add(tokenizerSiguiente);
                        }
                        listaTokens.add(tokenizerSiguiente + ">");
                        tokenizer.nextToken();
                        while (tokenizer.hasMoreTokens()) {
                            listaTokens.add(tokenizer.nextToken());
                        }
                    } else if (ultimoToken.contains("->")) {
                        StringTokenizer tokenizer = new StringTokenizer(ultimoToken, "->", true);
                        String tokenizerSiguiente = "";
                        while (tokenizerSiguiente.compareTo("-") != 0 && tokenizer.hasMoreTokens()) {
                            tokenizerSiguiente = tokenizer.nextToken();
                        }
                        listaTokens.add(tokenizerSiguiente + ">");
                        tokenizer.nextToken();
                        while (tokenizer.hasMoreTokens()) {
                            listaTokens.add(tokenizer.nextToken());
                        }
                    } else if (ultimoToken.contains("-")) {
                        StringTokenizer tokenizer = new StringTokenizer(ultimoToken, "-", true);
                        while (tokenizer.hasMoreTokens()) {
                            listaTokens.add(tokenizer.nextToken());
                        }
                    } else {
                        listaTokens.add(ultimoToken);
                    }
                }
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
            if (!ubicacion.endsWith(".upl")) {
                ubicacion += ".upl";
            }
            guardarArchivo(jTextAreaCodigo.getText(), ubicacion);
            JOptionPane.showMessageDialog(null, "Se guardó en la dirección \"" + ubicacion + "\"", "Archivo guardado", JOptionPane.INFORMATION_MESSAGE);
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
