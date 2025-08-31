package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReadPointServiceTest {

    @Mock
    private UserPointTable userPointTable; // 외부 의존성을 Mock으로 대체

    @InjectMocks
    private ReadPointService readPointService; // 테스트 대상 객체 (Mock 자동 주입)

    /**
     * 상태 검증 케이스 : 존재하는 유저의 ID로 포인트 조회.
     *
     * 시나리오: 사용자의 ID를 검색 시 올바른 포인트가 반환됨..
     * - Given: 존재하는 사용자(ID : 1L)
     * - When:  getUserPoint(1L) 호출
     * - Then:  expectedUserPoint 반환.
     */
    @Test
    void givenExistingUser_whenGetUserPoint_thenReturnsUserPoint() {

        // given - 테스트 조건 설정
        long userId = 1L;
        long pointAmount = 1000L;
        long updateTime = System.currentTimeMillis();
        UserPoint expectedUserPoint = new UserPoint(userId, pointAmount, updateTime);

        // Mock 동작 정의: selectById(1) 호출 시 expectedUserPoint 반환
        // readPointService 내부의 userPointTable Mock 객체가 expectedUserPoint 를 반환하게 합니다.
        when(userPointTable.selectById(userId)).thenReturn(expectedUserPoint);

        // when - 테스트 실행
        UserPoint result = readPointService.getUserPoint(userId);

        // then - 결과 검증 (행위 중심: 올바른 결과가 반환되는지 확인)
        assertEquals(expectedUserPoint, result);
    }

    /**
     * 상태 검증 케이스 : 존재하지 않는 유저의 ID를 검색 시 빈 유저 포인트 반환.
     *
     * 시나리오: 존재하지 않는 유저의 ID로 조회 시 빈 유저 포인트 (동일 userId, 0L의 포인트) 반환됨.
     * - Given: 존재하지 않는 사용자 ID(999L)
     * - When: getUserPoint(999L) 호출
     * - Then: 빈 userPoint 출력.
     */
    @Test
    void givenNonExistUser_whenGetUserPoint_thenReturnsEmptyUserPoint() {
        // given - 존재하지 않는 사용자에 대한 빈 UserPoint 생성
        long userId = 999L;
        UserPoint emptyUserPoint = UserPoint.empty(userId);
        // Mock 동작 정의: selectById(999) 호출 시 빈 UserPoint 반환
        when(userPointTable.selectById(userId)).thenReturn(emptyUserPoint);

        // when - 테스트 실행
        UserPoint result = readPointService.getUserPoint(userId);

        // then - 상태 검증 (userId는 같게, 포인트는 0이 반환되어야 합니다.)
        assertEquals(userId, result.id());
        assertEquals(0L, result.point());
    }

    /**
     * 행위 검증 케이스: 유저 Id로 조회 시, 조회 매서드 호출 여부 검증.
     * 
     * 시나리오: 유저(존재유무 상관 없음)의 포인트를 조회 시, userPointTable의 selectById가 1회 호출된다.
     * - Given: 유저Id (1L)
     * - When:  getUserPoint(1L) 호출
     * - Then:  selectById가 한번만 호출된다.
     */
    @Test
    void givenUserId_whenGetUserPoint_thenCallsSelectByIdOnce() {
        // given
        long userId = 1L;
        UserPoint mockUserPoint = UserPoint.empty(userId);
        when(userPointTable.selectById(userId)).thenReturn(mockUserPoint);

        // when
        readPointService.getUserPoint(userId);

        // then - 행위 검증: selectById가 정확히 1번 호출되었는지 확인
        verify(userPointTable, times(1)).selectById(userId);
    }
}