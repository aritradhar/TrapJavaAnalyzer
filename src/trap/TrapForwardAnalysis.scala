package trap

import soot.toolkits.scalar.ForwardFlowAnalysis
import scala.collection.JavaConversions._
import soot.toolkits.scalar.{FlowSet => Tainted}
import soot.toolkits.graph.UnitGraph
import soot.ValueBox
import soot.jimple.FieldRef
import soot.jimple.InvokeExpr
import soot.jimple.internal.JArrayRef
import soot.jimple.internal.JAssignStmt
import soot.jimple.internal.JIdentityStmt
import soot.jimple.internal.JInvokeStmt
import soot.jimple.internal.JReturnStmt
import soot.jimple.internal.JReturnVoidStmt
//import soot.tagkit.SourceLnPosTag
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.ArrayFlowUniverse
import soot.toolkits.scalar.ArraySparseSet
import soot.{Unit => JimpleCode}
import soot.Value
import TrapUtil._
import TrapPattern._


class TrapForwardAnalysis(in:Tainted, g:UnitGraph, taintParamsIndex:Array[Int], filename:String) 
  extends ForwardFlowAnalysis[JimpleCode, Tainted](g.asInstanceOf[DirectedGraph[JimpleCode]]) {
  val numParms = g.getBody.getMethod.getParameterCount
//  val debug = true
  val debug = false
  def printdbg(s:Any) = if (debug) println(s)
  printdbg (" scanning method: "+g.getBody.getMethod+" num params: "+numParms)
    
  val l = g.getBody.getLocals // local variables
  val f = new ArrayFlowUniverse(l.toArray)
  var affectedParams:List[Int] = Nil
  var isOutputTainted:Boolean = false
  def copy(src:Tainted, dest:Tainted) { src.copy(dest) }
  def merge(in1:Tainted, in2:Tainted, dest:Tainted) { in1.union(in2, dest)} // taint is union
  def entryInitialFlow:Tainted = in
  def newInitialFlow:Tainted = getEmptySet
  
  def flowThrough(in:Tainted, code:JimpleCode, out:Tainted) {
    in.copy(out)        
    val defined = code.getDefBoxes.map(_.asInstanceOf[ValueBox].getValue)
    val used = code.getUseBoxes.map(_.asInstanceOf[ValueBox].getValue)
    printdbg ("   DEBUG =======> "+code.getClass.getSimpleName+ ": "+code)
    //val inStrings = in.toList.map(_.toString)
    lazy val inList = in.toList
    lazy val inDeref = inList.collect(x => x match { case s:FieldRef => s.getField })
    lazy val isTaintUsed = used.foldLeft(false)((x, y) => x | inContains(y)) 
    // to fix this code
    def inContains(v:Value) = v match {
      case f:FieldRef => 
        printdbg ("       ## in:fieldRef "+f+
                 "\n                 "+f.getFieldRef+
                 "\n                 "+f.getField)
        inDeref.contains(f.getField) // field references need to be dereferenced
      case f => 
        printdbg ("       %% in:other      "+f.getClass.getSimpleName+": "+f)
        in.contains(f) // printdbg ("       used ========> "+any.getClass+": "+any)        
    }
    def isTaintGenerated(code:InvokeExpr) = sourceMatch(code.getMethod)
    def isTaintInserted(code:InvokeExpr) = stringAppendMatch(code.getMethod)
    def taintParamIndex(expr:InvokeExpr) = Array.range(0, expr.getArgCount-1).collect{
      case i:Int if (inContains(expr.getArg(i))) => i 
    }   
    def insertTaint = {
      println("     [insert_taint]: Is taint used? => "+isTaintUsed)        
      if (isTaintUsed) used.foreach(x => out.add(x)) 
    }
    def doDFA(expr:InvokeExpr):Boolean = 
	if (expr.getMethod.hasActiveBody) {  // 
          printdbg(" INSIDE doDFA")
          val newIn = new ArraySparseSet
          val l = g.getBody.getLocals.toArray
          (0 to in.size-1).foreach(i => if (!l.contains(inList(i))) newIn.add(inList(i)))      
          val dfa = new TrapForwardAnalysis(newIn,
                                                                      new soot.toolkits.graph.BriefUnitGraph(expr.getMethod.getActiveBody),
                                                                      taintParamIndex(expr), 
                                                                      fileName(expr.getMethod.getDeclaringClass)) 
          dfa.affectedParams.foreach(i => out.add(expr.getArg(i)))
          dfa.isOutputTainted
	} else 
    false
  
    code match {
      /* 
       * All Jimple statements: 
       * 
       * JIdentityStmt, JReturnStmt, JReturnVoidStmt, JBreakpointStmt, JEnterMonitorStmt,
       * JExitMonitorStmt, JGotoStmt, JIdentityStmt, JIfStmt, JLookupSwitchStmt, JNopStmt,
       * JRetStmt, JTableSwitchStmt, JThrowStmt, JAssignStmt, JInvokeStmt
       * 
       * Statements we will consider:
       * 
       * JIdentityStmt (A stmt that assigns to a variable from one of {parameters, this, caughtexception})
       * JAssignStmt
       * JInvokeStmt*
       * JReturnStmt*    
       * JReturnVoidStmt*
       * 
       * Those with * are for inter-procedural control flow (from soot manual)
       * 
       */
      
      /*
       * Intraprocedural analysis start
       */
      case any: JIdentityStmt => // to do: write code here
      case any: JReturnStmt => 
        printdbg (" return : "+any.getOp)
        isOutputTainted = inContains(any.getOp)
      case any: JReturnVoidStmt =>  // do nothing
      case any: JInvokeStmt  =>  // foo(...)            
        println ("JInvoke[1]  ====> "+any+": "+getSourceLineNo(any))
        processInvokeExpr(any.getInvokeExpr, getSourceLineNo(any))
/*
//        println (" ==field ref==> "+{try{any.getFieldRef} catch { case _ => "no field ref"}})
//        val u = any.getUseBoxes;        
//        println (" ==use boxes==> use boxes "+{any.getUseBoxes.foldLeft("")((x, y)=> x+y.toString)})
//        val expr = any.getInvokeExpr
//        if (sinkMatch(expr.getMethod) && isTaintUsed) 
//          foundVulnerability(classFileName, stLine, expr.getMethod.getName)    
//        if (isTaintInserted(expr)) insertTaint else doDFA(expr)
*/        
      case any: JAssignStmt if (any.containsInvokeExpr) => /* a = foo(...) */
        println ("JAssign[2]  ====> "+any+": "+getSourceLineNo(any))
        useDefDebug(any)
        processInvokeExpr(any.getInvokeExpr, getSourceLineNo(any))
        processAssignStmt(any, isTaintUsed || isTaintGenerated(any.getInvokeExpr))
/*        
//        val expr = any.getInvokeExpr
//        println ("     Is tained used? ===> "+isTaintUsed)
        
//        if (isTaintUsed || isTaintGenerated(expr)) {
//          println ("    ===> taint used || generated (JAssign[2]")
//          defined.foreach(x => out.add(x))
//        }
//        println (" ==use boxes==> "+{any.getUseBoxes.foldLeft("")((x, y)=> x+y.toString)})
        
//        defined.foreach(x => out.remove(x)) // kill redefined values
//        if (sinkMatch(expr.getMethod) && isTaintUsed) { 
//          println ("     !!! VULNERABILITY !!! sink match AND taint used (JAssign[2])")          
//          foundVulnerability(classFileName, stLine, expr.getMethod.getName)    
//        }
//        if (isTaintGenerated(expr)) defined.foreach(x => out.add(x)) // add generated to tainted list      
//        else 
//          if (isTaintInserted(expr)) insertTaint 
             //else if (doDFA(expr)) 
//             defined.foreach(x => out.add(x))        
*/
      /*
       * Intraprocedural analysis end
       */
      case any: JAssignStmt => /* a = b // no invoke expression contained  */
        println ("JAssign[3]  ====> "+any+": "+getSourceLineNo(any))        
        useDefDebug(any)
        processAssignStmt(any, isTaintUsed)
      case any => //println ("   Not supported ("+any.getClass.getSimpleName+"): "+any)
    }
    def processInvokeExpr(expr:InvokeExpr, line:Int) = {
      if (sinkMatch(expr.getMethod) && isTaintUsed) 
        foundVulnerability(filename, line, expr.getMethod.getName)    
      if (isTaintInserted(expr)) insertTaint else doDFA(expr)
    }
    def processAssignStmt(any:JAssignStmt, isRHSTainted:Boolean) = {
      println ("     ===> Is RHS tainted? "+isRHSTainted)
/*     
//      printdbg ("     ^^"+any.containsFieldRef)
//      printdbg ("     ^^"+any.getRightOp) 
*/      
      defined.foreach(x => out.remove(x)) // kill redefined values        
      if (isRHSTainted) {
         defined.foreach(x => {
             out.add(x)
             x match {
               case j:JArrayRef => out.add(j.getBase)
               case _ =>
             }
           }
         )
      }
    }
    def useDefDebug(any:JimpleCode) = {
      println (" ==use boxes==> "+{any.getUseBoxes.foldLeft("")((x, y)=> x+y)})
      println (" ==def boxes==> "+{any.getDefBoxes.foldLeft("")((x, y)=> x+y)})
/*      println (" == array Used(0)? ==> "+any.getUseBoxes.apply(0).getValue.isInstanceOf[JArrayRef])
//      println (" == array Def(0)? ==> "+any.getDefBoxes.apply(0).getValue.isInstanceOf[JArrayRef])
//      println (" ==  Def(0)? ==> "+{if (any.getDefBoxes.apply(0).getValue.isInstanceOf[JArrayRef]) 
//      any.getDefBoxes.apply(0).getValue.asInstanceOf[JArrayRef].getBase else " None!"})
//      val x = any.getUseBoxes
//      x(0).getValue.isInstanceOf[JArrayRef]
*/      
    }
  }
  doAnalysis
}
