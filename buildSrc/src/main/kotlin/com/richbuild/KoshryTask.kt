package com.richbuild

import io.github.tarek360.koshry.Koshry
import io.github.tarek360.koshry.koshry
import io.github.tarek360.rules.core.Level.ERROR
import io.github.tarek360.rules.protectedFileRule
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class KoshryTask : DefaultTask() {


  @TaskAction
  fun run() {

    val configuration = koshry {

      baseSha = ""

      rules {
        rule = protectedFileRule {
          reportTitle = "Files are protected and can't be modified, ask @tarek360 to modify"
          issueLevel = ERROR()
          files {
            filePath = ".circleci/config.yml"
          }
          excludeAuthors {
            author = "tarek360"
          }
        }
      }
    }

    Koshry.run(configuration)
  }
}