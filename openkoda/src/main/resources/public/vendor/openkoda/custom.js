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
        var $menuItem = $("[data-sidebar-menu-item*='" + menuItemName +"']");
        $menuItem.addClass("active");
        $menuSection = $menuItem.parents("li.nav-item");
        $menuSection.find("div.collapse").addClass("show");
        $menuSection.find("a.nav-link").removeClass("collapsed").attr("aria-expanded", "true");
    }

    if ($("[data-menu-item]").length > 0) {
        var menuItemName = $("[data-menu-item]").attr("data-menu-item");
        if (!!menuItemName) {
            selectMenuItem(menuItemName);
        }
    } else if ($("[data-sidebar-menu-item]").length > 0) {
        var menuItemName = window.location.pathname;
        selectMenuItem(menuItemName);
    }
    $(".pagination-wrapper form").submit(function () {
        var gotoInput = $(this).find("input#goto");
        var max = Number(gotoInput.attr("max"));
        var val = Number(gotoInput.val());
        var pageInput = $(this).find("input[name$='_page']");
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