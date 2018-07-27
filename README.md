# Macramé and SafeCondig Intellij scala plugin

Plagin that implements Macramé and Safe-condig macroses support in IntelliJ IDEA

## General information

 - It have two pieces to support two kind of macroses [Macramé](https://github.com/ClaireNeveu/macrame) and [Safe Config](https://github.com/ClaireNeveu/safe-config)
 - The goal is not to create ideal plugin, but to reduce number of errors in IDEA and make life easier for those who use them.
 - Safe Config part works correctly only with IDEA 2018.2+. Macramé may work fine with few earlier releases too.
 - There some known cases where this plugin doesn't work as expected (IDEA may still show errors or navigation broken). Due to limited support for whitebox macroses in IDEA it's not obvious is it possible to fix it or not. Your contibutions are welcome.
 - This plugin also is a good minimal example how to add support for your whitebox macros to IDEA. I am going to write some article about this topic later.
 - I wrote this plugin in spare time and not guarantee any support. But still you may try...

# How To Build

```bash
> sbt updateIdea package
```

# Hot To Use

Build and install `./target/macrame-idea-scala-plugin-1.0.jar` into IDEA as plugin (or download release from github)
Be happy.
