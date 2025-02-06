package uk.tw.energy.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.tw.energy.domain.ElectricityReading;

public class MeterReadingServiceTest {

    private MeterReadingService meterReadingService;
    Map<String, List<ElectricityReading>> meterAssociatedReadings;
    private final static String SMART_METER_ID = "random-id";
    private List<ElectricityReading> electricityReadings;


    @BeforeEach
    public void setUp() {
        meterAssociatedReadings = Mockito.mock(Map.class);
        meterReadingService = new MeterReadingService(meterAssociatedReadings);
        electricityReadings = new ArrayList<>();
    }

    @Test
    public void givenMeterIdThatDoesNotExistShouldReturnNull() {
        assertThat(meterReadingService.getReadings("unknown-id")).isEqualTo(Optional.empty());
    }

    @Test
    public void givenMeterReadingThatExistsShouldReturnMeterReadings() {
        List<ElectricityReading> mockList = Mockito.mock(List.class);
        Mockito.when(meterAssociatedReadings.computeIfAbsent(Mockito.eq(SMART_METER_ID), Mockito.any())).thenReturn(mockList);

        meterReadingService.storeReadings(SMART_METER_ID, electricityReadings);

        Mockito.verify(meterAssociatedReadings).computeIfAbsent(Mockito.eq(SMART_METER_ID), Mockito.any());
        Mockito.verify(mockList).addAll(electricityReadings);
    }

    @Test
    public void givenMeterIdAndReadingNotEmptyShouldReturnMeterReadings() {
        List<ElectricityReading> expectedReadings = List.of(
                new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
        meterReadingService.storeReadings("random-id", expectedReadings);
        assertThat(meterReadingService.getReadings("random-id")).hasValue(expectedReadings);
        assertThat(meterReadingService.getReadings("random-id")).hasValueSatisfying(readings -> assertThat(readings.size()).isEqualTo(expectedReadings.size()));
    }
}
