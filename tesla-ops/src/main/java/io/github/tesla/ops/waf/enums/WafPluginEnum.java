package io.github.tesla.ops.waf.enums;

public enum WafPluginEnum {
    BlackCookieRequestPlugin("BlackCookieRequestPlugin"), BlackIpRequesPlugin("BlackIpRequestPlugin"),
    BlackUaRequestPlugin("BlackUaRequestPlugin"), BlackURLRequestPlugin("BlackURLRequestPlugin"),
    BlackURLParamRequestPlugin("BlackURLParamRequestPlugin"),
    SecurityScannerRequestPlugin("SecurityScannerRequestPlugin"),
    AppKeyControlRequestPlugin("AppKeyControlRequestPlugin"),
    FileUploadDownloadRequestPlugin("FileUploadDownloadRequestPlugin"),
    UrlChangeRequestPlugin("UrlChangeRequestPlugin");

    private String filterType;

    WafPluginEnum(String wafPluginType) {
        this.filterType = wafPluginType;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

}
