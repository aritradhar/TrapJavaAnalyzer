/* Author:Amitabh Saxena (amitabh.saxena@accenture.com)
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trap.Java

import trap.TrapBodyTransformer
import trap.TrapUtil

//import java.io.File
//import org.apache.commons.io.FileUtils
import scala.collection.JavaConversions._
import soot.options.Options
import soot.Scene
import soot.PackManager
import soot.Transform;
import soot.options.Options;
import soot.PhaseOptions;
import soot.PackManager;
import trap.file.Util._


object JavaAnalysis {
  
  def doAnalysis(inputDir:String, outputDir:String, libDir:String) = {
    TrapUtil.reset
    try {
      val libFiles = getAllFiles(libDir, Array("jar"), true)

      soot.G.reset
      PackManager.v().getPack("jap").add(new Transform("jap.myTransform", TrapBodyTransformer));
      PhaseOptions.v().setPhaseOption("tag.ln", "on");	  
      Options.v.set_process_dir(List(inputDir))
//      Options.v.set_process_dir(List("c:\\test\\soot"))
      Options.v.set_output_dir(outputDir)
//      Options.v.set_output_dir("c:\\test\\sootOutput")
//      Options.v.set_output_format(Options.output_format_jimple)
      Options.v.set_output_format(Options.output_format_none)
      Options.v.set_allow_phantom_refs(true)
      Options.v.set_src_prec(Options.src_prec_java)
//      Options.v.set_whole_program(true)
      Options.v.set_whole_program(false)
//      Options.v.set_main_class("test.Main")
      Options.v.set_exclude(List("java","sun", "java.lang"))
//      Options.v.set_main_class("test.Main");C:\\scala\\lib\\
      Options.v.set_keep_line_number(true)
      val classPath = libFiles.foldLeft(inputDir)((x, y) => x+";"+y)
//      println ("classpath is: "+classPath)
      Options.v.set_soot_classpath(classPath)
//      Options.v.set_soot_classpath(inputDir+";C:\\scala\\lib\\servlet-api-2.5-20081211.jar;c:\\scala\\lib\\mongodb\\mongo-2.7.3.jar")
      Options.v.set_prepend_classpath(true)
      Options.v.setPhaseOption("jb", "use-original-names:true")
      Options.v.set_no_bodies_for_excluded(true)
      
//      PhaseOptions.v().setPhaseOption("cg", "enabled:true");
//      PhaseOptions.v().setPhaseOption("cg", "implicit-entry:false");
//      PhaseOptions.v().setPhaseOption("cg", "verbose:true");

//      Options.v().set_verbose(true);
//      PhaseOptions.v().setPhaseOption("jap.npc", "on"); // ???
//      CallGraphExample.main(null)
      Scene.v().loadNecessaryClasses()
      PackManager.v().runPacks()
      PackManager.v().writeOutput()
    } catch {
      case ce:soot.CompilationDeathException => println("TRAP error: Error during compilation of Java code")
        ce.printStackTrace
      case any => any.printStackTrace
    }
    TrapUtil.getVulnerabilities
  }
}
