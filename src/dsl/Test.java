package dsl;


import java.io.FileReader;
import java.io.IOException;

import dsl.Token.TokenType;

public class Test {

	public static void main(String[] args) throws IOException {
//		DSLexerStream lexer = new DSLexerStream(new FileReader(args[0]));
//		Token read=lexer.read();
//		System.out.println(read);
//		while(!read.getTokenType().equals(TokenType.EOS)){
//			System.out.println(read = lexer.read());
//		}
//		lexer.close();
		DSLParser parse = new DSLParser(new FileReader(args[0]));
		parse.eval();

	}

}
