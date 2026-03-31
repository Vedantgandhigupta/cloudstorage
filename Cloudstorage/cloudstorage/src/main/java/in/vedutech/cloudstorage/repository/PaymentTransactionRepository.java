package in.vedutech.cloudstorage.repository;

import in.vedutech.cloudstorage.document.PaymentTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction, String> {
}
