/*
 * Copyright 2024-2026 Rudy De Busscher (https://www.atbash.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.demo.spring.rest.test;

import be.atbash.demo.spring.rest.dbunit.JpaMetaModelHelper;
import be.atbash.demo.spring.rest.dbunit.TestDataHelper;
import be.atbash.demo.spring.rest.jupiter.TestDataExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(TestDataExtension.class)
@Import(value = JpaMetaModelHelper.class)
public abstract class UpdatingRepositoryTest extends TestDataHelper {

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * Runs statements within a separate Thread and within a transaction. The block must execute within 1 second
     * or there will be an test assertion failure.
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
