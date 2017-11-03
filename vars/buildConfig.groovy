class buildConfig implements Serializable {

    def signingKeys = [
            'release': [:]


    ]


    def alphaReleases= [
            'uk-release': [
                    'label'     : 'build-uk-debug',
                    'gradleTask': 'assembleUkRelease',
                    'hockeyId'  : 'cc0df4bdadd44e7ebfe0d4c0d3e34566'

            ]
    ]

    def gradleArgs = "-PjenkinsFastDexguardBuildsEnabled=true -Pandroid.enableBuildCache=true -PtestCoverageFlag=true --profile --no-daemon"

}
