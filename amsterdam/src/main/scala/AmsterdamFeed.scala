import java.io.{File, InputStream}
import java.util.concurrent.LinkedBlockingQueue
import java.util._

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.endpoint.Location.Coordinate
import com.twitter.hbc.core.endpoint.{Location, StatusesSampleEndpoint, StatusesFilterEndpoint}
import com.twitter.hbc.core.processor.{StringDelimitedProcessor, HosebirdMessageProcessor}
import com.twitter.hbc.core.{Constants, HttpHosts}
import com.twitter.hbc.httpclient.auth.OAuth1
import play.api.libs.json._

import scala.io.Source

object AmsterdamFeed extends App{

  val filename = "twitter_keys.json"
  val credentialsFile = getClass.getResource(filename)
  if(credentialsFile == null){
    Console.err.println(s"could not find resource $filename")
  } else{
    val credentials = Json.parse(Source.fromURL(credentialsFile).mkString)

    val optAuth = for{
      consumerKey <- (credentials \ "consumer_key").asOpt[String]
      consumerSecret <- (credentials \ "consumer_secret").asOpt[String]
      accessToken <- (credentials \ "access_token").asOpt[String]
      accessSecret <- (credentials \ "access_secret").asOpt[String]
    } yield new OAuth1(consumerKey, consumerSecret, accessToken, accessSecret)

    optAuth match{
      case Some(auth) => feed(auth)
      case None => {
        Console.err.println("could not load OAuth credentials from file " + credentialsFile.getPath)
      }
    }
  }

  def feed(auth: OAuth1): Unit ={
    val sw = new Coordinate(4.841869, 52.338354)
    val ne = new Coordinate(4.963288, 52.420458)

    val filteredEndpoint = new StatusesFilterEndpoint()
      .locations(Arrays.asList(new Location(sw, ne)))

    val simpleEndpoint = new StatusesSampleEndpoint()

    val msgQueue = new LinkedBlockingQueue[String](10000)

    val client = new ClientBuilder()
      .name("[WSE] Amsterdam")
      .hosts(Constants.STREAM_HOST)
      .authentication(auth)
      .endpoint(filteredEndpoint)
      .processor(new StringDelimitedProcessor(msgQueue))
      .build()

    client.connect()

    while(!client.isDone){
      val msg = msgQueue.take()
      val json = Json.parse(msg)
      for{
        user <- (json \\ "user")
        screenName <- (user \ "screen_name").asOpt[String]
        fullName <- (user \ "name").asOpt[String]
        content <- (json \ "text").asOpt[String]
      } yield println(s"[$fullName] @$screenName: $content")
    }
    println("done!")
  }
}
