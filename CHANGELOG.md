# Changelog

All notable changes to this library will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.1] - 2026-03-18

- Upgrade to Kotlin 2.0.21 and Gradle 8.12
- Enable explicitApi() for stricter public API surface
- Add issueManagement to POM metadata

## [Unreleased]

## [0.2.0] - 2026-03-18

### Added
- `isValidSlug()` function to check if a string is already a valid slug
- `slugifyAll()` batch function to generate slugs for a list of strings
- `NormalizationForm` enum with NFC, NFD, NFKC, NFKD options
- `SlugConfig.normalizationForm` property to configure Unicode normalization
- Support for configurable separator in `isValidSlug()`

### Changed
- Unicode normalization is now always applied (previously only when `transliterate` was enabled)
- Improved handling of empty separator edge cases

## [0.1.1] - 2026-03-18

- Fix CI badge and gradlew permissions

## [0.1.0] - 2026-03-17

### Added
- `slug()` top-level function for URL-friendly slug generation
- Unicode transliteration via NFD normalization
- `SlugConfig` with `separator`, `maxLength`, `lowercase`, `transliterate`, `customReplacements`
- `uniqueSlug()` suspend function for generating unique slugs with incrementing suffixes
