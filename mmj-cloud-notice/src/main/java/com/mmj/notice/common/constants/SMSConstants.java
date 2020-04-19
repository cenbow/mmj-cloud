package com.mmj.notice.common.constants;

public interface SMSConstants {

    String SmsUrl = "http://api.91jianmi.com/sdk/SMS";

    String uid = "4843";

    String psw = "fa11fe6246de6816230deed845bfb524";

    String redisLocalKey = "SMS_SEND_";

    String redisTemplateKey = "SMS_TEMPLATE:";

    String smsAcount = "cmd=send&uid=" + uid + "&psw=" + psw + "&mobiles=";

    String uidDelay = "3856";

    String pswDelay = "c2b1d04373e80dd126ef805d9593791a";

    String smsAcountDelay = "cmd=send&uid=" + uidDelay + "&psw=" + pswDelay + "&mobiles=";
}
