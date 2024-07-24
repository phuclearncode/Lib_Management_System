package swp391.learning.application.service.Implements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.learning.application.service.BookCopyService;
import swp391.learning.application.service.LoanService;
import swp391.learning.application.service.MemberSubscriptionService;
import swp391.learning.domain.dto.response.user.rent.RentResponse;
import swp391.learning.domain.entity.BookCopy;
import swp391.learning.domain.entity.Loan;
import swp391.learning.domain.enums.EnumBookStatus;
import swp391.learning.domain.enums.EnumLoanStatus;
import swp391.learning.repository.BookCopyRepository;
import swp391.learning.repository.LoanRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookCopyService bookCopyService;
    @Autowired
    private BookCopyRepository bookCopyRepository;
    @Autowired
    private MemberSubscriptionService memberSubscriptionService;

    @Override
    public void addLoan(Loan loan) {
        loanRepository.save(loan);
    }

    private RentResponse mapLoanToRentResponse(Loan loan) {
        RentResponse rentResponse = RentResponse.builder()
                .loanId(loan.getId())
                .userName(loan.getUser().getUsername())
                .maxBook(memberSubscriptionService.findById(loan.getUser().getMemberSubscription().getId()).getMaxBook())
                .bookCopyResponse(bookCopyService.mapBookCopyToBookCopyResponse(loan.getBookCopy()))
                .memberId(loan.getUser().getMemberSubscription().getId())
                .memFee(memberSubscriptionService.findById(loan.getUser().getMemberSubscription().getId())
                        .getFeeMember())
                .borrowAt(loan.getBorrowAt())
                .returnAt(loan.getReturnAt())
                .dueDate(loan.getDueDate())
                .note(loan.getNote())
                .status(loan.getStatus().name())
                .userId(loan.getUser().getId())
                .price(loan.getPrice())
                .build();
        return rentResponse;
    }

    @Override
    public List<RentResponse> getLoansByUserIdAndActive(int userId) {
        List<RentResponse> list = new ArrayList<>();
        List<String> statusList = Arrays.asList(EnumLoanStatus.ACTIVE.name(), EnumLoanStatus.PENDING.name());
        List<Loan> listLoan = loanRepository.findByUserIdAndStatusIn(userId, statusList);
        
        for (Loan loan : listLoan) {
            list.add(mapLoanToRentResponse(loan));
        }

        return list;
    }

    @Override
    public List<RentResponse> getLoansWithPendingState() {
        List<RentResponse> list = new ArrayList<>();

        List<Loan> listLoan = loanRepository.getLoansByStatus(EnumLoanStatus.PENDING);
        for (Loan loan : listLoan) {
            list.add(mapLoanToRentResponse(loan));
        }

        return list;
    }

    @Override
    public List<RentResponse> getLoansWithReturnState() {
        List<RentResponse> list = new ArrayList<>();

        List<Loan> listLoan = loanRepository.getLoansByStatus(EnumLoanStatus.RETURNED);
        for (Loan loan : listLoan) {
            list.add(mapLoanToRentResponse(loan));
        }

        return list;
    }

    @Override
    public Loan approveLoan(int loanId, int bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId);
        bookCopy.setStatus(EnumBookStatus.ACTIVE);
        bookCopyRepository.save(bookCopy);

        Loan loan = loanRepository.findById(loanId).orElse(null);
        loan.setStatus(EnumLoanStatus.ACTIVE);
        return loanRepository.save(loan);
    }

    @Override
    public List<RentResponse> getLoansWithRejectState() {
        List<RentResponse> list = new ArrayList<>();

        List<Loan> listLoan = loanRepository.getLoansByStatus(EnumLoanStatus.REJECT);
        for (Loan loan : listLoan) {
            list.add(mapLoanToRentResponse(loan));
        }

        return list;
    }

    @Override
    public Loan rejectLoan(int loanId, int bookCopyId, String note) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId);
        bookCopy.setStatus(EnumBookStatus.REJECT);
        bookCopyRepository.save(bookCopy);

        Loan loan = loanRepository.findById(loanId).orElse(null);
        loan.setStatus(EnumLoanStatus.REJECT);
        loan.setNote(note);
        return loanRepository.save(loan);
    }

    @Override
    public Loan returningLoan(int loanId, int bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId);
        bookCopy.setStatus(EnumBookStatus.RETURNING);
        bookCopyRepository.save(bookCopy);

        Loan loan = loanRepository.findById(loanId).orElse(null);
        loan.setStatus(EnumLoanStatus.RETURNING);
        return loanRepository.save(loan);
    }

    @Override
    public Loan returnLoan(int loanId, int bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId);
        bookCopy.setStatus(EnumBookStatus.RETURNED);
        bookCopyRepository.save(bookCopy);

        Loan loan = loanRepository.findById(loanId).orElse(null);
        loan.setStatus(EnumLoanStatus.RETURNED);
        return loanRepository.save(loan);
    }

    @Override
    public List<RentResponse> getLoansWithReturningState() {
        List<RentResponse> list = new ArrayList<>();

        List<Loan> listLoan = loanRepository.getLoansByStatus(EnumLoanStatus.RETURNING);
        for (Loan loan : listLoan) {
            list.add(mapLoanToRentResponse(loan));
        }

        return list;
    }

}
