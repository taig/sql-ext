package io.taig.skunk.ext

object Password {
  final case class Plaintext(value: String) extends AnyVal
  final case class Hashed(value: String) extends AnyVal
}
