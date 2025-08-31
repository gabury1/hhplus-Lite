package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadPointHistoryServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private ReadPointHistoryService readPointHistoryService;


    @Test
    @DisplayName("상태 검증 케이스: 존재하는 유저의 포인트 이력 조회 시 올바른 이력 목록 반환")
    void getPointHistory_ExistingUser_ReturnsCorrectHistoryList() {

        /**
         * 상태 검증 케이스 : 존재하는 유저의 포인트 이력 조회 시 올바른 이력 목록 반환
         *
         * 시나리오: 존재하는 유저의 Id로 조회 시, expectedHistories 를 반환한다.
         * - Given: 존재하는 유저의 ID (1L)
         * - When:  selectAllByUserId 호출 시 expectedHistories 반환
         * - Then:  expectedHistories 와 동일한 결괏괎 반환.
         */

        // Given : 존재하는 유저 1L
        Long userId = 1L;

        // 반환할 expectedHistories 입니다.
        List<PointHistory> expectedHistories = List.of(
                new PointHistory(1L, userId, 100L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, 50L, TransactionType.USE, System.currentTimeMillis())
        );
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(expectedHistories);

        // When : getPointHistory 호출
        List<PointHistory> result = readPointHistoryService.getPointHistory(userId);

        // Then : expectedHistories 와 동일한 결괏값 반환
        assertThat(result).isEqualTo(expectedHistories);
    }

    @Test
    @DisplayName("상태 검증 케이스 : 존재하지 않는 유저를 조회할 시에 텅 빈 List 를 반환합니다.")
    void getPointHistory_NonExistingUser_ReturnsEmptyList() {
        /**
         * 상태 검증 케이스 : 존재하지 않는 유저를 조회할 시에 텅 빈 List 를 반환합니다.
         *
         * 시나리오: 존재하지 않는 유저 조회 시 텅 빈 List 반환
         * - Given: 존재하지 않는 유저Id(999L)
         * - When:  getPointHistory 호출 시 텅 빈 List 반환
         * - Then: 결괏값이 비어있어야함.
         */
        
        // Given : 존재하지 않는 유저Id
        Long userId = 999L;

        // selectAllByUserId가 빈 List를 반환하게 함.
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(List.of());

        // When : getPointHistory 호출
        List<PointHistory> result = readPointHistoryService.getPointHistory(userId);

        // Then : 결괏값이 비어있어야 합니다.
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("상태 검증 케이스 : 여러 이력을 가진 유저의 Id로 검색 시 모든 이력이 조회 되어야합니다.")
    void getPointHistory_UserWithMultipleHistories_ReturnsAllHistories() {
        /**
         * 상태 검증 케이스 : 여러 이력을 가진 유저의 Id로 검색 시 모든 이력이 조회 되어야합니다.
         *
         * 시나리오: 4개의 이력을 가진 유저의 Id 를 조회 시 4개의 이력이 반환되어야 합니다.
         * - Given: 존재하는 유저Id(1L), 4개의 포인트 이력
         * - When: selectAllByUserId 호출 시 multipleHistories 가 반환.
         * - Then: 결괏값 이력이 4개의 원소를 갖고 있어야 함.
         */
        
        // Given : 존재하는 유저 ID(1L), 4개의 이력을 가진 expectedHistories
        Long userId = 1L;
        List<PointHistory> multipleHistories = List.of(
                new PointHistory(1L, userId, 100L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, 30L, TransactionType.USE, System.currentTimeMillis()),
                new PointHistory(3L, userId, 200L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(4L, userId, 50L, TransactionType.USE, System.currentTimeMillis())
        );

        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(multipleHistories);

        // When : getPointHistory 호출
        List<PointHistory> result = readPointHistoryService.getPointHistory(userId);

        // Then : 결괏값의 원소가 4개여야 함.
        assertThat(result).hasSize(4);
    }

}