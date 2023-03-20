package com.cookieinformation.mobileconsents.ui

import com.cookieinformation.mobileconsents.ConsentSolution
import java.util.UUID

/**
 * The events listener of [ConsentSolutionPresenter]
 */
public interface ConsentSolutionListener {

  /**
   * The method is called when the consents have been saved.
   * The save action can be triggered by current presenter, then the [external] parameter is set to [false],
   * otherwise is set to [true].
   *
   * @param consentSolution [ConsentSolution] associated with current presenter.
   * @param consents map of all saved choices of consents.
   * @param external true if consents were accepted in current presenter otherwise false
   */
  public fun onConsentsChosen(consentSolution: ConsentSolution, consents: Map<UUID, Boolean>, external: Boolean)

  /**
   * The method is called when the user wants to close the view without saving his choice.
   */
  public fun onDismissed()

  /**
   * The method is called when a user wants to read more about the consents.
   * In most cases it should lead to "Privacy Center" view.
   */
  public fun onReadMore(info: String, poweredBy: String)
}
