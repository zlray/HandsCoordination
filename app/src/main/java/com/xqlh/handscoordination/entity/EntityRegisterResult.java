package com.xqlh.handscoordination.entity;

/**
 * Created by Administrator on 2017/9/6.
 */

public class EntityRegisterResult {

    /**
     * code : 1
     * msg : OK
     * Result : {"ID":"xxxxxxx","ClientType":111,"CompanyName":"心企领航","CreateTime":"2017-07-20","TermOfValidity":"2018-09-10","PollCode":"EB45B13D183A005C7DCC9EF50889FC74","UUID":"xxxxxxxxxxxxxxxxxxxxxxxxxxx","IsUsede":true}
     * ResultMsg : 注册成功
     */

    private int code;
    private String msg;
    private ResultBean Result;
    private String ResultMsg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
        this.Result = Result;
    }

    public String getResultMsg() {
        return ResultMsg;
    }

    public void setResultMsg(String ResultMsg) {
        this.ResultMsg = ResultMsg;
    }

    public static class ResultBean {
        /**
         * ID : xxxxxxx
         * ClientType : 111
         * CompanyName : 心企领航
         * CreateTime : 2017-07-20
         * TermOfValidity : 2018-09-10
         * PollCode : EB45B13D183A005C7DCC9EF50889FC74
         * UUID : xxxxxxxxxxxxxxxxxxxxxxxxxxx
         * IsUsede : true
         */
        private String ID;
        private int ClientType;
        private String CompanyName;
        private String CreateTime;
        private String TermOfValidity;
        private String PollCode;
        private String UUID;
        private boolean IsUsede;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public int getClientType() {
            return ClientType;
        }

        public void setClientType(int ClientType) {
            this.ClientType = ClientType;
        }

        public String getCompanyName() {
            return CompanyName;
        }

        public void setCompanyName(String CompanyName) {
            this.CompanyName = CompanyName;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getTermOfValidity() {
            return TermOfValidity;
        }

        public void setTermOfValidity(String TermOfValidity) {
            this.TermOfValidity = TermOfValidity;
        }

        public String getPollCode() {
            return PollCode;
        }

        public void setPollCode(String PollCode) {
            this.PollCode = PollCode;
        }

        public String getUUID() {
            return UUID;
        }

        public void setUUID(String UUID) {
            this.UUID = UUID;
        }

        public boolean isIsUsede() {
            return IsUsede;
        }

        public void setIsUsede(boolean IsUsede) {
            this.IsUsede = IsUsede;
        }
    }
}
