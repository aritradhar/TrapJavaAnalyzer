/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trap
import scala.collection.JavaConversions._
import soot.SceneTransformer;
import java.util.{Map => Mp};
import soot.Scene;

object TrapSceneTransformer extends SceneTransformer {
      protected def internalTransform(phase:String, options:Mp[_, _]) {
              val cg = Scene.v.getCallGraph
              val pk = Scene.v.getPkgList
              println (" ~~~~> main class is "+Scene.v.getMainClass)
              println (" ~~~~> main method is "+Scene.v.getMainMethod)
              println (" ~~~~> pkg list is "+pk)
//              println (" ~~~~> Call graph is "+cg)
              println (" ~~~~> Call graph size is "+cg.size)
              
      }
}

