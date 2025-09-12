package lox;

import java.util.ArrayList;
import java.util.List;

import static lox.TokenType.*;

public class Scanner {
  /**
   * The string of the source code to be parsed
   */
  private final String source;
  /**
   * A list to populate with tokens from lexemes identified in the parsed source code
   */
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  public Scanner(String source) {
    this.source = source;
  }

  public List<Token> scanTokens() {
    while(!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    char c = advance();

    switch (c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      case '/':
        if(match('/')) {
          while(peek() != '\n' && !isAtEnd()) advance();
        } else  {
          addToken(SLASH);
        }
        break;
      case ' ':
      case '\r':
      case '\t':
        break;
      case '\n':
        line++;
        break;

      default: com.craftinginterprers.lox.Lox.error(line, "Unexpected character.");
      break;
    }
  }

  /**
   * @return next character in the source file
   */
  private char advance() {
    return source.charAt(current++);
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  /**
   *
   * @param type type of token that has been identified - mapped against TokenType enum
   *
   */
  private void addToken(TokenType type) {
    addToken(type, null);
  }

  /**
   * Takes substring of the identified token from the source file and appends it to the tokens list.
   * @param type type of token that has been identified - mapped against TokenType enum
   * @param literal object literal that has been identified - mapped against TokenType enum
   */
  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private boolean match(char expected) {
    if(isAtEnd()) return false;
    if(source.charAt(current) != expected) return false;

    current++;
    return true;
  }

  private char peek() {
    if(isAtEnd()) return '\0';
    return source.charAt(current);
  }
}
