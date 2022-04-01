package analizadorlexico;

/**
 *
 * @author Daniel Cazarez
 */
public class Token {
    private final String symbol, categoria, identificador;

    public Token(String symbol, String categoria, String identificador) {
        this.symbol = symbol;
        this.categoria = categoria;
        this.identificador = identificador;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getIdentificador() {
        return identificador;
    }
    
    public String[] toStringArray() {
        return new String[]{symbol, identificador, categoria};
    }
}
