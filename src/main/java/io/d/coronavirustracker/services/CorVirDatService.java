package io.d.coronavirustracker.services;

import io.d.coronavirustracker.models.LocationStates;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

@Service
public class CorVirDatService {

    private static String VIRUS_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStates> allStats=new ArrayList<>();

    public List<LocationStates> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStates> newStats=new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        HttpResponse<String> httpResponce = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(httpResponce.body());
        StringReader csvBodyReader=new StringReader(httpResponce.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStates locationState = new LocationStates();
            locationState.setState(record.get("Province/State"));
            locationState.setCountry(record.get("Country/Region"));
            int LatestCases=(Integer.parseInt(record.get(record.size()-1)));
            int PreviousCases=(Integer.parseInt(record.get(record.size()-2)));
            locationState.setLatestTotalCases(LatestCases);
            locationState.setDiffFromPrevDay(LatestCases-PreviousCases);
            newStats.add(locationState);
        }
        this.allStats=newStats;

    }
}
