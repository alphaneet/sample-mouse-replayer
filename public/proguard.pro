-dontnote
-dontwarn
-injars       in.jar
-injars       /Users/alphaneet/lib/scala-library.jar(!META-INF/MANIFEST.MF,!library.properties)
-injars       /Users/alphaneet/lib/jar/core.jar(!META-INF/MANIFEST.MF)
-libraryjars  <java.home>/../Classes/classes.jar
-outjars      out.jar
-keep public class MouseReplayer {
  public static void main(java.lang.String[]);
}
-keep public class AppletMain
-keep public class * extends processing.core.PGraphics
