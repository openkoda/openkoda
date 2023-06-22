###How to run locally as a standalone project

Based on this [blog post](https://www.sumologic.com/blog/building-autocomplete-antlr-codemirror/).

Locally proved to work on WSL Ubuntu 22.

1. Install yarn, npm, node etc.
2. Download dependencies: `yarn`.  Then copy `/autocomplete/src/show-hint.js`
   to `/autocomplete/node_modules/codemirror/addon/hint/show-hint.js`
3. Start app: `NODE_OPTIONS=--openssl-legacy-provider yarn start` 
   
   Alternatively, in `packagaje.json` set 
    <pre>"scripts": {
   "start": "react-scripts --openssl-legacy-provider start",
   (...)
   }  </pre>
and run `yarn start`.

In case of grammar modification (src/grammar/Flow.g4), refresh grammar with `yarn grammar`

