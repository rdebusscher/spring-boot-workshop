package be.atbash.demo.spring.rest.test;

import be.atbash.demo.spring.rest.jupiter.TestDataExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(TestDataExtension.class)
public abstract class UpdatingRepositoryTest {

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * Runs statements within a separate Thread and within a transaction. The block must execute within 1 second
     * or there will be an test assertion failure.
     *
     * @param statementBlock Block of statements to be executed
     * @throws InterruptedException Since we are running a separate thread.
     */
    protected void runInTransaction(StatementBlock statementBlock) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // The separate thread makes sure we use a different Hibernate session and thus are independent
        // of the test.
        new Thread(() -> {
            transactionTemplate.execute((status) -> {
                try {
                    statementBlock.execute();
                } catch (Throwable ex) {
                    Assertions.fail(ex.getMessage());
                }
                return null;
            });
            countDownLatch.countDown();

        }).start();

        Assertions.assertThat(countDownLatch.await(1, TimeUnit.SECONDS)).as("Timeout waiting for transaction to complete.").isTrue();

    }
}
