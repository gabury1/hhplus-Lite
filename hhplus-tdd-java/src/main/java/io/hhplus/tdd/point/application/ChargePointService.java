package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class ChargePointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public UserPoint chargeUserPoint(long userId, long amount) {


        UserPoint point = userPointTable.selectById(userId);
        if(amount < 0) {
            // 충전량이 음수면 충전되어선 안됨.
            return point;
        }

        UserPoint chargedPoint = userPointTable.insertOrUpdate(userId, point.point() + amount);

        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis());

        return chargedPoint;
    }



}
