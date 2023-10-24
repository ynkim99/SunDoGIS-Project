package com.gis.mapper;

import com.gis.dto.CarAndDateDto;
import com.gis.dto.OnlyCarNumDto;
import com.gis.dto.StatDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GisStatMapper {

    List<CarAndDateDto> findLocalDb();

    List<OnlyCarNumDto> findCarNum();

    Double findRatio(StatDto YearMonthDate);

    List<StatDto> findCleanTime(StatDto YearMonthDate);

    List<StatDto> findTotalDistance(StatDto YearMonthDate);
    List<StatDto> findNoCleanDistance(StatDto YearMonthDate);


}
