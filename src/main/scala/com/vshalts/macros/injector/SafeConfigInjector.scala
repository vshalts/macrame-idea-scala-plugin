package com.vshalts.macros.injector

import org.jetbrains.plugins.scala.lang.psi.api.statements.ScPatternDefinition
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.ScTypeDefinition
import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.SyntheticMembersInjector
import org.jetbrains.plugins.scala.lang.psi.types.ScParameterizedType

class SafeConfigInjector extends SyntheticMembersInjector {
  import SafeConfigInjector._


  override def injectSupers(source: ScTypeDefinition): Seq[String] = {
    source match {
      case SafeConfigClassOrObject(_) =>
        Seq("_root_.com.kinja.config.ConfigApi")
      case _ =>
        Seq.empty
    }
  }

  override def injectMembers(source: ScTypeDefinition): Seq[String] = {
    source match {
      case SafeConfigClassOrObject(clazz) =>

        val replacedVars = for {
          parameter <- clazz.extendsBlock.templateBody.toSeq.flatMap(_.getChildren).collect {
            case f: ScPatternDefinition => f
          }
          binding <- parameter.bindings
          expectedType <- binding.pattern.expectedType
          valueType <- expectedType match {
            case eType: ScParameterizedType if eType.designator.canonicalText == "_root_.com.kinja.config.BootupErrors" => Some(eType.typeArguments.head)
            case _ => None
          }
        } yield {
          s"""val ${binding.name}: ${valueType.canonicalText} = ???"""
        }

        Seq(s"""protected val root: _root_.com.kinja.config.BootupErrors[_root_.com.kinja.config.LiftedTypesafeConfig] = ???""") ++ replacedVars
      case _ =>
        Seq.empty
    }
  }
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
}