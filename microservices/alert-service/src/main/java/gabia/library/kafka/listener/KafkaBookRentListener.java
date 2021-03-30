package gabia.library.kafka.listener;

import gabia.library.common.MailUtils;
import gabia.library.domain.Alert;
import gabia.library.domain.AlertRepository;
import gabia.library.kafka.BookRentMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

import static gabia.library.common.MailUtils.BOOK_RENT_ALERT_MAIL_TEMPLATES_PATH;
import static gabia.library.common.MailUtils.MAIL_TEMPLATES_PREFIX_PATH;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaBookRentListener {

    private final AlertRepository alertRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final MailUtils mailUtils;

    /**
     * add alert data about book rent event
     */
    @Transactional
    @KafkaListener(topics = "${kafka.topic.rent.name}",
            groupId = "${kafka.consumer.rent.alert.groupName}",
            containerFactory = "bookRentAlertListenerContainerFactory")
    public void addBookRentAlert(@Payload BookRentMessage message,
                                 @Headers MessageHeaders messageHeaders) {


        alertRepository.save(Alert.builder()
                .identifier(message.getIdentifier())
                .email(message.getEmail())
                .title("gabia library [" + message.getAlertType().value() + "] 알람 메일입니다.")
                .message("도서대여 알람 메시지 입니다.")
                .alertType(message.getAlertType())
                .build());
    }

    /**
     * send mail about book rent event
     */
    @KafkaListener(topics = "${kafka.topic.rent.name}",
            groupId = "${kafka.consumer.rent.mail.groupName}",
            containerFactory = "bookRentMailListenerContainerFactory")
    public void sendBookRentMail(@Payload BookRentMessage message,
                                 @Headers MessageHeaders messageHeaders) throws Exception {
        Context context = getBookRentMailContext(message);

        String mailString = templateEngine.process(MAIL_TEMPLATES_PREFIX_PATH + BOOK_RENT_ALERT_MAIL_TEMPLATES_PATH, context);

        MimeMessage mimeMessage = mailUtils.getMimeMessage(message.getEmail(), message.getAlertType().value(), mailString, javaMailSender);

        javaMailSender.send(mimeMessage);

        log.info("메일 전송에 성공했습니다.");
    }

    private Context getBookRentMailContext(BookRentMessage message) {
        Context context = new Context();
        context.setVariable("identifier", message.getIdentifier());
        context.setVariable("message", "해당 도서를 대여하셨습니다.");
        context.setVariable("bookTitle", message.getBookTitle());
        context.setVariable("bookAuthor", message.getBookAuthor());
        context.setVariable("rentExpiredDate", message.getRentExpiredDate());

        return context;
    }

}
