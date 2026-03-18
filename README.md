# kt-slug

[![CI](https://github.com/philiprehberger/kt-slug/actions/workflows/publish.yml/badge.svg)](https://github.com/philiprehberger/kt-slug/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.philiprehberger/slug)](https://central.sonatype.com/artifact/com.philiprehberger/slug)

URL-friendly slug generation from Unicode strings with transliteration, validation, and batch processing.

## Requirements

- Kotlin 1.9+ / Java 17+

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.philiprehberger:slug:0.2.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.philiprehberger:slug:0.2.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>slug</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Usage

```kotlin
import com.philiprehberger.slug.*

slug("Hello, World!")       // "hello-world"
slug("Caf\u00e9 Latt\u00e9")   // "cafe-latte"
slug("Version 2.0.1")      // "version-2-0-1"
```

### Configuration

```kotlin
slug("Hello World") { separator = "_" }          // "hello_world"
slug("Hello World") { separator = "." }          // "hello.world"
slug("A Very Long Title") { maxLength = 10 }     // "a-very"
slug("Hello World") { lowercase = false }         // "Hello-World"

slug("C++ Programming") {
    customReplacements = mapOf("++" to "plus-plus")
}
// "cplus-plus-programming"
```

### Unicode Normalization

Control the Unicode normalization form used during slug generation:

```kotlin
// NFKD decomposes compatibility characters (e.g., ligatures)
slug("\ufb01nance") { normalizationForm = NormalizationForm.NFKD }
// "finance"

// Default is NFD (canonical decomposition)
slug("Caf\u00e9") { normalizationForm = NormalizationForm.NFD }
// "cafe"
```

Available forms: `NFC`, `NFD`, `NFKC`, `NFKD`.

### Slug Validation

Check if a string is already a valid slug:

```kotlin
isValidSlug("hello-world")         // true
isValidSlug("Hello-World")         // false (uppercase)
isValidSlug("hello--world")        // false (consecutive separators)
isValidSlug("-hello-world")        // false (leading separator)
isValidSlug("hello world")         // false (spaces)

// With custom separator
isValidSlug("hello_world", separator = "_")  // true
```

### Batch Slugification

Generate slugs for multiple strings at once:

```kotlin
slugifyAll(listOf("Hello World", "Foo Bar", "Caf\u00e9"))
// ["hello-world", "foo-bar", "cafe"]

slugifyAll(listOf("Hello World", "Foo Bar")) { separator = "_" }
// ["hello_world", "foo_bar"]
```

### Unique Slugs

```kotlin
val uniqueSlug = uniqueSlug("hello-world") { candidate ->
    database.slugExists(candidate)
}
// "hello-world", "hello-world-1", "hello-world-2", etc.
```

## API

| Function / Class | Description |
|------------------|-------------|
| `slug(input, config)` | Generate a URL-friendly slug |
| `slugifyAll(inputs, config)` | Generate slugs for a list of strings |
| `isValidSlug(value, separator)` | Check if a string is a valid slug |
| `uniqueSlug(base, exists)` | Generate a unique slug with suffix |
| `SlugConfig.separator` | Word separator (default: `"-"`) |
| `SlugConfig.maxLength` | Maximum slug length (default: unlimited) |
| `SlugConfig.lowercase` | Convert to lowercase (default: `true`) |
| `SlugConfig.transliterate` | Strip accents via normalization (default: `true`) |
| `SlugConfig.normalizationForm` | Unicode normalization form (default: `NFD`) |
| `SlugConfig.customReplacements` | Custom string replacements |
| `NormalizationForm` | Enum: `NFC`, `NFD`, `NFKC`, `NFKD` |

## Development

```bash
./gradlew test       # Run tests
./gradlew check      # Run all checks
./gradlew build      # Build JAR
```

## License

MIT
