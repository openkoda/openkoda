// place for javascript customization code that needs to be executed as late as possible

(function iframeResize() { // after iframe is loaded resize it to cover whole parent container
    let iframesWrappers = document.querySelectorAll("iframe.embedded-iframe");
    for (let i = 0; i < iframesWrappers.length; i++) {
        iframesWrappers[i].addEventListener('load', function (e) {
            let iframe = e.target.contentWindow.document.querySelector("iframe");
            iframe.style.height = "100%";
            iframe.style.width = "100%";
        });
    }
})();