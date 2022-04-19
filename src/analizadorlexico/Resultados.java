package analizadorlexico;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Stack;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class Resultados extends javax.swing.JFrame {

    private static LinkedList<String> listaTokens;
    private final LinkedList<Token> entrada;
    private final Hashtable<String, Token> simbolos;

    /**
     * Creates new form Resultados
     */
    public Resultados(LinkedList<String> listaTokens) {
        initComponents();
        Resultados.listaTokens = listaTokens;
        entrada = new LinkedList<>();
        boolean error = false;

        simbolos = getSimbolos();
        DefaultTableModel modelSimbolos = (DefaultTableModel) jTableSimbolos.getModel();

        DefaultTableModel model = (DefaultTableModel) jTableTokens.getModel();
        String[] filas = (String[]) listaTokens.toArray(new String[listaTokens.size()]);
        for (String fila : filas) {
            if (simbolos.containsKey(fila)) {
                Token token = simbolos.get(fila);
                entrada.add(token);
                model.addRow(new String[]{fila, token.getIdentificador(), token.getCategoria()});
            } else if (fila.matches("^(\\$\\w+)$")) {
                Token token = new Token(fila, "V", "VAR");
                model.addRow(token.toStringArray());
                if (!simbolos.containsKey(fila)) {
                    simbolos.put(fila, token);
                    modelSimbolos.addRow(token.toStringArray());
                }
                entrada.add(token);
            } else if (fila.matches("^('.*')$")) {
                Token token = new Token(fila, "T", "TEX");
                model.addRow(token.toStringArray());
                if (!simbolos.containsKey(fila)) {
                    simbolos.put(fila, token);
                    modelSimbolos.addRow(token.toStringArray());
                }
                entrada.add(token);
            } else if (fila.matches("^(\\d+|(\\d+\\.\\d+))$")) {
                Token token = new Token(fila, "N", "NUM");
                model.addRow(token.toStringArray());
                if (!simbolos.containsKey(fila)) {
                    simbolos.put(fila, token);
                    modelSimbolos.addRow(token.toStringArray());
                }
                entrada.add(token);
            } else {
                JOptionPane.showMessageDialog(null, "Error de análisis: Símbolo \"" + fila + "\" no reconocido", "Error de análisis", JOptionPane.WARNING_MESSAGE);

                error = true;

                WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
                setVisible(false);
                dispose();

                break;
            }
        }

        if (!error) {
            analisisSintacticoTabular();
        }
    }

    private void analisisSintacticoTabular() {
        Hashtable<String, Hashtable<String, String>> tabla = getTablaSintacticaTabular();
        /**
         * Comparar si primero de tokens y primero de tabla son iguales =
         * eliminar token de ambos caso contrario consultar tabla y reemplazar
         * primero del stack con prod encontrada y en caso de que la prod sea
         * nula = mala sintaxis
         *
         * pila -> filas entrada -> columnas
         */

        Stack<String> pila = new Stack<>();
        pila.push("$");
        pila.push("prog");
        boolean error = false;
        DefaultTreeModel modelArbol = (DefaultTreeModel) jTreeArbol.getModel();
        DefaultMutableTreeNode nodosTokens = new DefaultMutableTreeNode("prog");

        while (!entrada.isEmpty()) {
            System.out.println("\n"
                    + "Pila: " + pila
                    + "\nEntrada: " + Helpers.entradaToString(entrada));

            System.out.printf("Comparando \"%s\" con \"%s\"%n", entrada.getFirst().getIdentificador(), pila.peek());
            if (entrada.getFirst().getIdentificador().compareTo(pila.peek()) == 0) {
                System.out.printf("Removiendo \"%s\" de la pila y entrada%n", pila.peek());
                entrada.removeFirst();
                pila.pop();

                continue;
            }

            System.out.printf("Consultando fila \"%s\" y columna \"%s\" de la tabla sintáctica%n", pila.peek(), entrada.getFirst().getIdentificador());
            Hashtable<String, String> fila = tabla.get(pila.peek());
            if (fila != null) {
                if (fila.containsKey(entrada.getFirst().getIdentificador())) {
                    System.out.printf("Cambiando \"%s\" por \"%s\" en la pila%n", pila.peek(), fila.get(entrada.getFirst().getIdentificador()));
                    String copia = pila.peek();
                    pila.pop();
                    String[] produccion = fila.get(entrada.getFirst().getIdentificador()).split(" ");

                    Enumeration hijos = nodosTokens.depthFirstEnumeration();
                    while (hijos.hasMoreElements()) {
                        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) hijos.nextElement();
                        if (nodo.toString().compareTo(copia) == 0 && nodo.isLeaf()) {
                            for (String prod : produccion) {
                                if (prod.length() > 0) {
                                    nodo.add(new DefaultMutableTreeNode(prod));
                                } else {
                                    nodo.add(new DefaultMutableTreeNode("ε"));
                                }
                            }
                            break;
                        }
                    }

                    for (int i = produccion.length - 1; i >= 0; i--) {
                        if (!produccion[i].isEmpty()) {
                            pila.push(produccion[i]);
                        }
                    }

                    continue;
                }
            }

            String seEsperaba = pila.peek();
            String elError = entrada.getFirst().getSymbol();
            LinkedList<String> primeros = getPrimeros(tabla, seEsperaba, new LinkedList<>());
            String mensaje = "Se esperaban: ";
            for (String p : primeros) {
                mensaje += "\"" + p + "\", ";
            }
            mensaje = mensaje.substring(0, mensaje.length() - 2) + "\nSímbolo inválido: " + elError;

            JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            error = true;
            break;
        }

        if (!error) {
            JOptionPane.showMessageDialog(null, "Sintaxis correcta :D", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            modelArbol.setRoot(nodosTokens);
            jTreeArbol.setModel(modelArbol);
        }
    }

    private LinkedList<String> getPrimeros(Hashtable<String, Hashtable<String, String>> tabla, String id, LinkedList<String> agregados) {
        LinkedList<String> primeros = new LinkedList<>();
        try {
            Hashtable<String, String> fila = tabla.get(id);

            if (fila != null) {
                Enumeration<String> columnas = fila.keys();
                while (columnas.hasMoreElements()) {
                    String col = columnas.nextElement();

                    String primero = fila.get(col).split(" ")[0];

                    if (tabla.containsKey(primero)) {
                        LinkedList<String> recs = getPrimeros(tabla, primero, agregados);
                        primeros.addAll(recs);
                    } else {
                        if (primero.compareTo("") != 0 && !agregados.contains(primero)) {
                            primeros.add(primero);
                            agregados.add(primero);
                        }
                    }
                }
            } else {
                if (!agregados.contains(id)) {
                    agregados.add(id);
                    primeros.add(id);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return primeros;
    }

    private Hashtable<String, Hashtable<String, String>> getTablaSintacticaTabular() {
        Hashtable<String, Hashtable<String, String>> tabla = new Hashtable<>();

        Hashtable<String, String> filaProg = new Hashtable<>();
        filaProg.put("ps", "ps sent pe");
        tabla.put("prog", filaProg);

        Hashtable<String, String> filaSent = new Hashtable<>();
        filaSent.put("V", "asign sent");
        filaSent.put("v", "decl sent");
        filaSent.put("c", "comp sent");
        filaSent.put("l", "cic sent");
        filaSent.put("p", "imp sent");
        filaSent.put("in", "leer sent");
        filaSent.put("ec", "");
        filaSent.put("el", "");
        filaSent.put("pe", "");
        filaSent.put(":", "");
        tabla.put("sent", filaSent);

        Hashtable<String, String> filaDecl = new Hashtable<>();
        filaDecl.put("v", "v V : tipos vals");
        tabla.put("decl", filaDecl);

        Hashtable<String, String> filaVals = new Hashtable<>();
        filaVals.put("->", "-> valsAux");
        filaVals.put("V", "");
        filaVals.put("v", "");
        filaVals.put(":", "");
        filaVals.put("c", "");
        filaVals.put("ec", "");
        filaVals.put("l", "");
        filaVals.put("el", "");
        filaVals.put("p", "");
        filaVals.put("in", "");
        filaVals.put("pe", "");
        tabla.put("vals", filaVals);

        Hashtable<String, String> filaValsAux = new Hashtable<>();
        filaValsAux.put("(", "op");
        filaValsAux.put("N", "op");
        filaValsAux.put("F", "op");
        filaValsAux.put("V", "op");
        filaValsAux.put("T", "T");
        tabla.put("valsAux", filaValsAux);

        Hashtable<String, String> filaAsign = new Hashtable<>();
        filaAsign.put("V", "V -> valsAux");
        tabla.put("asign", filaAsign);

        Hashtable<String, String> filaImp = new Hashtable<>();
        filaImp.put("p", "p cad");
        tabla.put("imp", filaImp);

        Hashtable<String, String> filaCad = new Hashtable<>();
        filaCad.put("(", "op cadAux");
        filaCad.put("N", "op cadAux");
        filaCad.put("F", "op cadAux");
        filaCad.put("V", "op cadAux");
        filaCad.put("T", "T cadAux");
        tabla.put("cad", filaCad);

        Hashtable<String, String> filaCadAux = new Hashtable<>();
        filaCadAux.put("~", "~ cad");
        filaCadAux.put("V", "");
        filaCadAux.put("v", "");
        filaCadAux.put(":", "");
        filaCadAux.put("c", "");
        filaCadAux.put("ec", "");
        filaCadAux.put("l", "");
        filaCadAux.put("el", "");
        filaCadAux.put("p", "");
        filaCadAux.put("in", "");
        filaCadAux.put("pe", "");
        tabla.put("cadAux", filaCadAux);

        Hashtable<String, String> filaOp = new Hashtable<>();
        filaOp.put("(", "( op ) opAux");
        filaOp.put("N", "nums opAux");
        filaOp.put("F", "nums opAux");
        filaOp.put("V", "nums opAux");
        tabla.put("op", filaOp);

        Hashtable<String, String> filaOpAux = new Hashtable<>();
        filaOpAux.put("V", "");
        filaOpAux.put("v", "");
        filaOpAux.put(":", "");
        filaOpAux.put("c", "");
        filaOpAux.put("ec", "");
        filaOpAux.put("l", "");
        filaOpAux.put("el", "");
        filaOpAux.put("p", "");
        filaOpAux.put("in", "");
        filaOpAux.put("pe", "");
        filaOpAux.put(")", "");
        filaOpAux.put("<", "");
        filaOpAux.put(">", "");
        filaOpAux.put("<>", "");
        filaOpAux.put("=", "");
        filaOpAux.put("~", "");
        filaOpAux.put("+", "ops op");
        filaOpAux.put("-", "ops op");
        filaOpAux.put("*", "ops op");
        filaOpAux.put("/", "ops op");
        tabla.put("opAux", filaOpAux);

        Hashtable<String, String> filaOps = new Hashtable<>();
        filaOps.put("+", "+");
        filaOps.put("-", "-");
        filaOps.put("*", "*");
        filaOps.put("/", "/");
        tabla.put("ops", filaOps);

        Hashtable<String, String> filaNums = new Hashtable<>();
        filaNums.put("N", "N");
        filaNums.put("F", "F");
        filaNums.put("V", "V");
        tabla.put("nums", filaNums);

        Hashtable<String, String> filaTipos = new Hashtable<>();
        filaTipos.put("i", "i");
        filaTipos.put("f", "f");
        filaTipos.put("s", "s");
        tabla.put("tipos", filaTipos);

        Hashtable<String, String> filaLeer = new Hashtable<>();
        filaLeer.put("in", "in -> V");
        tabla.put("leer", filaLeer);

        Hashtable<String, String> filaComp = new Hashtable<>();
        filaComp.put("c", "c op opcomps op : sent compAux ec");
        tabla.put("comp", filaComp);

        Hashtable<String, String> filaCompAux = new Hashtable<>();
        filaCompAux.put(":", ": else : sent");
        filaCompAux.put("ec", "");
        tabla.put("compAux", filaCompAux);

        Hashtable<String, String> filaCic = new Hashtable<>();
        filaCic.put("l", "l op opcomps op : sent el");
        tabla.put("cic", filaCic);

        Hashtable<String, String> filaOpcomps = new Hashtable<>();
        filaOpcomps.put("<", "<");
        filaOpcomps.put(">", ">");
        filaOpcomps.put("<>", "<>");
        filaOpcomps.put("=", "=");
        tabla.put("opcomps", filaOpcomps);

        return tabla;
    }

    private Hashtable<String, Token> getSimbolos() {
        Hashtable<String, Token> simbolos = new Hashtable<>();
        simbolos.put("Int", new Token("Int", "i", "PR"));
        simbolos.put("Float", new Token("Float", "f", "PR"));
        simbolos.put("Str", new Token("Str", "s", "PR"));
        simbolos.put("var", new Token("var", "v", "PR"));
        simbolos.put("print", new Token("print", "p", "PR"));
        simbolos.put("input", new Token("input", "in", "PR"));
        simbolos.put("compare", new Token("compare", "c", "PR"));
        simbolos.put("else", new Token("else", "e", "PR"));
        simbolos.put("endCompare", new Token("endCompare", "ec", "PR"));
        simbolos.put("loop", new Token("loop", "l", "PR"));
        simbolos.put("endLoop", new Token("endLoop", "el", "PR"));
        simbolos.put("programStart", new Token("programStart", "ps", "PR"));
        simbolos.put("programEnd", new Token("programEnd", "pe", "PR"));
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
        simbolos.put("(", new Token("(", "(", "OP"));
        simbolos.put(")", new Token(")", ")", "OP"));
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
        jScrollPaneArbol = new javax.swing.JScrollPane();
        jTreeArbol = new javax.swing.JTree();
        jLabelArbol = new javax.swing.JLabel();

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

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTreeArbol.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPaneArbol.setViewportView(jTreeArbol);

        jLabelArbol.setText("Árbol");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneSimbolos, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSimbolos))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneTokens, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTokens))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelArbol)
                    .addComponent(jScrollPaneArbol, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSimbolos)
                    .addComponent(jLabelTokens)
                    .addComponent(jLabelArbol))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPaneSimbolos)
                        .addComponent(jScrollPaneTokens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPaneArbol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
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
    private javax.swing.JLabel jLabelArbol;
    private javax.swing.JLabel jLabelSimbolos;
    private javax.swing.JLabel jLabelTokens;
    private javax.swing.JScrollPane jScrollPaneArbol;
    private javax.swing.JScrollPane jScrollPaneSimbolos;
    private javax.swing.JScrollPane jScrollPaneTokens;
    private javax.swing.JTable jTableSimbolos;
    private javax.swing.JTable jTableTokens;
    private javax.swing.JTree jTreeArbol;
    // End of variables declaration//GEN-END:variables
}
