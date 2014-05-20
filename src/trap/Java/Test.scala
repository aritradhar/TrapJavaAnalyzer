/* Author:Amitabh Saxena (amitabh.saxena@accenture.com)
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trap.Java

import trap.Util._
import trap.Java.analyzer.Main._

object Test {
  def main(args:Array[String]):Unit = {
    // val destDir = "sample\\output"
    // val srcDir = "sample\\mysql"
    val (destDir, srcDir) = if (args.length < 2) 
            ("c:\\test\\sootOutput", "c:\\test\\soot")
            else (args(1), args(0))
    val libDir = if(args.length == 3) args(2) else ""
    println ("Analyzing source directory: "+srcDir+"; libDir is "+libDir);
    doFolderAnalysis(srcDir, destDir, libDir)    
  }
}
