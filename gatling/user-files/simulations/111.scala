
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import java.util.concurrent.ThreadLocalRandom

object UC01 {

  val Req = exec(http("UC01_01_Request")
      .get("/"))
    .exec(http("UC01_02_Request")
      .get("/computers?f=macbook")
		.check(status.is(session => 200)))
    .exec(http("UC01_03_Request")
      .get("/computers/6")
		.check(regex("bbbboooody").exists)
		.check(status is 200))
    .pause(3)
    .exec(http("UC01_04_Request")
      .get("/"))

  val StartUC01 = scenario("CreateUserRequest") // A scenario is a chain of requests and pauses
		.forever(
			pace(10 seconds)
			    
				   .group("UC01_XX_CreateUserRequest"){
                   exec(UC01.Req)
				   
				}
			    
	)

}

object UC02 {

  val Req = exec(http("UC02_01_Request")
	  .get("/computers?p=3"))
    .exec(http("request_8")
      .get("/computers?p=4"))
    .exec(http("request_9")
      .get("/computers/new"))
	.pause(3)
	  
  val Req2 = exec(http("UC02_02_Request")	  
      .get("/computers/new"))
	.exec(http("request_10") // Here's an example of a POST request
      .post("/computers")
      .formParam("name", "Beautiful Computer") // Note the triple double quotes: used in Scala for protecting a whole chain of characters (no need for backslash)
      .formParam("introduced", "2012-05-30")
      .formParam("discontinued", "")
      .formParam("company", "37"))
	.pause(3)

	  
	  
  val StartUC02 = scenario("CreateAnotherUserRequest") // A scenario is a chain of requests and pauses
		.forever(
			pace(4 seconds)
                exitBlockOnFail {
                    group("UC02_XX_CreateAnotherUserRequest"){
					exec(UC02.Req)
					.exec(UC02.Req2)
					}
				}
	)
}


class Testo2 extends Simulation {

  val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")


	
	
	
  setUp(
	UC01.StartUC01.inject(constantConcurrentUsers(2) during (30 minutes)),
	UC02.StartUC02.inject(constantConcurrentUsers(4) during (30 minutes))
	)
	.protocols(httpProtocol)
	.maxDuration(30	minutes)
	
}
