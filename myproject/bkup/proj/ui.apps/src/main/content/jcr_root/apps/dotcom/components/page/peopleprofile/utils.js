
use(function () {
    // you can reference the parameters via the this keyword.
    var retValue = this.value1;
    var obj = JSON.parse(retValue);
    var path = currentPage.getPath()+".html?start="+obj.adv_zipcode+"&destination="+obj.adv_address+", "+obj.adv_city+", "+obj.adv_state+", "+obj.adv_zipcode;
    return {
        advisor_location_name:obj.adv_location_name,
        advisor_address: obj.adv_address,
        advisor_city:obj.adv_city,
        advisor_state:obj.adv_state,
        advisor_zipcode:obj.adv_zipcode,
        advisor_latitude:obj.adv_latitude,
        advisor_longitude:obj.adv_longitude,
        advisor_path:path
    };
}); 