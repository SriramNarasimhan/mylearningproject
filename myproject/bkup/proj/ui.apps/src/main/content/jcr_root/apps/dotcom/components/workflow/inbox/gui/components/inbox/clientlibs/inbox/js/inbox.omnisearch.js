/*
 ADOBE CONFIDENTIAL

 Copyright 2016 Adobe Systems Incorporated
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Adobe Systems Incorporated and its suppliers,
 if any.  The intellectual and technical concepts contained
 herein are proprietary to Adobe Systems Incorporated and its
 suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Adobe Systems Incorporated.
 */
(function(document, Granite, $) {

    var url = location.href.replace(/(.*\.html).*/, '$1');

    var suffix = location.href.replace(/.*\.html(.*)/, '$1');

    var pathPredicate = suffix;

    $(function() {

        if (!pathPredicate.match(/\/content\/projects\/.*/)) {
            // set default search path to root
            pathPredicate = '/';
            // remove invalid suffix in history entry
            history.replaceState(history.state, document.title, url);
        }

        // set the hidden path predicate
        // required to toggle opening of the filter rail if suffix is missing
        $('#granite-shell-content .foundation-collection').data('foundationCollectionId', pathPredicate);

    });

    // the rail is available, set the path
    $(document).on('foundation-contentloaded', '#granite-omnisearch-result-rail', function() {
        $('input[name="path"]', $(this)).val(pathPredicate);

        // add path predicate tag if necessary
        if (pathPredicate !== '/') {
            var form = document.querySelector('.granite-omnisearch-form');
            var tagList = document.querySelector('.granite-omnisearch-typeahead-tags');

            var pathInputSuggestion = form.querySelector('input[type=hidden][name="path.suggestion"]');
            if (pathInputSuggestion === null) {
                pathInputSuggestion = document.createElement('input');
                pathInputSuggestion.setAttribute('type', 'hidden');
                pathInputSuggestion.setAttribute('name', 'path.suggestion');
                form.appendChild(pathInputSuggestion);
            }

            var pathTag = tagList.querySelector('coral-tag[name="path"]');
            if (pathTag === null) {
                pathTag = new Coral.Tag();
                pathTag.set({
                    label: {
                        innerHTML: '<span class="u-coral-text-capitalize u-coral-text-italic u-coral-text-secondary">' + 
                                    Granite.I18n.get('Path') + 
                                    ':</span> ' + pathPredicate
                    }
                });
                // used to search for it afterwards
                pathTag.setAttribute('name', 'path');

                // makes sure the component is ready before using the collection api
                Coral.commons.ready(tagList, function() {
                    tagList.items.add(pathTag);
                });
            }
        }
    });

    // Path: clear
    $(document).on('granite-omnisearch-predicate-clear', function(event) {
        if (!event.detail.item) {
            return false;
        }

        if (event.detail.item.getAttribute('name') !== 'path') {
            return false;
        }

        var form = event.target;
        var tagList = event.detail.tagList;

        var pathTag = event.detail.item;
        if (pathTag) {
            $(pathTag).remove();
        }        var pathInputSuggestion = form.querySelector('input[type=hidden][name="path.suggestion"]');
        $(pathInputSuggestion).remove();

        var pathInput = form.querySelector('input[type=hidden][name="path"]');
        $(pathInput).val('/');
    });

})(document, Granite, Granite.$);