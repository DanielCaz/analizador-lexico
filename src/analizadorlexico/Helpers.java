package analizadorlexico;

import java.util.LinkedList;

/**
 *
 * @author Daniel Cazarez
 */
public class Helpers {
    public static String entradaToString(LinkedList<Token> entrada) {
        String str = "[";
        for (Token token : entrada) {
            str += token.getIdentificador() + ", ";
        }
        str = str.substring(0, str.length() - 2) + "]";
        return str;
    }
}
