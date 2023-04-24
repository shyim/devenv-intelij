package de.shyim.idea.devenv.listener

import de.shyim.idea.devenv.action.ConfigurePHPInterpreter
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VfsUtil

class ProjectOpenedListener: ProjectActivity {
    override suspend fun execute(project: Project) {
        if (!isDevenvProject(project)) {
            return
        }

        if (ConfigurePHPInterpreter.hasDevenvInterpreter(project) != null) {
            return
        }

        val notification: Notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("Devenv")
                .createNotification("Detected Devenv files. Enable Devenv support?", NotificationType.INFORMATION)

        notification.addAction(object : NotificationAction("Enable") {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                ConfigurePHPInterpreter().actionPerformed(e)
                notification.expire()
            }
        })

        notification.notify(project)
    }

    private fun isDevenvProject(project: Project): Boolean {
        return VfsUtil.findRelativeFile(project.guessProjectDir(), ".devenv", "profile", "bin") != null
    }
}