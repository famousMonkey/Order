package com.meatball.utils;

public class TextConversion {

    public static String textStatus(Integer orderStatus,Integer refundStatus) {
        String textStatus=null;
        if(orderStatus == null || refundStatus == null){
            return null;
        }

        switch (orderStatus) {
            case 0:
                textStatus = "进行中";
                break;
            case 1:
                textStatus = "待支付";
                break;
            case 2:
                textStatus = "支付成功";
                break;
            case 3:
                textStatus = "已取消";
                break;
        }

        if(orderStatus == 2){
            switch (refundStatus) {
                case 2:
                    textStatus = "部分退款";
                    break;
                case 1:
                    textStatus = "全部退款";
                    break;
            }
        }
        return textStatus;
    }

    public static String textPaymentMethod(Integer paymentMethod) {
        String textPaymentMethod=null;
        if(paymentMethod == null){
            return null;
        }
        switch (paymentMethod) {
            case 11:
                textPaymentMethod = "现金";
                break;
            case 21:
                textPaymentMethod = "银行卡";
                break;
            case 31:
                textPaymentMethod = "微信";
                break;
            case 32:
                textPaymentMethod = "支付宝";
                break;
            case 40:
                textPaymentMethod = "通用账户";
                break;
            case 41:
                textPaymentMethod = "汽油账户";
                break;
            case 42:
                textPaymentMethod = "柴油账户";
                break;
            case 43:
                textPaymentMethod = "CNG账户";
                break;
            case 51:
                textPaymentMethod = "白条";
                break;
        }
        return textPaymentMethod;
    }
}
