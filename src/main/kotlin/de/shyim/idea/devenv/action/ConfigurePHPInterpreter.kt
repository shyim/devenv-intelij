package de.shyim.idea.devenv.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import com.jetbrains.php.config.PhpProjectConfigurationFacade;
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.config.interpreters.PhpInterpretersPhpInfoCacheImpl
import com.jetbrains.php.config.phpInfo.PhpInfoUtil

class ConfigurePHPInterpreter: DumbAwareAction() {
    companion object {
        const val INTERPRETER_NAME = "Devenv"

        fun hasDevenvInterpreter(project: Project): PhpInterpreter? {
            return PhpInterpretersManagerImpl.getInstance(project).interpreters.find { it.name == INTERPRETER_NAME }
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val phpInterpretersManagerImpl = PhpInterpretersManagerImpl.getInstance(e.project!!)
        val phpConfigurationFacade = PhpProjectConfigurationFacade.getInstance(e.project!!);
        val phpConfiguration = phpConfigurationFacade.projectConfiguration;

        var devenvInterpreter: String;

        try {
            devenvInterpreter = getInterpreterPath(e.project!!)
        } catch (e: Exception) {
            Messages.showInfoMessage("Cannot find PHP binary", "Devenv")
            return
        }

        val existingInterpreter = hasDevenvInterpreter(e.project!!)
        if (existingInterpreter != null) {
            existingInterpreter.homePath = devenvInterpreter
            existingInterpreter.setIsProjectLevel(true)

            phpConfiguration.interpreterName = existingInterpreter.name;
            loadPhpInfo(e.project!!, existingInterpreter)

            return
        }

        val interpreter = PhpInterpreter();
        interpreter.name = INTERPRETER_NAME
        interpreter.setIsProjectLevel(true)
        interpreter.homePath = devenvInterpreter

        if (phpConfiguration.interpreterName == null) {
            phpConfiguration.interpreterName = interpreter.name;
        }

        phpInterpretersManagerImpl.addInterpreter(interpreter)

        loadPhpInfo(e.project!!, interpreter)
    }

    private fun getInterpreterPath(project: Project): String {
        val phpBinaries = listOf("php-pcov", "php-xdebug", "php")

        for (binary in phpBinaries) {
            if (VfsUtil.findRelativeFile(project.guessProjectDir(), ".devenv", "profile", "bin", binary) !== null) {
                return VfsUtil.findRelativeFile(project.guessProjectDir(), ".devenv", "profile", "bin", binary)!!.path
            }
        }

        throw Exception("No PHP binary found")
    }

    private fun loadPhpInfo(project: Project, interpreter: PhpInterpreter) {
        val phpInfo = PhpInfoUtil.getPhpInfo(project, interpreter, null) ?: return
        PhpInterpretersPhpInfoCacheImpl.getInstance(project).setPhpInfo(interpreter.name, phpInfo);
    }
}