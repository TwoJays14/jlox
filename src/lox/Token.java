package lox;

public class Token {
  final TokenType type;
  final String lexeme;
  final Object literal;
  final int line;

  private Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  public static Token Create(TokenType type, String lexeme, Object literal, int line) {
    if(type == null) {
      return null;
    }

    return new Token(type, lexeme, literal, line);
  }

  public String toString() {
    return type + " " + lexeme + " " + literal;
  }
}
