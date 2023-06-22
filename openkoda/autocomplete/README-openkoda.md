###How to add to openkoda project

1. If not downloaded already, download dependencies with `yarn`. Then copy `/autocomplete/src/show-hint.js`
to `/autocomplete/node_modules/codemirror/addon/hint/show-hint.js`
2. In `packagaje.json` set
    <pre>"scripts": {
    (...)
   "build": "react-scripts --openssl-legacy-provider build",
   (...)
   }  </pre>
and build with `yarn build`.
3. In `/autocomplete/build/index.html` file navigate to `script` tags:
<pre>
   &lt;script&gt;!function(e){function t(t){for(var n,a,i=t[0] (...) &lt;/script&gt;
   &lt;script src="/static/js/2.xxxxxxxx.chunk.js"&gt;&lt;/script&gt;
   &lt;script src="/static/js/main.xxxxxxxxx.chunk.js"&gt;&lt;/script&gt;
</pre>
4. Copy the first (inline) script to `/src/main/resources/templates/forms.html` , pasting it inside  `<th:block th:fragment="code-editor-with-autocomplete-prerequisites">` block.
   Substitute an existing inline script there, just above the lines 
   <pre>
        &lt;script type="text/javascript" src="/vendor/autocomplete/chunk2.min.js"&gt;&lt;/script&gt;
        &lt;script type="text/javascript" src="/vendor/autocomplete/main.min.js"&gt;&lt;/script&gt;
   </pre>
6. Copy `/autocomplete/build/static/js/2.xxxxxxxx.chunk.js` to `/src/main/resources/public/vendor/autocomplete/chunk2.min.js`
and remove last line <pre>//# sourceMappingURL=2.xxxxxxxx.chunk.js.map</pre>
6.Copy `/autocomplete/build/static/js/main.xxxxxxxxx.chunk.js` to `/src/main/resources/public/vendor/autocomplete/main.min.js`
and remove last line <pre>//# sourceMappingURL=main.xxxxxxxxx.chunk.js.map</pre>