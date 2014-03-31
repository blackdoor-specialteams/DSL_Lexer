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
	
	protected Reader source;
	private int line, column = 0;
	private char charAtHand = ' ';

	/**
	 * @param source - the Reader containing a character stream that represents
	 * source code
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
		boolean tokenFound = false;
		Token token;
		//TODO whitespace handling
		if(charAtHand == ' '){
			nextChar();
		}
		if(Character.isDigit(charAtHand)){
			token = getNumLiteral();
		}else{
			TokenType type;
			String lexeme = "" + charAtHand;
			List<TokenType> possibleTypes = new ArrayList<TokenType>();
			
			for(TokenType t : TokenType.values()){
				if(charAtHand == t.getIdString().charAt(0))
					possibleTypes.add(t);
			}
			int i = 0;
			while(possibleTypes.size() > 1){
				lexeme += nextChar();
				i++;
				for(TokenType t : possibleTypes){
					if(charAtHand != t.getIdString().charAt(i))
						possibleTypes.remove(t);
				}
			}
			type = possibleTypes.get(0);
			if(Character.isLetter(lexeme.charAt(0)))
				while(Character.isDigit(charAtHand) || Character.isLetter(charAtHand) || charAtHand == '_'){
					type = TokenType.ID;
					lexeme += charAtHand;
					nextChar();
				}
			token = new Token(type, line, column, lexeme);
		}
		
		return token;
	}
	
	/**
	 * returns the next char in source, incrementing line and column appropriately
	 * also saves the char to charAtHand
	 * @return the next char in source
	 * @throws IOException
	 */
	private char nextChar() throws IOException{
		charAtHand = (char) source.read();
		if(charAtHand == '\n'){
			line ++;
		}else
			column ++;
		return charAtHand;
	}
	
	private Token getNumLiteral() throws IOException{
		Token token;
		String characters = "" + charAtHand;//List<Character> characters = new ArrayList<Character>();
		TokenType type;
		
		//characters.add(charAtHand);
		while(Character.isDigit(nextChar())){
			characters += charAtHand;
		}
		
		if(nextChar() == '.'){
			type = TokenType.FLOAT;
			characters += charAtHand;
			while(Character.isDigit(nextChar())){
				characters += charAtHand;
			}
		}else{
			type = TokenType.INT;
		}
		
		token = new Token(type, line, column, characters);
		return token;
	}
	
	/**
	 * Reads up to byte.length tokens from this input stream into an array of tokens. This method blocks until some input is available.
	 * This method simply performs the call read(b, 0, b.length) and returns the result. It is important that it does not do source.read(b).
	 * @param b - the buffer into which the data is read.
	 * @return the total number of tokens read into the buffer, or -1 if there is no more data because the end of the stream has been reached; in this case the last token written will be the end of stream token.
	 * @throws IOException
	 */
	public int read(Token[] b)throws IOException{
		return 0;
	}
	
	/**
	 * Reads tokens into a portion of an array. This method will block until some input is available, an I/O error occurs, or the end of the stream is reached; in this case the last token written will be the end of stream token.
	 * @param b - Destination buffer
	 * @param off - Offset at which to start storing tokens
	 * @param len - Maximum number of tokens to read
	 * @return The number of characters read, or -1 if the end of the stream has been reached
	 * @throws IOException
	 */
	public int read(Token[] b, int off, int len) throws IOException{
		return 0;
	}
	
	
	/**
	 * Skips tokens. This method will block until some tokens are available, an I/O error occurs, or the end of the stream is reached.
	 * @param n - the number of tokens to skip.
	 * @return the number of tokens actually skipped.
	 */
	public long skip(long n){
		return 0;
	}

}
