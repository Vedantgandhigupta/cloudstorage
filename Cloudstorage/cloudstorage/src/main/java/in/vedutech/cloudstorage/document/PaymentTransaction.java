package in.vedutech.cloudstorage.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "payment_transactions")
public class PaymentTransaction {

    @Id
    private String id;
}
