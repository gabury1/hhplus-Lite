package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class UsePointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public UserPoint useUserPoint(long userId, long amount) {
        UserPoint point = userPointTable.selectById(userId);

        if (amount < 0) {
            // 사용량이 음수면 사용되어선 안됨.
            return point;
        }

        if (point.point() < amount) {
            // 잔액이 부족하면 사용되어선 안됨.
            return point;
        }

        UserPoint usedPoint = userPointTable.insertOrUpdate(userId, point.point() - amount);

        pointHistoryTable.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());

        return usedPoint;
    }
}
