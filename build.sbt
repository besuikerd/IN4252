name := "Twitter Assignment"

scalaVersion in ThisBuild := "2.11.7"

lazy val root = (project in file("."))
  .dependsOn(shared).aggregate(shared)
  .dependsOn(amsterdam).aggregate(amsterdam)

lazy val shared = (project in file("shared"))

lazy val amsterdam = (project in file("amsterdam"))
  .dependsOn(shared)


libraryDependencies ++= Seq(
)
