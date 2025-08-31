package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadPointHistoryService
{
    private final PointHistoryTable pointHistoryTable;

    public List<PointHistory> getPointHistory(Long userId)
    {
        return null;
    }

}
