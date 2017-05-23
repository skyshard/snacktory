val Jsoup = "org.jsoup" % "jsoup" % "1.8.3"
val Slf4jApi = "org.slf4j" % "slf4j-api" % "1.6.6"
val Slf4jLog4j12 = "org.slf4j" % "slf4j-log4j12" % "1.6.6"
val CommonsLang = "commons-lang" % "commons-lang" % "2.6"
val CommonsLang3 = "org.apache.commons" % "commons-lang3" % "3.1"
val Log4j = "log4j" % "log4j" % "1.2.14"
val Guava = "com.google.guava" % "guava" % "18.0"
val HtmlCleaner = "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.16"
// https://mvnrepository.com/artifact/org.yaml/snakeyaml
val SnakeYaml = "org.yaml" % "snakeyaml" % "1.18"

lazy val commonSettings = Seq(
  name := "snacktory-fork",
  version := "1.2.1-SNAPSHOT",
  scalaVersion in ThisBuild := "2.11.8"
)

lazy val snacktory = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies += "com.novocode" % "junit-interface" % "0.10-M4" % "test",
    libraryDependencies ++= Seq(Jsoup, Slf4jApi, Slf4jLog4j12, CommonsLang, CommonsLang3, Log4j, Guava, HtmlCleaner, SnakeYaml),
    resolvers += "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
  )


