This is a read only FIleSystem implementation for zip/jar files. It is possible with this to open zip files already contained in zip files or any other place, as long as they are readable.

# URI Syntax
The URI Syntax is `jarInJar:<path in zip>?<URI>`, URI has to be a fully working URI to the ZIP file you want to read, this works as long as you can open an InputStream to with the URI so opening jars in zips in jars in zips is totally fine.

# Usage & Helper

## Building

With `./gradlew build` you build the jar, the jar is configured with the manifest so it registers the `jarInJar` FileSystemProvider. 

## Test

Run `./gradlew testJar` to run tests

## Helper

`mcenderdragon.nio.jarInjar.HelperURIFormatter` is a helper with the method `asJarInJarURI` converting, `File`, `Path` and `URI` objects to jarInJar URIs.

# Benchmarks

Some Benchmarks are implemented via JHM. It tests ordered (the order provided by `Files#walk` ) and random File access for small, medium and large jars. The results are against the Java default `jar` FileSystem:
```
Benchmark                                                Mode  Cnt     Score    Error  Units
jarInjar.jmh.Benchmarks.benchJAR_large_ordered          thrpt   15     3,576 ±  0,035  ops/s
jarInjar.jmh.Benchmarks.benchJAR_large_unordered        thrpt   15     3,120 ±  0,026  ops/s
jarInjar.jmh.Benchmarks.benchJAR_medium_ordered         thrpt   15     8,403 ±  0,064  ops/s
jarInjar.jmh.Benchmarks.benchJAR_medium_unordered       thrpt   15     7,722 ±  0,060  ops/s
jarInjar.jmh.Benchmarks.benchJAR_small_ordered          thrpt   15    76,833 ±  0,763  ops/s
jarInjar.jmh.Benchmarks.benchJAR_small_unordered        thrpt   15    76,155 ±  0,886  ops/s
jarInjar.jmh.Benchmarks.benchJARinJAR_large_ordered     thrpt   15    41,230 ±  0,420  ops/s
jarInjar.jmh.Benchmarks.benchJARinJAR_large_unordered   thrpt   15    32,468 ±  1,889  ops/s
jarInjar.jmh.Benchmarks.benchJARinJAR_medium_ordered    thrpt   15   101,938 ±  2,200  ops/s
jarInjar.jmh.Benchmarks.benchJARinJAR_medium_unordered  thrpt   15    90,128 ±  1,703  ops/s
jarInjar.jmh.Benchmarks.benchJARinJAR_small_ordered     thrpt   15  1343,054 ± 96,711  ops/s
jarInjar.jmh.Benchmarks.benchJARinJAR_small_unordered   thrpt   15  1268,893 ± 88,214  ops/s
```