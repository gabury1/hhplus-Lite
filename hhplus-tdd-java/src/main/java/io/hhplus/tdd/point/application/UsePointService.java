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
        return null;
    }
}
