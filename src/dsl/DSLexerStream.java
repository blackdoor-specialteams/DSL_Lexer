/**
 * 
 */
package dsl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import dsl.Token.TokenType;



/**
 * @author nfischer3
 *
 */
public class DSLexerStream{
	
	public static final char NULL = '\u0000';
	public static final String SYMBOLS = "!;:,=+-*/<>()";
	public static final String WHITECHARS = " \n\r\t";
	protected Reader source;
	private int line, column = 0;
	private char charAtHand = ' ';

	/**
	 * @param source - the Reader containing a character stream that represents source code
	 */
	public DSLexerStream(Reader source) {
		this.source = source;
	}
	
	/**
	 * Reads a single token. This method will block until a token is available, or an I/O error occurs.
	 * @return The token read.
	 * @throws IOException
	 */
	public Token read() throws IOException{
		if(charAtHand == NULL){
			return new Token(TokenType.EOS, line, column, null);
		}
		if(isWhiteChar(charAtHand)){
			nextChar();
			return read();
		}
		
		if(charAtHand == '#'){
			while(nextChar() != '\n' && charAtHand != '\r');
			return read();
		}
		
		if(Character.isDigit(charAtHand)){
			return getNumLiteralToken();
		}if(isSymbol(charAtHand)){
			return getSymbolToken();
		}
		if (charAtHand == '\"') {
			return getStringLiteralToken();
		}
		
		String lexeme = "" + charAtHand;
		while (nextChar() == '_' || Character.isLetterOrDigit(charAtHand)) {
			lexeme += charAtHand;
		}

		for (TokenType t : TokenType.values()) {
			if (t.getIdString().equals(lexeme))
				return new Token(t, line, column, lexeme);
		}
		
		return new Token(TokenType.ID, line, column, lexeme);
	}

	private static List<TokenType> refinePossibleTypes(char charAtHand, int i, List<TokenType> possibleTypes){
		List<TokenType> stupid = new ArrayList<TokenType>(possibleTypes);
		for (TokenType t : possibleTypes) {
			try {
				if (charAtHand != t.getIdString().charAt(i))
					stupid.remove(t);
			} catch (IndexOutOfBoundsException e) {
				stupid.remove(t);
			}
		}
		return stupid;
	}
	
	private static List<TokenType> getPossibleTypes(char firstChar){
		List<TokenType> possibleTypes = new ArrayList<TokenType>();
		for(TokenType t : TokenType.values()){
			try{
			if(firstChar == t.getIdString().charAt(0))
				possibleTypes.add(t);
			}catch (StringIndexOutOfBoundsException e){
				
			}
		}
		return possibleTypes;
	}
	
	private static boolean isSymbol(char c){
		return SYMBOLS.contains("" + c);
	}
	
	private static boolean isWhiteChar(char c){
		return WHITECHARS.contains("" + c);
	}
	
	private Token getSymbolToken() throws IOException{
		String lexeme = "" + charAtHand;
		List<TokenType> possibleTypes = getPossibleTypes(charAtHand);
		
		
		while(isSymbol(nextChar()) && charAtHand != ';'){
			lexeme += charAtHand;
		}
		
		int i = 0;
		for(char c : lexeme.toCharArray()){
			possibleTypes = refinePossibleTypes(c, i++, possibleTypes);
		}
		try{
			return new Token(possibleTypes.get(0), line, column, lexeme);
		}catch (IndexOutOfBoundsException e){
			System.err.println(new LexerException(lexeme + " is not a valid token.", line, column));
			return new Token(TokenType.ERROR, line,column,lexeme);
		}
	}
	
	/**
	 * returns the next char in source, incrementing line and column appropriately
	 * also saves the char to charAtHand
	 * @return the next char in source
	 * @throws IOException
	 */
	private char nextChar() throws IOException{
		int read = source.read();
		if(read == -1){
			charAtHand = NULL;
			return charAtHand;
		}
		charAtHand = (char) read;
		if(charAtHand == '\n' || charAtHand == '\r'){
			line ++;
		}else
			column ++;
		return charAtHand;
	}
	
	private Token getNumLiteralToken() throws IOException{
		Token token;
		String characters = "" + charAtHand;//List<Character> characters = new ArrayList<Character>();
		TokenType type;
		
		//characters.add(charAtHand);
		while(Character.isDigit(nextChar())){
			characters += charAtHand;
		}
		
		if(charAtHand == '.'){
			type = TokenType.FLOAT_VAL;
			characters += charAtHand;
			while(Character.isDigit(nextChar())){
				characters += charAtHand;
			}
		}else{
			type = TokenType.INT_VAL;
		}
		
		token = new Token(type, line, column, characters);
		return token;
	}
	
	private Token getStringLiteralToken() throws IOException{
		String string = "";
		while(nextChar() != '\"'){
			string += charAtHand;
		}
		nextChar();
		return new Token(TokenType.STRING_VAL, line, column, string);
	}
	
	/**
	 * Reads up to byte.length tokens from this input stream into an array of tokens. This method blocks until some input is available.
	 * This method simply performs the call read(b, 0, b.length) and returns the result. It is important that it does not do source.read(b).
	 * @param b - the buffer into which the data is read.
	 * @return the total number of tokens read into the buffer, or -1 if there is no more data because the end of the stream has been reached; in this case the last token written will be the end of stream token.
	 * @throws IOException
	 */
	public int read(Token[] b)throws IOException{
		return read(b, 0, b.length);
	}
	
	/**
	 * Reads tokens into a portion of an array. This method will block until some input is available, an I/O error occurs, or the end of the stream is reached; in this case the last token written will be the end of stream token.
	 * @param b - Destination buffer
	 * @param off - Offset at which to start storing tokens
	 * @param len - Maximum number of tokens to read
	 * @return The number of characters read.
	 * @throws IOException
	 */
	public int read(Token[] b, int off, int len) throws IOException{
		for(int i = 0; i < len; i++){
			Token read = read();
			if(read.getTokenType() != TokenType.EOS && !read.getTokenType().equals(TokenType.ERROR)){
				b[off+i] = read;
			}else{
				b[off+i] = read;
				return i;
			}
		}
		return 0;
	}
	
	
	/**
	 * Skips tokens. This method will block until some tokens are available, an I/O error occurs, or the end of the stream is reached.
	 * @param n - the number of tokens to skip.
	 * @return the number of tokens actually skipped.
	 * @throws IOException 
	 */
	public long skip(long n) throws IOException{
		TokenType read = read().getTokenType();
		long i;
		for(i = 0; i < n; i++){
			if((read == TokenType.EOS) || (read == TokenType.ERROR)){
				return i;
			}
			read =  read().getTokenType();
		}
		return i;
	}
	
	@SuppressWarnings("serial")
	public class LexerException extends AnalysisException{
		private int line, column;
		public LexerException(String error, int line, int column){
			super(error);
			this.line = line;
			this.column = column;
		}
		
		public String toString(){
			return "LexerException at " + line + ":" + column + " : " + getMessage();
		}
	}
	
	

}
