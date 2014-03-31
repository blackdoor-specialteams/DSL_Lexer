/**
 * 
 */
package dsl;

import java.io.Reader;


/**
 * @author nfischer3
 *
 */
public class DSLexerStream{

	/**
	 * @param source - the Reader containing a character stream that represents
	 * source code
	 */
	public DSLexerStream(Reader source) {
		
	}
	
	/**
	 * Reads a single token. This method will block until a token is available, an I/O error occurs, or the end of the stream is reached.
	 * @return The token read.
	 */
	public Token read(){
		return null;
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
