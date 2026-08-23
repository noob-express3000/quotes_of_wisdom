$ErrorActionPreference = "Stop"

$version = "9.5.0"
$zip = Join-Path $PSScriptRoot "gradle-$version-bin.zip"
$dir = Join-Path $PSScriptRoot ".bootstrap-gradle"
$gradleHome = Join-Path $dir "gradle-$version"

if (!(Test-Path $gradleHome)) {
    New-Item -ItemType Directory -Force -Path $dir | Out-Null
    Invoke-WebRequest `
        -Uri "https://services.gradle.org/distributions/gradle-$version-bin.zip" `
        -OutFile $zip
    Expand-Archive -Path $zip -DestinationPath $dir -Force
    Remove-Item $zip -Force
}

& (Join-Path $gradleHome "bin\gradle.bat") wrapper --gradle-version $version
Write-Host ""
Write-Host "Gradle wrapper generated."
Write-Host "Run: .\gradlew.bat :app:assembleDebug"
