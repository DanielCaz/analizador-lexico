package analizadorlexico;

import java.util.LinkedList;

/**
 *
 * @author Daniel Cazarez
 */
public class Helpers {
    public static String entradaToString(LinkedList<Token> entrada) {
        String str = "[";
        str = entrada.stream().map((token) -> token.getIdentificador() + ", ").reduce(str, String::concat);
        str = str.substring(0, str.length() - 2) + "]";
        return str;
    }
}
