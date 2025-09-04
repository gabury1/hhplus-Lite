package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsePointServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;
    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private UsePointService usePointService;

    /**
     * 상태 검증 케이스 : 충분한 포인트가 있는 유저의 포인트 사용 시 올바른 차감량 반환.
     *
     * 시나리오:
     * - Given: 포인트가 있는 유저의 ID (1L), 기존 포인트 100L
     * - When:  useUserPoint 호출 (사용량 30L)
     * - Then:  기존 포인트 - 사용량 = 70L 반환
     */
    @Test
    @DisplayName("상태 검증 케이스 : 충분한 포인트가 있는 유저의 포인트 사용 시 올바른 차감량 반환.")
    void givenSufficientPointUser_whenUseUserPoint_thenRightPointDeduction() {
        // given : 충분한 포인트가 있는 유저Id(1L)
        long userId = 1L;
        long existingPoint = 100L;
        long usePoint = 30L;
        long expectedPoint = existingPoint - usePoint;

        UserPoint existingUserPoint = new UserPoint(userId, existingPoint, System.currentTimeMillis());
        UserPoint usedUserPoint = new UserPoint(userId, expectedPoint, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(existingUserPoint);
        when(userPointTable.insertOrUpdate(userId, expectedPoint)).thenReturn(usedUserPoint);

        // when : useUserPoint 호출
        UserPoint result = usePointService.useUserPoint(userId, usePoint);

        // then : 차감된 포인트 반환
        assertThat(result).isEqualTo(usedUserPoint);
        assertThat(result.point()).isEqualTo(expectedPoint); // 70L
    }

    /**
     * 상태 검증 케이스 : 잔액이 부족한 유저의 포인트 사용 시 기존 포인트 그대로 반환.
     *
     * 시나리오:
     * - Given: 포인트가 부족한 유저의 ID (1L), 기존 포인트 50L
     * - When:  useUserPoint 호출 (사용량 100L)
     * - Then:  기존 포인트 그대로 반환 (50L)
     */
    @Test
    @DisplayName("상태 검증 케이스 : 잔액이 부족한 유저의 포인트 사용 시 기존 포인트 그대로 반환.")
    void givenInsufficientPointUser_whenUseUserPoint_thenNoPointChange() {
        // given : 잔액이 부족한 유저Id(1L)
        long userId = 1L;
        long existingPoint = 50L;
        long usePoint = 100L;

        UserPoint existingUserPoint = new UserPoint(userId, existingPoint, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(existingUserPoint);

        // when : useUserPoint 호출
        UserPoint result = usePointService.useUserPoint(userId, usePoint);

        // then : 기존 포인트 그대로 반환
        assertThat(result).isEqualTo(existingUserPoint);
        assertThat(result.point()).isEqualTo(existingPoint); // 50L
    }

    /**
     * 상태 검증 케이스 : 음수 사용 시 포인트 변화가 없어야 함.
     *
     * 시나리오:
     * - Given: 기존 포인트가 있는 유저의 ID (1L), 기존 포인트 50L
     * - When:  useUserPoint 호출 (사용량 -10L)
     * - Then:  기존 포인트 그대로 반환 (50L)
     */
    @Test
    @DisplayName("상태 검증 케이스 : 음수 사용 시 포인트 변화가 없어야 함.")
    void givenNegativeAmount_whenUseUserPoint_thenNoPointChange() {
        // given : 기존 포인트가 있는 유저Id(1L)
        long userId = 1L;
        long existingPoint = 50L;
        long negativeUsePoint = -10L;

        UserPoint existingUserPoint = new UserPoint(userId, existingPoint, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(existingUserPoint);

        // when : useUserPoint 호출
        UserPoint result = usePointService.useUserPoint(userId, negativeUsePoint);

        // then : 기존 포인트 그대로 반환
        assertThat(result).isEqualTo(existingUserPoint);
        assertThat(result.point()).isEqualTo(existingPoint);
    }

    /**
     * 행위 검증 케이스: 정상 사용 시 포인트 히스토리가 저장되어야 함.
     *
     * 시나리오:
     * - Given: 충분한 포인트가 있는 유저의 ID (1L), 기존 포인트 100L
     * - When:  useUserPoint 호출 (사용량 30L)
     * - Then:  pointHistoryTable.insert가 1번 호출되어야 함
     */
    @Test
    @DisplayName("행위 검증 케이스: 정상 사용 시 포인트 히스토리가 저장되어야 함.")
    void givenSufficientPoint_whenUseUserPoint_thenHistoryInserted() {
        // given : 충분한 포인트가 있는 유저Id(1L)
        long userId = 1L;
        long existingPoint = 100L;
        long usePoint = 30L;
        long expectedPoint = existingPoint - usePoint;

        UserPoint existingUserPoint = new UserPoint(userId, existingPoint, System.currentTimeMillis());
        UserPoint usedUserPoint = new UserPoint(userId, expectedPoint, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(existingUserPoint);
        when(userPointTable.insertOrUpdate(userId, expectedPoint)).thenReturn(usedUserPoint);

        // when : useUserPoint 호출
        UserPoint result = usePointService.useUserPoint(userId, usePoint);

        // then : pointHistoryTable.insert가 올바른 파라미터로 호출되어야 함
        verify(pointHistoryTable, times(1)).insert(
                eq(userId),
                eq(usePoint),
                eq(TransactionType.USE),
                anyLong()
        );
    }

    /**
     * 행위 검증 케이스: 잔액 부족 시 포인트 히스토리가 저장되지 않아야 함.
     *
     * 시나리오:
     * - Given: 포인트가 부족한 유저의 ID (1L), 기존 포인트 50L
     * - When:  useUserPoint 호출 (사용량 100L)
     * - Then:  pointHistoryTable.insert가 0번 호출되어야 함
     */
    @Test
    @DisplayName("행위 검증 케이스: 잔액 부족 시 포인트 히스토리가 저장되지 않아야 함.")
    void givenInsufficientPoint_whenUseUserPoint_thenNoHistoryInsert() {
        // given : 잔액이 부족한 유저Id(1L)
        long userId = 1L;
        long existingPoint = 50L;
        long usePoint = 100L;

        UserPoint existingUserPoint = new UserPoint(userId, existingPoint, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(existingUserPoint);

        // when : useUserPoint 호출
        UserPoint result = usePointService.useUserPoint(userId, usePoint);

        // then : pointHistoryTable.insert가 호출되지 않아야 함
        verify(pointHistoryTable, times(0)).insert(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }

    /**
     * 행위 검증 케이스: 음수 사용 시 포인트 히스토리가 저장되지 않아야 함.
     *
     * 시나리오:
     * - Given: 기존 포인트가 있는 유저의 ID (1L), 기존 포인트 50L
     * - When:  useUserPoint 호출 (사용량 -10L)
     * - Then:  pointHistoryTable.insert가 0번 호출되어야 함
     */
    @Test
    @DisplayName("행위 검증 케이스: 음수 사용 시 포인트 히스토리가 저장되지 않아야 함.")
    void givenNegativeAmount_whenUseUserPoint_thenNoHistoryInsert() {
        // given : 기존 포인트가 있는 유저Id(1L)
        long userId = 1L;
        long existingPoint = 50L;
        long negativeUsePoint = -10L;

        UserPoint existingUserPoint = new UserPoint(userId, existingPoint, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(existingUserPoint);

        // when : useUserPoint 호출
        UserPoint result = usePointService.useUserPoint(userId, negativeUsePoint);

        // then : pointHistoryTable.insert가 호출되지 않아야 함
        verify(pointHistoryTable, times(0)).insert(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }

}
