package com.multimatics.bankflow.account;
import com.multimatics.bankflow.account.api.*; import com.multimatics.bankflow.account.application.AccountApplicationService; import com.multimatics.bankflow.account.domain.*;
import com.multimatics.bankflow.account.exception.DuplicateAccountException; import com.multimatics.bankflow.account.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.Test; import java.math.BigDecimal; import static org.junit.jupiter.api.Assertions.*;
class AccountApplicationServiceTest {
 @Test void createsActiveAccount(){var s=new AccountApplicationService(new InMemoryAccountRepository());var r=s.create(new CreateAccountRequest("100000000001",AccountType.SAVINGS,new BigDecimal("2500000.00"),"IDR"));assertEquals(AccountStatus.ACTIVE,r.status());assertEquals("100000000001",r.accountNumber());}
 @Test void rejectsDuplicateNumber(){var s=new AccountApplicationService(new InMemoryAccountRepository());var q=new CreateAccountRequest("100000000001",AccountType.SAVINGS,BigDecimal.ZERO,"IDR");s.create(q);assertThrows(DuplicateAccountException.class,()->s.create(q));}
}
