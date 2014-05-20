/* Author:Amitabh Saxena (amitabh.saxena@accenture.com)
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trap.Java.analyzer
//import trap.Java.taint.Patterns
import trap.TrapPattern
import trap.Util._
import trap.file.Util._
import scala.collection.JavaConversions._
import trap.Java.JavaAnalysis

      
object Main {
  private var tempFiles:List[String] = Nil
  private def clean = tempFiles.foreach(x => println ("Trying to delete file: "+x + ", result is: "+deleteFile(x)))
  private lazy val taintOutputFile = "trapAnalysis.xml"
  private def getTaintOutputFile(analysisOutputDir:String) = analysisOutputDir+"/"+taintOutputFile
  private lazy val extensions="java" 
  private lazy val sourceType="java"
  private lazy val ignored=""
  def doFolderAnalysis(sourceDir:String, outputDir:String):Unit = doFolderAnalysis(sourceDir, outputDir, "")
  def doFolderAnalysis(sourceDir:String, outputDir:String, libDir:String) {
    val r = new Report(sourceDir, taintOutputFile, extensions, sourceType, ignored, TrapPattern.patterns); //   patterns:List[TaintPattern])
    r.writeTaintReport(sourceDir, JavaAnalysis.doAnalysis(sourceDir, outputDir, libDir), outputDir)        
    clean
  }
}


  
      