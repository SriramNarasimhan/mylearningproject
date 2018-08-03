use(function () {
    // you can reference the parameters via the this keyword.
    var currentDay = new Date().getDay();
    var retValue = this.templateName;
    var searchVal = this.searchVal;

    if(retValue == "getDirection"){
    	 console.log(searchVal);
         var loadURL = currentPage.getPath()+".html";
         if(searchVal == 'undefined')
     	{
         	loadURL = loadURL.trim();
     	}
         else
         {
         	loadURL = loadURL.trim() + "?start="+encodeURIComponent(searchVal);
         }
     	//return currentPage.getPath()+".html?start="+searchVal;
         return loadURL;
    }
    if(retValue == "pageUrl"){
    	return currentPage.getPath()+".html";
    	
    	//return this.loadURL;
    }
    /*if(retValue == "services"){
        var services = currentPage.properties.get("loc_services");
        var formatedService = [];
        for(var i=0; i < services.length; i++){
            services[i] = services[i].replace("services:", "");
            formatedService.push(services[i]);
        }
    	return formatedService;
    }*/
    if(retValue == "states"){
        var services = currentPage.properties.get("loc_state");
        services = services.replace("states:", "");
       return services.toUpperCase();
    }
	if(retValue == "services"){
        var resourceResolver = resource.getResourceResolver();
        var tagManager = resourceResolver.adaptTo(Packages.com.day.cq.tagging.TagManager);
        var loc_services = [];
        var temp_loc_services_unformatted = currentPage.properties.get("loc_services");
        for(var i=0; i < temp_loc_services_unformatted.length; i++){ 
            var servicesTagID = temp_loc_services_unformatted[i];
            var servicestag = tagManager.resolve(servicesTagID.toLowerCase().trim());
            if(servicestag != null)
                loc_services.push(servicestag.getTitle());
        }
		return loc_services;
    }
	if(retValue == "branchhours"){
        if(currentPage.properties.get("loc_sunbranchhours") != "Closed" ||currentPage.properties.get("loc_monbranchhours") != "Closed" ||currentPage.properties.get("loc_tuebranchhours") != "Closed" ||currentPage.properties.get("loc_wedbranchhours") != "Closed" ||currentPage.properties.get("loc_thubranchhours") != "Closed" ||currentPage.properties.get("loc_fribranchhours") != "Closed" || currentPage.properties.get("loc_satbranchhours") != "Closed"){
            if(currentDay == 0){
                return currentPage.properties.get("loc_sunbranchhours");
            }else if(currentDay == 1){
                return currentPage.properties.get("loc_monbranchhours");
            }else if(currentDay == 2){
               return currentPage.properties.get("loc_tuebranchhours");
            }else if(currentDay == 3){
                return currentPage.properties.get("loc_wedbranchhours");
            }else if(currentDay == 4){
                return currentPage.properties.get("loc_thubranchhours");
            }else if(currentDay == 5){
                return currentPage.properties.get("loc_fribranchhours");
            }else if(currentDay == 6){
                return currentPage.properties.get("loc_satbranchhours");
            }
        }
    }else if(retValue == "drivethru"){
        if(currentPage.properties.get("loc_sundriveinhours") != "Closed" ||currentPage.properties.get("loc_mondriveinhours") != "Closed" ||currentPage.properties.get("loc_tuedriveinhours") != "Closed" ||currentPage.properties.get("loc_weddriveinhours") != "Closed" ||currentPage.properties.get("loc_thudriveinhours") != "Closed" ||currentPage.properties.get("loc_fridriveinhours") != "Closed" || currentPage.properties.get("loc_satdriveinhours") != "Closed"){
            if(currentDay == 0){
                return currentPage.properties.get("loc_sundriveinhours");
            }else if(currentDay == 1){
                return currentPage.properties.get("loc_mondriveinhours");
            }else if(currentDay == 2){
               return currentPage.properties.get("loc_tuedriveinhours");
            }else if(currentDay == 3){
                return currentPage.properties.get("loc_weddriveinhours");
            }else if(currentDay == 4){
                return currentPage.properties.get("loc_thudriveinhours");
            }else if(currentDay == 5){
                return currentPage.properties.get("loc_fridriveinhours");
            }else if(currentDay == 6){
                return currentPage.properties.get("loc_satdriveinhours");
            }
        }
    }else if(retValue == "tellerconnect"){
        if(currentPage.properties.get("loc_suntellerhours") != "Closed" ||currentPage.properties.get("loc_montellerhours") != "Closed" ||currentPage.properties.get("loc_tuetellerhours") != "Closed" ||currentPage.properties.get("loc_wedtellerhours") != "Closed" ||currentPage.properties.get("loc_thutellerhours") != "Closed" ||currentPage.properties.get("loc_fritellerhours") != "Closed" || currentPage.properties.get("loc_sattellerhours") != "Closed"){
            if(currentDay == 0){
                return currentPage.properties.get("loc_suntellerhours");
            }else if(currentDay == 1){
                return currentPage.properties.get("loc_montellerhours");
            }else if(currentDay == 2){
               return currentPage.properties.get("loc_tuetellerhours");
            }else if(currentDay == 3){
                return currentPage.properties.get("loc_wedtellerhours");
            }else if(currentDay == 4){
                return currentPage.properties.get("loc_thutellerhours");
            }else if(currentDay == 5){
                return currentPage.properties.get("loc_fritellerhours");
            }else if(currentDay == 6){
                return currentPage.properties.get("loc_sattellerhours");
            }
        }
    }



});