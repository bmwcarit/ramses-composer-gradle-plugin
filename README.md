# ramses-composer-gradle-plugin
A Gradle plugin to enable ramses scene export integration in Gradle projects.

Plugin enables integration of automated scene exporting from [Ramses Composer](https://github.com/bmwcarit/ramses-composer) projects.

Once applied Ramses Compose Plugin makes *RaCoExport* task available in the project, which needs to be configured via *raCoConfig* extention with something similar to this:
    
    raCoConfig {
        raCoHeadlessPath = 'path_to_raco_headless_executable'
        // inputs should be specified as a list of pairs of inputs and output locations
        inputs = [['path_to_scene1/scene1.rca', 'export_folder_for_scene1'],
                  ['path_to_scene2/scene2.rca', 'export_folder_for_scene2']]
    }

    preBuild.dependsOn RaCoExport

