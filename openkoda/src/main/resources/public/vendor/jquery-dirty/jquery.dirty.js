/*
 * Dirty
 * jquery plugin to detect when a form is modified
 * (c) 2016 Simon Reynolds - https://github.com/simon-reynolds/jquery.dirty
 * originally based on jquery.dirrty by Ruben Torres - https://github.com/rubentd/dirrty
 * Released under the MIT license
 */

(function($) {

    //Save dirty instances
    var singleDs = [];
    var dirty = "dirty";
    var clean = "clean";
    var dataInitialValue = "dirtyInitialValue";
    var dataIsDirty = "isDirty";

    var getSingleton = function(id) {
        var result;
        singleDs.forEach(function(e) {
            if (e.id === id) {
                result = e;
            }
        });
        return result;
    };

    var setSubmitEvents = function(d) {
        d.form.on("submit", function() {
            d.submitting = true;
        });

        if (d.options.preventLeaving) {
            $(window).on("beforeunload", function(event) {
                if (d.isDirty && !d.submitting) {
                    event.preventDefault();
                    return d.options.leavingMessage;
                }
            });
        }
    };

    var setNamespacedEvents = function(d) {

        d.form.find("input, select, textarea").on("change.dirty click.dirty keyup.dirty keydown.dirty blur.dirty", function(e) {
            d.checkValues(e);
        });

        d.form.on("dirty", function() {
            if (typeof d.options.onDirty !== "function") return;

            d.options.onDirty();
        });

        d.form.on("clean", function() {
            if (typeof d.options.onClean !== "function") return;

            d.options.onClean();
        });
    };

    var clearNamespacedEvents = function(d) {
        d.form.find("input, select, textarea").off("change.dirty click.dirty keyup.dirty keydown.dirty blur.dirty");

        d.form.off("dirty");

        d.form.off("clean");
    };

    var Dirty = function(form, options) {
        this.form = form;
        this.isDirty = false;
        this.options = options;
        this.history = [clean, clean]; //Keep track of last statuses
        this.id = $(form).attr("id");
        singleDs.push(this);
    };

    Dirty.prototype = {
        init: function() {
            this.saveInitialValues();
            this.setEvents();
        },

        isRadioOrCheckbox: function(el){
            return $(el).is(":radio, :checkbox");
        },

        isFileInput: function(el){
            return $(el).is(":file")
        },

        saveInitialValues: function() {
            var d = this;
            this.form.find("input, select, textarea").each(function(_, e) {

                var isRadioOrCheckbox = d.isRadioOrCheckbox(e);
                var isFile = d.isFileInput(e);

                if (isRadioOrCheckbox) {
                    var isChecked = $(e).is(":checked") ? "checked" : "unchecked";
                    $(e).data(dataInitialValue, isChecked);
                } else if(isFile){
                    $(e).data(dataInitialValue, JSON.stringify(e.files))
                } else {
                    $(e).data(dataInitialValue, $(e).val() || '');
                }
            });
        },

        refreshEvents: function () {
            var d = this;
            clearNamespacedEvents(d);
            setNamespacedEvents(d);
        },

        showDirtyFields: function() {
            var d = this;

            return d.form.find("input, select, textarea").filter(function(_, e){
                return $(e).data("isDirty");
            });
        },

        setEvents: function() {
            var d = this;

            setSubmitEvents(d);
            setNamespacedEvents(d);
        },

        isFieldDirty: function($field) {
            var initialValue = $field.data(dataInitialValue);
             // Explicitly check for null/undefined here as value may be `false`, so ($field.data(dataInitialValue) || '') would not work
            if (initialValue == null) { initialValue = ''; }
            var currentValue = $field.val();
            if (currentValue == null) { currentValue = ''; }

            // Boolean values can be encoded as "true/false" or "True/False" depending on underlying frameworks so we need a case insensitive comparison
            var boolRegex = /^(true|false)$/i;
            var isBoolValue = boolRegex.test(initialValue) && boolRegex.test(currentValue);
            if (isBoolValue) {
                var regex = new RegExp("^" + initialValue + "$", "i");
                return !regex.test(currentValue);
            }

            return currentValue !== initialValue;
        },

        isFileInputDirty: function($field) {
            var initialValue = $field.data(dataInitialValue);

            var plainField = $field[0];
            var currentValue = JSON.stringify(plainField.files);

            return currentValue !== initialValue;
        },

        isCheckboxDirty: function($field) {
            var initialValue = $field.data(dataInitialValue);
            var currentValue = $field.is(":checked") ? "checked" : "unchecked";

            return initialValue !== currentValue;
        },

        checkValues: function(e) {
            var d = this;
            var formIsDirty = false;

            this.form.find("input, select, textarea").each(function(_, el) {
                var isRadioOrCheckbox = d.isRadioOrCheckbox(el);
                var isFile = d.isFileInput(el);
                var $el = $(el);

                var thisIsDirty;
                if (isRadioOrCheckbox) {
                    thisIsDirty = d.isCheckboxDirty($el);
                } else if (isFile) {
                    thisIsDirty = d.isFileInputDirty($el);
                } else {
                    thisIsDirty = d.isFieldDirty($el);
                }

                $el.data(dataIsDirty, thisIsDirty);

                formIsDirty |= thisIsDirty;
            });

            if (formIsDirty) {
                d.setDirty();
            } else {
                d.setClean();
            }
        },

        setDirty: function() {
            this.isDirty = true;
            this.history[0] = this.history[1];
            this.history[1] = dirty;

            if (this.options.fireEventsOnEachChange || this.wasJustClean()) {
                this.form.trigger("dirty");
            }
        },

        setClean: function() {
            this.isDirty = false;
            this.history[0] = this.history[1];
            this.history[1] = clean;

            if (this.options.fireEventsOnEachChange || this.wasJustDirty()) {
                this.form.trigger("clean");
            }
        },

        //Lets me know if the previous status of the form was dirty
        wasJustDirty: function() {
            return (this.history[0] === dirty);
        },

        //Lets me know if the previous status of the form was clean
        wasJustClean: function() {
            return (this.history[0] === clean);
        },

        setAsClean: function(){
            this.saveInitialValues();
            this.setClean();
        },

        setAsDirty: function(){
            this.saveInitialValues();
            this.setDirty();
        },

        resetForm: function(){
            var d = this;
            this.form.find("input, select, textarea").each(function(_, e) {

                var $e = $(e);
                var isRadioOrCheckbox = d.isRadioOrCheckbox(e);
                var isFile = d.isFileInput(e);

                if (isRadioOrCheckbox) {
                    var initialCheckedState = $e.data(dataInitialValue);
                    var isChecked = initialCheckedState === "checked";

                    $e.prop("checked", isChecked);
                } if(isFile) {
                    e.value = "";
                    $(e).data(dataInitialValue, JSON.stringify(e.files))

                } else {
                    var value = $e.data(dataInitialValue);
                    $e.val(value);
                }
            });

            this.checkValues();
        }
    };

    $.fn.dirty = function(options) {

        if (typeof options === "string" && /^(isDirty|isClean|refreshEvents|resetForm|setAsClean|setAsDirty|showDirtyFields)$/i.test(options)) {
            //Check if we have an instance of dirty for this form
            // TODO: check if this is DOM or jQuery object
            var d = getSingleton($(this).attr("id"));

            if (!d) {
                d = new Dirty($(this), options);
                d.init();
            }
            var optionsLowerCase = options.toLowerCase();

            switch (optionsLowerCase) {
            case "isclean":
                return !d.isDirty;
            case "isdirty":
                return d.isDirty;
            case "refreshevents":
                d.refreshEvents();
            case "resetform":
                d.resetForm();
            case "setasclean":
                return d.setAsClean();
            case "setasdirty":
                return d.setAsDirty();
            case "showdirtyfields":
                return d.showDirtyFields();
            }

        } else if (typeof options === "object" || !options) {

            return this.each(function(_, e) {
                options = $.extend({}, $.fn.dirty.defaults, options);
                var dirty = new Dirty($(e), options);
                dirty.init();
            });

        }
    };

    $.fn.dirty.defaults = {
        preventLeaving: false,
        leavingMessage: "There are unsaved changes on this page which will be discarded if you continue.",
        onDirty: $.noop, //This function is fired when the form gets dirty
        onClean: $.noop, //This funciton is fired when the form gets clean again
        fireEventsOnEachChange: false, // Fire onDirty/onClean on each modification of the form
    };

})(jQuery);