# Changelog

## 0.17.1

_2025-07-16_

> Failed to release 0.17.0

## 0.17.0

_2025-07-16_

- Upgrade to skunk 1.0.0-M11
- Upgrade to sbt 1.11.3

## 0.16.1

_2025-06-20_

- Upgrade to sbt 1.11.2
- Upgrade to sbt-ci-release 1.11.1
- Upgrade to sbt-houserules 0.11.5
- Upgrade to case-insensitive 1.5.0
- Upgrade to scala 3.3.6
- Upgrade to enumeration-ext 0.4.0

## 0.16.0

_2025-02-26_

- Remove deprecated definitions
- Upgrade to sbt-houserules 0.11.4
- Upgrade to sbt-blowout 0.2.1
- Upgrade to cats 2.13.0
- More modular CI steps

## 0.15.2

_2025-01-17_

- Upgrade to sbt-houserules 0.11.3
- Upgrade to sbt-blowout 0.1.2
- Upgrade to skunk 1.0.0-M10
- Fix sbt in CI

## 0.15.1

_2025-01-13_

- Upgrade to skunk 1.0.0-M9
- Upgrade to sbt 1.10.7
- Upgrade to enumeration-ext 0.3.1
- Upgrade to sbt-houserules 0.11.0
- Upgrade to sbt-ci-release 1.9.2

## 0.15.0

_2024-11-20_

- Upgrade to enumeration-ext 0.3.0
- Upgrade to skunk 1.0.0-M8

## 0.14.3

_2024-11-06_

- Discard `ClosedChannelException` in `UnixSocketSession.googleCloudRun`
- Deprecate `Record`
- Add skunk.upsert helper
- Make Upsert covariant
- Upgrade to sbt-ci-release 1.9.0
- Upgrade to sbt 1.10.5

## 0.14.2

_2024-10-20_

- Add `Upsert`
- Upgrade to case-insensitive 1.4.2
- Upgrade to sbt-ci-release 1.8.0
- Upgrade to scala 3.3.4
- Upgrade to sbt-houserules 0.9.0
- Apply scalafix
- Upgrade CI dependencies

## 0.14.1

_2024-09-25_

- Upgrade to sbt 1.10.2
- Upgrade to skunk 1.0.0-M7

## 0.14.0

_2024-08-09_

- Upgrade to enumeration-ext 0.2.0

## 0.13.4

_2024-07-03_

- Add Resource[F, Session[F]] alias SxPool[F]
- Add Session alias Sx
- Add codec +: and | extensions
- Upgrade to cats 2.12.0

## 0.13.3

_2024-05-21_

- Upgrade to sbt 1.10.0
- Add accessMode and isolationLevel controls to Tx

## 0.13.2

_2024-05-13_

- Upgrade to skunk 1.0.0-M6

## 0.13.1

_2024-04-23_

- Upgrade to skunk 1.0.0-M5
- Add named parameter

## 0.13.0

_2024-03-20_

- Rename .skunk.codecs.record.encoder and .decoder to .apply

## 0.12.1

_2024-03-20_

- Add .skunk.codecs.record.encoder and .decoder
- Upgrade to enumeration-ext 0.0.4

## 0.12.0

_2024-03-14_

- Stay on 3.3.x (LTS)

## 0.11.0

_2024-03-01_

- Upgrade to sbt 1.9.9
- Upgrade to scala 3.4.0
- Rename Transaction to Tx
- Add Order instance for Record

## 0.10.3

_2024-01-24_

- Upgrade to skunk 1.0.0-M4

## 0.10.2

_2024-01-23_

- Add skunk arr codec helpers
- Revert "Upgrade to skunk-ext 1.1.0-M3"

## 0.10.1

_2024-01-22_

- Upgrade to skunk-ext 1.1.0-M3

## 0.10.0

- Use F[_] instead of IO in Transaction

## 0.9.2

_2024-01-18_

- Add RecordMissingException

## 0.9.1

_2024-01-18_

- Add ad-hoc skunk enumeration builder

## 0.9.0

_2024-01-07_

- Rename to sql-ext and split into core and skunk package

## 0.8.3

_2024-01-02_

- Add mapping helper that works on a base enum
- Upgrade to sbt 1.9.8

## 0.8.2

_2023-10-09_

- Add UnixSocketSession

## 0.8.1

_2023-10-09_

- Add enumeration-ext integration

## 0.8.0

_2023-10-08_

- Remove deprecated code
- Upgrade to skunk 1.0.0-RC1

## 0.7.1

_2023-10-04_

- `identifiers` -> `_identifier`
- Upgrade to sbt-houserules 0.7.4
- Upgrade to scala 3.3.1

## 0.7.0

_2023-09-04_

- Remove Record.Created and .Updated
- Deprecate query builder helpers
- Add codecs.\_citext
- Upgrade to sbt-houserules 0.7.2
- Upgrade to sbt 1.9.4

## 0.6.0

_2023-07-24_

- Make Record types covariant
- Remove Fragments
- Upgrade to sbt-houserules 0.7.1
- Upgrade to sbt 1.9.3

## 0.5.4

_2023-07-06_

- Distinct Insert & Select types

## 0.5.3

_2023-07-06_

- Introduce a table type

## 0.5.2

_2023-07-06_

- Introduce a Columns data structure
- Deprecate Fragments helpers

## 0.5.1

_2023-06-26_

- Upgrade to sbt 1.9.1
- Add Transaction.use helper

## 0.5.0

_2023-06-16_

- Remove codecs.enumeration inf favor of enumeration-ext-skunk
- Upgrade to sbt-houserules 0.7.0
- Upgrade to sbt-ci-release 1.5.12
- Upgrade to sbt 1.9.0

## 0.4.4

_2023-05-31_

- Upgrade to skunk 0.6.0

## 0.4.3

_2023-05-26_

- Upgrade to scala 3.3.0
- Upgrade to case-insensitive 1.4.0

## 0.4.2

_2023-05-08_

- Add enum codec

## 0.4.1

_2023-05-03_

- Upgrade to scala 3.3.0-RC4
- Upgrade to skunk 0.6.0-RC2

## 0.4.0

_2023-05-02_

- Upgrade to skunk 0.6.0-RC1
- Upgrade to sbt-houserules 0.6.2

## 0.3.0

_2023-02-12_

- Remove Database helpers
- Drop support for scala 2.13
- Upgrade to skunk 0.5.2

## 0.2.0

_2023-01-18_

- Upgrade to skunk 0.5.0
- Upgrade to sbt 1.8.2

## 0.1.2

_2023-01-04_

- Upgrade to skunk 0.4.0-M3

## 0.1.1

_2022-11-29_

- Upgrade to skunk 0.4.0-M2

## 0.1.0

_2022-11-29_

- Upgrade to skunk 0.4.0-M1
- ciString -> citext

## 0.0.5

_2022-11-29_

- Upgrade to sbt-houserules 0.6.1
- Upgrade to sbt-ci-release 1.5.11
- Upgrade to skunk 0.3.2
- Upgrade to scala 3.2.1
- Upgrade to scala 2.13.10
- Use setup-java caching mechanism

## 0.0.4

_2022-09-12_

- Add Fragments.insert
- Add Fragments.plain
- Add convenience Fragments overloads
- Upgrade to sbt-houserules 0.5.0

## 0.0.3

_2022-09-11_

- Distinct `Record` implementation for Scala 3

## 0.0.2

_2022-09-11_

- Introduce `Fragments` helper

## 0.0.1

_2022-09-11_

Initial release
