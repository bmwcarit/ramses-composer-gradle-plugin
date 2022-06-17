//  -------------------------------------------------------------------------
//  Copyright (C) 2022 BMW AG
//  -------------------------------------------------------------------------
//  This Source Code Form is subject to the terms of the Mozilla Public
//  License, v. 2.0. If a copy of the MPL was not distributed with this
//  file, You can obtain one at https://mozilla.org/MPL/2.0/.
//  -------------------------------------------------------------------------

package io.github.bmwcarit

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty
import org.gradle.api.GradleException

import java.util.concurrent.TimeUnit

interface RaCoAssetGeneratorPluginExtension {
    Property<String> getRaCoHeadlessPath()

    // Timeout for a single RaCo exporter run
    Property<Long> getExecutionTimeout()

    Property<TimeUnit> getExecutionTimeoutUnit()

    ListProperty<List<Tuple2<String, String>>> getInputs()
}

class RaCoPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('raCoConfig', RaCoAssetGeneratorPluginExtension)

        extension.executionTimeout.convention(20L)
        extension.executionTimeoutUnit.convention(TimeUnit.SECONDS)

        project.task('RaCoExport') {
            doLast {
                for (input in extension.inputs.get()) {
                    def racoFile = input[0]
                    def outputFile = input[1]

                    def racoFileObject = new File(racoFile)
                    def outputFileObject = new File("${outputFile}.ramses")

                    if (!racoFileObject.exists()) {
                        throw new GradleException("Input RaCo file ${racoFile} does not exist.")
                    }

                    // create output directory(ies) if not present
                    def outputDirs = new File(outputFileObject.getParent())
                    if (!outputDirs.exists()) {
                        println "Output directory ${outputDirs.toString()} does not exist, creating"
                        outputDirs.mkdirs()
                    }

                    if (outputFileObject.lastModified() >= racoFileObject.lastModified()) {
                        println "Not re-running RaCo exporter since ${outputFile}.ramses is newer than ${racoFile}"
                        continue
                    }

                    def command = "${extension.raCoHeadlessPath.get()} -p ${racoFile} -e ${outputFile} -l 1"
                    println "Executing ${command}"
                    def process = command.execute()

                    // set a timeout to kill the process if it takes too long
                    def executionTimeout = extension.executionTimeout.get()
                    def timeUnit = extension.executionTimeoutUnit.get()
                    def processSuccessfullyEnded = process.waitFor(executionTimeout, timeUnit)
                    if (!processSuccessfullyEnded) {
                        process.destroy()
                        throw new GradleException("External command timed out after ${executionTimeout} ${timeUnit}: ${command}")
                    }

                    if (process.exitValue()) {
                        throw new GradleException("Error when exporting ${racoFile}")
                    } else {
                        println "${racoFile} successfully exported to ${outputFile}"
                    }
                }
            }
        }
    }
}
