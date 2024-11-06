package io.taig.sql.ext.skunk

import cats.effect.Resource
import cats.effect.Temporal
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.io.net.Socket
import fs2.io.net.unixsocket.UnixSocketAddress
import fs2.io.net.unixsocket.UnixSockets
import org.typelevel.otel4s.trace.Tracer
import skunk.RedactionStrategy
import skunk.Session
import skunk.Session.Recyclers
import skunk.net.protocol.Describe
import skunk.net.protocol.Parse
import skunk.util.Pool
import skunk.util.Typer

import java.nio.channels.ClosedChannelException
import scala.concurrent.duration.Duration

object UnixSocketSession:
  def pooled[F[_]: Temporal: Tracer: Console](
      sockets: Resource[F, Socket[F]],
      user: String,
      database: String,
      password: Option[String] = none,
      max: Int,
      debug: Boolean = false,
      strategy: Typer.Strategy = Typer.Strategy.BuiltinsOnly,
      parameters: Map[String, String] = Session.DefaultConnectionParameters,
      commandCache: Int = 1024,
      queryCache: Int = 1024,
      parseCache: Int = 1024,
      readTimeout: Duration = Duration.Inf,
      redactionStrategy: RedactionStrategy = RedactionStrategy.OptIn
  ): Resource[F, SxPool[F]] =
    def session(sockets: Resource[F, Socket[F]], cache: Describe.Cache[F], tracer: Tracer[F]): SxPool[F] =
      for
        pc <- Resource.eval(Parse.Cache.empty[F](parseCache))
        session <- Session.fromSockets[F](
          sockets,
          user,
          database,
          password,
          debug,
          strategy,
          sslOptions = None,
          parameters,
          cache,
          pc,
          readTimeout,
          redactionStrategy
        )(using Temporal[F], tracer, Console[F])
      yield session

    for
      dc <- Resource.eval(Describe.Cache.empty[F](commandCache, queryCache))
      pool <- Pool.ofF(session(sockets, dc, _), max)(Recyclers.full)
    yield pool(Tracer[F])

  def googleCloudRun[F[_]: Temporal: Tracer: UnixSockets: Console](
      instanceConnectionName: String,
      user: String,
      database: String,
      password: Option[String] = none,
      max: Int,
      debug: Boolean = false,
      strategy: Typer.Strategy = Typer.Strategy.BuiltinsOnly,
      parameters: Map[String, String] = Session.DefaultConnectionParameters,
      commandCache: Int = 1024,
      queryCache: Int = 1024,
      parseCache: Int = 1024,
      readTimeout: Duration = Duration.Inf,
      redactionStrategy: RedactionStrategy = RedactionStrategy.OptIn
  ): Resource[F, SxPool[F]] =
    val sessions = pooled(
      UnixSockets[F].client(UnixSocketAddress(s"/cloudsql/$instanceConnectionName/.s.PGSQL.5432")),
      user,
      database,
      password,
      max,
      debug,
      strategy,
      parameters,
      commandCache,
      queryCache,
      parseCache,
      readTimeout,
      redactionStrategy
    )

    Resource
      .make(sessions.allocated) { case (_, finalize) =>
        finalize.recoverWith { case _: ClosedChannelException =>
          Temporal[F].unit
        }
      }
      .map { case (px, _) => px }
