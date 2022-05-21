package analizadorlexico;

/**
 *
 * @author Daniel Cazarez
 */
public class Token {

    private final String lexema, categoria, identificador;
    private String tipoDato;
    private boolean declarada;

    /**
     * Token de la entrada
     *
     * @param lexema Es lo que ha escrito el usuario
     * @param identificador Una abreviación que es lo que se usa en los análisis
     * @param categoria La categoría del token
     */
    public Token(String lexema, String identificador, String categoria) {
        this.lexema = lexema;
        this.categoria = categoria;
        this.identificador = identificador;
        this.tipoDato = null;
        this.declarada = false;
    }

    public String getLexema() {
        return lexema;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public void setDeclarada(boolean declarada) {
        this.declarada = declarada;
    }

    public boolean isDeclarada() {
        return declarada;
    }

    @Override
    public String toString() {
        String str = lexema;
        
        if (identificador.compareTo("V") == 0) {
            if (declarada) {
                str += " [D]";
            } else {
                str += " [ND]";
            }
        }
        
        if (tipoDato != null) {
            str += " [" + tipoDato + "]";
        }
        
        return str;
    }

    public String[] toStringArray() {
        return new String[]{lexema, identificador, categoria};
    }

    public String getTipoDato() {
        return tipoDato;
    }
}
