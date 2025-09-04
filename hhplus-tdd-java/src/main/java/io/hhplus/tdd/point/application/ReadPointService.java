package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class ReadPointService {

    private final UserPointTable userPointTable;


    public UserPoint getUserPoint(long userId) {

        return userPointTable.selectById(userId);
    }
}
