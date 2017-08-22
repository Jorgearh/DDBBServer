/* Generated By:JavaCC: Do not edit this line. ParserXML.java */
        package servidorfisql.interpretes.Analizadores.XML.analizador;

        import servidorfisql.interpretes.Analizadores.Nodo;

        public class ParserXML implements ParserXMLConstants {
                public static void main(String[] args) throws ParseException{
                                try{
                                        ParserXML parser = new ParserXML(System.in);
                                        parser.INI();
                                }catch(ParseException pe){
                                        System.err.println("Error en el analisis...");
                                        System.err.println(pe.getMessage());
                                }
                        }

  final public Nodo INI() throws ParseException {
        Nodo nodo;
    nodo = XML();
    jj_consume_token(0);
                                 {if (true) return nodo;}
    throw new Error("Missing return statement in function");
  }

  final public Nodo XML() throws ParseException {
        Nodo nodo, content;
        Token t;
    t = jj_consume_token(open);
                    nodo = new Nodo(t.image.substring(1, t.image.length() - 1));
    label_1:
    while (true) {
      content = CONTENT();
                                              nodo.agregarHijo(content);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case _text:
      case _integer:
      case _double:
      case _bool:
      case _date:
      case _datetime:
      case pk:
      case fk:
      case nulo:
      case nonulo:
      case unico:
      case autoinc:
      case entero:
      case doble:
      case booleano:
      case fecha:
      case fecha_hora:
      case cadena:
      case codigo:
      case id:
      case path:
      case open:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
    }
    jj_consume_token(close);
         {if (true) return nodo;}
    throw new Error("Missing return statement in function");
  }

  final public Nodo CONTENT() throws ParseException {
        Nodo nodo;
        Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case entero:
    case doble:
    case booleano:
    case fecha:
    case fecha_hora:
    case cadena:
      nodo = VALUE();
      break;
    case _text:
    case _integer:
    case _double:
    case _bool:
    case _date:
    case _datetime:
      nodo = TIPO();
      break;
    case pk:
    case fk:
    case nulo:
    case nonulo:
    case unico:
    case autoinc:
      nodo = COMPLEMENT();
      break;
    case open:
      nodo = XML();
      break;
    case id:
      t = jj_consume_token(id);
                                 nodo = new Nodo("ID", t.image, t.beginLine, t.beginColumn);
      break;
    case codigo:
      t = jj_consume_token(codigo);
                         nodo = new Nodo("COD", t.image, t.beginLine, t.beginColumn);
      break;
    case path:
      t = jj_consume_token(path);
                                 nodo = new Nodo("PATH", t.image, t.beginLine, t.beginColumn);
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
         {if (true) return nodo;}
    throw new Error("Missing return statement in function");
  }

  final public Nodo COMPLEMENT() throws ParseException {
        Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case pk:
      t = jj_consume_token(pk);
      break;
    case fk:
      t = jj_consume_token(fk);
      break;
    case nulo:
      t = jj_consume_token(nulo);
      break;
    case nonulo:
      t = jj_consume_token(nonulo);
      break;
    case unico:
      t = jj_consume_token(unico);
      break;
    case autoinc:
      t = jj_consume_token(autoinc);
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
         {if (true) return new Nodo("COMPLEMENTO", t.image, t.beginLine, t.beginColumn);}
    throw new Error("Missing return statement in function");
  }

  final public Nodo TIPO() throws ParseException {
        Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case _text:
      t = jj_consume_token(_text);
      break;
    case _integer:
      t = jj_consume_token(_integer);
      break;
    case _double:
      t = jj_consume_token(_double);
      break;
    case _bool:
      t = jj_consume_token(_bool);
      break;
    case _date:
      t = jj_consume_token(_date);
      break;
    case _datetime:
      t = jj_consume_token(_datetime);
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
         {if (true) return new Nodo("TIPO", t.image, t.beginLine, t.beginColumn);}
    throw new Error("Missing return statement in function");
  }

  final public Nodo VALUE() throws ParseException {
        Nodo nodo;
        Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case cadena:
      t = jj_consume_token(cadena);
                                 nodo = new Nodo("CAD", t.image.substring(1, t.image.length() - 1), t.beginLine, t.beginColumn);
      break;
    case entero:
      t = jj_consume_token(entero);
                                 nodo = new Nodo("ENT", t.image, t.beginLine, t.beginColumn);
      break;
    case doble:
      t = jj_consume_token(doble);
                                 nodo = new Nodo("DOB", t.image, t.beginLine, t.beginColumn);
      break;
    case booleano:
      t = jj_consume_token(booleano);
                                 nodo = new Nodo("BOOL", t.image, t.beginLine, t.beginColumn);
      break;
    case fecha:
      t = jj_consume_token(fecha);
                                 nodo = new Nodo("DATE", t.image, t.beginLine, t.beginColumn);
      break;
    case fecha_hora:
      t = jj_consume_token(fecha_hora);
                                 nodo = new Nodo("DATETIME", t.image, t.beginLine, t.beginColumn);
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
         {if (true) return nodo;}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public ParserXMLTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[5];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x1fffff80,0x1fffff80,0x7e000,0x1f80,0x1f80000,};
   }

  /** Constructor with InputStream. */
  public ParserXML(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ParserXML(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserXMLTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public ParserXML(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserXMLTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public ParserXML(ParserXMLTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ParserXMLTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 5; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[30];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 5; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 30; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

                }
