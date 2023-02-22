package com.cookieinformation.mobileconsents

import com.cookieinformation.mobileconsents.ConsentItem.Type
import java.util.UUID

/**
 * One of [ConsentSolution] items, which user can accept or reject.
 *
 * All items with type [ConsentItem.Type.Setting] are required and should be accepted by default.
 *
 * @param consentItemId [UUID] of consent item.
 * @param shortText list of all available translations for brief description of the consent.
 * @param longText list of all available translations for full description of the consent.
 * @param required true if uses must accept the consent, otherwise false.
 * @param type [Type] of the consent.
 */
public data class ConsentItem(
  val consentItemId: UUID,
  val shortText: List<TextTranslation>,
  val longText: List<TextTranslation>,
  val required: Boolean,
  val type: Type,
) {

  public sealed class Type(public val name: String) {
    public abstract val isSetting: Boolean

    public companion object{
      public fun findTypeByValue(value: String): Type {
        return when (value) {
          Info.name -> Info
          TypeNecessary.name -> TypeNecessary
          TypeMarketing.name -> TypeMarketing
          TypeStatistical.name -> TypeStatistical
          TypeFunctional.name -> TypeFunctional
          else -> Setting(value)
        }
      }
    }
    public object Info : Type("privacy policy") {
      override val isSetting: Boolean = false
    }

    public class Setting(private val customValue: String) : Type(customValue) {
      override val isSetting: Boolean = true
    }

    public object TypeNecessary : Type("necessary") {
      override val isSetting: Boolean = true
    }

    public object TypeMarketing : Type("marketing") {
      override val isSetting: Boolean = true
    }

    public object TypeStatistical : Type("statistical") {
      override val isSetting: Boolean = true
    }

    public object TypeFunctional : Type("functional") {
      override val isSetting: Boolean = true
    }
  }
}
