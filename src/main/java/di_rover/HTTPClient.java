package di_rover;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.util.List;

public class HTTPClient {
    private static final String REMOTE_SERVICE_URI = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";
    private static final ObjectMapper mapper = new ObjectMapper();


    public static void start() throws IOException {

        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(30))
                .setRedirectsEnabled(false)
                .build();

        try (PoolingHttpClientConnectionManager poolingManager = new PoolingHttpClientConnectionManager()
        ) {
            poolingManager.setMaxTotal(1);
            poolingManager.setDefaultMaxPerRoute(1);

            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(poolingManager)
                    .setUserAgent("My Test Programm")
                    .setDefaultRequestConfig(config)
                    .build()) {

                HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
                request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

                try (CloseableHttpResponse response = httpClient.execute(request)) {

                    List<Cat> cats = mapper.readValue(
                                    response.getEntity().getContent(),
                                    new TypeReference<List<Cat>>() {
                                    }
                            ).stream()
                            .filter(cat -> cat.getUpvotes() > 0)
                            .toList();

                    cats.forEach(System.out::println);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
