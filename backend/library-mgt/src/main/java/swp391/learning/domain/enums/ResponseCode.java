package swp391.learning.domain.enums;

import lombok.Getter;

@Getter
public enum ResponseCode { // thang cong: 200, that bai: 400, khong tim thay: 404, Khong ton tai:401
    SUCCESS(200, "Success"),
    FAIL(400, "Fail"),
    USER_NOT_FOUND(404, "User not found"),
    PASSWORD_INCORRECT(400, "Password incorrect"),
    OTP_INCORRECT(400, "OTP incorrect"),
    Expired_OTP(400, "Expired OTP"),
    USER_EXIST(400, "User exist"),
    OLD_PASSWORD_INCORRECT(400, "Old password incorrect"),
    //    category
    CATEGORY_EXIST(400, "Category exist"),
    CATEGORY_NOT_EXIST(401, "Category not exist"),
    CATEGORY_LIST_IS_EMPTY(400, "Category list is empty"),
    //    category
    AUTHOR_EXIST(400, "Author exist"),
    AUTHOR_NOT_EXIST(401, "Author not exist"),
    AUTHOR_LIST_IS_EMPTY(400, "Author list is empty"),
    // book
    BOOK_EXIST(400,"BOOK exist"),
    BOOK_NOT_EXIST(401,"BOOK not exist"),
    BOOK_LIST_IS_EMPTY(400,"BOOK list is empty"),
    //Payment
    ORDER_NOT_EXIST(3800,"ORDER NOT EXIST"),
    PAYMENT_FAIL(3900,"PAYMENT FAIL VNPAY"),
    SEND_URL_PAYMENT_FAIL(4000,"SEND URL PAYMENT FAIL"),
    CHANGE_PARAM(4100,"CHANGE PARAM"),
    ORDER_NOT_FOUND(4200,"ORDER NOT FOUND"),
    INVALID_AMOUNT(4300,"IVALID AMOUNT"),
    ORDER_ALREADY_CONFIRM(4400,"ORDER ALREADY CONFIRM"),
    USER_CANCEL_BILL(4500,"USER CANCEL BILL"),
    //membership
    SUBSCRIPTION_EXIST(400,"SUBSCRIPTION exist"),
    SUBSCRIPTION_NOT_EXIST_LIST_IS_EMPTY(400,"SUBSCRIPTION list is empty"),
    SUBSCRIPTION_NOT_EXIST(404,"SUBSCRIPTION not exits"),
    ;
    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

