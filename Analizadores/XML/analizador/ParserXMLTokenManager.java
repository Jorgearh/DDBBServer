/* Generated By:JavaCC: Do not edit this line. ParserXMLTokenManager.java */

/** Token Manager. */
public class ParserXMLTokenManager implements ParserXMLConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x1f00L) != 0L)
         {
            jjmatchedKind = 19;
            return 56;
         }
         if ((active0 & 0x80L) != 0L)
         {
            jjmatchedKind = 19;
            return 2;
         }
         return -1;
      case 1:
         if ((active0 & 0x1f80L) != 0L)
         {
            jjmatchedKind = 19;
            jjmatchedPos = 1;
            return 54;
         }
         return -1;
      case 2:
         if ((active0 & 0x1f80L) != 0L)
         {
            jjmatchedKind = 19;
            jjmatchedPos = 2;
            return 54;
         }
         return -1;
      case 3:
         if ((active0 & 0x300L) != 0L)
         {
            if (jjmatchedPos != 3)
            {
               jjmatchedKind = 19;
               jjmatchedPos = 3;
            }
            return 54;
         }
         if ((active0 & 0x1c80L) != 0L)
            return 54;
         return -1;
      case 4:
         if ((active0 & 0x1300L) != 0L)
         {
            jjmatchedKind = 19;
            jjmatchedPos = 4;
            return 54;
         }
         return -1;
      case 5:
         if ((active0 & 0x1100L) != 0L)
         {
            jjmatchedKind = 19;
            jjmatchedPos = 5;
            return 54;
         }
         if ((active0 & 0x200L) != 0L)
            return 54;
         return -1;
      case 6:
         if ((active0 & 0x1000L) != 0L)
         {
            if (jjmatchedPos != 6)
            {
               jjmatchedKind = 19;
               jjmatchedPos = 6;
            }
            return 54;
         }
         if ((active0 & 0x100L) != 0L)
            return 54;
         return -1;
      case 7:
         if ((active0 & 0x1000L) != 0L)
            return 54;
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 60:
         return jjMoveStringLiteralDfa1_0(0x7ffffffe00000L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa1_0(0x400L);
      case 68:
      case 100:
         return jjMoveStringLiteralDfa1_0(0x1a00L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa1_0(0x100L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa1_0(0x80L);
      default :
         return jjMoveNfa_0(3, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 47:
         return jjMoveStringLiteralDfa2_0(active0, 0x556669a400000L);
      case 65:
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x1800L);
      case 68:
      case 100:
         return jjMoveStringLiteralDfa2_0(active0, 0x20800000L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x80L);
      case 77:
      case 109:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000200000L);
      case 78:
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000100L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000100000600L);
      case 80:
      case 112:
         return jjMoveStringLiteralDfa2_0(active0, 0x44000000L);
      case 82:
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x9000000000L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x90800000000L);
      case 86:
      case 118:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000000L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 65:
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x280825200000L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000100800000L);
      case 68:
      case 100:
         return jjMoveStringLiteralDfa3_0(active0, 0x410000000L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x800000000000L);
      case 77:
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000000400000L);
      case 78:
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000000L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x4009200000400L);
      case 80:
      case 112:
         return jjMoveStringLiteralDfa3_0(active0, 0x88000000L);
      case 82:
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x42040000000L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa3_0(active0, 0x124000001900L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x200L);
      case 86:
      case 118:
         return jjMoveStringLiteralDfa3_0(active0, 0x400000000000L);
      case 88:
      case 120:
         return jjMoveStringLiteralDfa3_0(active0, 0x80L);
      case 89:
      case 121:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000000000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x800000L) != 0L)
            return jjStopAtPos(3, 23);
         break;
      case 65:
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x50440a400000L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x4080a10000200L);
      case 69:
      case 101:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 11;
            jjmatchedPos = 3;
         }
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000001100L);
      case 74:
      case 106:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000100000000L);
      case 76:
      case 108:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(3, 10, 54);
         return jjMoveStringLiteralDfa4_0(active0, 0x200000000000L);
      case 77:
      case 109:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x42040000000L);
      case 80:
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x10000000000L);
      case 82:
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x80000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x200000L);
      case 84:
      case 116:
         if ((active0 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(3, 7, 54);
         return jjMoveStringLiteralDfa4_0(active0, 0x800024000000L);
      case 87:
      case 119:
         return jjMoveStringLiteralDfa4_0(active0, 0x9000000000L);
      case 89:
      case 121:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000000000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x10000000L) != 0L)
            return jjStopAtPos(4, 28);
         else if ((active0 & 0x8000000000L) != 0L)
            return jjStopAtPos(4, 39);
         break;
      case 65:
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x20000000L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa5_0(active0, 0x104000000000L);
      case 67:
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x40000000L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x2010101000000L);
      case 71:
      case 103:
         return jjMoveStringLiteralDfa5_0(active0, 0x100L);
      case 72:
      case 104:
         return jjMoveStringLiteralDfa5_0(active0, 0x800004000000L);
      case 74:
      case 106:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000200000000L);
      case 76:
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0x480800000200L);
      case 77:
      case 109:
         return jjMoveStringLiteralDfa5_0(active0, 0x2000000L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x80000000L);
      case 80:
      case 112:
         return jjMoveStringLiteralDfa5_0(active0, 0x20000000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000400000L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000408201000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x200000000000L);
      case 87:
      case 119:
         return jjMoveStringLiteralDfa5_0(active0, 0x42000000000L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x1000000L) != 0L)
            return jjStopAtPos(5, 24);
         else if ((active0 & 0x4000000L) != 0L)
            return jjStopAtPos(5, 26);
         else if ((active0 & 0x1000000000L) != 0L)
            return jjStopAtPos(5, 36);
         else if ((active0 & 0x10000000000L) != 0L)
            return jjStopAtPos(5, 40);
         else if ((active0 & 0x40000000000L) != 0L)
            return jjStopAtPos(5, 42);
         break;
      case 65:
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x400000000L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa6_0(active0, 0x20000000L);
      case 67:
      case 99:
         return jjMoveStringLiteralDfa6_0(active0, 0x2000180000000L);
      case 69:
      case 101:
         if ((active0 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(5, 9, 54);
         return jjMoveStringLiteralDfa6_0(active0, 0x42a0a42200100L);
      case 72:
      case 104:
         return jjMoveStringLiteralDfa6_0(active0, 0x1000008000000L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa6_0(active0, 0x1000L);
      case 76:
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x104000000000L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa6_0(active0, 0x800000000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa6_0(active0, 0x2000000000L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa6_0(active0, 0x400000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa6_0(active0, 0x400000000000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x2000000L) != 0L)
            return jjStopAtPos(6, 25);
         else if ((active0 & 0x8000000L) != 0L)
            return jjStopAtPos(6, 27);
         else if ((active0 & 0x800000000L) != 0L)
         {
            jjmatchedKind = 35;
            jjmatchedPos = 6;
         }
         else if ((active0 & 0x2000000000L) != 0L)
            return jjStopAtPos(6, 37);
         else if ((active0 & 0x20000000000L) != 0L)
            return jjStopAtPos(6, 41);
         else if ((active0 & 0x80000000000L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 6;
         }
         else if ((active0 & 0x200000000000L) != 0L)
            return jjStopAtPos(6, 45);
         break;
      case 65:
      case 97:
         return jjMoveStringLiteralDfa7_0(active0, 0x20000000L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa7_0(active0, 0x400000000L);
      case 67:
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x4000200000000L);
      case 68:
      case 100:
         return jjMoveStringLiteralDfa7_0(active0, 0x800040000000L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa7_0(active0, 0x504080400000L);
      case 77:
      case 109:
         return jjMoveStringLiteralDfa7_0(active0, 0x1000L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa7_0(active0, 0x1000000000000L);
      case 82:
      case 114:
         if ((active0 & 0x100L) != 0L)
            return jjStartNfaWithStates_0(6, 8, 54);
         return jjMoveStringLiteralDfa7_0(active0, 0x200000L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa7_0(active0, 0x2000100000000L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x200000L) != 0L)
            return jjStopAtPos(7, 21);
         else if ((active0 & 0x4000000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 7;
         }
         else if ((active0 & 0x100000000000L) != 0L)
         {
            jjmatchedKind = 44;
            jjmatchedPos = 7;
         }
         else if ((active0 & 0x400000000000L) != 0L)
            return jjStopAtPos(7, 46);
         else if ((active0 & 0x800000000000L) != 0L)
            return jjStopAtPos(7, 47);
         else if ((active0 & 0x2000000000000L) != 0L)
            return jjStopAtPos(7, 49);
         break;
      case 65:
      case 97:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000000L);
      case 68:
      case 100:
         return jjMoveStringLiteralDfa8_0(active0, 0x1000080000000L);
      case 69:
      case 101:
         if ((active0 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(7, 12, 54);
         break;
      case 82:
      case 114:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa8_0(active0, 0x120000000L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa8_0(active0, 0x4000200000000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa8_0(active0, 0x40000000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x400000L) != 0L)
            return jjStopAtPos(8, 22);
         else if ((active0 & 0x100000000L) != 0L)
            return jjStopAtPos(8, 32);
         else if ((active0 & 0x1000000000000L) != 0L)
            return jjStopAtPos(8, 48);
         else if ((active0 & 0x4000000000000L) != 0L)
            return jjStopAtPos(8, 50);
         break;
      case 69:
      case 101:
         return jjMoveStringLiteralDfa9_0(active0, 0x20000000L);
      case 82:
      case 114:
         return jjMoveStringLiteralDfa9_0(active0, 0x40000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa9_0(active0, 0x600000000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa9_0(active0, 0x80000000L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x20000000L) != 0L)
            return jjStopAtPos(9, 29);
         else if ((active0 & 0x200000000L) != 0L)
            return jjStopAtPos(9, 33);
         break;
      case 69:
      case 101:
         return jjMoveStringLiteralDfa10_0(active0, 0x440000000L);
      case 82:
      case 114:
         return jjMoveStringLiteralDfa10_0(active0, 0x80000000L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x400000000L) != 0L)
            return jjStopAtPos(10, 34);
         break;
      case 69:
      case 101:
         return jjMoveStringLiteralDfa11_0(active0, 0x80000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa11_0(active0, 0x40000000L);
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
private int jjMoveStringLiteralDfa11_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x40000000L) != 0L)
            return jjStopAtPos(11, 30);
         break;
      case 83:
      case 115:
         return jjMoveStringLiteralDfa12_0(active0, 0x80000000L);
      default :
         break;
   }
   return jjStartNfa_0(10, active0);
}
private int jjMoveStringLiteralDfa12_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(10, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(11, active0);
      return 12;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x80000000L) != 0L)
            return jjStopAtPos(12, 31);
         break;
      default :
         break;
   }
   return jjStartNfa_0(11, active0);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 56;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 19)
                        kind = 19;
                     jjCheckNAdd(54);
                  }
                  else if (curChar == 58)
                     jjCheckNAdd(11);
                  break;
               case 3:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 13)
                        kind = 13;
                     jjCheckNAddStates(0, 6);
                  }
                  else if (curChar == 45)
                     jjCheckNAddTwoStates(19, 21);
                  else if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 12;
                  else if (curChar == 34)
                     jjCheckNAddTwoStates(9, 10);
                  break;
               case 56:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 19)
                        kind = 19;
                     jjCheckNAdd(54);
                  }
                  else if (curChar == 58)
                     jjCheckNAdd(11);
                  break;
               case 8:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(9, 10);
                  break;
               case 9:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(9, 10);
                  break;
               case 10:
                  if (curChar == 34 && kind > 18)
                     kind = 18;
                  break;
               case 11:
                  if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 13:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(13, 11);
                  break;
               case 14:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjAddStates(7, 8);
                  break;
               case 15:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 17:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 18:
                  if (curChar == 45)
                     jjCheckNAddTwoStates(19, 21);
                  break;
               case 19:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjCheckNAddTwoStates(20, 19);
                  break;
               case 20:
                  if (curChar == 45)
                     jjCheckNAdd(19);
                  break;
               case 21:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(9, 11);
                  break;
               case 22:
                  if (curChar == 45)
                     jjCheckNAdd(21);
                  break;
               case 23:
                  if (curChar == 46)
                     jjCheckNAdd(24);
                  break;
               case 24:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 14)
                     kind = 14;
                  jjCheckNAdd(24);
                  break;
               case 25:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjCheckNAddStates(0, 6);
                  break;
               case 26:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 27;
                  break;
               case 27:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 28;
                  break;
               case 28:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 29:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 30;
                  break;
               case 30:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 31;
                  break;
               case 31:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 32;
                  break;
               case 32:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 33;
                  break;
               case 33:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 34;
                  break;
               case 34:
                  if ((0x3ff000000000000L & l) != 0L && kind > 16)
                     kind = 16;
                  break;
               case 35:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 36:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 37;
                  break;
               case 37:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 38;
                  break;
               case 38:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 39;
                  break;
               case 39:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 40;
                  break;
               case 40:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 41;
                  break;
               case 41:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 42;
                  break;
               case 42:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 43;
                  break;
               case 43:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 44;
                  break;
               case 44:
                  if (curChar == 32)
                     jjstateSet[jjnewStateCnt++] = 45;
                  break;
               case 45:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 46;
                  break;
               case 46:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 47;
                  break;
               case 47:
                  if (curChar == 58)
                     jjstateSet[jjnewStateCnt++] = 48;
                  break;
               case 48:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 49;
                  break;
               case 49:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 50;
                  break;
               case 50:
                  if (curChar == 58)
                     jjstateSet[jjnewStateCnt++] = 51;
                  break;
               case 51:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 52;
                  break;
               case 52:
                  if ((0x3ff000000000000L & l) != 0L && kind > 17)
                     kind = 17;
                  break;
               case 54:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAdd(54);
                  break;
               case 55:
                  if (curChar == 58)
                     jjCheckNAdd(11);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 19)
                        kind = 19;
                     jjCheckNAdd(54);
                  }
                  if ((0x4000000040000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 3:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 19)
                        kind = 19;
                     jjCheckNAddTwoStates(54, 55);
                  }
                  if ((0x4000000040L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 6;
                  else if ((0x10000000100000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 56:
               case 54:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAdd(54);
                  break;
               case 0:
                  if ((0x2000000020L & l) != 0L && kind > 15)
                     kind = 15;
                  break;
               case 1:
                  if ((0x20000000200000L & l) != 0L)
                     jjCheckNAdd(0);
                  break;
               case 4:
                  if ((0x8000000080000L & l) != 0L)
                     jjCheckNAdd(0);
                  break;
               case 5:
                  if ((0x100000001000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 6:
                  if ((0x200000002L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 7:
                  if ((0x4000000040L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 9:
                  jjAddStates(12, 13);
                  break;
               case 12:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddStates(14, 17);
                  break;
               case 13:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(13, 11);
                  break;
               case 14:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddTwoStates(14, 15);
                  break;
               case 16:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAdd(17);
                  break;
               case 17:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAdd(17);
                  break;
               case 53:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddTwoStates(54, 55);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 9:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(12, 13);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 56 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   20, 19, 22, 21, 23, 26, 35, 14, 15, 22, 21, 23, 9, 10, 13, 11, 
   14, 15, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, null, null, null, null, null, };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
static final long[] jjtoToken = {
   0x7ffffffffff81L, 
};
static final long[] jjtoSkip = {
   0x1eL, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[56];
private final int[] jjstateSet = new int[112];
protected char curChar;
/** Constructor. */
public ParserXMLTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public ParserXMLTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 56; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
