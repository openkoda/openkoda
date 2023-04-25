var SESSION_ADMIN_MODE_ON;
var IS_CHECKBOX_CHECKED_ATTR;
if (!app) {
    var app = {};
    app._HTML = "/html";
    app.stompClient = null;
    app._ORGANIZATION = "/organization";
    SESSION_ADMIN_MODE_ON = "adminModeOn";
    IS_CHECKBOX_CHECKED_ATTR = "ischecked"
}

app.entityBase = function(organizationId, entityKey, id) {
    return app.entityBase(organizationId, entityKey) + "/" + id;
}

app.entityBase = function(organizationId, entityKey) {
    return organizationId == null ? app._HTML + "/" + entityKey : app._HTML + app._ORGANIZATION + organizationId + "/" + entityKey;
}

app.entityBase = function(entityKey) {
    return entityBase(null, entityKey);
}

app.entityBase = function(entityKey, id) {
    return app.entityBase(null, entityKey) + "/" + id;
}

app.organizationBase = function(id) {
    return app._HTML + app._ORGANIZATION + "/" + id;
}


app.operation = function(organizationId, entityKey, entityId, operation) {
    let base = app.entityBase(organizationId, entityKey);
    return entityId == null ? base + _NEW + operation : base + "/" + entityId + operation;
}

app.removeThisTableRow = function() {
    app.refreshView();
};

app.submitAndCallback = function( domForm, callback ) {
    let form = $(domForm);
    app.submitToUrlAndCallback(domForm, form.attr('action'), callback);
};

app.submitAsync = function( domForm ) {
    let form = $(domForm);
    app.submitToUrlAsync(domForm, form.attr('action'));
};

app.confirmThenSubmitAsyncAndCallback = function( domForm, confirm, callback, failureCallback ) {
    if(confirm){
        app.submitAsync(domForm);
        callback();
    } else {
        failureCallback();
    }
};

app.submitToUrlAndCallback = function( domForm, targetUrl, callback ) {
    let form = $(domForm);
    let isPostMethod = 'post' === form.get(0).method;
    let params = {
        url : targetUrl,
        data : form.serialize(),
        success : (data) => {
            if (app.assertNotRedirectToLogin(data)) {
                if(typeof callback == "function") {
                    callback( data, form );
                }
                eval(callback)( data, form );
            }
        }
    };
    if (isPostMethod) {
        $.post(params);
    } else {
        $.get(params);
    }
};

app.submitToUrlAsync = function( domForm, targetUrl ) {
    let form = $(domForm);
    let postParam = {
        url : targetUrl,
        data : form.serialize()
    };
    $.post(postParam);
};

app.submitJsonToUrlAndCallback = function( domForm, targetUrl, callback ) {
    let form = $(domForm);
    let postParam = {
        url : targetUrl,
        data : form.serialize(),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success : (data) => {
            if (app.assertNotRedirectToLogin(data)) {
                if(typeof callback == "function") {
                    callback( data, form );
                }
                eval(callback)( data, form );
            }
        }
    };
    $.post(postParam);
};

app.submitToUrlToNewTab = function( domForm, targetUrl ) {
    let currentAction = domForm.action;
    let currentTarget = domForm.target;
    domForm.action = targetUrl;
    domForm.target = '_blank';
    domForm.submit();
    domForm.action = currentAction;
    domForm.target = currentTarget;
};

app.submitToUrlAndCallbackOrElseFailure = function( domForm, callback, failureCallback ) {
    let form = $(domForm);
    $.ajax(form.attr('action'), { data: form.serialize(),
        type: "POST",
        error: function(error) {
            failureCallback(error, form);
        },
        success: function(data) {
            if (app.assertNotRedirectToLogin(data) && app.assertNotRedirectToLoginOrError(data)) {
                if(typeof callback == "function") {
                    callback( data, form );
                }
                eval(callback)( data, form );
            } else {
                failureCallback(data, form);
            }
        }
    });
};

app.submitAndReplace = function( domForm ) {
    let form = $(domForm);
    app.submitToUrlAndReplace(domForm, form.attr('action'));
};

app.submitParentFormAndReplace = function( elemUnderForm ) {
    let elem = $(elemUnderForm);
    let form = elem.closest("form");
    app.submitToUrlAndReplace(form.get(0), form.attr('action'));
};

app.replaceByElemId = function(replacedElemId) {
    let replacedElem = $('#' + replacedElemId);
    return data => replacedElem.replaceWith( data );
}

app.submitCheckboxAndReplaceByElemId = function( element, formId, replacedElemId ) {
    let replacedElem = $('#' + replacedElemId)
    let callback = data => replacedElem.replaceWith( data );
    app.submitCheckboxAndCallback(element, formId, callback);
};

app.submitCheckboxAndCallback = function( element, formId, callback) {
    element.value = element.checked;
    let form = $(document.getElementById(formId));
    let data = form.serialize();
    //cause html form does not submit unckecked checkboxes
    //so in this case input must be added to post parameters
    if(element.value == 'false') data += '&' + element.name + '=false';
    let postParam = {
        url : form.attr('action'),
        data : data,
        success : (data) => {
            if(typeof callback == "function") {
                callback( data, form );
            }
            eval(callback)( data, form );
            }
    };
    $.post(postParam)
};


app.submitToUrlAndReplace = function( domForm , targetUrl) {
    let form = $(domForm);
    let formParent = form.closest('.form-parent');
    let postParam = {
        url : targetUrl,
        data : form.serialize(),
        success : (data) => {
            if (app.assertNotRedirectToLogin(data)) {
                formParent.replaceWith( data );
            }
        }
    };
    $.post(postParam);
};

app.confirmAndSubmitAndCallback = function(confirmText, domForm, callback ) {
  var confirmation = confirm(confirmText);
  if(confirmation){
      let form = $(domForm);
      let postParam = {
          url : form.attr('action'),
          data : form.serialize(),
        success : (data) => {
            if (app.assertNotRedirectToLogin(data)) {
                if(typeof callback == "function") {
                    callback( data, form );
                }
                eval(callback)( data, form );
            }
        }
      };
      $.post(postParam);
  }
};

app.confirmSubmitToUrlAndCallback = function(confirmText, domForm, targetUrl, callback) {
    var confirmation = confirm(confirmText);
    if(confirmation) {
        app.submitToUrlAndCallback(domForm, targetUrl, callback);
    }
}

app.confirmAndSubmit = function( confirmText, domForm ) {
  app.confirmAndSubmitAndCallback(confirmText, domForm, app.refreshView);
};

app.dictionaryToOptions = function (dictionaryName, fieldName, selectedValue, showDefault, defaultText) {
    let a = commonDictionaries[dictionaryName];
    let result = showDefault ? "<option value=''>" + defaultText + "</option>" : "";
    try {
        var arr = JSON.parse(a)
        arr.forEach(e => {
            let selected = (selectedValue === e['k']) ? " selected='selected'" : "";
            let disabled = (e['disabled'] === false) ? " disabled " : "";
            result = result + "<option value='" + e['k'] + "'" + selected + disabled + ">" + e['v'] + "</option>";
        })
    } catch (exception) {
        for (e in a) {
            let selected = (selectedValue === e) ? " selected='selected'" : "";
            result = result + "<option value='" + e + "'" + selected + ">" + a[e] + "</option>";
        }
    }
    return result;
};

app.dictionaryToRadiobuttonTable = function (dictionaryName, fieldName, selectedValue) {
    let a = commonDictionaries[dictionaryName];
    let result = "";
    let fieldNameClass = "radio-option " + fieldName.replace(".", "-")
    try {
        var arr = JSON.parse(a)
        arr.forEach(e => {
            let row = e['v'];
            let isArray = Array.isArray(row);
            let labels = "";
            if (isArray) {
                for (td in row) {
                    labels = labels + "<td class='" + fieldNameClass + "'>" + row[td] + "</td>"
                }
            } else {
                labels = "<td class='" + fieldNameClass + "'>" + row + "</td>";
            }
            let selected = (selectedValue === e) ? " checked='checked'" : "";
            result += "<tr><td><input class='" + fieldNameClass + "' type='radio'  name='" + fieldName + "' value='" + e + "'" + selected + "/></td>" + labels + "</tr>";
        })
    } catch (exception) {
        for (e in a) {
            let row = a[e];
            let isArray = Array.isArray(row);
            let labels = "";
            if (isArray) {
                for (td in row) {
                    labels = labels + "<td class='" + fieldNameClass + "'>" + row[td] + "</td>"
                }
            } else {
                labels = "<td class='" + fieldNameClass + "'>" + row + "</td>";
            }
            let selected = (selectedValue === e) ? " checked='checked'" : "";
            result += "<tr><td><input class='" + fieldNameClass + "' type='radio'  name='" + fieldName + "' value='" + e + "'" + selected + "/></td>" + labels + "</tr>";
        }
    }
    return result;
};

app.createFileGalleryCard = function(fieldName, file, selected, multipleSelection, entityRelated) {
    let isImage = file.contentType.indexOf("image/") == 0;
    let isVideo = file.contentType.indexOf("video/") == 0;
    let selectedPart = selected ? " checked='checked'" : "";
    let fieldType = multipleSelection ? "checkbox" : "radio";

    let cardId = "file-card-" + file.id;
    let confirmationText = "Are you sure?";
    let callback = "data => app.removeElemOnSuccess(&quot;" + cardId + "&quot;, data, function() {alert(&quot;File could not be deleted.&quot;)})";

    return "<div id='" + cardId + "' class='card' style='height:fit-content; width:12%; margin: 0 0.5% 4px; padding:0; overflow:hidden; text-align:center'>"
           + (entityRelated? "" : "<div style='padding:5px;' class=''><span style='display:inline-block;vertical-align: baseline; width:1.25em;'><input type='" + fieldType + "' name='" + fieldName + "' value='" + file.id + "'" + selectedPart + "/></span>Select<br/>")
           + (entityRelated? "<input type='checkbox' name='" + fieldName + "' value='" + file.id + "'" + " checked='checked' " +"style='visibility:hidden;'/>" : "")
           + "<p class='card-text' style='font-size:0.75em'>" + file.filename + "</p>"
           + (isImage ? "<img class='' style='object-fit: cover; width:100%; height:100px;margin: 0 auto;' src='" + file.downloadUrl + "'/>" :
                (isVideo ? "<video controls width='100%' height='100'><source src='" + file.downloadUrl + "'/></video>" : ""))
           + (entityRelated? "" : "</div>")
           + "<div>"
           + "<button class='btn btn-i btn-sm m-1' onclick='app.swapWithPrev(&quot;file-card-" + file.id + "&quot;)' type='button'>"
           + "<i class='fas fa-chevron-left'></i></button>"
           + "<button class='btn btn-i btn-sm m-1' onclick='app.swapWithNext(&quot;file-card-" + file.id + "&quot;)' type='button'>"
           + "<i class='fas fa-chevron-right'></i></button>"
           + "</div>"
           + "<div>"
           + "<a class='btn btn-i btn-sm m-1' href='" + file.downloadUrl + "' download='" + file.filename + "'><i class='fas fa-download'></i></a>"
           + (entityRelated? "<button type='button' class='btn btn-i btn-sm mr-1' onclick='app.confirmSubmitToUrlAndCallback(&quot;" + confirmationText + "&quot;, this.form, &quot;" + file.deleteUrl + "&quot;," + callback + ")'><i class='fas fa-trash-alt'></i></button>" : "")
           + "</div>"
           + "</div>"
           ;

}

app.swapWithNext = function(elemId) {

    let elem = $('#' + elemId);
    nextElem = $(elem).next().find('.card').prevObject;
    if(!nextElem.is(':empty'))
        elem.insertAfter(nextElem);
}

app.swapWithPrev = function(elemId) {

    let elem = $('#' + elemId);
    prevElem = $(elem).prev();
    if(!prevElem.is(':empty'))
        elem.insertBefore(prevElem);
}

app.dictionaryToFileGallery = function(dictionaryName, fieldName, selectedValuesArray, allowedContentTypesCommaSeparated, multipleSelection, entityRelated) {

   let a = commonDictionaries[dictionaryName];
   let result = "";
   selectedValuesArrayWithStringIds = selectedValuesArray == null ? [] : selectedValuesArray.map(a => a + "");
   let allowedContentTypeArray = allowedContentTypesCommaSeparated.split(',');

   for (e in a) {
       let file = a[e];
       for (ct in allowedContentTypeArray) {
           let allowedContentType = allowedContentTypeArray[ct];
           let allowedGroupAndExtension = allowedContentType.split('/');
           let allowedGroup = allowedGroupAndExtension[0];
           let allowedExtension = allowedGroupAndExtension[1];
            if (allowedGroup != '*') {
                if (allowedExtension != '*') {
                    if (allowedContentType != file.contentType) {continue;}
                } else {
                    let fileGroup = file.contentType.split('/')[0];
                    if (allowedGroup != fileGroup) {continue;}
                }
            }
            let selected = "";
            selected = (selectedValuesArrayWithStringIds.indexOf(e) >= 0);
            result += app.createFileGalleryCard(fieldName, file, selected, multipleSelection, entityRelated);
       }
   }
   return result;
};

app.dictionaryToCheckboxTable = function (dictionaryName, fieldName, selectedValuesArray) {

     let a = commonDictionaries[dictionaryName];
     let result = "";
     selectedValuesArrayWithStringIds = selectedValuesArray == null ? [] : selectedValuesArray.map(a => a + "");
     try {
         var arr = JSON.parse(a)
         arr.forEach(e => {
             let row = e['v'];
             let isArray = Array.isArray(row);
             let labels = "";
             if (isArray) {
                 for (td in row) {
                     labels = labels + "<td>" + row[td] + "</td>"
                 }
             } else {
                 labels = "<td>" + row + "</td>";
             }
             let selected = "";
             selected = (selectedValuesArrayWithStringIds.indexOf(e['k']) >= 0) ? " checked='checked'" : "";
             result += "<tr><td class='td-checkbox'><input type='checkbox' name='" + fieldName + "' value='" + e['k'] + "'" + selected + "/></td>" +
                 labels + "</tr>";

         })
     }
     catch {
         for (e in a) {
             let row = a[e];
             let isArray = Array.isArray(row);
             let labels = "";
             if (isArray) {
                 for (td in row) {
                     labels = labels + "<td>" + row[td] + "</td>"
                 }
             } else {
                 labels = "<td>" + row + "</td>";
             }
             let selected = "";
             selected = (selectedValuesArrayWithStringIds.indexOf(e) >= 0) ? " checked='checked'" : "";
             result += "<tr><td class='td-checkbox'><input type='checkbox' name='" + fieldName + "' value='" + e + "'" + selected + "/></td>" +
                 labels + "</tr>";
         }
     }
     return result;
 };

app.dictionaryTableHeader = function(dictionaryName){
    let row = commonDictionariesHeaders[dictionaryName];
    if(row === undefined){
        return "";
    }
    let result = "";
    let isArray = Array.isArray(row);
    if (isArray) {
        for (td in row) {
            result = result + "<th>" + row[td] + "</th>"
        }
    } else {
        result = "<th>" + row + "</th>";
    }

    return result;
};

app.populateSelect = function( selectId, fieldName, fieldValue, datalistId, showDefault, defaultText, disableOptions) {
    let elem = document.getElementById(selectId);
    elem.innerHTML = app.dictionaryToOptions(datalistId, fieldName, fieldValue, showDefault, defaultText);
};

app.populateRadiobuttonTable = function( selectId, fieldName, fieldValue, datalistId ) {
    let elem = document.getElementById(selectId);
    let header = app.dictionaryTableHeader(datalistId);
    elem.innerHTML = header + app.dictionaryToRadiobuttonTable(datalistId, fieldName, fieldValue);
};

app.populateCheckboxTable = function( selectId, fieldName, fieldValuesArray, datalistId) {
    let elem = document.getElementById(selectId);
    elem.innerHTML = app.dictionaryToCheckboxTable(datalistId, fieldName, fieldValuesArray);
};

app.populateFileGallery = function(selectId, fieldName, fieldValuesArray, datalistId, allowedContentType, multipleSelection, entityRelated) {
    let elem = document.getElementById(selectId);
    elem.innerHTML = app.dictionaryToFileGallery(datalistId, fieldName, fieldValuesArray, allowedContentType, multipleSelection, entityRelated);
};

app.selectAndReplace = function( selector, url ) {
    let elem = $(selector);
    $.get(url, function ( data ) {
        elem.replaceWith( data );
        app.refreshView();
    });
};

app.getAndAppend = function( selector, url, reqData ) {
    let elem = $(selector);
    $.get(url, reqData,  function ( data ) {
        elem.append( data );
    });
};

app.refreshView = function () {
    location.reload();
};

app.assertNotRedirectToLogin = function (data) {
    if (((typeof data) === "string") && data.indexOf('action="/login"') > 0) {
        window.location.reload(false);
        return false;
    }
    return true;
};

app.assertNotRedirectToLoginOrError = function (data) {
    if (((typeof data) === "string") && data.indexOf('<title>Error') > 0) {
        return false;
    }
    return true;
};

app.showMessage = function (message) {
    alert(message);
};

app.confirmPrompt = function (message, textToConfirm) {
    let text = prompt(message);
    return text === textToConfirm;
};


app.showMessageAndReload = function (message) {
    alert(message);
    location.reload();
};

app.adminMode = function(isOn) {
    sessionStorage.setItem(SESSION_ADMIN_MODE_ON, isOn);
};

app.isAdminModeOn = function () {
    return sessionStorage.getItem(SESSION_ADMIN_MODE_ON) === 'true';
};

app.showCheckboxAlert = function (checkboxId, alertMsg) {
    let elem = document.getElementById(checkboxId);
    let wasChecked = elem.getAttribute(IS_CHECKBOX_CHECKED_ATTR) === 'true';
    elem.setAttribute(IS_CHECKBOX_CHECKED_ATTR, wasChecked ? 'false' : 'true');
    let isChecked = elem.getAttribute(IS_CHECKBOX_CHECKED_ATTR) === 'true';
    if(isChecked) {
        app.showMessage(alertMsg);
    }
};

app.wrapCheckboxSectionAndShowIfAllGroupVisible = function (allSelector, elementSelector, collapseId, isVisible) {
    $(document).ready(function(){
        let groupVisible = $("div[class*='" + allSelector + "']").css("display") === "block";
        $("div[class*=" + elementSelector).wrapAll( "<div class='collapse" + (isVisible ? " show" : "") + "' id='" + collapseId + "' />");
        $("div[class*=" + elementSelector).css("display", (groupVisible ? "block" : "none"));
    });
}

app.wrapCheckboxSection = function (elementSelector, collapseId, isVisible) {
    if(isVisible) {
        $("div[class*=" + elementSelector).css("display", "block");
    }
    $(document).ready(function(){
        $("div[class*=" + elementSelector).wrapAll( "<div class='collapse" + (isVisible ? " show" : "") + "' id='" + collapseId + "' />");
        $("div[class*=" + elementSelector).css("display", "block");
    });
};

app.removeElemOnSuccess = function (elemId, isSuccess, failureCallback) {
    isSuccess ? $("#" + elemId).remove() : failureCallback();
}

app.removeElem = function (elemId, after=true) {
    $("#" + elemId).remove();
    return after;
}

app.redirectView = function (redirectUrl) {
    window.location.replace(redirectUrl);
};
app.initializeMap = function (mapid,fieldName) {
    console.log("MapID:" + mapid);
    console.log("FieldName: " + fieldName);
    var stringCord = document.getElementById(fieldName);
    var val = stringCord.value;
    var arrCord = val.match(/\w*\s*\(\s*(-?\d+[.\d]*)\s*(-?\d+[.\d]*)/);
    let cord;
    if (arrCord === null) {
        cord = [0, 0];
    } else
    {
        cord = [+arrCord[1],+arrCord[2]];

    }
    console.log(cord);
    var mymap = L.map(mapid).setView(cord, 13);
    L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
        maxZoom: 18,
        id: 'mapbox/streets-v11',
        tileSize: 512,
        draggable: true,
        zoomOffset: -1,
        accessToken: 'pk.eyJ1IjoiYmJpbmRhIiwiYSI6ImNrOHI3Z2phZTA0bDMzZGxvZW81MWcyazkifQ.7nkcVGbW0C3illdrTfFbGw'
    }).addTo(mymap);
    var marker = L.marker(cord,{
        draggable: 'true',
        autoPan: 'true'
    }).addTo(mymap);
    stringCord.value  = "POINT ("+ marker.getLatLng().lat + " "+ marker.getLatLng().lng + ")";
    mymap.on('click',
        function (e) {
            var pos = e.latlng;
            console.log('map click event');
            console.log('map delete event');
            mymap.removeLayer(marker);
            marker = L.marker(
                pos,
                {
                    draggable: true,
                    autoPan: 'true'
                }
            );
            console.log("event lang: " + e.latlng);
            console.log("marker lang: " + marker.getLatLng());
            stringCord.value = "POINT ("+ marker.getLatLng().lat + " "+ marker.getLatLng().lng + ")";
            marker.on('drag', function (e) {
                console.log('marker drag event');
                stringCord.value = "POINT ("+ marker.getLatLng().lat + " "+ marker.getLatLng().lng + ")";
                console.log("event lang: " + e.latlng);
                console.log("marker lang: " + marker.getLatLng());
            });
            marker.on('click', L.DomEvent.stopPropagation);
            marker.addTo(mymap);

        }
    );
    marker.on('drag', function (e) {
        console.log('marker drag event');
        stringCord.value  = "POINT ("+ marker.getLatLng().lat + " "+ marker.getLatLng().lng + ")";
        console.log("event lang: " + e.latlng);
        console.log("marker lang: " + marker.getLatLng());

        marker.on('click', L.DomEvent.stopPropagation);
        marker.addTo(mymap);
    });
};

app.getNextRuleLine = function(orgId, fieldName, lineType, key, datalistId, disabled, advanced) {
    let nextLineIndex = Number($('.rule-' + lineType + ':last').attr('data-id')) + 1;
    //indexForKey, indexToDisplay, indexForImgUrl are only for simple rules for now and those cannot have second line
    app.getAndAppend('div[id="' + fieldName + '"] .rules-' + lineType, '/html/organization/'+ orgId +'/organization/'+ orgId +'/rule-line/' + lineType,
        'index=' + nextLineIndex + '&datalistId=' + datalistId + '&fieldName=' + encodeURI(fieldName) + '&key=' + key + '&disabled=' + disabled
        + '&advanced=' + advanced + '&indexForKey=' + -1 + '&indexToDisplay=' + -1 + '&indexForImgUrl=' + -1);
}

app.setupInputFilter = function(id, inputFilter) {
    let elem = document.getElementById(id);
    ["input", "keydown", "keyup", "mousedown", "mouseup", "select", "contextmenu", "drop"].forEach(function(event) {
        elem.addEventListener(event, function() {
            if (inputFilter(this.value)) {
                this.oldValue = this.value;
                this.oldSelectionStart = this.selectionStart;
                this.oldSelectionEnd = this.selectionEnd;
            } else if (this.hasOwnProperty("oldValue")) {
                this.value = this.oldValue;
                this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
            } else {
                this.value = "";
            }
        });
    });
}

app.allowDigitsLettersSpaceDash = function (value) {
    return /^([a-zA-Z0-9,. _-]+)$/.test(value);
}

app.addHiddenInput = function (formId, id, fieldName) {
    let val = document.getElementById(id).value
    app.removeElem(id);
    let result = "<input type='hidden' name='dto." + fieldName + "' value='" + val + "' id='" + fieldName + "-" + val + "'>";
    $("form[id*=" + formId + "]").append(result);
}

app.compose = function(f2, f1) {
    return a => f2(f1(a));
}

app.compose3 = function(f3, f2, f1) {
    return a => f3(f2(f1(a)));
}

app.compose4 = function(f4, f3, f2, f1) {
    return a => f4(f3(f2(f1(a))));
}


app.populateDatalistOptions = function (datalistId, data, indexForKey, indexToDisplay, indexForImgUrl, indexForUrl) {
    let options = '';
    for (const [key, value] of Object.entries(data)) {
        options += '<option value="' + (indexForKey > -1 ? value[indexForKey] : key) + '" '
            + (indexForImgUrl > -1 ? 'data-img-url="' + value[indexForImgUrl] + '"' : '')
            + (indexForUrl > -1 ? 'data-url="' + value[indexForUrl] + '"' : '') + '>'
            + (indexToDisplay > -1 ? value[indexToDisplay] : value) + '</option>';
    }
    $('#' + $.escapeSelector(datalistId)).html(options);
}

app.appendSelectedFromSearch = function (orgId, selectedDivId, inputId, fieldName) {
    let selectedId = $('#' + $.escapeSelector(inputId)).val();
    let selectedOption = $('option[value="' + $.escapeSelector(selectedId) + '"]');
    let imgUrl = selectedOption.attr('data-img-url');
    let label = selectedOption.text();
    let url = selectedOption.attr('data-url');
    app.getAndAppend('div[id="' + $.escapeSelector(selectedDivId) + '"]', '/html/organization/'+ orgId +'/organization/'+ orgId +'/rule/search/selected',
        'selectedId=' + selectedId + '&fieldName=' + encodeURI(fieldName) + '&label=' + encodeURI(label) + '&imgUrl=' + encodeURI(imgUrl) + '&url=' + encodeURI(url));
    app.updateSelectedFromSearch(selectedDivId, fieldName, selectedId);
}

app.updateSelectedFromSearch = function (selectedDivId, fieldName, selectedId) {
    let selectedArray = [];
    $('div[id="' + $.escapeSelector(selectedDivId) + '"] .selected-box').each(function (index) {
        selectedArray.push($(this).attr('data-value'));
    });
    if(selectedId !== null) {
        selectedArray.push(selectedId);
    }
    $('#' + $.escapeSelector(fieldName)).val(selectedArray.join(','));
}

app.switchCssClass = function (parentId, selector, oldClass, newClass) {
    let element = $('#' + $.escapeSelector(parentId) + (selector !== '' ? ' ' + selector : ''));
    element.removeClass(oldClass);
    element.addClass(newClass);
}

app.manageDropdownSwitch = function (sectionClass, selectedValue) {
    let allElements = $("div[class*=" + sectionClass + "]");
    allElements.css("display", "none");
    if(selectedValue == '') return;
    let selectedElements = $("div[class*=" + selectedValue + "]");
    selectedElements.css("display", "block");
}

app.copyUrlToClipboard = function (url, copiedMessage) {
    var textToCopy = url;
    var tempInput = document.createElement("textarea");
    tempInput.value = textToCopy;
    document.body.appendChild(tempInput);

    tempInput.select();
    document.execCommand("copy");
    document.body.removeChild(tempInput);
    app.showMessage(copiedMessage);
}

app.switchVisibility = function (selectedClass) {
    let selectedElements = $("div[class*=" + selectedClass + "]");
    if(selectedElements.is(":visible")) {
        selectedElements.css("display", "none");
    } else {
        selectedElements.css("display", "block");
    }
}

/*
requires
        <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
*/
app.subscribeToChannel = function(channelName, handler) {
    if (app.stompClient == null) {
        app.stompClient = Stomp.over(new SockJS('/html/websocket'));
        app.stompClient.connect({}, function (frame) {
            app.stompClient.subscribe(channelName , handler);
        });
    } else {
        app.stompClient.subscribe(channelName , handler);
    }
}
