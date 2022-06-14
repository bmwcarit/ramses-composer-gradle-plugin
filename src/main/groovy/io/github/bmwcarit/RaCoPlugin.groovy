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

interface RaCoAssetGeneratorPluginExtension {
    Property<String> getRaCoHeadlessPath()

    ListProperty<List<Tuple2<String, String>>> getInputs()
}

class RaCoPlugin implements Plugin<Project> {
    void apply(Project project) {
        def extension = project.extensions.create('raCoConfig', RaCoAssetGeneratorPluginExtension)
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
                    if(! outputDirs.exists()) {
                        println "Output directory ${outputDirs.toString()} does not exist, creating"
                        outputDirs.mkdirs()
                    }

                    if (outputFileObject.lastModified() >= racoFileObject.lastModified()) {
                        println "Not re-running RaCo exporter since ${outputFile}.ramses is newer than ${racoFile}"
                        continue
                    }

                    def command = "${extension.raCoHeadlessPath.get()} -p ${racoFile} -e ${outputFile}  -l 1"
                    println "Executing ${command}"
                    def process = command.execute()
                    process.waitFor()

                    if (process.exitValue()) {
                        throw new GradleException("Error while exporting ${racoFile}")
                    }
                }
            }
        }
    }
}
