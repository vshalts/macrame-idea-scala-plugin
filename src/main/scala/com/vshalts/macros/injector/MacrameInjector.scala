package com.vshalts.macros.injector

import org.jetbrains.plugins.scala.lang.psi.api.expr.ScMethodCall
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ScClass, ScObject, ScTypeDefinition}
import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.SyntheticMembersInjector

class MacrameInjector extends SyntheticMembersInjector {
  import MacrameInjector._

  override def injectFunctions(source: ScTypeDefinition): Seq[String] = {
    source match {
      case EnumClass(_) =>
        Seq(
          "override def canEqual(that: Any): _root_.scala.Boolean = ???",
          "override def productElement(n: Int): _root_.scala.Any = ???",
          "override def productArity: _root_.scala.Int = 0"
        )
      case EnumObject(clazz, _) =>
        Seq(
          s"override def asStringImpl(enum : ${clazz.name}): _root_.scala.String = ???",
          s"override def fromStringImpl(str : String) :  _root_.scala.Option[${clazz.name}] = ???",
          s"override def asIntImpl(enum : ${clazz.name}): _root_.scala.Int = ???",
          s"override def fromIntImpl(int : Int): _root_.scala.Option[${clazz.name}] = ???",
          s"override def firstImpl: ${clazz.name} = ???",
          s"override def lastImpl: ${clazz.name} = ???",
          s"override def valuesImpl: _root_.scala.Predef.Set[${clazz.name}] = ???",
          s"override def className: String = ???"
        )
      case _ =>
        Seq.empty
    }
  }

  override def injectInners(source: ScTypeDefinition): Seq[String] = {
    source match {
      case EnumClass(clazz) =>
        // We need this only to prevent IDEA report errors.
        enumMembers(clazz).map(name => s"private case object $name extends ${clazz.name}")
      case EnumObject(clazz, _) =>
        enumMembers(clazz).map(name => s"case object $name extends ${clazz.name}")
      case _ =>
        Seq.empty
    }
  }


  override def injectSupers(source: ScTypeDefinition): Seq[String] = {
    source match {
      case EnumClass(_) =>
        Seq("Product", "Serializable")
      case EnumObject(clazz, _) =>
        Seq(s"_root_.macrame.EnumApi[${clazz.name}]")
      case _ =>
        Seq.empty
    }
  }

  override def needsCompanionObject(source: ScTypeDefinition): Boolean =
    hasMacrameEnum(source)
}

object MacrameInjector {

  private def enumMembers(clazz: ScClass) =
    clazz.extendsBlock.templateBody.toSeq.flatMap(_.getChildren).map { ref =>
      ref match {
        case mc: ScMethodCall =>
          mc.deepestInvokedExpr.getText
        case _ =>
          ref.getText
      }
    }

  def hasMacrameEnum(source: ScTypeDefinition) =
    source.findAnnotationNoAliases("macrame.enum") != null

  object EnumClass {
    def unapply(source: ScTypeDefinition): Option[ScClass] =
      source match {
        case clazz: ScClass if hasMacrameEnum(source) => Some(clazz)
        case _ => None
      }
  }

  object EnumObject {
    def unapply(source: ScTypeDefinition): Option[(ScClass, ScObject)] =
      source match {
        case obj: ScObject =>
          obj.fakeCompanionClassOrCompanionClass match {
            case clazz: ScClass if hasMacrameEnum(clazz) =>
              Some((clazz, obj))
            case _ =>
              None
          }
        case _ => None
      }
  }
}
