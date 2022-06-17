# ramses-composer-gradle-plugin
A Gradle plugin to enable ramses scene export integration in Gradle projects.

Plugin enables integration of automated scene exporting from [Ramses Composer](https://github.com/bmwcarit/ramses-composer) projects.

Plugin can be configured with *raCoConfig* extention object.
Following properties are available:
* *raCoHeadlessPath* - path to RaCoHeadless executable
* *inputs* - a list of input and output location pairs
* *executionTimeout* - timeout to wait for a single RaCoHeadless execution to finish, defaults to 20 seconds.
* *executionTimeoutUnit* - *TimeUnit* for *executionTimeout*, defaults to *TimeUnit.SECONDS*

Once applied Ramses Compose Plugin makes *RaCoExport* task available in the project, which needs to be configured via *raCoConfig* extention with something similar to this:
    
    // configuring RaCo Plugin
    raCoConfig {
        raCoHeadlessPath = 'path_to_raco_headless_executable'
        // inputs should be specified as a list of input and output location pairs
        inputs = [['path_to_scene1/scene1.rca', 'export_folder_for_scene1'],
                  ['path_to_scene2/scene2.rca', 'export_folder_for_scene2']]
    }

    // setting a dependency
    preBuild.dependsOn RaCoExport

