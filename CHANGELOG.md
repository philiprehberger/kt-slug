# Changelog

## 0.2.4 (2026-03-31)

- Standardize README to 3-badge format with emoji Support section
- Update CI checkout action to v5 for Node.js 24 compatibility
- Add GitHub issue templates, dependabot config, and PR template

## 0.2.3 (2026-03-20)

- Fix README: remove Groovy section, update badge label to "Tests"
- Fix CHANGELOG formatting: split malformed entry, remove preamble

## 0.2.2 (2026-03-20)

- Standardize README: fix title, badges, version sync, remove Requirements section

## 0.2.1 (2026-03-18)

- Upgrade to Kotlin 2.0.21 and Gradle 8.12
- Enable explicitApi() for stricter public API surface
- Add issueManagement to POM metadata

## 0.2.0 (2026-03-18)

### Added
- `isValidSlug()` function to check if a string is already a valid slug
- `slugifyAll()` batch function to generate slugs for a list of strings
- `NormalizationForm` enum with NFC, NFD, NFKC, NFKD options
- `SlugConfig.normalizationForm` property to configure Unicode normalization
- Support for configurable separator in `isValidSlug()`

### Changed
- Unicode normalization is now always applied (previously only when `transliterate` was enabled)
- Improved handling of empty separator edge cases

## 0.1.1 (2026-03-18)

- Fix CI badge and gradlew permissions

## 0.1.0 (2026-03-17)

### Added
- `slug()` top-level function for URL-friendly slug generation
- Unicode transliteration via NFD normalization
- `SlugConfig` with `separator`, `maxLength`, `lowercase`, `transliterate`, `customReplacements`
- `uniqueSlug()` suspend function for generating unique slugs with incrementing suffixes
