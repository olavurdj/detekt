package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports class names which are forbidden per configuration.
 * By default this rule does not report any classes.
 * Examples for forbidden names might be too generic class names like `...Manager`.
 *
 * @configuration forbiddenName - forbidden class names (default: `''`)
 * @author Marvin Ramin
 */
class ForbiddenClassName(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "Forbidden class name as per configuration detected.",
            Debt.FIVE_MINS)
    private val forbiddenNames = SplitPattern(valueOrDefault(FORBIDDEN_NAME, ""))

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        val name = classOrObject.name ?: ""
        val forbiddenEntries = forbiddenNames.matches(name)

        if (forbiddenEntries.isNotEmpty()) {
            var message = "Class name $name is forbidden as it contains:"
            forbiddenEntries.forEach { message += " $it," }
            message.trimEnd { it == ',' }

            report(CodeSmell(issue, Entity.from(classOrObject), message))
        }
    }

    companion object {
        const val FORBIDDEN_NAME = "forbiddenName"
    }
}
