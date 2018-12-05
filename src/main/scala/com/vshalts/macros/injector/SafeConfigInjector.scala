package com.vshalts.macros.injector

import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ScClass, ScObject, ScTypeDefinition}
import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.SyntheticMembersInjector

class SafeConfigInjector extends SyntheticMembersInjector {
  import SafeConfigInjector._

  override def injectFunctions(source: ScTypeDefinition): Seq[String] = {

    def genFuncs(clazz: ScClass) = {
      val prefix = clazzPrefix(clazz)
      Seq(
        s"implicit def bootupErrorsToType[T](value:${prefix}BootupErrors[T]):T = ???",

        s"def BootupErrors[A](value:A): ${prefix}BootupErrors[A] = ???",

        s"def getBoolean(name: String): ${prefix}BootupErrors[Boolean] = ???",
        s"def getString(name: String): ${prefix}BootupErrors[String] = ???",
        s"def getInt(name: String): ${prefix}BootupErrors[Int] = ???",
        s"def getDouble(name: String): ${prefix}BootupErrors[Double] = ???",
        s"def getDuration(name: String): ${prefix}BootupErrors[_root_.scala.concurrent.duration.Duration] = ???",
        s"def getLong(name: String): ${prefix}BootupErrors[Long] = ???",
        s"def getObject(name: String): ${prefix}BootupErrors[_root_.com.typesafe.config.ConfigObject] = ???",

        s"def getBooleanList(name: String): ${prefix}BootupErrors[_root_.scala.List[Boolean]] = ???",
        s"def getStringList(name: String): ${prefix}BootupErrors[_root_.scala.List[String]] = ???",
        s"def getIntList(name: String): ${prefix}BootupErrors[_root_.scala.List[Int]] = ???",
        s"def getDoubleList(name: String): ${prefix}BootupErrors[_root_.scala.List[Double]] = ???",
        s"def getDurationList(name: String): ${prefix}BootupErrors[_root_.scala.List[_root_.scala.concurrent.duration.Duration]] = ???",
        s"def getLongList(name: String): ${prefix}BootupErrors[_root_.scala.List[Long]] = ???",
        s"def getObjectList(name: String): ${prefix}BootupErrors[_root_.scala.List[_root_.com.typesafe.config.ConfigObject]] = ???",

        s"def getConfig(name: String): ${prefix}BootupErrors[${prefix}LiftedTypesafeConfig]  = ???",
        s"def getRawConfig(name: String): ${prefix}BootupErrors[_root_.com.typesafe.config.Config]  = ???"
      )
    }

    source match {
      case SafeConfigObject(clazz, _) => genFuncs(clazz)
      case SafeConfigClass(clazz) => genFuncs(clazz)
      case _ =>
        Seq.empty
    }
  }

  override def injectInners(source: ScTypeDefinition): Seq[String] = {
    source match {
      case SafeConfigObject(clazz, _) =>
        val prefix = clazzPrefix(clazz)
        Seq(s"""
          |final class BootupErrors[A] private[BootupErrors] (val value: A) extends AnyVal {
          |  def map[B](f : A ⇒ B) : ${prefix}BootupErrors[B] = ???
          |  def flatMap[B](f : A ⇒ ${prefix}BootupErrors[B]) : ${prefix}BootupErrors[B] = ???
          |  def <*>[B, C](that : ${prefix}BootupErrors[B])(implicit ev : <:<[A, B ⇒ C]) : ${prefix}BootupErrors[C] = ???
          |  def flatten[B](implicit ev : A <:< ${prefix}BootupErrors[B]) : ${prefix}BootupErrors[B] = ???
          |  def fold[B](err : Seq[_root_.com.kinja.config.ConfigError] ⇒ B, succ : A ⇒ B) : B = ???
          |  def getOrElse(e : Seq[_root_.com.kinja.config.ConfigError] ⇒ A) : A = ???
          |  def errors : Seq[_root_.com.kinja.config.ConfigError] = ???
          |  def toOption : Option[A] = ???
          |}
        """.stripMargin,
        s"""
           |object BootupErrors {
           |  def apply[A](a : A) : ${prefix}BootupErrors[A] = ???
           |	implicit def bootupErrorsToType[T](value:${prefix}BootupErrors[T]):T = ???
           |}
        """.stripMargin,
        s"""
           |trait LiftedTypesafeConfig {
           |	def getBoolean(name: String): ${prefix}BootupErrors[Boolean] = ???
           |	def getString(name: String): ${prefix}BootupErrors[String] = ???
           |	def getInt(name: String): ${prefix}BootupErrors[Int] = ???
           |	def getDouble(name: String): ${prefix}BootupErrors[Double] = ???
           |	def getDuration(name: String): ${prefix}BootupErrors[_root_.scala.concurrent.duration.Duration] = ???
           |	def getLong(name: String): ${prefix}BootupErrors[Long] = ???
           |	def getObject(name: String): ${prefix}BootupErrors[_root_.com.typesafe.config.ConfigObject] = ???
           |
           |	def getBooleanList(name: String): ${prefix}BootupErrors[_root_.scala.List[Boolean]] = ???
           |	def getStringList(name: String): ${prefix}BootupErrors[_root_.scala.List[String]] = ???
           |	def getIntList(name: String): ${prefix}BootupErrors[_root_.scala.List[Int]] = ???
           |	def getDoubleList(name: String): ${prefix}BootupErrors[_root_.scala.List[Double]] = ???
           |	def getDurationList(name: String): ${prefix}BootupErrors[_root_.scala.List[_root_.scala.concurrent.duration.Duration]] = ???
           |	def getLongList(name: String): ${prefix}BootupErrors[_root_.scala.List[Long]] = ???
           |	def getObjectList(name: String): ${prefix}BootupErrors[_root_.scala.List[_root_.com.typesafe.config.ConfigObject]] = ???
           |
           |	def getConfig(name: String): ${prefix}BootupErrors[${prefix}LiftedTypesafeConfig]  = ???
           |	def getRawConfig(name: String): ${prefix}BootupErrors[_root_.com.typesafe.config.Config]  = ???
           |	def underlying : _root_.com.typesafe.config.Config = ???
           |}
        """.stripMargin)
      case _ =>
        Seq.empty
    }
  }

  override def injectMembers(source: ScTypeDefinition): Seq[String] = {
    source match {
      case SafeConfigClass(clazz) =>
        val prefix = clazzPrefix(clazz)
        Seq(s"""type BootupErrors[A]=${prefix}BootupErrors[A]""".stripMargin)
      case _ =>
        Seq.empty
    }
  }

  def clazzPrefix(clazz: ScClass) =  s"_root_.${clazz.getPath}.${clazz.name}."

  override def needsCompanionObject(source: ScTypeDefinition): Boolean =
    hasSafeConfig(source)
}

object SafeConfigInjector {

  def hasSafeConfig(source: ScTypeDefinition) =
    source.findAnnotationNoAliases("com.kinja.config.safeConfig") != null

  object SafeConfigClassOrObject {
    def unapply(source: ScTypeDefinition): Option[ScTypeDefinition] =
      source match {
        case clazz: ScTypeDefinition if hasSafeConfig(source) => Some(clazz)
        case _ => None
      }
  }

  object SafeConfigClass {
    def unapply(source: ScTypeDefinition): Option[ScClass] =
      source match {
        case clazz: ScClass if hasSafeConfig(source) => Some(clazz)
        case _ => None
      }
  }

  object SafeConfigObject {
    def unapply(source: ScTypeDefinition): Option[(ScClass, ScObject)] =
      source match {
        case obj: ScObject =>
          obj.fakeCompanionClassOrCompanionClass match {
            case clazz: ScClass if hasSafeConfig(clazz) =>
              Some((clazz, obj))
            case _ =>
              None
          }
        case _ => None
      }
  }
}