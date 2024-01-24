# clj-pokeapi-sdk

A partial implementation of a client SDK in Clojure to consume `pokeapi.co`. 

## Getting started

The library is written in Clojure and uses Leiningen as dependency management.
Assuming `brew` is installed in the target machine run `make install-clojure` to install Clojure and Leiningen.

To run tests `make tests` it will run some unit tests and some integration tests with stub responses.

To play around with the SDK either explore the `playground` package and its `examples` or alternatively using the REPL:

    lein repl
    (require '[playground.examples :as examples])
    (examples/fetch-pokemon-by-name-and-resolve-stats-by-name)

    (require '[clj-pokeapi-sdk.api.pokemon :as pokemon-api])
    (-> (pokemon-api/response-schema examples/client) (clojure.pprint/pprint))
    (-> (pokemon-api/by-id 2 examples/client) :stats) 


## Thinking process

I decided to use Clojure to build this SDK because I am familiar with the language, there was no other PokeAPI Clojure implementation available and most importantly it provides a nice and concise SDK.

My first approach was to look for an OpenAPI specification for these APIs and make use of **https://github.com/OpenAPITools/openapi-generator** to create the boilerplate code.
Unfortunately it doesn't look like there is a clear OpenAPI spec available, so I had to consider a couple of options:

1) Write the entire code myself. Doable, but not very interesting or impactful
2) Be creative and see if I could generate an OpenAPI spec as precise as possible from the sample responses and then make use of the code generator.

Because option 1 feels like a "brute force" approach I decided to go for option 2 and time box the exercise.
I Googled around and found a few different libraries to try, but I wasn't very satisfied with it.
I then decided to go for the AI route, asking support from Claude (Anthropic equivalent of ChatGPT).
The schema that has been generated is ok-ish but there are some missing bits. One of the reason is that the sample response used to generate the schema does not necessarily have all the fields populated.
For this reason I had to do some manual checking and post editing but, I do admit it feels quite tedious. This can certainly be improved and automated with LLMs but it's outside the scope of the exercise.

I tried to use the OpenAPI-Generator for Clojure. It turned out to be a rabbit hole. It created a lot of code which didn't do much.
After some research I came across https://github.com/oliyh/martian which turned out to be a beautiful library with batteries included.

As Clojure is a data oriented programming model there is no real value to create a lot of domain objects or even wrapper.
For this reason the code will look almost not-existent.

In the api module there are three namespaces with some very simple logic to fetch a resource either by `id` or by `name`. 
The `core` namespace contains all the logic to fetch a resource. It can also resolve any extra resource that is referenced in the response payload by URL.

The `examples` namespace provides some examples on how to consume APIs.

There is plenty that can be done. In not particular order:
- circuit breaking (started looking into https://github.com/fpischedda/interruttore and add it as an interceptor)
- caching (started looking at https://github.com/clojure/core.cache)
- tracing 


## License

Copyright © 2024 Edoardo Tosca

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
