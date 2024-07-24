package swp391.learning.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp391.learning.application.service.MemberSubscriptionService;
import swp391.learning.domain.dto.common.ResponseCommon;
import swp391.learning.domain.dto.request.admin.membership.AddMemberSubscriptionRequest;
import swp391.learning.domain.dto.request.admin.membership.DeleteMemberSubscriptionRequest;
import swp391.learning.domain.dto.request.admin.membership.EnrollMembershipRequest;
import swp391.learning.domain.dto.request.admin.membership.UpdateMemberSubscriptionRequest;
import swp391.learning.domain.dto.request.user.book.PaymentConfirmRequest;
import swp391.learning.domain.dto.response.admin.membership.AddMemberSubscriptionResponse;
import swp391.learning.domain.dto.response.admin.membership.DeleteMemberSubscriptionResponse;
import swp391.learning.domain.dto.response.admin.membership.UpdateMemberSubscriptionResponse;
import swp391.learning.domain.dto.response.user.membership.EnrollMembershipResponse;
import swp391.learning.domain.dto.response.user.membership.MembershipResponse;
import swp391.learning.domain.dto.response.user.membership.PaymentConfirmResponse;
import swp391.learning.domain.enums.ResponseCode;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membership")
@AllArgsConstructor
public class MemberSubController {

    private final MemberSubscriptionService memberSubscriptionService;

    @GetMapping("/memberships")
    public ResponseEntity<ResponseCommon<List<MembershipResponse>>> getAllMembershipSubscription(){
        return ResponseEntity.ok(memberSubscriptionService.getAllMemberships());
    }

    @PostMapping("/add-subscription")
    public ResponseEntity<ResponseCommon<AddMemberSubscriptionResponse>> addMemberSubscription(
            @Valid @RequestBody AddMemberSubscriptionRequest addMemberSubscriptionRequest) {
        ResponseCommon<AddMemberSubscriptionResponse> response = memberSubscriptionService.addMemberSubscription(addMemberSubscriptionRequest);
        if (response.getCode() == ResponseCode.SUCCESS.getCode()) {
            return ResponseEntity.ok(response);
        } else if (response.getCode() == ResponseCode.SUBSCRIPTION_NOT_EXIST.getCode()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseCommon<>(response.getCode(), "Subscription already exists", null));
        } else {
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.FAIL.getCode(), "Add subscription failed", null));
        }
    }

    @PostMapping("/update-subscription")
    public ResponseEntity<ResponseCommon<UpdateMemberSubscriptionResponse>> updateMemberSubscription(
            @Valid @RequestBody UpdateMemberSubscriptionRequest updateMemberSubscriptionRequest) {
        ResponseCommon<UpdateMemberSubscriptionResponse> response = memberSubscriptionService.updateMemberSubscription(updateMemberSubscriptionRequest);
        if (response.getCode() == ResponseCode.SUCCESS.getCode()) {
            return ResponseEntity.ok(response);
        } else if (response.getCode() == ResponseCode.SUBSCRIPTION_NOT_EXIST.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseCommon<>(response.getCode(), "Subscription does not exist", null));
        } else {
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.FAIL.getCode(), "Update subscription failed", null));
        }
    }

    @PostMapping("/delete-subscription")
    public ResponseEntity<ResponseCommon<DeleteMemberSubscriptionResponse>> deleteMemberSubscription(
            @Valid @RequestBody DeleteMemberSubscriptionRequest deleteMemberSubscriptionRequest) {
        ResponseCommon<DeleteMemberSubscriptionResponse> response = memberSubscriptionService.deleteMemberSubscription(deleteMemberSubscriptionRequest);
        if (response.getCode() == ResponseCode.SUCCESS.getCode()) {
            return ResponseEntity.ok(response);
        } else if (response.getCode() == ResponseCode.CATEGORY_NOT_EXIST.getCode()) { // Check if this code matches your delete subscription scenario
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseCommon<>(response.getCode(), "Subscription does not exist", null));
        } else {
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.FAIL.getCode(), "Delete subscription failed", null));
        }
    }
    @PostMapping("/enroll-membership")
    public ResponseEntity<ResponseCommon<EnrollMembershipResponse>> enrollMembership(@Valid @RequestBody EnrollMembershipRequest enrollMembershipRequest){
        ResponseCommon<EnrollMembershipResponse> response = memberSubscriptionService.enrollMembership(enrollMembershipRequest);
        if(response.getCode() == ResponseCode.SEND_URL_PAYMENT_FAIL.getCode()){
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.SEND_URL_PAYMENT_FAIL.getCode(),"Send url payment fail",null));
        } else if(response.getCode()==ResponseCode.FAIL.getCode()){
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.FAIL.getCode(),"Exception send url",null));
        } else {
            return ResponseEntity.ok().body(new ResponseCommon<>(ResponseCode.SUCCESS.getCode(),response.getMessage(),response.getData()));
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<ResponseCommon<PaymentConfirmResponse>> enrollMembership(@Valid @RequestBody PaymentConfirmRequest paymentConfirmRequest) {
        ResponseCommon<PaymentConfirmResponse> response = memberSubscriptionService.paymentConfirm(paymentConfirmRequest);
        if (response.getCode() == ResponseCode.SUCCESS.getCode()) {
            // Xử lý khi thanh toán thành công
            return ResponseEntity.ok().body(new ResponseCommon<>(ResponseCode.SUCCESS.getCode(), "Payment success", response.getData()));
        } else if (response.getCode() == ResponseCode.ORDER_NOT_FOUND.getCode()) {
            // Xử lý khi không tìm thấy đơn hàng
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.ORDER_NOT_FOUND.getCode(), "Order not found", null));
        } else if (response.getCode() == ResponseCode.PAYMENT_FAIL.getCode()) {
            // Xử lý khi thanh toán thất bại
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.PAYMENT_FAIL.getCode(), "Payment fail", null));
        } else if (response.getCode() == ResponseCode.FAIL.getCode()) {
            // Xử lý khi có lỗi trong quá trình xác nhận thanh toán
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.FAIL.getCode(), "Confirm payment fail", null));
        } else if (response.getCode() == ResponseCode.CHANGE_PARAM.getCode()) {
            // Xử lý khi thấy sự thay đổi trong các tham số
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.CHANGE_PARAM.getCode(), "Param is hacker", null));
        } else if (response.getCode() == ResponseCode.INVALID_AMOUNT.getCode()) {
            // Xử lý khi số tiền không hợp lệ
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.INVALID_AMOUNT.getCode(), "Invalid Amount", null));
        } else if (response.getCode() == ResponseCode.ORDER_ALREADY_CONFIRM.getCode()) {
            // Xử lý khi đơn hàng đã được xác nhận trước đó
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.ORDER_ALREADY_CONFIRM.getCode(), "Order already confirmed", null));
        } else if (response.getCode() == ResponseCode.USER_CANCEL_BILL.getCode()) {
            // Xử lý khi người dùng hủy thanh toán
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.USER_CANCEL_BILL.getCode(), "User cancel bill", null));
        } else {
            // Xử lý khi có lỗi không xác định
            return ResponseEntity.badRequest().body(new ResponseCommon<>(ResponseCode.FAIL.getCode(), "Unknown error", null));
        }
    }
}
