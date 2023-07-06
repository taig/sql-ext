package io.taig.skunk.ext

import skunk.{Fragment, Void}
import skunk.syntax.all.*
import skunk.util.Origin

final case class Column(table: Option[Table], column: String):
  def sql(using origin: Origin): Fragment[Void] =
    table.fold(Fragment(List(Left(s"\"$column\"")), Void.codec, origin)): table =>
      Fragment(List(Left(s"\"$table\".\"$column\"")), Void.codec, origin)
