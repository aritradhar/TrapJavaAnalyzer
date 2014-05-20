//processCmdLine()
//:load TrapIntraREPL.scala
package trap

import scala.collection.JavaConversions._

import soot.options.Options
import soot.Scene
import soot.PackManager
import soot.Transform;

import soot.options.Options;
import soot.PhaseOptions;
import soot.PackManager;


object Trap {
  def main(args: Array[String]): Unit = run
  def run {
    try {
      soot.G.reset
      PackManager.v().getPack("jap").add(new Transform("jap.myTransform", TrapBodyTransformer));
//      PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTransform", TrapSceneTransformer));// Options.v().set_verbose(true);
//	PhaseOptions.v().setPhaseOption("jap.npc", "on");
      PhaseOptions.v().setPhaseOption("tag.ln", "on");	  
//      Options.v.set_verbose(true)
      Options.v.set_process_dir(List("c:\\test\\soot"))
//      Options.v.set_output_dir("c:\\test\\soot")
      Options.v.set_output_dir("c:\\test\\sootOutput")
     
      Options.v.set_output_format(Options.output_format_jimple)
//      Options.v.set_output_format(Options.output_format_none)
      Options.v.set_allow_phantom_refs(true)
      Options.v.set_src_prec(Options.src_prec_java)
//      Options.v.set_whole_program(true)
      Options.v.set_whole_program(false)
      Options.v.set_main_class("test.Main")
      Options.v.set_exclude(List("java","sun", "java.lang", "javax"))
//      Options.v.set_main_class("test.Main")
//      Options.v.set_keep_line_number(true)
      Options.v.set_soot_classpath("c:\\test\\soot")
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
      case any => any.printStackTrace
    }

  }
}


//                Options.v().set_src_prec(Options.src_prec_java);
//                Options.v().set_keep_line_number(true);
//                Options.v().set_polyglot(false); // Allow Java 5
//               
//                //Options.v().set_output_format(Options.output_format_jimple);
//     
//                //Work in whole-program mode
//                Options.v().set_whole_program(true);
//                Options.v().set_exclude(Arrays.asList("java", "javax",  "sun",
//                            "org.apache", "org.python", "org.jboss", "javassist",
//                            "org.codehaus", "org.zeroturnaround", "org.hibernate"));
//               
//                // General Options
//                Options.v().set_allow_phantom_refs(true);
//                Options.v().set_no_bodies_for_excluded(true);
//                Options.v().set_time(true);
//     
//                //Load the classes
//                Scene.v().addBasicClass("javax.servlet.http.HttpServlet",SootClass.SIGNATURES);
//                Scene.v().loadNecessaryClasses();
//                Scene.v().loadBasicClasses();
//     
//                //Set entry points
//                List<SootMethod> entryPoints = new ArrayList<SootMethod>();
//               
//                final String servletClassName = "javax.servlet.http.HttpServlet";
//                final SootClass servletClass = Scene.v().loadClassAndSupport(servletClassName);
//               
//                FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
//                // find the entry points
//                Scene.v().setEntryPoints(entryPoints);
//     
//                   
//                //Call graph options
//                PhaseOptions.v().setPhaseOption("cg", "enabled:true");
//                PhaseOptions.v().setPhaseOption("cg", "implicit-entry:false");
//                PhaseOptions.v().setPhaseOption("cg", "verbose:true");
//                PhaseOptions.v().setPhaseOption("cg.cha", "enabled:false");
//                PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
//                PhaseOptions.v().setPhaseOption("cg.paddle", "enabled:false");
//                PhaseOptions.v().setPhaseOption("cg.spark", "geom-pta:true");
//               
//                //Output control
//                PhaseOptions.v().setPhaseOption("cg.spark", "dump-html:true");
//                Options.v().set_ast_metrics(true);
//                Options.v().set_dump_cfg(Arrays.asList("ALL"));
//                Options.v().set_dump_cfg(Arrays.asList("cg"));
//                Options.v().set_verbose(false);
//                Options.v().set_debug(false);
//                Options.v().set_debug_resolver(false);
//     
//                PackManager.v().runPacks();
