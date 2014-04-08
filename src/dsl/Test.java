package dsl;


import java.io.FileReader;
import java.io.IOException;

import dsl.Token.TokenType;

public class Test {

	public static void main(String[] args) throws IOException {
		DSLexerStream lexer = new DSLexerStream(new FileReader("test_src.txt"));
		Token read=lexer.read();
		while(!read.getTokenType().equals(TokenType.EOS)){
			System.out.println(read = lexer.read());
		}

	}

}
