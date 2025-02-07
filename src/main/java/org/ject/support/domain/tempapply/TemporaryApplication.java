package org.ject.support.domain.tempapply;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ject.support.external.dynamodb.domain.CompositeKey;
import org.ject.support.external.dynamodb.domain.EntityWithPrimaryKey;
import org.ject.support.external.dynamodb.domain.LocalDateTimeConverter;
import org.ject.support.external.dynamodb.domain.MapConverter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;

@DynamoDBTable(tableName = "temporary_application")
@DynamoDbBean
@NoArgsConstructor
@Setter
@EqualsAndHashCode(callSuper = false)
public class TemporaryApplication extends EntityWithPrimaryKey {
    private static final String PK_PREFIX = "MEMBER";
    private static final String SK_PREFIX = "TIMESTAMP";

    private String memberId;
    private LocalDateTime timestamp;
    private Map<String, String> answers;

    @DynamoDbAttribute(value = "member_id")
    public String getMemberId() {
        return memberId;
    }

    @DynamoDbAttribute(value = "timestamp")
    @DynamoDbConvertedBy(value = LocalDateTimeConverter.class)
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @DynamoDbAttribute(value = "answers")
    @DynamoDbConvertedBy(value = MapConverter.class)
    public Map<String, String> getAnswers() {
        return answers;
    }

    public TemporaryApplication(final String memberId, final Map<String, String> answers) {
        this.memberId = memberId;
        this.timestamp = LocalDateTime.now();
        this.answers = answers;
        this.pk = new CompositeKey(PK_PREFIX, this.memberId);
        this.sk = new CompositeKey(SK_PREFIX, this.timestamp.toString());
    }
}
