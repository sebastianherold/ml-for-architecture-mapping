/* Generated By:JJTree&JavaCC: Do not edit this line. AnimTimeParser.java */
package com.kitfox.svg.animation.parser;

import com.kitfox.svg.SVGConst;
import com.kitfox.svg.animation.TimeBase;
import com.kitfox.svg.animation.TimeCompound;
import com.kitfox.svg.animation.TimeDiscrete;
import com.kitfox.svg.animation.TimeIndefinite;
import com.kitfox.svg.animation.TimeLookup;
import com.kitfox.svg.animation.TimeSum;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnimTimeParser/*@bgen(jjtree)*/implements AnimTimeParserTreeConstants, AnimTimeParserConstants {/*@bgen(jjtree)*/
  protected JJTAnimTimeParserState jjtree = new JJTAnimTimeParserState();/**
     * Test the parser
     */
    public static void main(String args[]) throws ParseException
    {
//        AnimTimeParser parser = new AnimTimeParser(System.in);
        StringReader reader;

        reader = new StringReader("1:30 + 5ms");
        AnimTimeParser parser = new AnimTimeParser(reader);
        TimeBase tc;

        tc = parser.Expr();
        System.err.println("AnimTimeParser eval to " + tc.evalTime());

        reader = new StringReader("19");
        parser.ReInit(reader);
        tc = parser.Expr();
        System.err.println("AnimTimeParser eval to " + tc.evalTime());
    }

/**
 * Expression structure
 */
  final public TimeBase Expr() throws ParseException {
 /*@bgen(jjtree) Expr */
    ASTExpr jjtn000 = new ASTExpr(JJTEXPR);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);TimeBase term;
    ArrayList list = new ArrayList();
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case FLOAT:
      case INDEFINITE:
      case MOUSE_OVER:
      case WHEN_NOT_ACTIVE:
      case IDENTIFIER:
        term = Sum();
            list.add(term);
        break;
      default:
        jj_la1[0] = jj_gen;
        ;
      }
      label_1:
      while (true) {
        if (jj_2_1(2)) {
          ;
        } else {
          break label_1;
        }
        jj_consume_token(15);
        term = Sum();
            list.add(term);
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
        jj_consume_token(15);
        break;
      default:
        jj_la1[1] = jj_gen;
        ;
      }
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
        switch (list.size())
        {
            case 0:
                {if (true) return new TimeIndefinite();}
            case 1:
                {if (true) return (TimeBase)list.get(0);}
            default:
                {if (true) return new TimeCompound(list);}
        }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public TimeBase Sum() throws ParseException {
 /*@bgen(jjtree) Sum */
    ASTSum jjtn000 = new ASTSum(JJTSUM);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t = null;
    TimeBase t1;
    TimeBase t2 = null;
    try {
      t1 = Term();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 16:
      case 17:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 16:
          t = jj_consume_token(16);
          break;
        case 17:
          t = jj_consume_token(17);
          break;
        default:
          jj_la1[2] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        t2 = Term();
        break;
      default:
        jj_la1[3] = jj_gen;
        ;
      }
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
            if (t2 == null) {if (true) return t1;}

            if (t.image.equals("-"))
            {
                {if (true) return new TimeSum(t1, t2, false);}
            }
            else
            {
                {if (true) return new TimeSum(t1, t2, true);}
            }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public TimeBase Term() throws ParseException {
 /*@bgen(jjtree) Term */
    ASTTerm jjtn000 = new ASTTerm(JJTTERM);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);TimeBase base;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INDEFINITE:
        base = IndefiniteTime();
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          {if (true) return base;}
        break;
      case INTEGER:
      case FLOAT:
        base = LiteralTime();
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          {if (true) return base;}
        break;
      case IDENTIFIER:
        base = LookupTime();
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          {if (true) return base;}
        break;
      case MOUSE_OVER:
      case WHEN_NOT_ACTIVE:
        base = EventTime();
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
          {if (true) return base;}
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public TimeIndefinite IndefiniteTime() throws ParseException {
 /*@bgen(jjtree) IndefiniteTime */
  ASTIndefiniteTime jjtn000 = new ASTIndefiniteTime(JJTINDEFINITETIME);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(INDEFINITE);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
        {if (true) return new TimeIndefinite();}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public TimeDiscrete EventTime() throws ParseException {
 /*@bgen(jjtree) EventTime */
  ASTEventTime jjtn000 = new ASTEventTime(JJTEVENTTIME);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case MOUSE_OVER:
        jj_consume_token(MOUSE_OVER);
        break;
      case WHEN_NOT_ACTIVE:
        jj_consume_token(WHEN_NOT_ACTIVE);
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
            //For now, map all events to the zero time
            {if (true) return new TimeDiscrete(0);}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public TimeDiscrete LiteralTime() throws ParseException {
 /*@bgen(jjtree) LiteralTime */
    ASTLiteralTime jjtn000 = new ASTLiteralTime(JJTLITERALTIME);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);double t1, t2, t3 = Double.NaN, value;
    Token t;
    try {
      t1 = Number();
            value = t1;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case UNITS:
      case 18:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 18:
          jj_consume_token(18);
          t2 = Number();
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case 18:
            jj_consume_token(18);
            t3 = Number();
            break;
          default:
            jj_la1[6] = jj_gen;
            ;
          }
            //Return clock time format (convert to seconds)
            if (Double.isNaN(t3))
            {
                value = t1 * 60 + t2;
            }
            else
            {
                value = t1 * 3600 + t2 * 60 + t3;
            }
          break;
        case UNITS:
          t = jj_consume_token(UNITS);
            //Return units format (convert to seconds)
            if (t.image.equals("ms")) value = t1 / 1000;
            if (t.image.equals("min")) value = t1 * 60;
            if (t.image.equals("h")) value = t1 * 3600;
          break;
        default:
          jj_la1[7] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[8] = jj_gen;
        ;
      }
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
        {if (true) return new TimeDiscrete(value);}
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public TimeLookup LookupTime() throws ParseException {
 /*@bgen(jjtree) LookupTime */
    ASTLookupTime jjtn000 = new ASTLookupTime(JJTLOOKUPTIME);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);double paramNum = 0.0;
    Token node, event;
    try {
      node = jj_consume_token(IDENTIFIER);
      jj_consume_token(19);
      event = jj_consume_token(IDENTIFIER);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 20:
        paramNum = ParamList();
        break;
      default:
        jj_la1[9] = jj_gen;
        ;
      }
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
        {if (true) return new TimeLookup(null, node.image, event.image, "" + paramNum);}
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public double ParamList() throws ParseException {
 /*@bgen(jjtree) ParamList */
    ASTParamList jjtn000 = new ASTParamList(JJTPARAMLIST);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);double num;
    try {
      jj_consume_token(20);
      num = Number();
      jj_consume_token(21);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
        {if (true) return num;}
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public double Number() throws ParseException {
 /*@bgen(jjtree) Number */
    ASTNumber jjtn000 = new ASTNumber(JJTNUMBER);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FLOAT:
        t = jj_consume_token(FLOAT);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
        try {
            {if (true) return Double.parseDouble(t.image);}
        }
        catch (Exception e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                "Could not parse double '" + t.image + "'", e);
        }

        {if (true) return 0.0;}
        break;
      case INTEGER:
        t = jj_consume_token(INTEGER);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
        try {
            {if (true) return Double.parseDouble(t.image);}
        }
        catch (Exception e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                "Could not parse double '" + t.image + "'", e);
        }

        {if (true) return 0.0;}
        break;
      default:
        jj_la1[10] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public int Integer() throws ParseException {
 /*@bgen(jjtree) Integer */
    ASTInteger jjtn000 = new ASTInteger(JJTINTEGER);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(INTEGER);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
        try {
            {if (true) return Integer.parseInt(t.image);}
        }
        catch (Exception e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                "Could not parse int '" + t.image + "'", e);
        }

        {if (true) return 0;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  private boolean jj_3R_13() {
    if (jj_scan_token(FLOAT)) return true;
    return false;
  }

  private boolean jj_3R_12() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_13()) {
    jj_scanpos = xsp;
    if (jj_3R_14()) return true;
    }
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_6() {
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3R_5() {
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3R_2() {
    if (jj_3R_3()) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_scan_token(15)) return true;
    if (jj_3R_2()) return true;
    return false;
  }

  private boolean jj_3R_3() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_4()) {
    jj_scanpos = xsp;
    if (jj_3R_5()) {
    jj_scanpos = xsp;
    if (jj_3R_6()) {
    jj_scanpos = xsp;
    if (jj_3R_7()) return true;
    }
    }
    }
    return false;
  }

  private boolean jj_3R_4() {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_14() {
    if (jj_scan_token(INTEGER)) return true;
    return false;
  }

  private boolean jj_3R_11() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(11)) {
    jj_scanpos = xsp;
    if (jj_scan_token(12)) return true;
    }
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_scan_token(INDEFINITE)) return true;
    return false;
  }

  private boolean jj_3R_9() {
    if (jj_3R_12()) return true;
    return false;
  }

  /** Generated Token Manager. */
  public AnimTimeParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[11];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x5f00,0x8000,0x30000,0x30000,0x5f00,0x1800,0x40000,0x42000,0x42000,0x100000,0x300,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public AnimTimeParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public AnimTimeParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new AnimTimeParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
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
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public AnimTimeParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new AnimTimeParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public AnimTimeParser(AnimTimeParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(AnimTimeParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
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
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[22];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 11; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 22; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
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

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
