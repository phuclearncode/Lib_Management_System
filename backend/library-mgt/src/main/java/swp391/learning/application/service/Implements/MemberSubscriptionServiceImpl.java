package swp391.learning.application.service.Implements;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import swp391.learning.application.service.MemberSubscriptionService;
import swp391.learning.application.service.PaymentService;
import swp391.learning.config.VnPayConfig;
import swp391.learning.domain.dto.common.PaymentRes;
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
import swp391.learning.domain.dto.response.user.membership.MembershipResponse.BenefitDTO;
import swp391.learning.domain.entity.Benefits;
import swp391.learning.domain.entity.MemberBenefit;
import swp391.learning.domain.entity.MemberBenefitKey;
import swp391.learning.domain.entity.MemberSubscription;
import swp391.learning.domain.entity.Order;
import swp391.learning.domain.entity.Payment;
import swp391.learning.domain.entity.User;
import swp391.learning.domain.enums.*;
import swp391.learning.repository.BenefitsRepository;
import swp391.learning.repository.MemberBenefitRepository;
import swp391.learning.repository.MemberSubscriptionRepository;
import swp391.learning.repository.OrderRepository;
import swp391.learning.repository.PaymentRepository;
import swp391.learning.repository.UserRepository;
import swp391.learning.utils.CommonUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberSubscriptionServiceImpl implements MemberSubscriptionService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final MemberSubscriptionRepository memberSubscriptionRepository;
    private static final Logger log = LoggerFactory.getLogger(MemberSubscriptionServiceImpl.class);
    private final MemberBenefitRepository memberBenefitRepository;
    private final BenefitsRepository benefitsRepository;

    @Override
    public ResponseCommon<List<MembershipResponse>> getAllMemberships() {
        List<MemberSubscription> memberSubscription = memberSubscriptionRepository
                .findMemberSubscriptionByIsDeleted(false);
        List<MembershipResponse> list = mapMemberSubscriptionToMembershipResponse(memberSubscription);
        log.debug("Get all membership with " + memberSubscription.size() + " records");
        return new ResponseCommon<>(ResponseCode.SUCCESS, list);
    }   

    private List<String> mapMemberBenefitToListBenefit(List<MemberBenefit> memberBenefits){
        List<String> list = new ArrayList<>();
        for (MemberBenefit m : memberBenefits) {
            list.add(m.getBenefits().getName());
        }
        return list;
    }

    private List<MembershipResponse> mapMemberSubscriptionToMembershipResponse(List<MemberSubscription> list){
        List<MembershipResponse> result = new ArrayList<>();
        for (MemberSubscription memberSubscription : list) {
            MembershipResponse membershipResponse = new MembershipResponse();
            List<MemberBenefit> memberBenefit = memberSubscription.getMemberBenefits().stream().filter(m -> m.getId().getMemberId() == memberSubscription.getId()).toList();
            List<BenefitDTO> benefits = memberBenefit.stream().map(m -> new MembershipResponse.BenefitDTO(m.getBenefits().getId(), m.getBenefits().getName())).toList();
            BeanUtils.copyProperties(memberSubscription, membershipResponse);
            membershipResponse.setBenefits(benefits);
            result.add(membershipResponse);
        }
        return result;
    }

    @Override
    public ResponseCommon<AddMemberSubscriptionResponse> addMemberSubscription(
            AddMemberSubscriptionRequest addMemberSubscriptionRequest) {
        try {
            MemberSubscription memberSubscription = memberSubscriptionRepository
                    .findMemberSubscriptionByNameSubscription(addMemberSubscriptionRequest.getNameSubscription())
                    .orElse(null);
            User user = userRepository.findByEmail(addMemberSubscriptionRequest.getEmail());
            if (Objects.isNull(user)) {
                log.debug("Add MemberSubscription failed: User does not exist");
                return new ResponseCommon<>(ResponseCode.SUBSCRIPTION_EXIST, null);
            }
            if (Objects.isNull(memberSubscription)) {
                memberSubscription = new MemberSubscription();
            }
            memberSubscription.setUserCreated(user);
            memberSubscription.setDeleted(false);
            memberSubscription.setNameSubscription(addMemberSubscriptionRequest.getNameSubscription());
            // memberSubscription.setSubscriptionPlan(addMemberSubscriptionRequest.getSubscriptionPlan());
            memberSubscription.setFeeMember(addMemberSubscriptionRequest.getFee_member());
            // memberSubscription.setStartDate(addMemberSubscriptionRequest.getStartDate());
            memberSubscription.setMaxBook(addMemberSubscriptionRequest.getMaxBook());
            // memberSubscription.setMembershipType(addMemberSubscriptionRequest.getMembershipType());
            // memberSubscription.setEndDate(addMemberSubscriptionRequest.getEndDate());
            memberSubscription.setExpireDate(Integer.parseInt(addMemberSubscriptionRequest.getExpireDate()));
            memberSubscription.setCreatedAt(LocalDateTime.now());

            MemberSubscription savedSubscription = memberSubscriptionRepository.save(memberSubscription);
            if (savedSubscription == null) {
                log.debug("Add Member Subscription failed: Unable to save the subscription");
                return new ResponseCommon<>(ResponseCode.FAIL, null);
            }
            for (String s : addMemberSubscriptionRequest.getSelectedBenefits()) {
                MemberBenefit memberBenefit = new MemberBenefit(
                    new MemberBenefitKey(savedSubscription.getId(), Integer.parseInt(s)), savedSubscription, benefitsRepository.findById(Integer.parseInt(s)).get());
                memberBenefitRepository.save(memberBenefit);
            }
            // memberBenefitRepository.save()
            AddMemberSubscriptionResponse response = new AddMemberSubscriptionResponse(
                    "Add Member Subscription successful");
            return new ResponseCommon<>(ResponseCode.SUCCESS, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("Add Member Subscription failed: " + e.getMessage());
            return new ResponseCommon<>(ResponseCode.FAIL, null);
        }
    }

    @Override
    public ResponseCommon<UpdateMemberSubscriptionResponse> updateMemberSubscription(
            UpdateMemberSubscriptionRequest updateMemberSubscriptionRequest) {
        try {
            MemberSubscription memberSubscription = memberSubscriptionRepository
                    .findById(updateMemberSubscriptionRequest.getSubId()).orElse(null);
            User user = userRepository.findByEmail(updateMemberSubscriptionRequest.getEmail());

            if (Objects.isNull(memberSubscription)) {
                log.debug("Update MemberSubscription failed: Subscription does not exist");
                return new ResponseCommon<>(ResponseCode.SUBSCRIPTION_NOT_EXIST, null);
            }
            MemberSubscription memberSubscriptionUpdate = memberSubscription;
            memberSubscriptionUpdate.setUserCreated(user);
            memberSubscription.setExpireDate(Integer.parseInt(updateMemberSubscriptionRequest.getExpireDate()));
            memberSubscription.setNameSubscription(updateMemberSubscriptionRequest.getNameSubscription());
            // memberSubscriptionUpdate.setSubscriptionPlan(updateMemberSubscriptionRequest.getSubscriptionPlan());
            memberSubscriptionUpdate.setFeeMember(updateMemberSubscriptionRequest.getFee_member());
            memberSubscription.setMaxBook(updateMemberSubscriptionRequest.getMaxBook());
            // memberSubscriptionUpdate.setMembershipType(updateMemberSubscriptionRequest.getMembershipType());
            // memberSubscriptionUpdate.setStartDate(updateMemberSubscriptionRequest.getStartDate());
            // memberSubscriptionUpdate.setEndDate(updateMemberSubscriptionRequest.getEndDate());
            memberSubscriptionUpdate.setCreatedAt(LocalDateTime.now());

            MemberSubscription updatedSubscription = memberSubscriptionRepository.save(memberSubscription);
            if (updatedSubscription == null) {
                log.debug("Update Member Subscription failed: Unable to update the subscription");
                return new ResponseCommon<>(ResponseCode.FAIL, null);
            }
            for (BenefitDTO b : updateMemberSubscriptionRequest.getSelectedBenefits()) {
                MemberBenefit memberBenefit = new MemberBenefit(
                    new MemberBenefitKey(updatedSubscription.getId(), b.getId()), updatedSubscription, benefitsRepository.findById(b.getId()).get());
                memberBenefitRepository.save(memberBenefit);
            }
            UpdateMemberSubscriptionResponse response = new UpdateMemberSubscriptionResponse(
                    "Update Member Subscription successful");
            return new ResponseCommon<>(ResponseCode.SUCCESS, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("Update Member Subscription failed: " + e.getMessage());
            return new ResponseCommon<>(ResponseCode.FAIL, null);
        }
    }

    @Override
    public ResponseCommon<DeleteMemberSubscriptionResponse> deleteMemberSubscription(
            DeleteMemberSubscriptionRequest deleteMemberSubscriptionRequest) {
        try {
            MemberSubscription memberSubscription = memberSubscriptionRepository
                    .findMemberSubscriptionById(deleteMemberSubscriptionRequest.getSubscriptionId()).orElse(null);
            User user = userRepository.findByEmail(deleteMemberSubscriptionRequest.getEmail());
            // if Member is null -> tell the user
            if (Objects.isNull(memberSubscription)) {
                log.debug("Delete Member Subscription failed: Member Subscription does not exist");
                return new ResponseCommon<>(ResponseCode.SUBSCRIPTION_NOT_EXIST, null);
            } else {
                MemberSubscription memberSubscriptionUpdate = memberSubscription;
                memberSubscriptionUpdate.setDeleted(true);
                memberSubscriptionUpdate.setUserCreated(user);
                memberSubscriptionRepository.save(memberSubscriptionUpdate);

                DeleteMemberSubscriptionResponse deleteMemberSubscriptionResponse = new DeleteMemberSubscriptionResponse();
                deleteMemberSubscriptionResponse.setMessage("Delete Member Subscription successful");
                return new ResponseCommon<>(ResponseCode.SUCCESS, deleteMemberSubscriptionResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("Delete Member Subscription failed: " + e.getMessage());
            return new ResponseCommon<>(ResponseCode.FAIL, null);
        }
    }

    @Override
    public ResponseCommon<EnrollMembershipResponse> enrollMembership(EnrollMembershipRequest enrollMembershipRequest) {
        try {
            EnrollMembershipResponse enrollMembershipResponse = new EnrollMembershipResponse();
            MemberSubscription memberSubscription = memberSubscriptionRepository
                    .findMemberSubscriptionById(enrollMembershipRequest.getMembershipId()).orElse(null);
            User user = userRepository.findByEmail(enrollMembershipRequest.getEmail());
            Order validOrder = orderRepository.getOrderByUserIdAndStatus(user.getId(), "ACTIVE");

            if (validOrder == null) {
                System.out.println(memberSubscription);
                Order order = new Order();
                order.setCreated_at(LocalDateTime.now());
                order.setUser(user);
                order.setMemberSubscription(memberSubscription);
                order.setEnumTypeProcessPayment(EnumTypeProcessPayment.INPROCESS);
                order.setAmount(memberSubscription.getFeeMember());
                order.setStatus("ACTIVE");
                orderRepository.save(order);
                user.setMemberSubscription(memberSubscription);
                userRepository.save(user);

                ResponseCommon<PaymentRes> paymentResponse = paymentService
                        .addPayment(memberSubscription.getFeeMember(), enrollMembershipRequest.getMembershipId());
                if (paymentResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
                    enrollMembershipResponse.setOrderId(order.getId());
                    enrollMembershipResponse.setUrlPayment(paymentResponse.getData().getUrl());
                    order.setChecksum(paymentResponse.getData().getVnp_TxnRef());
                    return new ResponseCommon<>(ResponseCode.SUCCESS.getCode(), "Thanh toán thành công",
                            enrollMembershipResponse);
                } else {
                    log.debug("Enroll member response failed because payment Response not success.");
                    return new ResponseCommon<>(ResponseCode.SEND_URL_PAYMENT_FAIL.getCode(), "Thanh toán thất bại", null);
                }
            }else{
                log.debug("Enroll member response failed because payment Response not success");
                return new ResponseCommon<>(ResponseCode.SUCCESS.getCode(), "Người dùng đã mua gói thành viên", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("Enroll member failed: " + e.getMessage());
            return new ResponseCommon<>(ResponseCode.FAIL, null);
        }
    }

    @Override
    public ResponseCommon<PaymentConfirmResponse> paymentConfirm(PaymentConfirmRequest paymentConfirmRequest) {
        try {
            Order order = orderRepository.getOrderById(paymentConfirmRequest.getOrderId()).orElse(null);
            if (Objects.isNull(order)) {
                return new ResponseCommon<>(ResponseCode.ORDER_NOT_EXIST, null);
            }
            String signValue = generateAndHashQueryString(paymentConfirmRequest);
            System.out.println(signValue);
            String vnp_SecureHash = paymentConfirmRequest.getVnp_SecureHash();
            System.out.println(vnp_SecureHash);
            String checksum = order.getChecksum();
            String vnp_TnxRef = paymentConfirmRequest.getVnp_TxnRef();
            System.out.println(checksum);
            System.out.println(vnp_TnxRef);
            double amountDB = order.getAmount();
            double amountReturn = Double.parseDouble(paymentConfirmRequest.getVnp_Amount());

            if (vnp_SecureHash.isEmpty()) {
                log.debug("Handle with vnp_secureHash: " + vnp_SecureHash);
                return new ResponseCommon<>(ResponseCode.FAIL, null);
            } else if (!signValue.equals(vnp_SecureHash)) {
                return new ResponseCommon<>(ResponseCode.CHANGE_PARAM.getCode(), "Không tìm thấy trang", null);
            } else if (!vnp_TnxRef.equals(checksum)) {
                return new ResponseCommon<>(ResponseCode.ORDER_NOT_FOUND.getCode(), "Không tìm thấy gói đăng ký", null);
            } else if (amountReturn == amountDB) {
                return new ResponseCommon<>(ResponseCode.INVALID_AMOUNT.getCode(), "Số tiền không hợp lệ", null);
            } else if (!order.getEnumTypeProcessPayment().equals(EnumTypeProcessPayment.INPROCESS)) {
                return new ResponseCommon<>(ResponseCode.ORDER_ALREADY_CONFIRM.getCode(), "gói đăng ký đã được xác nhận",
                        null);
            } else if (!paymentConfirmRequest.getVnp_ResponseCode().equals("00")) {
                return new ResponseCommon<>(ResponseCode.USER_CANCEL_BILL.getCode(), "User cancel bill", null);
            }

            // Create the payment only once
            Payment payment = new Payment();
            payment.setUser(order.getUser());
            payment.setMemberSubscription(order.getMemberSubscription());
            payment.setPaymentGateway(EnumPaymentGateway.VN_PAY);
            payment.setAmount(amountReturn);
            payment.setEnumPaymentProcess(EnumPaymentProcess.SUCCESS);
            payment.setTransactionId(order.getChecksum());
            payment.setCreatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            // Update order
            order.setPayment(payment);
            order.setEnumTypeProcessPayment(EnumTypeProcessPayment.DONE);
            orderRepository.save(order);

            return new ResponseCommon<>(ResponseCode.SUCCESS.getCode(), "Thanh toán thành công ", null);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("Enroll member failed: " + e.getMessage());
            return new ResponseCommon<>(ResponseCode.FAIL, null);
        }
    }

    public String generateAndHashQueryString(PaymentConfirmRequest paymentConfirmRequest) {
        Map<String, String> fields = new HashMap<>();
        fields.put("vnp_Amount", paymentConfirmRequest.getVnp_Amount());
        fields.put("vnp_BankCode", paymentConfirmRequest.getVnp_BankCode());
        fields.put("vnp_BankTranNo", paymentConfirmRequest.getVnp_BankTranNo());
        fields.put("vnp_CardType", paymentConfirmRequest.getVnp_CardType());
        fields.put("vnp_OrderInfo", paymentConfirmRequest.getVnp_OrderInfo());
        fields.put("vnp_PayDate", paymentConfirmRequest.getVnp_PayDate());
        fields.put("vnp_ResponseCode", paymentConfirmRequest.getVnp_ResponseCode());
        fields.put("vnp_TmnCode", paymentConfirmRequest.getVnp_TmnCode());
        fields.put("vnp_TransactionNo", paymentConfirmRequest.getVnp_TransactionNo());
        fields.put("vnp_TransactionStatus", paymentConfirmRequest.getVnp_TransactionStatus());
        fields.put("vnp_TxnRef", paymentConfirmRequest.getVnp_TxnRef());

        String queryString = VnPayConfig.hashAllFields(fields);

        return queryString;
    }

    @Override
    public MemberSubscription findById(int id) {
        return memberSubscriptionRepository.findMemberSubscriptionById(id).orElse(null);
    }
}
