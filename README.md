# kt-slug

[![CI](https://github.com/philiprehberger/kt-slug/actions/workflows/publish.yml/badge.svg)](https://github.com/philiprehberger/kt-slug/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.philiprehberger/slug)](https://central.sonatype.com/artifact/com.philiprehberger/slug)

URL-friendly slug generation from Unicode strings with transliteration.

## Requirements

- Kotlin 1.9+ / Java 17+

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.philiprehberger:slug:0.1.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.philiprehberger:slug:0.1.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>slug</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Usage

```kotlin
import com.philiprehberger.slug.*

slug("Hello, World!")       // "hello-world"
slug("Cafe\u0301 Latte\u0301")   // "cafe-latte"
slug("Version 2.0.1")      // "version-2-0-1"
```

### Configuration

```kotlin
slug("Hello World") { separator = "_" }          // "hello_world"
slug("A Very Long Title") { maxLength = 10 }     // "a-very"
slug("Hello World") { lowercase = false }         // "Hello-World"

slug("C++ Programming") {
    customReplacements = mapOf("++" to "plus-plus")
}
// "cplus-plus-programming"
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
| `uniqueSlug(base, exists)` | Generate a unique slug with suffix |
| `SlugConfig.separator` | Word separator (default: `"-"`) |
| `SlugConfig.maxLength` | Maximum slug length (default: unlimited) |
| `SlugConfig.lowercase` | Convert to lowercase (default: `true`) |
| `SlugConfig.transliterate` | Strip accents via NFD (default: `true`) |
| `SlugConfig.customReplacements` | Custom string replacements |

## Development

```bash
./gradlew test       # Run tests
./gradlew check      # Run all checks
./gradlew build      # Build JAR
```

## License

MIT
