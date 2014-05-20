/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trap.Java.taint.DataStructures

case class MethodPattern(className:String, 
                         methodName:String, 
                         paraTypes:Array[String], 
                         isStatic:Boolean)
abstract class Pattern(patternID:String,
                        patternName:String,
                        patternType:String,
                        platforms:Array[String])


case class TaintPattern(patternID:String,
                        patternName:String,
                        patternType:String,
                        preventDetails:Array[String], 
                        causeDetails:Array[String], 
                        consequenceDetails:Array[String], 
                        platforms:Array[String],
                        attackType:String, 
                        taintStrings:Array[String]) extends Pattern (patternID,
                                                                          patternName,
                                                                          patternType,
                                                                          platforms)

case class TaintVulnerability(taintClassName:String,
                              taintLineNumber:Int,
                              taintSource:String,
                              taintSink:String, 
                              patternID:String)

//object TestData{
//  lazy val sink = MethodPattern("Database", "query", Array("String"), true)
//  lazy val source = MethodPattern("ApexPages.currentPage().getParameters()", "get", Array("String"), true)
//  lazy val cleaner = MethodPattern("String", "escapeSingleQuotes", Array("String"), true)
//  lazy val patterns=List(p1) //, p2)
//  lazy val p1 = TaintPattern("pat01",
//                      "SOQL injection",
//                      "taint",
//                      "Query manipulation", 
//                      Array("'"),
//                      Array("Use escapeSingleQuotes method",
//                            "Use static SOQL queries",
//                            "Use \"with sharing\" keyword to prevent users from accessing unauthorized data even if query returns it",
//                            "Use a bind variable. It automatically escapes user input"),
//                      Array("SOQL statement constructed from user input without escaping the strings", 
//                            "Not using escapeSingleQuotes method", "Using dynamic SOQL"),
//                      Array("Unintended behaviour", "privacy loss", "data theft" ), 
//                      Array("Salesforce SFDC"),
//                      Array(sink), Array(source), Array(cleaner))
//
  
  

//  lazy val p2 = TaintPattern("pat02",
//                      "SOQL injection",
//                      "taint",
//                      "DDL/DML injection", 
//                      Array(";", "'", "--"),
//                      Array("Replace java.sql.Statement by java.sql.PreparedStatement",
//                            "Escape strings"),
//                      Array("cause"),
//                      Array("consequence"), 
//                      Array("Platforms"))
//                      
//                      
                      
//  lazy val aCode1 = AAssign("a", "b", Alias, ASourcePos("Foo.cls", 4))
//  aCode1.sourceText = "String a = b"
//  lazy val aCode2 = AInvoke("foo", "x")
//  aCode2.sourcePos = ASourcePos("Bar.cls", 5)
//  aCode2.sourceText = "x = foo()"
//  lazy val aCode3 = AAssign("x", "abc", Alias)
//  aCode3.sourcePos = ASourcePos("Foo1.cls", 4)
//  aCode3.sourceText = "x = abc"
//  lazy val aCode4 = AInvoke("myMethod", "myVar")
//  aCode4.sourcePos = ASourcePos("Bar1.cls", 5)
//  aCode4.sourceText = "myVar = myMethod()"
//  lazy val v1 = TaintVulnerability(aCode1, aCode2, Nil, "pat01") 
//  lazy val v2 = TaintVulnerability(aCode3, aCode4, Nil, "pat02") 
//  lazy val vulnerabilities = List(v1, v2)

//}