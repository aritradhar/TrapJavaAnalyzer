/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trap.Java.analyzer

import scala.xml.Elem
import trap.Java.taint.DataStructures.TaintPattern
import trap.Java.taint.DataStructures.TaintVulnerability
import trap.Util._
//import trap.apex.DataStructures._
//import trap.apex.taint.DataStructures._
//import trap.apex.stac.Util._

import trap.file.Util._
import scala.collection.JavaConversions._

class Report(sourceDir:String,
             taintOutputFile:String, 
             extensions:String, 
             sourceType:String, 
             ignored:String, 
             patterns:List[TaintPattern]) {
  private def getTaintOutputFile(analysisOutputDir:String) = analysisOutputDir+"/"+taintOutputFile

  def writeTaintReport(source:String, results:List[TaintVulnerability], outputDir:String) = { 
    println ("Writing analysis report to: "+getTaintOutputFile(outputDir))
    writeToTextFile(getTaintOutputFile(outputDir), getXMLTaintReport(source, results))    
  }
  
// change below
  private def getXMLTaintReport(source:String,results:List[TaintVulnerability]) = 
    """<?xml version="1.0"?>"""+"\n" + 
  <TRAP total={results.size.toString}>
    {getTaintXML(source, results)}
  </TRAP> 
  private def getTaintXML(source:String, v:List[TaintVulnerability]):Elem = 
  <analysis 
    sources={source} 
    extensions={extensions} 
    sourceType={sourceType}
    time={System.currentTimeMillis.toString}
    ignore={ignored}>
    {v.map(getTaintXML)}
  </analysis>
  
  private def getTaintXML(v:TaintVulnerability) = {
    val p = patterns.find(x => x.patternID == v.patternID).get
    <vulnerability 
      patternID={v.patternID}
      patternName = {p.patternName}
      type = {p.patternType}
      attack = {p.attackType}>
      <source 
        file={sourceDir+"/"+v.taintClassName} 
        line={v.taintLineNumber.toString} 
        column={"1"}>
        {v.taintSource}
      </source>
      <sink
        file={sourceDir+"/"+v.taintClassName}
        line={v.taintLineNumber.toString}
        column={"1"}>
        {readNthLine(sourceDir+"/"+v.taintClassName, v.taintLineNumber)}
      </sink>
      <taintStrings>{p.taintStrings.map(x => <strings>{x}</strings>)}</taintStrings>
      <preventdetails>{p.preventDetails.map(x => <prevent>{x}</prevent>)}</preventdetails>
      <causedetails>{p.causeDetails.map(x => <cause>{x}</cause>)}</causedetails>
      <consequencesDetails>{p.consequenceDetails.map(x => <consequences>{x}</consequences>)}</consequencesDetails>
      <platforms>{p.platforms.map(x => <platform>{x}</platform>)}</platforms>
    </vulnerability>
  }
}
