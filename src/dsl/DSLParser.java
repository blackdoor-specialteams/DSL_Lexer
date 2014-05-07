/**
 * 
 */
package dsl;

import java.io.IOException;
import java.io.Reader;

import dsl.Token.TokenType;

/**
 * @author nfischer3
 *
 */
public class DSLParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}
	
	private DSLexerStream tokens; 
	private Token token;
	private int line;
	private int col;
	
	public DSLParser(Reader source){
		tokens = new DSLexerStream(source);
	}
	
	private boolean nextIsA(TokenType t) throws IOException{
		return next().getTokenType().equals(t);
	}
	
	private Token next() throws IOException{
		token = tokens.read();
		line = token.getTokenLine();
		col = token.getTokenColumn();
		System.out.println(token);
		return token;
	}

	public void eval() {
		boolean valid = true;
		try {
			while (true) {

				valid &= stmt();

				if (!valid)
					break;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			if(!e.getMessage().equalsIgnoreCase("eos"))
				valid = false;
		}
		if(valid)
			System.out.println("Token stream is valid");
		else
			System.out.println("Token stream is not valid, error at " + line + ":" + col);
	}
//	# start symbol
//    <stmt>   ::= <expr> ';' | <let> ';' | EOS
	private boolean stmt() throws Exception{
		Token next = next();
		if(next.getTokenType().equals(TokenType.EOS))
			throw new Exception("eos");
		if(next.getTokenType().equals(TokenType.ERROR))
			throw new Exception("err");
		boolean validGrammar;
		if(next.getTokenType() == TokenType.LET_EXP){
			validGrammar = let();
		}else{
			validGrammar = expr();
		}
		return validGrammar && next().getTokenType().equals(TokenType.SEMICOLON);
	}
	
//
//    # expressions
//    <expr>   ::= <if> | <calc> 
	private boolean expr() throws IOException{
		System.out.println("expr");
		Token next = next();
		boolean validGrammar;
		if(next.getTokenType().equals(TokenType.IF_EXP)){
			validGrammar = if_();
		}else{
			validGrammar = calc();
		}
		return validGrammar;
	}
//
//    # if expression
//    <if>     ::= 'if' <bexpr> 'then' <expr> 'else' <expr>
	private boolean if_() throws IOException{
		boolean valid = bexpr();
		valid &= nextIsA(TokenType.THEN_EXP);
		valid &= expr();
		valid &= nextIsA(TokenType.ELSE_EXP);
		valid &= expr();
		return valid;
	}
//    <bexpr>  ::= ( <ID> | <val> ) <comp> ( <ID> | <val> )
	private boolean bexpr() throws IOException{
		boolean valid = true;
		Token next = next();
		if(!nextIsIDorVal())
			return false;
		valid = comp();
		valid &= nextIsIDorVal();
		return valid;
	}
	
	private boolean nextIsIDorVal() throws IOException{
		Token next = next();
		if(next.getTokenType().equals(TokenType.ID) || isVal(next))
			return true;
		return false;
	}
//    <comp>   ::= <LESS_THAN> | <GREATER_THAN> | <EQUAL> 
//              |  <GREATER_THAN_EQUAL> | <LESS_THAN_EQUAL> 
//              |  <NOT_EQUAL>
	private boolean comp() throws IOException{
		TokenType t = next().getTokenType();
		return t.equals(TokenType.LESS_THAN) || t.equals(TokenType.GREATER_THAN)
				|| t.equals(TokenType.EQUAL) || t.equals(TokenType.GREATER_THAN_EQUAL)
				|| t.equals(TokenType.LESS_THAN_EQUAL) || t.equals(TokenType.NOT_EQUAL);
	}
//    <val>    ::= <INT_VAL> | <FLOAT_VAL> | <STRING_VAL>
	private boolean isVal(Token t){
		TokenType tt = t.getTokenType();
		return tt.equals(TokenType.INT_VAL) || tt.equals(TokenType.FLOAT_VAL) || tt.equals(TokenType.STRING_VAL);
	}
//
//    # computation expression
//    <calc>   ::= ( <ID> | <val> | <read> ) <rcalc> 
	private boolean calc() throws IOException{
		Token t = next();
		return (t.getTokenType().equals(TokenType.ID) || isVal(t) || 
				read(t) ) //TODO does read need a param?
				&& rcalc();
	}
//    <rcalc>  ::= <op> <calc> | empty
	private boolean rcalc(){
		return true;//op() && calc();//TODO
	}
//    <op>     ::= <PLUS_OP> | <MINUS_OP> | <TIMES_OP> | <DIVIDE_OP>
	private boolean isOp(Token t){
		TokenType tt = t.getTokenType();
		return tt.equals(TokenType.PLUS_OP) || tt.equals(TokenType.MINUS_OP) ||
				tt.equals(TokenType.DIVIDE_OP) || tt.equals(TokenType.TIMES_OP);
	}
//
//    # read expression 
//    <read>   ::= <readt> '(' <readr>
	private boolean read(Token t) throws IOException{
		if(nextIsA(TokenType.LPAREN))
			return false;
		return readt(t) && readr();
	}
//    <readr>  ::= <STRING_VAL> ')' | ')'
	private boolean readr() throws IOException{
		if(nextIsA(TokenType.RPAREN))
			return true;
		if(nextIsA(TokenType.STRING_VAL) && nextIsA(TokenType.RPAREN))
			return true;
		return false;
	}
//    <readt>  ::= <IREAD> | <FREAD> | <SREAD>
	private boolean readt(Token t){
		TokenType tt = t.getTokenType();
		return tt.equals(TokenType.IREAD) || tt.equals(TokenType.FREAD) 
				|| tt.equals(TokenType.SREAD);
	}
//
//    # let expression
//    <let>    ::= 'let' <decls> 'in' <expr>
	private boolean let() throws IOException{
		System.out.println("let");
		boolean valid = decls();
		valid &= nextIsA(TokenType.IN_EXP);
		valid &= expr();
		return valid;
	}
//    <decls>  ::= <decl> <declsr>
	private boolean decls() throws IOException{
		return decl() && declsr();
	}
//    <declsr> ::= ',' <decls> | empty
	private boolean declsr() throws IOException{
		//TODO empty case?
		return nextIsA(TokenType.COMMA) && decls();
	}
//    <decl>   ::= <ID> '=' <expr> | <while>
	private boolean decl() throws IOException{
		TokenType tt = next().getTokenType();
		if(tt.equals(TokenType.ID)){
			return nextIsA(TokenType.ASSIGN) && expr();
		}else if(tt.equals(TokenType.WHILE)){
			return while_();
		}else{
			return false;
		}

	}
//
//    # while statements
//    <while>  ::= 'while' <bexpr> ':' <ID> '=' <expr>
	private boolean while_() throws IOException{
		return bexpr() && nextIsA(TokenType.COLON) && nextIsA(TokenType.ID) 
				&& nextIsA(TokenType.ASSIGN) && expr();
	}
}
