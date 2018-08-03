package com.suntrust.dotcom.beans;

import java.util.List;

/**
 * Created by ugrr162 on 6/24/2017.
 */
public class LocationBean {

    private String branchId;//3
    private String branchName;//4
    private String address;//5
    private String city;//6
    private String county;//7
    private String state;//8
    private String postal;//9
    private String country;//10
    private String branchStatus;//20
    private String seoName;//86
    private boolean isBranch;//29
    private boolean isAtm;//35

    private String monBrHrs;//38
    private String tueBrHrs;//38
    private String wedBrHrs;//38
    private String thuBrHrs;//38
    private String friBrHrs;//38
    private String satBrHrs;//38
    private String sunBrHrs;//38

    private String monDtHrs;//38
    private String tueDtHrs;//38
    private String wedDtHrs;//38
    private String thuDtHrs;//38
    private String friDtHrs;//38
    private String satDtHrs;//38
    private String sunDtHrs;//38

    private String monTcHrs;//38
    private String tueTcHrs;//38
    private String wedTcHrs;//38
    private String thuTcHrs;//38
    private String friTcHrs;//38
    private String satTcHrs;//38
    private String sunTcHrs;//38

    private String phoneNumber;//38
    private String faxNumber;
    private List<String> services;
    private String splMessage;
    private String latitude;//38
    private String longitude;//38
    private String pageTitle;//38
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(String isUpdated) {
        this.isUpdated = isUpdated;
    }

    public String getIsPrimaryAddress() {
        return isPrimaryAddress;
    }

    public void setIsPrimaryAddress(String isPrimaryAddress) {
        this.isPrimaryAddress = isPrimaryAddress;
    }

    private String isUpdated;
    private String isPrimaryAddress;


    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getPageTitle() {
        return pageTitle;
    }
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }




    public boolean isBranch() {
        return isBranch;
    }

    public void setBranch(boolean branch) {
        isBranch = branch;
    }

    public boolean isAtm() {
        return isAtm;
    }

    public void setAtm(boolean atm) {
        isAtm = atm;
    }


    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBranchStatus() {
        return branchStatus;
    }

    public void setBranchStatus(String branchStatus) {
        this.branchStatus = branchStatus;
    }

    public String getSeoName() {
        return seoName;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }
    public String getSunBrHours() {
        return sunBrHrs;
    }
    public void setSunBrHours(String sunBrHrs) {
        this.sunBrHrs = sunBrHrs;
    }
    public String getMonBrHours() {
        return monBrHrs;
    }
    public void setMonBrHours(String monBrHrs) {
        this.monBrHrs = monBrHrs;
    }
    public String getTueBrHours() {
        return tueBrHrs;
    }
    public void setTueBrHours(String tueBrHrs) {
        this.tueBrHrs = tueBrHrs;
    }
    public String getWedBrHours() {
        return wedBrHrs;
    }
    public void setWedBrHours(String wedBrHrs) {
        this.wedBrHrs = wedBrHrs;
    }
    public String getThuBrHours() {
        return thuBrHrs;
    }
    public void setThuBrHours(String thuBrHrs) {
        this.thuBrHrs = thuBrHrs;
    }
    public String getFriBrHours() {
        return friBrHrs;
    }
    public void setFriBrHours(String friBrHrs) {
        this.friBrHrs = friBrHrs;
    }
    public String getSatBrHours() {
        return satBrHrs;
    }
    public void setSatBrHours(String satBrHrs) {
        this.satBrHrs = satBrHrs;
    }
    public String getSunDtHours() {
        return sunDtHrs;
    }
    public void setSunDtHours(String sunDtHrs) {
        this.sunDtHrs = sunDtHrs;
    }
    public String getMonDtHours() {
        return monDtHrs;
    }
    public void setMonDtHours(String monDtHrs) {
        this.monDtHrs = monDtHrs;
    }
    public String getTueDtHours() {
        return tueDtHrs;
    }
    public void setTueDtHours(String tueDtHrs) {
        this.tueDtHrs = tueDtHrs;
    }
    public String getWedDtHours() {
        return wedDtHrs;
    }
    public void setWedDtHours(String wedDtHrs) {
        this.wedDtHrs = wedDtHrs;
    }
    public String getThuDtHours() {
        return thuDtHrs;
    }
    public void setThuDtHours(String thuDtHrs) {
        this.thuDtHrs = thuDtHrs;
    }
    public String getFriDtHours() {
        return friDtHrs;
    }
    public void setFriDtHours(String friDtHrs) {
        this.friDtHrs = friDtHrs;
    }
    public String getSatDtHours() {
        return satDtHrs;
    }
    public void setSatDtHours(String satDtHrs) {
        this.satDtHrs = satDtHrs;
    }
    public String getSunTcHours() {
        return sunTcHrs;
    }
    public void setSunTcHours(String sunTcHrs) {
        this.sunTcHrs = sunTcHrs;
    }
    public String getMonTcHours() {
        return monTcHrs;
    }
    public void setMonTcHours(String monTcHrs) {
        this.monTcHrs = monTcHrs;
    }
    public String getTueTcHours() {
        return tueTcHrs;
    }
    public void setTueTcHours(String tueTcHrs) {
        this.tueTcHrs = tueTcHrs;
    }
    public String getWedTcHours() {
        return wedTcHrs;
    }
    public void setWedTcHours(String wedTcHrs) {
        this.wedTcHrs = wedTcHrs;
    }
    public String getThuTcHours() {
        return thuTcHrs;
    }
    public void setThuTcHours(String thuTcHrs) {
        this.thuTcHrs = thuTcHrs;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public String getFriTcHours() {
        return friTcHrs;
    }
    public void setFriTcHours(String friTcHrs) {
        this.friTcHrs = friTcHrs;
    }
    public String getSatTcHours() {
        return satTcHrs;
    }
    public void setSatTcHours(String satTcHrs) {
        this.satTcHrs = satTcHrs;
    }
    public String getPhone() {
        return phoneNumber;
    }
    public void setPhone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getFax() {
        return faxNumber;
    }
    public void setFax(String faxNumber) {
        this.faxNumber = faxNumber;
    }
    public String getSpecialMessages() {
        return splMessage;
    }
    public void setSpecialMessages(String splMessage) {
        this.splMessage = splMessage;
    }
}
