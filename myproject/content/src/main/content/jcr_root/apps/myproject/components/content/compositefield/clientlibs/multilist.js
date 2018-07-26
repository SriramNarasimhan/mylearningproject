CQ.CustomWidget = CQ.Ext.extend(CQ.form.CompositeField, {

    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
    hiddenField: null,

   /**
    * @private
    * @type CQ.Ext.form.TextField
    */
    titleField: null,

    /**
    * @private
    * @type CQ.html5.form.SmartImage
    */
    largeImageField: null,


    /**
     * @private
     * @type CQ.form.PathField
     */
    mediumImageField: null,

    /**
     * @private
     * @type CQ.form.PathField
     */
    smallImageField: null,

    /**
     * @private
     * @type CQ.form.PathField
     */
    linkField: null,

    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
    styleField: null,

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": true,
            "layout": "form",
            "padding": 10
        };
        config = CQ.Util.applyDefaults(config, defaults);
        CQ.CustomWidget.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        CQ.CustomWidget.superclass.initComponent.call(this);
        //Hidden Field
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });

        this.add(this.hiddenField);

        //TITLE - START

        this.titleField = new CQ.Ext.form.TextField({
            fieldLabel: "Title ",
            fieldDescription: "List Item Title",
            width: 225,
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.titleField);

        //TITLE - END

        /*
        //SMART IMAGE(LARGE) FOR DESKTOP

        this.largeImageField = new CQ.form.SmartFile( {
            fieldLabel : "Image: ",
            editable:false,
            allowBlank : false,
            allowUpload: true,
            anchor: '75%',
            maxLength : 100,
            cropParameter :"./image/imageCrop",
            ddGroups : "media",
            ddAccept : "image/",
            draggable: {
                //      Config option of CQ.Ext.Panel.DD class.
                //      It's a floating Panel, so do not show a placeholder proxy in the original position.
                        insertProxy: false,
                
                //      Called for each mousemove event while dragging the DD object.
                        onDrag : function(e){
                //          Record the x,y position of the drag proxy so that we can
                //          position the Panel at end of drag.
                            var pel = this.proxy.getEl();
                            this.x = pel.getLeft(true);
                            this.y = pel.getTop(true);
                
                //          Keep the Shadow aligned if there is one.
                            var s = this.panel.getEl().shadow;
                            if (s) {
                                s.realign(this.x, this.y, pel.getWidth(), pel.getHeight());
                            }
                        },
                
                //      Called on the mouseup event.
                        endDrag : function(e){
                            this.panel.setPosition(this.x, this.y);
                        }
                    },
            fileNameParameter : "./image/fileName",
            fileReferenceParameter : "./image/fileReference",
            mapParameter :"./image/imageMap",
            rotateParameter : "./image/imageRotate",
            name : "./image/file",
            requestSuffix : "/image.img.png",
            sizeLimit : "100",
            autoUploadDelay : "1",
            useHTML5: true,
            uploadText : "Drop an asset or click to upload",
            listeners : {
                change : {
                    scope : this,
                    fn : this.updateHidden
                },
                dialogclose : {
                    scope : this,
                    fn : this.updateHidden
                }
            }
        }).show();
        this.add(this.largeImageField);*/

        //LARGE IMAGE FOR DESKTOP - START 

        this.largeImageField = new CQ.form.PathField({
            fieldLabel: "Large Image",
            fieldDescription: "Image for Desktop",
            rootPath:"/content/dam",
            //allowBlank: false,
            width: 225,
            listeners: {
                change: {
                    scope: this,
                    fn: this.updateHidden
                },
                dialogclose: {
                    scope: this,
                    fn: this.updateHidden
                }
            }
        });
        this.add(this.largeImageField);

        //LARGE IMAGE FOR DESKTOP - END

        //MEDIUM IMAGE FOR IPAD - START

        this.mediumImageField = new CQ.form.PathField({
            fieldLabel: "Medium Image",
            fieldDescription: "Image for IPad",
            rootPath:"/content/dam",
            //allowBlank: false,
            width: 225,
            listeners: {
                change: {
                    scope: this,
                    fn: this.updateHidden
                },
                dialogclose: {
                    scope: this,
                    fn: this.updateHidden
                }
            }
        });
        this.add(this.mediumImageField);

        //MEDIUM IMAGE FOR IPAD - END

        //SMALL IMAGE FOR IPHONE - START

        this.smallImageField = new CQ.form.PathField({
            fieldLabel: "Small Image",
            fieldDescription: "Image for IPhone",
            rootPath:"/content/dam",
            //allowBlank: false,
            width: 225,
            listeners: {
                change: {
                    scope: this,
                    fn: this.updateHidden
                },
                dialogclose: {
                    scope: this,
                    fn: this.updateHidden
                }
            }
        });
        this.add(this.smallImageField);

        //SMALL IMAGE FOR IPHONE - START

        //LINK - START

        this.linkField = new CQ.form.PathField({
            fieldLabel: "Link",
            fieldDescription: "Image for IPhone",
            rootPath:"/content/dam",
            //allowBlank: false,
            width: 225,
            listeners: {
                change: {
                    scope: this,
                    fn: this.updateHidden
                },
                dialogclose: {
                    scope: this,
                    fn: this.updateHidden
                }
            }
        });
        this.add(this.linkField);

        //LINK - END

        //STYLE - START

        this.styleField = new CQ.Ext.form.TextField({
            fieldLabel: "Style",
            fieldDescription: "Style",
            width: 225,
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.styleField);

        //STYLE - END

    },
 
    // overriding CQ.form.CompositeField#processPath
    processPath: function(path) {
        console.log("CustomWidget#processPath", path);
        this.largeImageField.processPath(path);
        //this.linkType.processPath(path);
    },
 
    // overriding CQ.form.CompositeField#processRecord
    processRecord: function(record, path) {
        console.log("CustomWidget#processRecord", path, record);
        this.largeImageField.processRecord(record, path);
        //this.linkType.processRecord(record, path);
    },
 
    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|");
        this.titleField.setValue(parts[0]);
        this.largeImageField.setValue(parts[1]);
        this.mediumImageField.setValue(parts[2]);
        this.smallImageField.setValue(parts[3]);
        this.linkField.setValue(parts[4]);
        this.styleField.setValue(parts[5]);
        this.hiddenField.setValue(value);
    },
 
    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        this.getRawValue();
        return this.getRawValue();
    },
 
    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
       return this.titleField.getValue() + "|" +
              this.largeImageField.getValue() + "|" +
              this.mediumImageField.getValue() + "|" +
              this.smallImageField.getValue() + "|" +
              this.linkField.getValue() + "|" +
              this.styleField.getValue();
    },
 
    // private
    updateHidden: function() {
        //alert('customwidget updatehidden');
        this.hiddenField.setValue(this.getValue());
    }
});
 
// register xtype
CQ.Ext.reg("imagelist", CQ.CustomWidget);
