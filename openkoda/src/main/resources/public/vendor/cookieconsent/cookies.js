
window.cookieconsent.enableMarketingCookies = function(){
    console.log("Marketing cookies are enabled");
}

window.cookieconsent.disableMarketingCookies = function(){
    console.log("Marketing cookies are disabled");
}

window.addEventListener("load", function(){
window.cookieconsent.initialise({
  "palette": {
    "popup": {
      "background": "#3c404d",
      "text": "#ffffff"
    },
    "button": {
      "background": "transparent",
      "text": "#8bed4f",
      "border": "#8bed4f"
    }
  },
  "type": "opt-out",
  "content": {
    "message":  "This website uses cookies to ensure you get the best experience on our website.",
    "dismiss": "Accept",
    "deny": "Decline",
    "link": "Privacy Policy",
    "href": "/privacy-policy"

  },
    onInitialise: function (status) {
      var type = this.options.type;
      var isConsent = this.hasConsented();
      if (isConsent) {
        window.cookieconsent.enableMarketingCookies();
      } else {
        window.cookieconsent.disableMarketingCookies();
      }
    },

    onStatusChange: function(status, chosenBefore) {
      var type = this.options.type;
      var hasConsented = this.hasConsented();
      if (hasConsented) {
        window.cookieconsent.enableMarketingCookies();
      } else {
        window.cookieconsent.disableMarketingCookies();
      }
    },

    onRevokeChoice: function() {
      var type = this.options.type;
      if (type == 'opt-out') {
        window.cookieconsent.enableMarketingCookies();
      }
      else if (type == 'opt-in') {
        window.cookieconsent.disableMarketingCookies();
      }
    }
})});