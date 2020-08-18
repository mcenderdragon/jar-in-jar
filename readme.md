This is a read only FIleSystem implementation for zip/jar files. It is possible with this to open zip files already contained in zip files or any other place, as long as they are readable.

# URI Syntax
The URI Syntax is `jarInJar:<path in zip>?<URI>`, URI has to be a fully working URI to the ZIP file you want to read, this works as long as you can open an InputStream to with the URI so opening jars in zips in jars in zips is totally fine.

# Usage & Helper

##Building

With `./gradlew build` you build the jar, the jar is configured with the manifest so it registers the `jarInJar` FileSystemProvider. 

##Test

Run `./gradlew testJar` to run tests

## Helper

`mcenderdragon.nio.jarInjar.HelperURIFormatter` is a helper with the method `asJarInJarURI` converting, `File`, `Path` and `URI` objects to jarInJar URIs.