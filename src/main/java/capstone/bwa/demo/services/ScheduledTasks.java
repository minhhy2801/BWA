package capstone.bwa.demo.services;

import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//tạo 1 class là container để chứa đựng các công việc cần lên lịch trình
@Component
public class ScheduledTasks {
    @Autowired
    private EventRepository eventRepository;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final String waitingPublic = "WAITING_PUBLIC";
    private final String ongoing = "ONGOING";
    private final String closeRegister = "CLOSE_REGISTER";
    private final String hidden = "HIDDEN";
    private final String finished = "FINISHED";

    //Cron chạy method
    //1 day run
//    @Scheduled(cron = "0 0 0 * * ?")
    //1 min run
    @Scheduled(cron = "0 * * ? * *")
    public void scheduleTaskWithCronExpression() {
        updateEventStatus();
        logger.info("Update db");
    }

    private void updateEventStatus() {
        List<String> statusEventShow = new ArrayList<>();
        statusEventShow.add(ongoing);
        statusEventShow.add(closeRegister);
        statusEventShow.add(waitingPublic);
        List<Object[]> listEventsWaiting = eventRepository.findAllPublicTimeAndEndRegisterTime(statusEventShow);
        try {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String strPublic = "";
            String strEndSignUp = "";
            String strEndEvent = "";
            int id;
            for (Object[] event : listEventsWaiting) {
                strPublic = event[1].toString().trim();
                strEndSignUp = event[2].toString().trim();
                strEndEvent = event[3].toString().trim();
                Date publicTime = format.parse(strPublic);
                Date endSignUpTime = format.parse(strEndSignUp);
                Date endEvent = format.parse(strEndEvent);
                id = Integer.parseInt(event[0].toString().trim());
                EventEntity eventEntity = eventRepository.findById(id);
                if (date.compareTo(publicTime) > 0) { //current earlier than public
                    System.out.println("Event " + id + "are public");
                    eventEntity.setStatus(ongoing);
                    eventRepository.save(eventEntity);
                }
                if (date.compareTo(endSignUpTime) > 0) {
                    System.out.println("Event " + id + "end sign up");
                    if (eventEntity.getTotalSoldTicket() < eventEntity.getMinTicket()) {
                        System.out.println("Không đủ điều kiện mở event");
                        eventEntity.setStatus(hidden);
                    } else {
                        System.out.println("Đóng đăng ký rồi nha");
                        eventEntity.setStatus(closeRegister);
                    }
                    eventRepository.save(eventEntity);
                }
                if (date.compareTo(endEvent) > 0) {
                    eventEntity.setStatus(finished);
                    eventRepository.save(eventEntity);
                }
            }
        } catch (ParseException ex) {
            logger.error("Cannot parse");
        } catch (NullPointerException e) {
            logger.error("Cannot compare");
        }
    }

    //Khoảng cách thời gian giữa các lần chạy method
    public void scheduleTaskWithFixedRate() {
    }

    //Khoảng cách thời gian giữa các lần chạy hoàn thành method
    public void scheduleTaskWithFixedDelay() {
    }

    //Thời gian delay cho lần đầu tiên chạy method
    public void scheduleTaskWithInitialDelay() {
    }

}