// BSON.java

package com.meidusa.fastbson.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class BSON {

	// ---- basics ----

	public static final byte EOO = 0;
	public static final byte NUMBER = 1;
	public static final byte STRING = 2;
	public static final byte OBJECT = 3;
	public static final byte ARRAY = 4;
	public static final byte BINARY = 5;
	public static final byte UNDEFINED = 6;
	public static final byte OID = 7;
	public static final byte BOOLEAN = 8;
	public static final byte DATE = 9;
	public static final byte NULL = 10;
	public static final byte REGEX = 11;
	public static final byte REF = 12;
	public static final byte CODE = 13;
	public static final byte SYMBOL = 14;
	public static final byte CODE_W_SCOPE = 15;
	public static final byte NUMBER_INT = 16;
	public static final byte TIMESTAMP = 17;
	public static final byte NUMBER_LONG = 18;

	// Added by Lichencheng
	public static final byte BIG_DECIMAL = 126;

	public static final byte MINKEY = -1;
	public static final byte MAXKEY = 127;

	// --- binary types
	/*
	 * these are binary types so the format would look like
	 * <BINARY><name><BINARY_TYPE><...>
	 */

	public static final byte B_FUNC = 1;
	public static final byte B_BINARY = 2;

    private static final int GLOBAL_FLAG = 256;
	
	
    public static enum RegexFlag { 
        CANON_EQ( Pattern.CANON_EQ, 'c', "Pattern.CANON_EQ" ),
        UNIX_LINES(Pattern.UNIX_LINES, 'd', "Pattern.UNIX_LINES" ),
        GLOBAL( GLOBAL_FLAG, 'g', null ),
        CASE_INSENSITIVE( Pattern.CASE_INSENSITIVE, 'i', null ),
        MULTILINE(Pattern.MULTILINE, 'm', null ),
        DOTALL( Pattern.DOTALL, 's', "Pattern.DOTALL" ),
        LITERAL( Pattern.LITERAL, 't', "Pattern.LITERAL" ),
        UNICODE_CASE( Pattern.UNICODE_CASE, 'u', "Pattern.UNICODE_CASE" ),
        COMMENTS( Pattern.COMMENTS, 'x', null );

        private static final Map<Character, RegexFlag> byCharacter = new HashMap<Character, RegexFlag>();

        static {
            for (RegexFlag flag : values()) {
                byCharacter.put(flag.flagChar, flag);
            }
        }

        public static RegexFlag getByCharacter(char ch) {
            return byCharacter.get(ch);
        }
        public final int javaFlag;
        public final char flagChar;
        public final String unsupported;

        RegexFlag( int f, char ch, String u ) {
            javaFlag = f;
            flagChar = ch;
            unsupported = u;
        }
    }

}
