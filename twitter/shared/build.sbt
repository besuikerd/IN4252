name := "Twitter Assignment - Shared"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.twitter" % "hbc-core" % "2.2.0",
  "org.slf4j" % "slf4j-simple" % "1.6.4",
  "com.typesafe.play" %% "play-json" % "2.3.0"
)
