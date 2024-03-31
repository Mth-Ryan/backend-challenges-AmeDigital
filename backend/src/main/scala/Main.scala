import cats.effect.*
import com.comcast.ip4s.*
import org.http4s.*
import org.http4s.ember.server.EmberServerBuilder

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder.default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"9000")
      .withHttpApp(Router.app)
      .build
      .useForever
  }
}
