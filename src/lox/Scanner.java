package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

public class Scanner {
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();

    keywords.put("and", AND);
    keywords.put("class", CLASS);
    keywords.put("else", ELSE);
    keywords.put("false", FALSE);
    keywords.put("for", FOR);
    keywords.put("fun", FUN);
    keywords.put("if", IF);
    keywords.put("nil", NIL);
    keywords.put("or", OR);
    keywords.put("print", PRINT);
    keywords.put("return", RETURN);
    keywords.put("super", SUPER);
    keywords.put("this", THIS);
    keywords.put("true", TRUE);
    keywords.put("var", VAR);
    keywords.put("while", WHILE);
    }
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
    while(!isAtFileEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(Token.Create(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    char currentChar = advanceToNextChar();

    switch (currentChar) {
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
        addToken(isCurrentMatch('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(isCurrentMatch('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(isCurrentMatch('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(isCurrentMatch('=') ? GREATER_EQUAL : GREATER);
        break;
      case '/':
        if(isCurrentMatch('/')) {
          while(peekCurrentChar() != '\n' && !isAtFileEnd()) {
            advanceToNextChar();
          }
        } else if (isCurrentMatch('*')) {
          multiLineComment();
        }
        else  {
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
      case '"':
        string();
        break;

      default:
        if(isDigit(currentChar)) {
          number();
        } else if (isAlpha(currentChar)) {
          identifier();
        } else {
          com.craftinginterprers.lox.Lox.error(line, "Unexpected character.");
        }
      break;
    }
  }

  /**
   * @return next character in the source file
   */
  private char advanceToNextChar() {
    return source.charAt(current++);
  }

  private boolean isAtFileEnd() {
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
    tokens.add(Token.Create(type, text, literal, line));
  }

  private boolean isCurrentMatch(char expected) {
    if(isAtFileEnd()) {
      return false;
    }

    if(source.charAt(current) != expected) {
      return false;
    }

    current++;
    return true;
  }

  private char peekCurrentChar() {
    if(isAtFileEnd()) {
      return '\0';
    }

    return source.charAt(current);
  }

  private char peekNextChar() {
    if(current+ 1 >= source.length()) {
      return '\0';
    }

    return source.charAt(current+ 1);
  }

  private void string() {
    while(peekCurrentChar() != '"' && !isAtFileEnd()) {
      if(peekCurrentChar() == '\n') line++;
      advanceToNextChar();
    }

    if(isAtFileEnd()) {
      com.craftinginterprers.lox.Lox.error(line, "Unterminated string.");
    }

    advanceToNextChar(); // To the closing "

    String value = source.substring(start + 1, current - 1); // Gets string without the quotation marks
    addToken(STRING, value);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private void number() {
    while(isDigit(peekCurrentChar())) {
      advanceToNextChar();

      if(peekCurrentChar() == '.' && isDigit(peekNextChar())) {
        advanceToNextChar();

        while(isDigit(peekCurrentChar())) {
          advanceToNextChar();
        }
      }
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void multiLineComment() {
    while(!isAtFileEnd()) {
      if(peekCurrentChar() == '*' && peekNextChar() == '/') {
        advanceToNextChar();
        advanceToNextChar();
        break;
      }
      if(peekCurrentChar() == '\n') {
        line++;
      }
      advanceToNextChar();
    }
  }

  private void identifier() {
    while(isAlphaNumeric(peekCurrentChar())) {
      advanceToNextChar();
    }

    String text = source.substring(start, current);
    TokenType type = keywords.get(text);

    if(type == null) {
      type = IDENTIFIER;
    }

    addToken(type);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
           (c >= 'A' && c <= 'Z') ||
            c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }
}
