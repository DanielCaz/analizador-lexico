package analizadorlexico;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Resultados extends javax.swing.JFrame {

    private static LinkedList<String> listaTokens;
    private final LinkedList<Token> objetosTokens;
    private final Hashtable<String, Token> simbolos;

    /**
     * Creates new form Resultados
     */
    public Resultados(LinkedList<String> listaTokens) {
        initComponents();
        Resultados.listaTokens = listaTokens;
        objetosTokens = new LinkedList<>();

        simbolos = getSimbolos();
        DefaultTableModel modelSimbolos = (DefaultTableModel) jTableSimbolos.getModel();

        DefaultTableModel model = (DefaultTableModel) jTableTokens.getModel();
        String[] filas = (String[]) listaTokens.toArray(new String[listaTokens.size()]);
        for (String fila : filas) {
            if (simbolos.containsKey(fila)) {
                Token token = simbolos.get(fila);
                objetosTokens.add(token);
                model.addRow(new String[]{fila, token.getIdentificador(), token.getCategoria()});
            } else if (fila.matches("^(\\$\\w+)$")) {
                Token token = new Token(fila, "V", "VAR");
                model.addRow(token.toStringArray());
                if (!simbolos.containsKey(fila)) {
                    simbolos.put(fila, token);
                    modelSimbolos.addRow(token.toStringArray());
                }
                objetosTokens.add(token);
            } else if (fila.matches("^('.*')$")) {
                Token token = new Token(fila, "T", "TEX");
                model.addRow(token.toStringArray());
                if (!simbolos.containsKey(fila)) {
                    simbolos.put(fila, token);
                    modelSimbolos.addRow(token.toStringArray());
                }
                objetosTokens.add(token);
            } else if (fila.matches("^(\\d+|(\\d+\\.\\d+))$")) {
                Token token = new Token(fila, "N", "NUM");
                model.addRow(token.toStringArray());
                if (!simbolos.containsKey(fila)) {
                    simbolos.put(fila, token);
                    modelSimbolos.addRow(token.toStringArray());
                }
                objetosTokens.add(token);
            } else {
                JOptionPane.showMessageDialog(null, "Error de análisis: Símbolo \"" + fila + "\" no reconocido", "Error de análisis", JOptionPane.WARNING_MESSAGE);

                WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
                setVisible(false);
                dispose();

                break;
            }
        }
        
        analisisSintacticoTabular();
    }

    private void analisisSintacticoTabular() {
        Hashtable<String, Hashtable<String, String>> tabla = getTablaSintacticaTabular();
        //TODO: Hacer análisis sintáctico
    }
    
    private Hashtable<String, Hashtable<String, String>> getTablaSintacticaTabular() {
        Hashtable<String, Hashtable<String, String>> table = new Hashtable<>();
        
        Hashtable<String, String> colProg = new Hashtable<>();
        colProg.put("programStart", "programStart sent programEnd");
        table.put("prog", colProg);
        
        Hashtable<String, String> colSent = new Hashtable<>();
        colSent.put("v", "asign sent");
        colSent.put("compare", "comp sent");
        colSent.put("loop", "cic sent");
        colSent.put("print", "imp sent");
        colSent.put("input", "leer sent");
        table.put("sent", colSent);
        
        Hashtable<String, String> colDecl = new Hashtable<>();
        colDecl.put("decl", "var v : tipos vals");
        table.put("decl", colDecl);
        
        Hashtable<String, String> colVals = new Hashtable<>();
        colVals.put("->", "-> valsAux");
        colVals.put("v", "");
        colVals.put("var", "");
        colVals.put(":", "");
        colVals.put("compare", "");
        colVals.put("endCompare", "");
        colVals.put("loop", "");
        colVals.put("endLoop", "");
        colVals.put("print", "");
        colVals.put("input", "");
        colVals.put("programEnd", "");
        table.put("vals", colVals);
        
        Hashtable<String, String> colValsAux = new Hashtable<>();
        colValsAux.put("op", "op");
        colValsAux.put("N", "op");
        colValsAux.put("F", "op");
        colValsAux.put("V", "op");
        colValsAux.put("T", "T");
        table.put("valsAux", colValsAux);
        
        Hashtable<String, String> colAsign = new Hashtable<>();
        colAsign.put("v", "v -> valsAux");
        table.put("asign", colAsign);
        
        Hashtable<String, String> colImp = new Hashtable<>();
        colImp.put("print", "print cad");
        table.put("imp", colImp);
        
        Hashtable<String, String> colCad = new Hashtable<>();
        colCad.put("(", "op cadAux");
        colCad.put("N", "op cadAux");
        colCad.put("F", "op cadAux");
        colCad.put("V", "op cadAux");
        colCad.put("T", "T cadAux");
        table.put("cad", colCad);
        
        Hashtable<String, String> colCadAux = new Hashtable<>();
        colCadAux.put("~", "~ cad");
        colCadAux.put("v", "");
        colCadAux.put("var", "");
        colCadAux.put(":", "");
        colCadAux.put("compare", "");
        colCadAux.put("endCompare", "");
        colCadAux.put("loop", "");
        colCadAux.put("endLoop", "");
        colCadAux.put("print", "");
        colCadAux.put("input", "");
        colCadAux.put("programEnd", "");
        table.put("cadAux", colCadAux);
        
        Hashtable<String, String> colOp = new Hashtable<>();
        colOp.put("(", "(op) opAux");
        colOp.put("N", "nums opAux");
        colOp.put("F", "nums opAux");
        colOp.put("V", "nums opAux");
        table.put("op", colOp);
        
        Hashtable<String, String> colOpAux = new Hashtable<>();
        colOpAux.put("v", "");
        colOpAux.put("var", "");
        colOpAux.put(":", "");
        colOpAux.put("compare", "");
        colOpAux.put("endCompare", "");
        colOpAux.put("loop", "");
        colOpAux.put("endLoop", "");
        colOpAux.put("print", "");
        colOpAux.put("input", "");
        colOpAux.put("programEnd", "");
        colOpAux.put(")", "");
        colOpAux.put("<", "");
        colOpAux.put(">", "");
        colOpAux.put("<>", "");
        colOpAux.put("=", "");
        colOpAux.put("~", "");
        colOpAux.put("+", "ops op");
        colOpAux.put("-", "ops op");
        colOpAux.put("*", "ops op");
        colOpAux.put("/", "ops op");
        table.put("opAux", colOpAux);
        
        Hashtable<String, String> colOps = new Hashtable<>();
        colOps.put("+", "+");
        colOps.put("-", "-");
        colOps.put("*", "*");
        colOps.put("/", "/");
        table.put("ops", colOps);
        
        Hashtable<String, String> colNums = new Hashtable<>();
        colNums.put("N", "N");
        colNums.put("F", "F");
        colNums.put("V", "V");
        table.put("nums", colNums);
        
        Hashtable<String, String> colTipos = new Hashtable<>();
        colTipos.put("I", "I");
        colTipos.put("F", "F");
        colTipos.put("S", "S");
        table.put("tipos", colTipos);
        
        Hashtable<String, String> colLeer = new Hashtable<>();
        colLeer.put("input", "input -> v");
        table.put("leer", colLeer);
        
        Hashtable<String, String> colComp = new Hashtable<>();
        colComp.put("compare", "compare op opcomps op : sent compAux endCompare");
        table.put("comp", colComp);
        
        Hashtable<String, String> colCompAux = new Hashtable<>();
        colCompAux.put(":", ": else : sent");
        table.put("compAux", colCompAux);
        
        Hashtable<String, String> colCic = new Hashtable<>();
        colCic.put("loop", "loop op opcomps op : sent endLoop");
        table.put("cic", colCic);
        
        Hashtable<String, String> colOpcomps = new Hashtable<>();
        colOpcomps.put("<", "<");
        colOpcomps.put(">", ">");
        colOpcomps.put("<>", "<>");
        colOpcomps.put("=", "=");
        table.put("opcomps", colOpcomps);
        
        return table;
    }

    private Hashtable<String, Token> getSimbolos() {
        Hashtable<String, Token> simbolos = new Hashtable<>();
        simbolos.put("Int", new Token("Int", "i", "PR"));
        simbolos.put("Float", new Token("Float", "f", "PR"));
        simbolos.put("Str", new Token("Str", "s", "PR"));
        simbolos.put("var", new Token("var", "var", "PR"));
        simbolos.put("print", new Token("print", "print", "PR"));
        simbolos.put("input", new Token("input", "input", "PR"));
        simbolos.put("compare", new Token("compare", "compare", "PR"));
        simbolos.put("else", new Token("else", "else", "PR"));
        simbolos.put("endCompare", new Token("endCompare", "endCompare", "PR"));
        simbolos.put("loop", new Token("loop", "loop", "PR"));
        simbolos.put("endLoop", new Token("endLoop", "endLoop", "PR"));
        simbolos.put("programStart", new Token("programStart", "programStart", "PR"));
        simbolos.put("programEnd", new Token("programEnd", "programEnd", "PR"));
        simbolos.put("~", new Token("~", "~", "SEP"));
        simbolos.put(":", new Token(":", ":", "SEP"));
        simbolos.put("+", new Token("+", "+", "OP"));
        simbolos.put("-", new Token("-", "-", "OP"));
        simbolos.put("*", new Token("*", "*", "OP"));
        simbolos.put("/", new Token("/", "*", "OP"));
        simbolos.put("->", new Token("->", "->", "OP"));
        simbolos.put("<>", new Token("<>", "<>", "OP"));
        simbolos.put("=", new Token("=", "=", "OP"));
        simbolos.put("<", new Token("<", "<", "OP"));
        simbolos.put(">", new Token(">", ">", "OP"));
        return simbolos;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelSimbolos = new javax.swing.JLabel();
        jScrollPaneSimbolos = new javax.swing.JScrollPane();
        jTableSimbolos = new javax.swing.JTable();
        jLabelTokens = new javax.swing.JLabel();
        jScrollPaneTokens = new javax.swing.JScrollPane();
        jTableTokens = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabelSimbolos.setText("Tabla Símbolos");

        jTableSimbolos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Int", "I", "PR"},
                {"Float", "F", "PR"},
                {"Str", "S", "PR"},
                {"var", "var", "PR"},
                {"print", "print", "PR"},
                {"input", "input", "PR"},
                {"compare", "compare", "PR"},
                {"else", "else", "PR"},
                {"endCompare", "endCompare", "PR"},
                {"loop", "loop", "PR"},
                {"endLoop", "endLoop", "PR"},
                {"programStart", "programStart", "PR"},
                {"programEnd", "programEnd", "PR"},
                {"~", "~", "SEP"},
                {":", ":", "SEP"},
                {"+", "+", "OP"},
                {"-", "-", "OP"},
                {"*", "*", "OP"},
                {"/", "/", "OP"},
                {"->", "->", "OP"},
                {"<>", "<>", "OP"},
                {"=", "=", "OP"},
                {"<", "<", "OP"},
                {">", ">", "OP"}
            },
            new String [] {
                "Lexema", "Identificador", "Categoría"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableSimbolos.setMinimumSize(new java.awt.Dimension(0, 200));
        jScrollPaneSimbolos.setViewportView(jTableSimbolos);

        jLabelTokens.setText("Tabla Tokens");

        jTableTokens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Lexema", "Identificador", "Categoría"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTokens.setMinimumSize(new java.awt.Dimension(0, 200));
        jScrollPaneTokens.setViewportView(jTableTokens);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneSimbolos, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSimbolos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneTokens, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTokens))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSimbolos)
                    .addComponent(jLabelTokens))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneSimbolos)
                    .addComponent(jScrollPaneTokens))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(Resultados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Resultados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Resultados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Resultados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Resultados(listaTokens).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelSimbolos;
    private javax.swing.JLabel jLabelTokens;
    private javax.swing.JScrollPane jScrollPaneSimbolos;
    private javax.swing.JScrollPane jScrollPaneTokens;
    private javax.swing.JTable jTableSimbolos;
    private javax.swing.JTable jTableTokens;
    // End of variables declaration//GEN-END:variables
}
