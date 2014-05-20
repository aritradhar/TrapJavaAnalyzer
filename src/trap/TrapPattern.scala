/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trap
import scala.collection.JavaConversions._
import soot.SootMethod

import trap.Java.taint.DataStructures.TaintPattern
import trap.file.TraitFilePropertyReader

object TrapPattern extends TraitFilePropertyReader {
  val propertyFile = "trapjava.properties"
  println ("READING PATTERNS")
  private val sources = read("source", "test.Source#mySource").split(",")
  private val sinks = read ("sink", "test.Sink#mySink").split(",")
  private val stringBufferAppend = read("append", "java.lang.StringBuffer#append").split(",")
  lazy val patternID = "pat01"
  def sourceMatch(m:SootMethod) = {
    println("  <<< Matching source >>> : "+path(m))
    methodMatch(m, sources)
  }
  def sinkMatch(m:SootMethod) = {
    println("  <<< Matching sink >>> : "+path(m))
    methodMatch(m, sinks)
  }
  def stringAppendMatch(m:SootMethod) = {    
    println("  <<< Matching string append >>> : "+path(m))
    methodMatch(m, stringBufferAppend)
  }

  private def path(m:SootMethod) = m.getDeclaringClass.getName+"#"+m.getName
  private def methodMatch(m:SootMethod, s:Array[String]) = {
    if (s.contains(path(m))) {
      println("   << found! >>   : "+path(m))    
      true 
    }
    else false
  }
  
  lazy val p1 = TaintPattern(patternID,
                      "SQL/NoSQL Injection",
                      "taint",
                      Array("Use static queries"),
                      Array("SQL statements or JSON constructed from user input without escaping the strings"),
                      Array("Unintended behaviour", "privacy loss", "data theft" ), 
                      Array("Java, SQL, NoSQL, Cassandra, CouchDB"),
                      "Query manipulation", 
                      Array("'"))
  lazy val patterns = List(p1)
}