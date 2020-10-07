# lein-less

This is a Leiningen plugin that will compile Less CSS files (see [lesscss.org](http://lesscss.org)) using the `less.js` 
compiler running on the GraalVM. `lein-less` is ideal for compiling Less CSS files during automated builds: compilation 
happens as part of your regular leiningen build without requiring any tools or configuration other than your leiningen 
project file.

Forked from archived [montoux/lein-less](https://github.com/montoux/lein-less)

## Requirements

This plugin is only tested against recent versions of leiningen and clojure. Works with GraalVM CE 20.0.0

## Usage

To install the plugin, add `[brsyuksel/lein-less "1.7.5"]` to your `project.clj` file:

```
  :plugins [[brsyuksel/lein-less "1.7.5"]]
```

The plugin will compile `.less` files found in your leiningen project's resource directories.

To compile `.less` files once:

```
lein less once
```

To continuously compile `.less` files whenever a file changes:

```
lein less auto
```

## Configuration

You can configure different behaviour by adding a `:less` map to your leiningen project:

```
  :less {:source-paths ["src/main/less"]
         :target-path "target/public/css"}
```

If you want less compilation to happen on regular lein targets (e.g. compile),
add `leiningen.less` to your leiningen project file's hooks:

```
  :hooks [leiningen.less]
```

## License

Copyright © 2015 Montoux Ltd.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
