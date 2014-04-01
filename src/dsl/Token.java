/**
 * 
 */
package dsl;

/**
 * @author nfischer3
 *
 */
public class Token {
	
	public enum TokenType {
		LAPREN("("), RPAREN(")"), SEMICOLON(";"), COLON(":"), COMMA(","), ADD(
				"+"), SUBTRACT("-"), MULT("*"), MOD("%"), ASSIGN("="), RBRACE(
				"{"), LBRACE("}"), INT("int"), FLOAT("float"), ID(" "), STRING(
				"string");
		private String idString;

		TokenType(String idString) {
			this.idString = idString;
		}

		public String getIdString() {
			return idString;
		}
	}
	
	private TokenType tokenType;
	private int tokenLine;
	private int tokenColumn;
	private String tokenLexeme;
	
	public Token(TokenType tokenType, int tokenLine, int tokenColumn,
			String tokenLexeme) {
		this.tokenType = tokenType;
		this.tokenLine = tokenLine;
		this.tokenColumn = tokenColumn;
		this.tokenLexeme = tokenLexeme;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public int getTokenLine() {
		return tokenLine;
	}

	public int getTokenColumn() {
		return tokenColumn;
	}

	public String getTokenLexeme() {
		return tokenLexeme;
	}

	@Override
	public String toString() {
		return "Token [tokenType=" + tokenType + ", tokenLine=" + tokenLine
				+ ", tokenColumn=" + tokenColumn + ", tokenLexeme="
				+ tokenLexeme + "]";
	}
}
