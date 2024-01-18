package io.taig.sql.ext

final case class RecordMissingException(table: String, column: String, value: Option[String])
    extends RuntimeException(
      s"Record $table.$column missing" + value.map(value => s" for value $value").getOrElse("")
    )

object RecordMissingException:
  def apply(table: String, column: String): RecordMissingException =
    RecordMissingException(table, column, value = None)

  def apply(table: String, column: String, value: String): RecordMissingException =
    RecordMissingException(table, column, Some(value))
