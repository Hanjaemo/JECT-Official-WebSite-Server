package org.ject.support.external.dynamodb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ject.support.domain.recruit.dto.ApplyPortfolioDto;
import org.ject.support.domain.tempapply.domain.TemporaryApplication;
import org.ject.support.domain.tempapply.repository.TemporaryApplicationRepository;
import org.ject.support.external.dynamodb.domain.CompositeKey;
import org.ject.support.testconfig.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TemporaryApplicationRepositoryTest {

    @Autowired
    private TemporaryApplicationRepository temporaryApplicationRepository;

    @AfterEach
    void tearDown() {
        temporaryApplicationRepository.deleteAll();
    }

    @Order(1)
    @Test
    @DisplayName("dynamodb repository save test")
    void dynamodb_save() {
        // given
        TemporaryApplication temporaryApplication = createTemporaryApplication(
                "1",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1")));

        // when
        temporaryApplicationRepository.save(temporaryApplication);

        // then
        Optional<TemporaryApplication> optional = temporaryApplicationRepository.findByPartitionKeyAndSortKey(
                temporaryApplication.getPk(), temporaryApplication.getSk());
        assertThat(optional).isPresent();
        TemporaryApplication saved = optional.get();
        assertThat(saved).isEqualTo(temporaryApplication);
    }

    @Order(2)
    @Test
    @DisplayName("dynamodb repository find by partition key test")
    void find_by_partition_key() {
        // given
        temporaryApplicationRepository.save(createTemporaryApplication(
                "1",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "1",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "1",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "2",
                Map.of("key", "value"),
                "FE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "2",
                Map.of("key", "value"),
                "FE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "3",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "4",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));

        // when
        String prefix = "MEMBER";
        List<TemporaryApplication> members1 = temporaryApplicationRepository.findByPartitionKey(
                new CompositeKey(prefix, "1"));

        // then
        assertThat(members1).hasSize(3);
        assertThat(members1).allMatch(temporaryApplication -> temporaryApplication.getMemberId().equals("1"));
        assertThat(members1).isSortedAccordingTo(Comparator.comparing(TemporaryApplication::getTimestamp));

        List<TemporaryApplication> members2 = temporaryApplicationRepository.findByPartitionKey(
                new CompositeKey(prefix, "2"));
        assertThat(members2).hasSize(2);
        assertThat(members2).allMatch(temporaryApplication -> temporaryApplication.getMemberId().equals("2"));
        assertThat(members2).isSortedAccordingTo(Comparator.comparing(TemporaryApplication::getTimestamp));
    }

    @Order(3)
    @Test
    @DisplayName("dynamodb repository find by partition with sort type test")
    void find_by_partition_with_sort_type() {
        // given
        temporaryApplicationRepository.save(createTemporaryApplication(
                "1",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "1",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "1",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "2",
                Map.of("key", "value"),
                "FE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "2",
                Map.of("key", "value"),
                "FE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "3",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(createTemporaryApplication(
                "4",
                Map.of("key", "value"),
                "BE",
                List.of(createApplyTemporaryPortfolio("1"))));

        // when
        String prefix = "TIMESTAMP";
        List<TemporaryApplication> members1 = temporaryApplicationRepository.findByPartitionWithSortType(
                new CompositeKey("MEMBER", "1"), prefix);
        // then
        assertThat(members1).hasSize(3);
        assertThat(members1).allMatch(temporaryApplication -> temporaryApplication.getMemberId().equals("1"));
        assertThat(members1).isSortedAccordingTo(Comparator.comparing(TemporaryApplication::getTimestamp));

        List<TemporaryApplication> members2 = temporaryApplicationRepository
                .findByPartitionWithSortType(new CompositeKey("MEMBER", "2"), prefix);
        assertThat(members2).hasSize(2);
        assertThat(members2).allMatch(temporaryApplication -> temporaryApplication.getMemberId().equals("2"));
        assertThat(members2).isSortedAccordingTo(Comparator.comparing(TemporaryApplication::getTimestamp));
    }

    @Order(4)
    @Test
    @DisplayName("dynamodb repository delete by member semesterId test")
    void delete_by_member_id() {
        // given
        temporaryApplicationRepository.save(createTemporaryApplication("1", Map.of(
                "8", "answer 1-1 for 8",
                "9", "answer 1-1 for 9",
                "10", "answer 1-1 for 10"), "BE", List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(new TemporaryApplication("1", Map.of(
                "8", "answer 1-2 for 8",
                "9", "answer 1-2 for 9",
                "10", "answer 1-2 for 10"), "BE", List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(new TemporaryApplication("1", Map.of(
                "8", "answer 1-3 for 8",
                "9", "answer 1-3 for 9",
                "10", "answer 1-3 for 10"), "BE", List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(new TemporaryApplication("2", Map.of(
                "8", "answer 2-1 for 8",
                "9", "answer 2-1 for 9",
                "10", "answer 2-1 for 10"), "BE", List.of(createApplyTemporaryPortfolio("1"))));
        temporaryApplicationRepository.save(new TemporaryApplication("2", Map.of(
                "8", "answer 2-2 for 8",
                "9", "answer 2-2 for 9",
                "10", "answer 2-2 for 10"), "BE", List.of(createApplyTemporaryPortfolio("1"))));

        // when
        temporaryApplicationRepository.deleteByPartitionKey(new CompositeKey("MEMBER", "1"));

        // then
        List<TemporaryApplication> temporaryApplicationsByMemberId1 =
                temporaryApplicationRepository.findByPartitionKey(new CompositeKey("MEMBER", "1"));
        assertThat(temporaryApplicationsByMemberId1).isEmpty();

        List<TemporaryApplication> temporaryApplicationsByMemberId2 =
                temporaryApplicationRepository.findByPartitionKey(new CompositeKey("MEMBER", "2"));
        assertThat(temporaryApplicationsByMemberId2).hasSize(2);
    }

    private TemporaryApplication createTemporaryApplication(String memberId,
                                                            Map<String, String> answers,
                                                            String jobFamily,
                                                            List<ApplyPortfolioDto> portfolios) {
        return new TemporaryApplication(memberId, answers, jobFamily, portfolios);
    }

    private ApplyPortfolioDto createApplyTemporaryPortfolio(String sequence) {
        return new ApplyPortfolioDto("url", "name", "10202", sequence);
    }
}
