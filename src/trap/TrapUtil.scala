package trap

import scala.collection.JavaConversions._
import soot.SootClass
import soot.jimple.InvokeExpr
import soot.tagkit.LineNumberTag
import soot.tagkit.SourceLnPosTag
import soot.toolkits.scalar.ArraySparseSet
import soot.toolkits.scalar.{FlowSet => Tainted}
import trap.Java.taint.DataStructures.TaintVulnerability
import soot.{Unit => JimpleCode}

object TrapUtil {  
//  private var v:List[(String, Int, String)] = Nil  // class name, line number, method name
  private var v:List[TaintVulnerability] = Nil  // class name, line number, method name
  def reset = v = Nil
  def fileName(cl:SootClass) = cl.getPackageName.replace('.', '/')+"/"+cl.getJavaStyleName+".java"
  val emptySet = new ArraySparseSet();    
  def getEmptySet = emptySet.clone
  def foundVulnerability(classFileName:String, stLine:Int, methodName:String) = {    
    println ("   => WARNING! <= Taint in sink ("+classFileName+"), line: "+stLine+")")    
    v ::= TaintVulnerability(classFileName, stLine, "", methodName, TrapPattern.patternID)
  }
  def getVulnerabilities = v
  def getSourceLineNo(code:JimpleCode) = try{      
      code.getTag("SourceLnPosTag").asInstanceOf[SourceLnPosTag].startLn
    } catch { 
      case any => 
        try {
          code.getTag("LineNumberTag").asInstanceOf[LineNumberTag].getLineNumber
        } catch {
          case any => 
            0          
        }
    }
    
}
      