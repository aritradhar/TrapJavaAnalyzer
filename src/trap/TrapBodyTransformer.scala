package trap

import scala.collection.JavaConversions._
import soot.BodyTransformer;
import java.util.{Map => Mp};
import soot.Body;
import TrapUtil._

object TrapBodyTransformer extends BodyTransformer {
  protected def internalTransform(body:Body, phase:String, options:Mp[_, _]) {
    body match {
      case j:soot.jimple.JimpleBody => //println ("Here") // if {val s:String = any.toString; !s.contains("<init>")}	
//        if (!j.toString().split('\n').apply(0).contains("<init>")) {
          val g = new soot.toolkits.graph.BriefUnitGraph(j)
          val cl = body.getMethod.getDeclaringClass
          val a = new TrapForwardAnalysis(getEmptySet, g, Array(), fileName(body.getMethod.getDeclaringClass))
          
//        } else {
//          println (" scanning method: "+j.getMethod+". Method name contains <init>: skipped.")
//        }
      case _ => 
    }
  }
}

