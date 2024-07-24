package swp391.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp391.learning.domain.entity.Loan;
import swp391.learning.domain.enums.EnumLoanStatus;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {
    List<Loan> getLoansByUserId(int userId);

    @Query(value = "SELECT * FROM loan WHERE user_id = :userId AND status IN (:statuses)", nativeQuery = true)
    List<Loan> findByUserIdAndStatusIn(@Param("userId") int userId, @Param("statuses") List<String> statuses);

    List<Loan> getLoansByStatus(EnumLoanStatus status);

    List<Loan> getLoansByBookCopyIdAndStatus(int bookCopyId, EnumLoanStatus status);

    List<Loan> getLoansByBookCopyId(int bookCopyId);
}
