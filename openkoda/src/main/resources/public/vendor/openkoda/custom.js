function sendEventToGA (eventCategory, eventAction, eventLabel) {
	if(typeof ga === 'function') {
		let tracker = ga.getAll()[0];
		if (tracker)  {
			tracker.send('event', eventCategory, eventAction, eventLabel);
		}
	}
}
$(document).ready(function(){
    function selectMenuItem(menuItemName) {
        let menuItem = $("[data-sidebar-menu-item*='" + menuItemName +"']");
        menuItem.addClass("active");
        let menuSection = menuItem.parents("li.nav-item");
        menuSection.find("div.collapse").addClass("show");
        menuSection.find("a.nav-link").removeClass("collapsed").attr("aria-expanded", "true");
    }

    if ($("[data-menu-item]").length > 0) {
        let menuItemName = $("[data-menu-item]").attr("data-menu-item");
        if (!!menuItemName) {
            selectMenuItem(menuItemName);
        }
    } else if ($("[data-sidebar-menu-item]").length > 0) {
        let menuItemName = window.location.pathname;
        selectMenuItem(menuItemName);
    }
    $(".pagination-wrapper form").submit(function () {
        let form = $(this);
        let gotoInput = form.find("input#goto");
        let entityKey = form.attr("entityKey");
        let max = Number(gotoInput.attr("max"));
        let val = Number(gotoInput.val());
        let pageInput = form.find("input[name$='" + entityKey + "_page']");
        if (!!gotoInput) {
            if (val > max) {
                pageInput.val(max - 1)
            } else if (val > 0) {
                pageInput.val(val - 1);
            } else {
                pageInput.val(0)
            }
        }
    })

})