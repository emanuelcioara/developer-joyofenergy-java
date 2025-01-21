package uk.tw.energy.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;

public class MeterReadingServiceTest {

    private MeterReadingService meterReadingService;

    @BeforeEach
    public void setUp() {
        meterReadingService = new MeterReadingService(new HashMap<>());
    }

    @Test
    public void givenMeterIdThatDoesNotExistShouldReturnNull() {
        assertThat(meterReadingService.getReadings("unknown-id")).isEqualTo(Optional.empty());
    }

    @Test
    public void givenMeterReadingThatExistsShouldReturnMeterReadings() {
        meterReadingService.storeReadings("random-id", new ArrayList<>());
        assertThat(meterReadingService.getReadings("random-id")).isEqualTo(Optional.of(new ArrayList<>()));
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
