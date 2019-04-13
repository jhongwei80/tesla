package io.github.tesla.filter.support;

public class ResponseMessage {

    public enum MesageCodeType {

        OSG001("Token expire"), OSG002("Wrong Token"), OSG003(" Token has not exist");

        private String code;

        MesageCodeType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

    }

    private MesageCodeType code;

    private String message;

    private String content;

    public ResponseMessage(MesageCodeType code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public MesageCodeType getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(MesageCodeType code) {
        this.code = code;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
