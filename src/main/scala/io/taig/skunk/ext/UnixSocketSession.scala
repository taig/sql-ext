package io.taig.skunk.ext

import cats.effect.std.Console
import cats.effect.{Resource, Temporal}
import cats.syntax.all.*
import fs2.io.net.unixsocket.{UnixSocketAddress, UnixSockets}
import fs2.io.net.Socket
import org.typelevel.otel4s.trace.Tracer
import skunk.Session.Recyclers
import skunk.net.protocol.{Describe, Parse}
import skunk.util.{Pool, Typer}
import skunk.{RedactionStrategy, Session}

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
  ): Resource[F, Resource[F, Session[F]]] =
    def session(sockets: Resource[F, Socket[F]], cache: Describe.Cache[F], tracer: Tracer[F]): Resource[F, Session[F]] =
      for
        pc <- Resource.eval(Parse.Cache.empty[F](parseCache))
        session <- Session.fromSockets[F](
          sockets,
          user,
          database,
          password,
          debug,
          strategy,
          None,
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
  ): Resource[F, Resource[F, Session[F]]] = pooled(
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
