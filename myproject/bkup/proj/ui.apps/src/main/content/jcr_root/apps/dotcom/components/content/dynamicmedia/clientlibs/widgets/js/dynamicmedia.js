CQ.s7dm = CQ.s7dm || {};

CQ.s7dm.Preset = {
    //Events
    events: {
        VIEWERPRESET: 'S7_VP_READY',
        IMAGEPRESET: 'S7_IP_READY'
    },

    IP : {
        data : null,
        path : Granite.HTTP.externalize('/etc/dam/imageserver/macros.children.2.json?props=id,wid,hei'),
        load : function($panel) {
            var othis = this;
            if (this.data == null) {
                CQ.HTTP.get(this.path, function(options, success, xhr, response) {
                    if (success) {
                        var jsonResponse = JSON.parse(xhr.responseText);
                        if (jsonResponse && jsonResponse.length ) {
                            othis.data = othis.clean(jsonResponse);
                            $panel.trigger(CQ.s7dm.Preset.events.IMAGEPRESET);
                        }
                    }
                });
            }
            else {
                $panel.trigger(CQ.s7dm.Preset.events.IMAGEPRESET);
            }
        },
        clean : function(json) {
        	var cleanedIP = [];
            for (var i = 0; i < json.length; i++){
                var hadSize = (json[i]['jcr:content'].hei || json[i]['jcr:content'].wid);
                cleanedIP.push({
                    id : json[i].id,
                    value : json[i].id,
                    hadSize : hadSize
                });
            }
            return cleanedIP;
        },
        filter : function(isImage) {
            if (isImage) {
                return this.data;
            }
            else {
                return [];
            }
        }
    },
    VP : {
        data : null,
        path : Granite.HTTP.externalize('/etc/dam/viewers.viewerpresets.children.2.json?include=isactive,true&props=config,isactive,category,platform,iscustom,jcr:content'),
        load : function($panel){
            var othis = this;
            if (this.data == null) {
                CQ.HTTP.get(this.path, function(options, success, xhr, response) {
                    if (success) {
                        var jsonResponse = JSON.parse(xhr.responseText);
                        if (jsonResponse && jsonResponse.length ) {
                            othis.data = othis.clean(jsonResponse);
                            $panel.trigger(CQ.s7dm.Preset.events.VIEWERPRESET);
                        }
                    }
                });
            }
            else {
                $panel.trigger(CQ.s7dm.Preset.events.VIEWERPRESET);
            }
        },
        clean : function(json) {
            var cleanedVP = [];
            for (var i = 0; i < json.length; i++){
                if (json[i].id !== 'rep:policy') {
                    cleanedVP.push({
                        id : json[i].id,
                        value : json[i].id + '|' + json[i]['jcr:content'].category + '|' + json[i].uri + '|' + json[i].iscustom,
                        category: json[i]['jcr:content'].category,
                        isactive: json[i]['jcr:content'].isactive,
                        uri: json[i].uri,
                        iscustom: json[i].iscustom
                    });
                }
            }
            return cleanedVP;
        },
        filter : function(s7type) {
            var filteredVP = [];
            for (var i=0; i<this.data.length; i++) {
                if (this.isMatched(s7type, this.data[i].category)) {
                    filteredVP.push(this.data[i]);
                }
            }
            return filteredVP;
        },
        isMatched : function(s7type, vpType) {
            var lowerS7Type = s7type.toLowerCase(),
                lowerVPType = vpType.toLowerCase();
            return ((lowerS7Type === lowerVPType)
                   || (lowerS7Type === 'image' && (lowerVPType === 'zoom' || lowerVPType === 'flyout_zoom'))
                   || (lowerS7Type === 'spinset' && lowerVPType === 'spin_set')
                   || (lowerS7Type === 'videoavs' && lowerVPType ==='video')
                   || (lowerS7Type === 'imageset' && lowerVPType === 'image_set')
                   || (lowerS7Type === 'mixedmediaset' && lowerVPType === 'mixed_media'));
        }
    },

    init : function(panel){
        this.VP.load(panel);
        this.IP.load(panel);
    }

};

CQ.s7dm.DMHelper = {

    currentPanel : null,

    init : function(panel) {
		var $panel = $('#' + panel.id);
        this.currentPanel = panel;
        $panel.on(CQ.s7dm.Preset.events.VIEWERPRESET, this.generateVPDropdown);
        $panel.on(CQ.s7dm.Preset.events.IMAGEPRESET, this.generateIPDropdown);
        CQ.s7dm.Preset.init($panel);
        this.initLayout(panel); //initialize layout of s7 tab
    },

    generateVPDropdown : function(){
        var panel = CQ.s7dm.DMHelper.currentPanel;
    	var viewerPresetsPanel = panel.find("name", "viewerPresetsHbox");
        var assetType = CQ.s7dm.DMHelper.getAssetType(panel);
        var selectedVP = '';

		var viewerPresets = CQ.s7dm.Preset.VP.filter(assetType);

		if (viewerPresetsPanel && viewerPresetsPanel.length > 0) {
            viewerPresetsPanel = viewerPresetsPanel[0];
            viewerPresetsPanel.removeAll();

            var selectedViewerPresetArray = panel.find("name", "./s7ViewerPreset");
            if (selectedViewerPresetArray && selectedViewerPresetArray.length > 0) {
                selectedVP = selectedViewerPresetArray[0].getValue();
            }

            var viewerPresetSelectWidget = new CQ.form.Selection({
                type: 'select',
                name: 'viewerPresetCombo',
                fieldLabel: CQ.I18n.getMessage('Viewer Preset'),
                fieldDescription: CQ.I18n.getMessage("Viewer Preset to use when rendering dynamic asset. For images, it cannot be set when image preset is set."),
                defaultValue: selectedVP,
                listeners: {
                    selectionchanged : function(select, value, isChecked ) {
                        if (selectedViewerPresetArray && selectedViewerPresetArray.length > 0) {
                            selectedViewerPresetArray[0].setValue(value);
                        }

                    }
                },
                options: [{ text: 'None', value: ''}]
            });

            var vpOptions = [{ text: 'None', value: ''}];
            for (var i = 0; i<viewerPresets.length; i++){
                vpOptions.push({text: viewerPresets[i].id, value: viewerPresets[i].value});
            }


			viewerPresetSelectWidget.setOptions(vpOptions);
            viewerPresetSelectWidget.setValue(selectedVP);
            viewerPresetsPanel.add(viewerPresetSelectWidget);
        }
        panel.doLayout();
    },

    generateIPDropdown : function(){
        var panel = CQ.s7dm.DMHelper.currentPanel;
    	var imagePresetsPanel = panel.find("name", "imagePresetsHbox");
        var assetType = CQ.s7dm.DMHelper.getAssetType(panel);
        var selectedIP = '';
		var imagePresets = CQ.s7dm.Preset.IP.filter(assetType === 'image' );

        if (imagePresetsPanel && imagePresetsPanel.length > 0) {
            imagePresetsPanel = imagePresetsPanel[0];
            imagePresetsPanel.removeAll();

            var selectedImagePresetArray = panel.find("name", "./s7ImagePreset");
            if (selectedImagePresetArray && selectedImagePresetArray.length > 0) {
                selectedIP = selectedImagePresetArray[0].getValue();
                //check initial image preset value to show breakpoint field when no size in image preset
                var hadSize = false;
                for (var i = 0; i<imagePresets.length; i++){
                    if (imagePresets[i].value === selectedIP) {
                        hadSize = imagePresets[i].hadSize;
                        break;
                    }
                }
                //only do breakpoint setup when asset is image
                if (assetType === 'image') {
                    CQ.s7dm.DMHelper.setupBreakpoint(panel, !hadSize);
                }
            }


            var presetSelectWidget = new CQ.form.Selection({
                type: 'select',
                name: 'imagePresetCombo',
                fieldLabel: CQ.I18n.getMessage('Image Preset'),
                fieldDescription: CQ.I18n.getMessage("Image Preset to use when rendering image. It cannot be set when viewer preset is set."),
                defaultValue: selectedIP,
                listeners: {
                    selectionchanged : function(select, value, isChecked ) {
                        if (selectedImagePresetArray && selectedImagePresetArray.length > 0) {
                            selectedImagePresetArray[0].setValue(value);
                        }
                        var hadSize = false;
                        for (var i = 0; i<imagePresets.length; i++){
                            if (imagePresets[i].value === value) {
                                hadSize = imagePresets[i].hadSize;
                                break;
                            }
                        }
                        CQ.s7dm.DMHelper.setupBreakpoint(panel, !hadSize);
                    }
                },
                options: [ { text: 'None', value: ''} ]
            });


            var ipOptions = [{ text: 'None', value: ''}];
            for (var i = 0; i<imagePresets.length; i++){
                ipOptions.push({text: imagePresets[i].id, value: imagePresets[i].value});
            }


			presetSelectWidget.setOptions(ipOptions);
            presetSelectWidget.setValue(selectedIP);
            imagePresetsPanel.add(presetSelectWidget);

        }
        panel.doLayout();
    },

    generatePresetTypeRadio : function(panel){

		var presetTypeRadio = panel.find("name", "presetTypeRadio");
        var assetType = this.getAssetType(panel)
        //only asset type image to have preset type selection
        if (!(presetTypeRadio && presetTypeRadio.length > 0) && assetType === 'image') {
            var pTypeContainer = panel.find("name", "presetType");
            pTypeContainer = pTypeContainer[0];
            var radioItems = [];
            radioItems.push({
                boxLabel: 'Image Preset',
                name: 'presetType' ,
                inputValue: 'image_preset' ,
                checked: (this.hadImagePreset() || !this.hadViewerPreset())
            });

            radioItems.push({
                    boxLabel: 'Viewer Preset',
                    name: 'presetType',
                    inputValue:'viewer_preset',
                    checked: ((!this.hadImagePreset() && this.hadViewerPreset()))
                });
            var radioGrp = new CQ.Ext.form.RadioGroup({
                xtype: 'radiogroup',
                fieldLabel: 'Preset Type',
                columns: 2,
                name: 'presetTypeRadio',
                vertical: true,
                items: radioItems,
                listeners: {
                    change: function(r){
                        CQ.s7dm.DMHelper.manageLayout(r.getValue().inputValue);
                    }
                }
            });

            pTypeContainer.add(radioGrp);

            panel.doLayout();
        }

    },


    setupBreakpoint : function(panel, enabled) {
        var breakpoints = panel.find("name", "./breakpoints");
        breakpoints = breakpoints[0];
        if (enabled) {
            breakpoints.show();
        }
        else {
            breakpoints.hide();
        }
    },

    initLayout : function(panel){
        this.generatePresetTypeRadio(panel);
        setTimeout( function(){
            if ((CQ.s7dm.DMHelper.hadImagePreset() || !CQ.s7dm.DMHelper.hadViewerPreset()) && CQ.s7dm.DMHelper.getAssetType(panel) === 'image') {
                CQ.s7dm.DMHelper.manageLayout('image_preset');
            }
            else {
                CQ.s7dm.DMHelper.manageLayout('viewer_preset');
            }
        }, 100);

    },

    manageLayout : function(presetType) {
        var panel = CQ.s7dm.DMHelper.currentPanel;
    	var imagePresetsPanel = panel.find("name", "imagePresetsHbox");
            imagePresetsPanel = imagePresetsPanel[0];
    	var viewerPresetsPanel = panel.find("name", "viewerPresetsHbox");
            viewerPresetsPanel = viewerPresetsPanel[0];
    	var urlModifier = panel.find("name", "./urlModifiers");
        	urlModifier = urlModifier[0];
    	var breakpoints = panel.find("name", "./breakpoints");
        	breakpoints = breakpoints[0];
        if (presetType === 'viewer_preset') {
            imagePresetsPanel.hide();
            urlModifier.hide();
			breakpoints.hide();
			viewerPresetsPanel.show();
        }
        else {
            imagePresetsPanel.show();
            urlModifier.show();
			breakpoints.show();
            viewerPresetsPanel.hide();
        }

    },



    getAssetType : function(panel) {
		var assetTypePanel = panel.find("name", "./assetType");
        var assetType = '';
        if (assetTypePanel && assetTypePanel.length > 0) {
            assetType = assetTypePanel[0].getValue();
        }
        assetType = assetType.toLowerCase();
        return assetType;

    },

    hadImagePreset : function() {
        var panel = CQ.s7dm.DMHelper.currentPanel;
		var selectedIP = "";
        var selectedImagePresetArray = panel.find("name", "./s7ImagePreset");
        if (selectedImagePresetArray && selectedImagePresetArray.length > 0) {
            selectedIP = selectedImagePresetArray[0].getValue();
        }
        return (selectedIP !== "");
    },

    hadViewerPreset : function() {
        var panel = CQ.s7dm.DMHelper.currentPanel;
		var selectedVP = "";
        var selectedViewerPresetArray = panel.find("name", "./s7ViewerPreset");
        if (selectedViewerPresetArray && selectedViewerPresetArray.length > 0) {
            selectedVP = selectedViewerPresetArray[0].getValue();
        }

        return (selectedVP !== "");
    },

    beforeSubmit : function(dialog) {

        if (!dialog) {
            return;
        }

        var shownImagePreset = dialog.find("name", "imagePresetsHbox")[0].isVisible();
        var shownViewerPreset = dialog.find("name", "viewerPresetsHbox")[0].isVisible();
        var breakpoints = dialog.find("name", "./breakpoints")[0];
        var shownBreakpoint = breakpoints.isVisible();
        if (!shownImagePreset){
            var selectedImagePreset = dialog.find("name", "./s7ImagePreset");
            selectedImagePreset = selectedImagePreset[0];
            selectedImagePreset.setValue("");
            var urlModifier = dialog.find("name", "./urlModifiers");
            urlModifier = urlModifier[0];
            urlModifier.setValue("");
        }
        if (!shownBreakpoint)  {
            breakpoints.setValue("");
        }
        if (!shownViewerPreset) {
            var selectedViewerPreset = dialog.find("name", "./s7ViewerPreset");
            selectedViewerPreset = selectedViewerPreset[0];
            selectedViewerPreset.setValue("");
        }
    },

    initAdvancePanel : function(panel) {
        if (!panel) {
            return;
        }
        setTimeout( function(){
            var dmPanel = panel.nextSibling(); //grab dm tab
            var assetType = CQ.s7dm.DMHelper.getAssetType(dmPanel);
            var linkUrl = panel.find("name", "./linkUrl");
            linkUrl = linkUrl[0];
            var linkTarget = panel.find("name", "./linkTarget");
            linkTarget = linkTarget[0];
            var altText = panel.find("name", "./alt");
            altText = altText[0];
            if (assetType === 'image') {
                linkUrl.show();
                altText.show();
                linkTarget.show();
            }
            else {
                linkUrl.hide();
                altText.hide();
                linkTarget.hide();
            }
        }, 100);

    }



};
