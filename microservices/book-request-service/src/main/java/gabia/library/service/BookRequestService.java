package gabia.library.service;

import gabia.library.config.NaverConfig;
import gabia.library.domain.BookRequest;
import gabia.library.domain.BookRequestRepository;
import gabia.library.domain.Status;
import gabia.library.dto.BookRequestDto;
import gabia.library.dto.NaverBook;
import gabia.library.dto.UserBookRequestDto;
import gabia.library.exception.EntityNotFoundException;
import gabia.library.kafka.sender.KafkaBookBuyingMessageSender;
import gabia.library.kafka.sender.KafkaBookRequestMessageSender;
import lombok.RequiredArgsConstructor;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

import static gabia.library.config.CommonUrlPathPrefix.USER_SERVICE_PREFIX;
import static gabia.library.exception.message.CommonExceptionMessage.ENTITY_NOT_FOUND;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Transactional
@Service
public class BookRequestService {

    private HttpEntity<?> headers;
    private final NaverConfig naverConfig;
    private final ModelMapper modelMapper;
    private final BookRequestRepository bookRequestRepository;
    private final RestTemplate restTemplate;
    private final KafkaBookRequestMessageSender kafkaBookRequestMessageSender;
    private final KafkaBookBuyingMessageSender kafkaBookBuyingMessageSender;

    private final static String GET_USER_URL = USER_SERVICE_PREFIX + "/users/";

    public BookRequestDto addBookRequest(BookRequestDto bookRequestDto) {
        BookRequest bookRequest = bookRequestRepository.save(BookRequest.builder()
                .userId(bookRequestDto.getUserId())
                .bookName(bookRequestDto.getBookName())
                .author(bookRequestDto.getAuthor())
                .destination(bookRequestDto.getDestination())
                .url(bookRequestDto.getUrl())
                .status(Status.REQUESTED)
                .thumbNail(bookRequestDto.getThumbnail())
                .publishDate(bookRequestDto.getPublishDate())
                .publisher(bookRequestDto.getPublisher())
                .content(bookRequestDto.getContent())
                .isDeleted(false)
                .build());

        UserBookRequestDto userBookRequestDto = getUserDtoById(bookRequestDto.getUserId()).getBody();

        if (isNull(userBookRequestDto)) {
            throw new EntityNotFoundException(ENTITY_NOT_FOUND);
        }

        /**
         * send return book request event to kafka
         */
        kafkaBookRequestMessageSender.send(userBookRequestDto.toBookRequestMessage(bookRequestDto));

        return modelMapper.map(bookRequest, BookRequestDto.class);
    }

    public ResponseEntity<UserBookRequestDto> getUserDtoById(Long id) {
        return restTemplate.getForEntity(GET_USER_URL + id, UserBookRequestDto.class);
    }

    public NaverBook getBookByNaverApi(String title, Long page) throws NoSuchAlgorithmException,
            KeyStoreException, KeyManagementException {

        String url = naverConfig.geturl(title, page);

        MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();
        requestHeaders.add("X-Naver-Client-Id", naverConfig.getClientId());
        requestHeaders.add("X-Naver-Client-Secret", naverConfig.getSecretId());

        this.headers = new HttpEntity<>(requestHeaders);

        return getRestTemplate().exchange(url, HttpMethod.GET, headers, NaverBook.class).getBody();
    }

    public List<BookRequestDto> findAll(){
        return bookRequestRepository.findAll()
                .stream()
                .map(bookRequest -> modelMapper.map(bookRequest, BookRequestDto.class))
                .collect(Collectors.toList());
    }

    public BookRequestDto confirmBookRequest(Long id) {
        BookRequest bookRequest = bookRequestRepository.findByIdAndIsDeleted(id, false).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        bookRequest.update();

        UserBookRequestDto userBookRequestDto = getUserDtoById(bookRequest.getUserId()).getBody();

        if (isNull(userBookRequestDto)) {
            throw new EntityNotFoundException(ENTITY_NOT_FOUND);
        }

        /**
         * send buy book request event to kafka
         */
        kafkaBookBuyingMessageSender.send(userBookRequestDto.toBookBuyingMessage(bookRequest));

        return modelMapper.map(bookRequest, BookRequestDto.class);
    }

    public BookRequestDto cancelBookRequest(Long id) {
        BookRequest bookRequest = bookRequestRepository.findByIdAndIsDeleted(id, false).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        bookRequest.remove();

        return modelMapper.map(bookRequest, BookRequestDto.class);
    }

    public RestTemplate getRestTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }
}
