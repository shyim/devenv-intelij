package de.shyim.idea.devenv.run

import de.shyim.idea.devenv.action.ConfigurePHPInterpreter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.codeInsight.hint.HintManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.NotificationsManager
import com.intellij.openapi.project.guessProjectDir
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.run.PhpRunConfiguration
import com.jetbrains.php.run.PhpRunConfigurationExtension
import org.apache.commons.io.IOUtils
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

class PhpConfigurationExtension: PhpRunConfigurationExtension() {
    override fun isApplicableFor(configuration: PhpRunConfiguration<*>): Boolean {
        return true
    }

    override fun isEnabledFor(applicableConfiguration: PhpRunConfiguration<*>, runnerSettings: RunnerSettings?): Boolean {
        return true
    }

    override fun patchCommandLine(configuration: PhpRunConfiguration<*>, runnerSettings: RunnerSettings?, cmdLine: GeneralCommandLine, runnerId: String) {
        val process = GeneralCommandLine("direnv", "exec", "/", "direnv", "export", "json")
                .withWorkDirectory(cmdLine.workDirectory)
                .createProcess()

        if (process.waitFor() != 0) {
            val stdErr: String = IOUtils.toString(process.errorStream, StandardCharsets.UTF_8)

            NotificationGroupManager.getInstance()
                .getNotificationGroup("Devenv")
                .createNotification("Direnv error", stdErr, NotificationType.ERROR)

            return
        }

        val stdOut: String = IOUtils.toString(process.inputStream, StandardCharsets.UTF_8)

        val type: Type = object : TypeToken<Map<String?, String?>?>() {}.type

        val returnMap  = Gson().fromJson<Map<String, String>>(stdOut, type)

        if (returnMap.isNotEmpty()) {
            returnMap.forEach { (key, value) ->
                cmdLine.environment[key] = value
            }
        }
    }

    override fun isApplicable(phpInterpreter: PhpInterpreter?): Boolean {
        return phpInterpreter?.name == ConfigurePHPInterpreter.INTERPRETER_NAME
    }
}
