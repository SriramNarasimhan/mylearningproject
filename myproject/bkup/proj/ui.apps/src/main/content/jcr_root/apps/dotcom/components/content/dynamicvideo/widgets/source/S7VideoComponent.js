CQ.scene7 = CQ.scene7 || {};
CQ.scene7.VideoHelper = {};

/**
 * Initialize video viewer preset list
 * @param {CQ.ext.panel} panel - the panel holds viewer presets
 */
CQ.scene7.VideoHelper.init = function(panel){
    var viewerPresetsPanel = panel.find("name", "viewerPresetsHbox");
    var selectedViewerPreset = "";
    var selectedViewerPresetArray = panel.find("name", "./s7ViewerPreset");

    if (viewerPresetsPanel && viewerPresetsPanel.length > 0) {

        viewerPresetsPanel = viewerPresetsPanel[0];
        viewerPresetsPanel.removeAll();

        var viewerPresetSelectWidget = new CQ.form.Selection({
            type: 'select',
            name: 'viewerPresetCombo',
            fieldLabel: CQ.I18n.getMessage('Viewer Preset'),
            fieldDescription: CQ.I18n.getMessage("Viewer Preset to use for rendering video viewer"),
            defaultValue: selectedViewerPreset,
            listeners: {
                selectionchanged : function(select, value, isChecked ) {
                    if (selectedViewerPresetArray && selectedViewerPresetArray.length > 0) {
                        selectedViewerPresetArray[0].setValue(value);
                    }

                }
            },
            options: [
                { text: 'None', value: ''},
            ]
        });


        CQ.scene7.VideoHelper.populateViewerPresets( '/etc/dam/presets/viewer.children.2.json?include=isactive,true',
            selectedViewerPreset,
            viewerPresetSelectWidget,
            'id',
            'uri');

        viewerPresetsPanel.add(viewerPresetSelectWidget);
    }

    //delay set selected data in dropdown viewer preset to wait for data to be populated inside the hidden field
    setTimeout( function(){
        CQ.scene7.VideoHelper.setSelectedViewerPreset(panel, viewerPresetSelectWidget);
    }, 100);
}

/**
 * Set correct selection in viewer preset dropdown
 */
CQ.scene7.VideoHelper.setSelectedViewerPreset = function(panel, widget) {
    var selectedViewerPreset = "";
    var selectedViewerPresetArray = panel.find("name", "./s7ViewerPreset");
    if (selectedViewerPresetArray && selectedViewerPresetArray.length > 0) {
        selectedViewerPreset = selectedViewerPresetArray[0].getValue();
    }
    widget.setValue(selectedViewerPreset);

}


/**
 * Populate the video viewer presets dropdown
 * @param {String} presetsEndpoint location of the servlet to call for viewer preset list
 * @param {String} currentViewerPresetValue current selected viewer preset
 * @param {CQ.form.Selection} presetSelectWidget dropdown component for viewer presets
 * @param {String} presetNameJSONKey field key in the response to be used as name
 */
CQ.scene7.VideoHelper.populateViewerPresets = function( presetsEndpoint,
                                                        currentViewerPresetValue,
                                                        presetSelectWidget,
                                                        presetNameJSONKey,
                                                        presetPathJSONKey) {
    presetSelectWidget.presetData = [];
    // Load the viewer presets
    CQ.HTTP.get(presetsEndpoint, function(options, success, xhr, response) {
        var presetOptions = [{	text: 'None',
            value: '' }];
        if (success) {
            var jsonResponse = JSON.parse(xhr.responseText);
            if (jsonResponse && jsonResponse.length ) {
                for (var viewerPresetIdx = 0 ; viewerPresetIdx < jsonResponse.length ; viewerPresetIdx++) {
                    var viewerPresetItem = jsonResponse[viewerPresetIdx];
                    if (viewerPresetItem[presetNameJSONKey]) {
                        // For value, we use id|category.
                        // Since we don't allow | as viewer preset name, | can be safely used as a delimiter
                        var viewerPresetCat = viewerPresetItem['jcr:content']['category']
                        viewerPresetCat = viewerPresetCat.toLowerCase();
                        if (viewerPresetCat === 'video') {
                            presetOptions.push({
                                text: viewerPresetItem[presetNameJSONKey],
                                value: viewerPresetItem[presetPathJSONKey],
                                assetType: viewerPresetCat });
                        }
                    }
                }
            }
        }

        presetSelectWidget.setOptions(presetOptions);

        if (currentViewerPresetValue) {
            presetSelectWidget.setValue(currentViewerPresetValue);
        }
    });
};