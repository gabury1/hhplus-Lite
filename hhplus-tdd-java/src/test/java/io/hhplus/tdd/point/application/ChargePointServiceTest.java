package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ChargePointServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;
    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private ChargePointService chargePointService;

    /**
     * 상태 검증 케이스 : 포인트가 없는 유저의 포인트 충전 시 올바른 충전량 반환.
     * <p>
     * 시나리오:
     * - Given: 존재하는 유저의 ID (1L)
     * - When:  chargeUserPoint 호출
     * - Then:  올바른 충전 포인트 반환
     */
    @Test
    @DisplayName("상태 검증 케이스 : 포인트가 없는 유저의 포인트 충전 시 올바른 충전량 반환.")
    void givenNoPointHistoryUser_whenChargeUserPoint_thenRightPoint() {
        // given : 존재하는 유저Id(1L)
        long userId = 1L;
        long chargePoint = 10L;

        UserPoint emptyUserPoint = UserPoint.empty(userId);
        UserPoint chargedUserPoint = new UserPoint(userId, chargePoint, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(emptyUserPoint);
        when(userPointTable.insertOrUpdate(userId, chargePoint)).thenReturn(chargedUserPoint);


        // when : chargeUserPoint 호출
        UserPoint result = chargePointService.chargeUserPoint(userId, chargePoint);

        // then : 충전된 포인트 반환
        assertThat(result).isEqualTo(chargedUserPoint);

    }

    /**
     * 상태 검증 케이스 : 포인트 이력이 없는 유저가 포인트 충전 시 올바른 이력 반환.
     * <p>
     * 시나리오:
     * - Given: 존재하는 유저의 ID (1L)
     * - When:  chargeUserPoint 호출
     * - Then:  올바른 충전 이력 반환
     */
    @Test
    @DisplayName("상태 검증 케이스 : 포인트 이력이 없는 유저가 포인트 충전 시 올바른 이력 반환.")
    void givenNoPointHistoryUser_whenChargeUserPoint_thenRightPointHistory() {
        // given : 존재하는 유저Id(1L)
        long userId = 1L;
        long chargePoint = 10L;

        // Mock 이 없다면 에러를 일으킴.
        UserPoint emptyUserPoint = UserPoint.empty(userId);
        UserPoint chargedUserPoint = new UserPoint(userId, chargePoint, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(emptyUserPoint);
        when(userPointTable.insertOrUpdate(userId, emptyUserPoint.point() + chargePoint)).thenReturn(chargedUserPoint);

        // when : chargeUserPoint 호출
        UserPoint result = chargePointService.chargeUserPoint(userId, chargePoint);

        // then : 포인트 히스토리가 올바르게 호출되었는지 확인
        verify(pointHistoryTable, times(1)).insert(
                eq(userId),
                eq(chargePoint),
                eq(TransactionType.CHARGE),
                anyLong()
        );

    }

}