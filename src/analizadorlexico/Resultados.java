package analizadorlexico;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class Resultados extends javax.swing.JFrame {

    private static LinkedList<String> listaTokens;
    private final LinkedList<Token> entrada;
    private final HashMap<String, Token> simbolos;
    private final DefaultTableModel modelSimbolos;
    private final HashMap<String, Integer> filasSimbolos;

    /**
     * Creates new form Resultados
     * @param listaTokens Lista de tokens
     */
    public Resultados(LinkedList<String> listaTokens) {
        initComponents();
        Resultados.listaTokens = listaTokens;
        entrada = new LinkedList<>();
        boolean error = false;

        simbolos = getSimbolos();
        modelSimbolos = (DefaultTableModel) jTableSimbolos.getModel();
        filasSimbolos = new HashMap<>();

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
                    filasSimbolos.put(token.getLexema(), modelSimbolos.getRowCount() - 1);
                }
                entrada.add(token);
            } else if (fila.matches("^('.*')$")) {
                Token token = new Token(fila, "T", "TEX");
                model.addRow(token.toStringArray());
                token.setTipoDato("Str");
                if (!simbolos.containsKey(fila)) {
                    simbolos.put(fila, token);
                    modelSimbolos.addRow(token.toStringArray());
                }
                entrada.add(token);
            } else if (fila.matches("^(\\d+\\.\\d+)$")) {
                Token token = new Token(fila, "F", "NUM");
                model.addRow(token.toStringArray());
                token.setTipoDato("Float");
                if (!simbolos.containsKey(fila)) {
                    simbolos.put(fila, token);
                    modelSimbolos.addRow(token.toStringArray());
                }
                entrada.add(token);
            } else if (fila.matches("^(\\d+)$")) {
                Token token = new Token(fila, "N", "NUM");
                model.addRow(token.toStringArray());
                token.setTipoDato("Int");
                if (!simbolos.containsKey(fila)) {
                    simbolos.put(fila, token);
                    modelSimbolos.addRow(token.toStringArray());
                }
                entrada.add(token);
            } else {
                JOptionPane.showMessageDialog(null, "Error de análisis: Símbolo \"" + fila + "\" no reconocido", "Error de análisis", JOptionPane.ERROR_MESSAGE);

                error = true;

                WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);

                break;
            }
        }

        if (!error) {
            analisisSintacticoTabular();
        }
    }

    private void analisisSintacticoTabular() {
        HashMap<String, HashMap<String, String>> tabla = getTablaSintacticaTabular();

        Stack<String> pila = new Stack<>();
        pila.push("$");
        pila.push("prog");
        boolean error = false;
        DefaultTreeModel modelArbol = (DefaultTreeModel) jTreeArbol.getModel();
        DefaultMutableTreeNode nodosTokens = new DefaultMutableTreeNode(new Token("prog", "prog", null));

        while (!entrada.isEmpty()) {
            System.out.println("\n"
                    + "Pila: " + pila
                    + "\nEntrada: " + Helpers.entradaToString(entrada));

            System.out.printf("Comparando \"%s\" con \"%s\"%n", entrada.getFirst().getIdentificador(), pila.peek());
            if (entrada.getFirst().getIdentificador().compareTo(pila.peek()) == 0) { //El primero de la entrada y lo más arriba de la pila son iguales
                System.out.printf("Removiendo \"%s\" de la pila y entrada%n", pila.peek());
                entrada.removeFirst();
                pila.pop();

                continue;
            }

            String nombreFila = pila.peek();
            System.out.printf("Consultando fila \"%s\" y columna \"%s\" de la tabla sintáctica%n", pila.peek(), entrada.getFirst().getIdentificador());
            HashMap<String, String> fila = tabla.get(nombreFila); //Consultando fila de la tabla
            if (fila != null) {
                if (fila.containsKey(entrada.getFirst().getIdentificador())) { //Se encontró la fila y columna en la tabla
                    System.out.printf("Cambiando \"%s\" por \"%s\" en la pila%n", pila.peek(), fila.get(entrada.getFirst().getIdentificador()));
                    String copia = pila.peek();
                    pila.pop();
                    String[] produccion = fila.get(entrada.getFirst().getIdentificador()).split(" ");

                    Enumeration hijos = nodosTokens.depthFirstEnumeration(); //Buscamos el nodo al que le queremos agregar los hijos
                    while (hijos.hasMoreElements()) {
                        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) hijos.nextElement();
                        Token datos = (Token) nodo.getUserObject();
                        if (datos.getIdentificador().compareTo(copia) == 0 && nodo.isLeaf()) { //Revisamos que el nodo que buscamos sea el actual y que no tenga ya hijos
                            for (String prod : produccion) {
                                if (prod.length() > 0) { //Revisamos si se agrega cadena vacía o la producción
                                    if (nombreFila.compareTo("asign") == 0) {
                                        if (prod.compareTo("V") == 0) {
                                            nodo.add(new DefaultMutableTreeNode(entrada.getFirst()));
                                        } else {
                                            Token aAgregar = getSimbolos2().get(prod);
                                            if (aAgregar == null) {
                                                nodo.add(new DefaultMutableTreeNode(new Token(prod, prod, null)));
                                            } else {
                                                nodo.add(new DefaultMutableTreeNode(aAgregar));
                                            }
                                        }
                                    } else if (nombreFila.compareTo("decl") == 0) {
                                        if (prod.compareTo("V") == 0) {
                                            Token aAgregar = entrada.get(1);
                                            nodo.add(new DefaultMutableTreeNode(aAgregar));
                                        } else {
                                            Token aAgregar = getSimbolos2().get(prod);
                                            if (aAgregar == null) {
                                                nodo.add(new DefaultMutableTreeNode(new Token(prod, prod, null)));
                                            } else {
                                                nodo.add(new DefaultMutableTreeNode(aAgregar));
                                            }
                                        }
                                    } else if (nombreFila.compareTo("nums") == 0) {
                                        if (prod.compareTo("V") == 0) {
                                            nodo.add(new DefaultMutableTreeNode(entrada.getFirst()));
                                        } else {
                                            if (prod.compareTo("N") == 0) {
                                                Token aAgregar = entrada.getFirst();
                                                aAgregar.setTipoDato("Int");
                                                nodo.add(new DefaultMutableTreeNode(aAgregar));
                                                System.out.println();
                                            } else if (prod.compareTo("F") == 0) {
                                                Token aAgregar = entrada.getFirst();
                                                aAgregar.setTipoDato("Float");
                                                nodo.add(new DefaultMutableTreeNode(aAgregar));
                                            } else {
                                                nodo.add(new DefaultMutableTreeNode(new Token(prod, prod, null)));
                                            }
                                        }
                                    } else if (nombreFila.compareTo("leer") == 0) {
                                        if (prod.compareTo("V") == 0) {
                                            nodo.add(new DefaultMutableTreeNode(entrada.get(2)));
                                        } else {
                                            nodo.add(new DefaultMutableTreeNode(new Token(prod, prod, null)));
                                        }
                                    } else if (nombreFila.compareTo("cad") == 0 || nombreFila.compareTo("valsAux") == 0) {
                                        if (prod.compareTo("T") == 0) {
                                            Token aAgregar = new Token(prod, prod, null);
                                            aAgregar.setTipoDato("Str");
                                            nodo.add(new DefaultMutableTreeNode(aAgregar));
                                        } else {
                                            nodo.add(new DefaultMutableTreeNode(new Token(prod, prod, null)));
                                        }
                                    } else {
                                        Token aAgregar = getSimbolos2().get(prod);
                                        if (aAgregar == null) {
                                            nodo.add(new DefaultMutableTreeNode(new Token(prod, prod, null)));
                                        } else {
                                            nodo.add(new DefaultMutableTreeNode(aAgregar));
                                        }
                                    }
                                } else {
                                    nodo.add(new DefaultMutableTreeNode(new Token("ε", "ε", null)));
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
            String elError = entrada.getFirst().getLexema();
            LinkedList<String> primeros = getPrimeros(tabla, seEsperaba, new LinkedList<>());
            String mensaje = "Se esperaban: ";
            mensaje = primeros.stream().map((p) -> "\"" + p + "\", ").reduce(mensaje, String::concat);
            mensaje = mensaje.substring(0, mensaje.length() - 2) + "\nSímbolo inválido: " + elError;

            JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            error = true;
            break;
        }

        if (!error) {
            JOptionPane.showMessageDialog(null, "Sintaxis correcta :D", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            modelArbol.setRoot(nodosTokens);
            jTreeArbol.setModel(modelArbol);
            analisisSemantico();
        }
    }

    private void analisisSemantico() {
        // Para indicar que una variable ha sido declarada
        DefaultTreeModel modelArbol = (DefaultTreeModel) jTreeArbol.getModel();
        DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) modelArbol.getRoot();
        Enumeration nodos = raiz.depthFirstEnumeration();
        while (nodos.hasMoreElements()) {
            DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) nodos.nextElement();
            Token objeto = (Token) nodo.getUserObject();
            if (objeto.getLexema().compareTo("decl") == 0) {
                DefaultMutableTreeNode variable = nodo.getNextNode().getNextNode();
                Token datosVariable = (Token) variable.getUserObject();
                datosVariable.setDeclarada(true);
                DefaultMutableTreeNode hermano = nodo.getNextSibling();
                if (hermano != null) {
                    Enumeration nodoHermano = hermano.depthFirstEnumeration();
                    while (nodoHermano.hasMoreElements()) {
                        DefaultMutableTreeNode hijoHermano = (DefaultMutableTreeNode) nodoHermano.nextElement();
                        Token datoHijoHermano = (Token) hijoHermano.getUserObject();
                        if (datoHijoHermano.getLexema().compareTo(datosVariable.getLexema()) == 0) {
                            datoHijoHermano.setDeclarada(true);
                        }
                    }
                }

                if (!checarDeclaradas(variable.getNextNode().getNextNode().getNextNode().getNextNode(), variable.toString())) {
                    return;
                }

                DefaultMutableTreeNode tipo = variable.getNextNode().getNextNode().getNextNode();
                String tipoBuscar = ((Token) tipo.getUserObject()).getLexema();
                datosVariable.setTipoDato(tipoBuscar);
                String nombreVar = ((Token) variable.getUserObject()).getLexema();
                modelSimbolos.setValueAt(tipoBuscar, filasSimbolos.get(nombreVar), 3);
                if (!checarOp(variable.getNextSibling().getNextSibling().getNextSibling(), tipoBuscar)) {
                    return;
                }
            } else if (objeto.getLexema().compareTo("asign") == 0) {
                if (!checarDeclaradas(nodo, null)) {
                    return;
                }

                if (!checarOp(nodo, ((Token) nodo.getNextNode().getUserObject()).getTipoDato())) {
                    return;
                }
            } else if (objeto.getLexema().compareTo("comp") == 0) {
                if (!checarDeclaradas(nodo, null)) {
                    return;
                }

                if (!checarTipos(nodo)) {
                    return;
                }
            } else if (objeto.getLexema().compareTo("cic") == 0) {
                if (!checarDeclaradas(nodo, null)) {
                    return;
                }

                if (!checarTipos(nodo)) {
                    return;
                }
            } else if (objeto.getLexema().compareTo("imp") == 0) {
                if (!checarDeclaradas(nodo, null)) {
                    return;
                }

                if (!checarTipos(nodo)) {
                    return;
                }
            } else if (objeto.getLexema().compareTo("leer") == 0) {
                if (!checarDeclaradas(nodo, null)) {
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(null, "Semántica correcta >w<", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean checarOp(DefaultMutableTreeNode raiz, String tipoDatoComprobar) {
        Enumeration<DefaultMutableTreeNode> tokens = raiz.depthFirstEnumeration();
        while (tokens.hasMoreElements()) {
            Token actual = (Token) tokens.nextElement().getUserObject();
            if (actual.getTipoDato() != null && actual.getTipoDato().compareTo(tipoDatoComprobar) != 0) {
                JOptionPane.showMessageDialog(null, "Tipos de dato incompatibles", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private boolean checarTipos(DefaultMutableTreeNode raiz) {
        String tipoAComprobar = null;
        Enumeration<DefaultMutableTreeNode> tokens = raiz.depthFirstEnumeration();
        while (tokens.hasMoreElements()) {
            Token actual = (Token) tokens.nextElement().getUserObject();
            if (actual.getTipoDato() != null) {
                if (tipoAComprobar == null) {
                    tipoAComprobar = actual.getTipoDato();
                } else {
                    if (actual.getTipoDato().compareTo(tipoAComprobar) != 0) {
                        JOptionPane.showMessageDialog(null, "Tipos de dato incompatibles", "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checarDeclaradas(DefaultMutableTreeNode nodo, String excluir) {
        Enumeration nodos = nodo.depthFirstEnumeration();
        while (nodos.hasMoreElements()) {
            DefaultMutableTreeNode actual = (DefaultMutableTreeNode) nodos.nextElement();
            Token datos = (Token) actual.getUserObject();
            if (datos.getIdentificador().compareTo("V") == 0 && !datos.isDeclarada()) {
                JOptionPane.showMessageDialog(null, "Variable \"" + datos.getLexema() + "\" no ha sido declarada", "Error semántico", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (excluir != null && datos.getLexema().compareTo(excluir) == 0) {
                datos.setDeclarada(false);
                JOptionPane.showMessageDialog(null, "Variable \"" + datos.getLexema() + "\" no ha sido declarada", "Error semántico", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private LinkedList<String> getPrimeros(HashMap<String, HashMap<String, String>> tabla, String id, LinkedList<String> agregados) {
        LinkedList<String> primeros = new LinkedList<>();
        try {
            HashMap<String, String> fila = tabla.get(id);

            if (fila != null) {
                Set<String> columnasSet = fila.keySet();
                Iterator<String> columnas = columnasSet.iterator();
                while (columnas.hasNext()) {
                    String col = columnas.next();

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

    private HashMap<String, HashMap<String, String>> getTablaSintacticaTabular() {
        HashMap<String, HashMap<String, String>> tabla = new HashMap<>();

        HashMap<String, String> filaProg = new HashMap<>();
        filaProg.put("V", "asign sent");
        filaProg.put("v", "decl sent");
        filaProg.put("c", "comp sent");
        filaProg.put("l", "cic sent");
        filaProg.put("p", "imp sent");
        filaProg.put("in", "leer sent");
        filaProg.put("ec", "");
        filaProg.put("el", "");
        filaProg.put("pe", "");
        filaProg.put(":", "");
        filaProg.put("$", "");
        tabla.put("prog", filaProg);

        HashMap<String, String> filaSent = new HashMap<>();
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
        filaSent.put("$", "");
        tabla.put("sent", filaSent);

        HashMap<String, String> filaDecl = new HashMap<>();
        filaDecl.put("v", "v V : tipos vals");
        tabla.put("decl", filaDecl);

        HashMap<String, String> filaVals = new HashMap<>();
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
        filaVals.put("$", "");
        tabla.put("vals", filaVals);

        HashMap<String, String> filaValsAux = new HashMap<>();
        filaValsAux.put("(", "op");
        filaValsAux.put("N", "op");
        filaValsAux.put("F", "op");
        filaValsAux.put("V", "op");
        filaValsAux.put("T", "T");
        tabla.put("valsAux", filaValsAux);

        HashMap<String, String> filaAsign = new HashMap<>();
        filaAsign.put("V", "V -> valsAux");
        tabla.put("asign", filaAsign);

        HashMap<String, String> filaImp = new HashMap<>();
        filaImp.put("p", "p cad");
        tabla.put("imp", filaImp);

        HashMap<String, String> filaCad = new HashMap<>();
        filaCad.put("(", "op cadAux");
        filaCad.put("N", "op cadAux");
        filaCad.put("F", "op cadAux");
        filaCad.put("V", "op cadAux");
        filaCad.put("T", "T cadAux");
        tabla.put("cad", filaCad);

        HashMap<String, String> filaCadAux = new HashMap<>();
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
        filaCadAux.put("$", "");
        tabla.put("cadAux", filaCadAux);

        HashMap<String, String> filaOp = new HashMap<>();
        filaOp.put("(", "( op ) opAux");
        filaOp.put("N", "nums opAux");
        filaOp.put("F", "nums opAux");
        filaOp.put("V", "nums opAux");
        tabla.put("op", filaOp);

        HashMap<String, String> filaOpAux = new HashMap<>();
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
        filaOpAux.put("$", "");
        tabla.put("opAux", filaOpAux);

        HashMap<String, String> filaOps = new HashMap<>();
        filaOps.put("+", "+");
        filaOps.put("-", "-");
        filaOps.put("*", "*");
        filaOps.put("/", "/");
        tabla.put("ops", filaOps);

        HashMap<String, String> filaNums = new HashMap<>();
        filaNums.put("N", "N");
        filaNums.put("F", "F");
        filaNums.put("V", "V");
        tabla.put("nums", filaNums);

        HashMap<String, String> filaTipos = new HashMap<>();
        filaTipos.put("i", "i");
        filaTipos.put("f", "f");
        filaTipos.put("s", "s");
        tabla.put("tipos", filaTipos);

        HashMap<String, String> filaLeer = new HashMap<>();
        filaLeer.put("in", "in -> V");
        tabla.put("leer", filaLeer);

        HashMap<String, String> filaComp = new HashMap<>();
        filaComp.put("c", "c op opcomps op : sent compAux ec");
        tabla.put("comp", filaComp);

        HashMap<String, String> filaCompAux = new HashMap<>();
        filaCompAux.put(":", ": e : sent");
        filaCompAux.put("ec", "");
        filaCompAux.put("$", "");
        tabla.put("compAux", filaCompAux);

        HashMap<String, String> filaCic = new HashMap<>();
        filaCic.put("l", "l op opcomps op : sent el");
        tabla.put("cic", filaCic);

        HashMap<String, String> filaOpcomps = new HashMap<>();
        filaOpcomps.put("<", "<");
        filaOpcomps.put(">", ">");
        filaOpcomps.put("<>", "<>");
        filaOpcomps.put("=", "=");
        tabla.put("opcomps", filaOpcomps);

        return tabla;
    }

    private HashMap<String, Token> getSimbolos() {
        HashMap<String, Token> simbolosTabla = new HashMap<>();
        simbolosTabla.put("Int", new Token("Int", "i", "PR"));
        simbolosTabla.put("Float", new Token("Float", "f", "PR"));
        simbolosTabla.put("Str", new Token("Str", "s", "PR"));
        simbolosTabla.put("var", new Token("var", "v", "PR"));
        simbolosTabla.put("print", new Token("print", "p", "PR"));
        simbolosTabla.put("input", new Token("input", "in", "PR"));
        simbolosTabla.put("compare", new Token("compare", "c", "PR"));
        simbolosTabla.put("else", new Token("else", "e", "PR"));
        simbolosTabla.put("endCompare", new Token("endCompare", "ec", "PR"));
        simbolosTabla.put("loop", new Token("loop", "l", "PR"));
        simbolosTabla.put("endLoop", new Token("endLoop", "el", "PR"));
        simbolosTabla.put("~", new Token("~", "~", "SEP"));
        simbolosTabla.put(":", new Token(":", ":", "SEP"));
        simbolosTabla.put("+", new Token("+", "+", "OP"));
        simbolosTabla.put("-", new Token("-", "-", "OP"));
        simbolosTabla.put("*", new Token("*", "*", "OP"));
        simbolosTabla.put("/", new Token("/", "/", "OP"));
        simbolosTabla.put("->", new Token("->", "->", "OP"));
        simbolosTabla.put("<>", new Token("<>", "<>", "OP"));
        simbolosTabla.put("=", new Token("=", "=", "OP"));
        simbolosTabla.put("<", new Token("<", "<", "OP"));
        simbolosTabla.put(">", new Token(">", ">", "OP"));
        simbolosTabla.put("(", new Token("(", "(", "OP"));
        simbolosTabla.put(")", new Token(")", ")", "OP"));
        return simbolosTabla;
    }

    public HashMap<String, Token> getSimbolos2() {
        HashMap<String, Token> simbolosTabla = new HashMap<>();
        simbolosTabla.put("i", new Token("Int", "i", "PR"));
        simbolosTabla.put("f", new Token("Float", "f", "PR"));
        simbolosTabla.put("s", new Token("Str", "s", "PR"));
        simbolosTabla.put("v", new Token("var", "v", "PR"));
        simbolosTabla.put("p", new Token("print", "p", "PR"));
        simbolosTabla.put("in", new Token("input", "in", "PR"));
        simbolosTabla.put("c", new Token("compare", "c", "PR"));
        simbolosTabla.put("e", new Token("else", "e", "PR"));
        simbolosTabla.put("ec", new Token("endCompare", "ec", "PR"));
        simbolosTabla.put("l", new Token("loop", "l", "PR"));
        simbolosTabla.put("el", new Token("endLoop", "el", "PR"));
        simbolosTabla.put("~", new Token("~", "~", "SEP"));
        simbolosTabla.put(":", new Token(":", ":", "SEP"));
        simbolosTabla.put("+", new Token("+", "+", "OP"));
        simbolosTabla.put("-", new Token("-", "-", "OP"));
        simbolosTabla.put("*", new Token("*", "*", "OP"));
        simbolosTabla.put("/", new Token("/", "/", "OP"));
        simbolosTabla.put("->", new Token("->", "->", "OP"));
        simbolosTabla.put("<>", new Token("<>", "<>", "OP"));
        simbolosTabla.put("=", new Token("=", "=", "OP"));
        simbolosTabla.put("<", new Token("<", "<", "OP"));
        simbolosTabla.put(">", new Token(">", ">", "OP"));
        simbolosTabla.put("(", new Token("(", "(", "OP"));
        simbolosTabla.put(")", new Token(")", ")", "OP"));
        return simbolosTabla;
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
                {"Int", "I", "PR", "Int"},
                {"Float", "F", "PR", "Float"},
                {"Str", "S", "PR", "Str"},
                {"var", "var", "PR", null},
                {"print", "print", "PR", null},
                {"input", "input", "PR", null},
                {"compare", "compare", "PR", null},
                {"else", "else", "PR", null},
                {"endCompare", "endCompare", "PR", null},
                {"loop", "loop", "PR", null},
                {"endLoop", "endLoop", "PR", null},
                {"~", "~", "SEP", null},
                {":", ":", "SEP", null},
                {"+", "+", "OP", null},
                {"-", "-", "OP", null},
                {"*", "*", "OP", null},
                {"/", "/", "OP", null},
                {"->", "->", "OP", null},
                {"<>", "<>", "OP", null},
                {"=", "=", "OP", null},
                {"<", "<", "OP", null},
                {">", ">", "OP", null}
            },
            new String [] {
                "Lexema", "Identificador", "Categoría", "Tipo de dato"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelArbol)
                        .addGap(0, 158, Short.MAX_VALUE))
                    .addComponent(jScrollPaneArbol))
                .addContainerGap())
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneSimbolos, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                    .addComponent(jScrollPaneTokens)
                    .addComponent(jScrollPaneArbol))
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Resultados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Resultados(listaTokens).setVisible(true);
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
