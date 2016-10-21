# Ozu!

Spanish town name generator using Markov chains

## Usage

This assumes a working [link](http://leiningen.org/ "Leiningen") setup.

After cloning the project, run this leiningen task at the project root in order 
to generate the necessary data files:

```
lein run -m ozu.core/generate
```

then, to start the server, run 

```
lein ring server
```

Your web browser should open automatically and display the main page. Have fun!

## Standalone JAR

`lein uberjar` will generate a standalone JAR that you can run with `java -jar`



Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
