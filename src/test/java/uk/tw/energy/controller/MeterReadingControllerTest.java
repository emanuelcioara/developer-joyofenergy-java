package uk.tw.energy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

@ExtendWith(MockitoExtension.class)
public class MeterReadingControllerTest {

    private static final String SMART_METER_ID = "10101010";

    @InjectMocks
    private MeterReadingController meterReadingController;
    @Mock
    private MeterReadingService meterReadingService;

    @Test
    public void givenNoMeterIdIsSuppliedWhenStoringShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadings(null, Collections.emptyList());
        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenEmptyMeterReadingShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, Collections.emptyList());
        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Test
    public void givenNullReadingsAreSuppliedWhenStoringShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, null);
        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenNullMeterReadingsWhenStoringShouldReturnErrorResponse() {
        assertThat(meterReadingController.storeReadings(null).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Test
    public void givenNullSmartMeterIdAndExistingElectricityReadingsWhenStoringShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadingsBuilder()
                .setSmartMeterId(null)
                .generateElectricityReadings()
                .build();

        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenMeterReadingsShouldStoreAndReturnOKStatus() {
        MeterReadings meterReadings = new MeterReadingsBuilder()
                .setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        ResponseEntity response = meterReadingController.storeReadings(meterReadings);

        Mockito.verify(meterReadingService).storeReadings(SMART_METER_ID, meterReadings.electricityReadings());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenMeterReadingsAssociatedWithTheUserShouldReturnUserReading() {
        MeterReadings meterReadings = new MeterReadingsBuilder()
                .setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        Mockito.when(meterReadingService.getReadings(SMART_METER_ID)).thenReturn(Optional.of(meterReadings.electricityReadings()));

        ResponseEntity response = meterReadingController.readReadings(SMART_METER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(meterReadings.electricityReadings());
    }

    @Test
    public void givenMeterIdThatIsNotRecognisedShouldReturnNotFound() {
        Mockito.when(meterReadingService.getReadings(SMART_METER_ID)).thenReturn(Optional.empty());
        var result = meterReadingController.readReadings(SMART_METER_ID);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenMeterIdThatIsRecognisedAndReadingsEmptyShouldReadings() {
        Mockito.when(meterReadingService.getReadings(SMART_METER_ID)).thenReturn(Optional.of(Collections.emptyList()));

        ResponseEntity response = meterReadingController.readReadings(SMART_METER_ID);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isEqualTo(Collections.emptyList());
    }

}
