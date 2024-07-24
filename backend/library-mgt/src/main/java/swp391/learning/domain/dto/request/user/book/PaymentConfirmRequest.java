package swp391.learning.domain.dto.request.user.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentConfirmRequest {
    @NotNull
    private int orderId;

    @NotBlank
    private String vnp_Amount;

    @NotBlank
    private String vnp_BankCode;

    @NotBlank
    private String vnp_BankTranNo;

    @NotBlank
    private String vnp_CardType;

    @NotBlank
    private String vnp_OrderInfo;

    @NotBlank
    private String vnp_PayDate;

    @NotBlank
    private String vnp_ResponseCode;

    @NotBlank
    private String vnp_TmnCode;

    @NotBlank
    private String vnp_TransactionNo;

    @NotBlank
    private String vnp_TransactionStatus;

    @NotBlank
    private String vnp_TxnRef;

    @NotBlank
    private String vnp_SecureHash;
}
