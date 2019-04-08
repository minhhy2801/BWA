package capstone.bwa.demo.services;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.*;
import capstone.bwa.demo.repositories.*;
import capstone.bwa.demo.utils.StringCompareUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class NotificationService {
    @Autowired
    private SupplyProductRepository supplyProductRepository;
    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private RequestNotificationRepository requestNotificationRepository;
    @Autowired
    private RequestProductRepository requestProductRepository;
    @Autowired
    private AccessoryRepository accessoryRepository;

    //still ambitious
    private double compareBikeEntityAndRequest(Map<String, Object> requestBike, BikeEntity bikeEntity) {
        //initial comparation string
        String requestBikeCompare = String.format("%s %s %s", requestBike.get("name"), requestBike.get("brand"), requestBike.get("version"));
        String bikeEntityComparare = String.format("%s %s %s", bikeEntity.getName(), bikeEntity.getBrand(), bikeEntity.getVersion());

        //calculate point
        double point = StringCompareUtils.calculateSimilarity(requestBikeCompare, bikeEntityComparare) * 100;

        if (point >= MainConstants.COMPARISON_POINT) {
            requestBikeCompare = String.format("%s %s", requestBike.get("state"), requestBike.get("desc"));
            bikeEntityComparare = bikeEntity.getDescription();

            // tính dựa vào hệ bất phương trình
            if (point < StringCompareUtils.calculateSimilarity(requestBikeCompare, bikeEntityComparare)) {
                point = ((point * 0.7) + StringCompareUtils.calculateSimilarity(requestBikeCompare, bikeEntityComparare) * 0.3) * 100;
            }
        }
        return point;
    }

    //=======================
    private double compareAccessoryEntityAndRequest(Map<String, Object> requestAccessory, AccessoryEntity accessoryEntity) {
        //initial comparation string
        String requestComparationString = String.format("%s %s", requestAccessory.get("name"), requestAccessory.get("brand"));
        String accesssoryEntityComparationString = String.format("%s %s", accessoryEntity.getName(), accessoryEntity.getBrand());

        //calculate point
        double point = StringCompareUtils.calculateSimilarity(requestComparationString, accesssoryEntityComparationString) * 100;
        System.out.println("POINT " + point);
        if (point >= MainConstants.COMPARISON_POINT) {

            // re-initial the comparation string
            requestComparationString = String.format("%s %s", requestAccessory.get("bikeCompatible"), requestAccessory.get("state"));
            accesssoryEntityComparationString = accessoryEntity.getDescription();

            // tính dựa vào hệ bất phương trình
            if (point < StringCompareUtils.calculateSimilarity(requestComparationString, accesssoryEntityComparationString)) {
                point = ((point * 0.7) + StringCompareUtils.calculateSimilarity(requestComparationString, accesssoryEntityComparationString) * 0.3) * 100;
            }
        }
        return point;
    }

    //init the notification object based on the tree map and save to db
    private void addRequestNotificationFromBike(TreeMap<Double, BikeEntity> treeMap, RequestProductEntity requestProductEntity) {
        List<RequestNotificationEntity> notis = new ArrayList<>();

        for (Map.Entry<Double, BikeEntity> entry : treeMap.entrySet()) {
            BikeEntity bike = entry.getValue();
            RequestNotificationEntity noti = new RequestNotificationEntity();
            if (bike.getUrl() != null) {
                noti.setStatus(MainConstants.NOTI_NEW);
                noti.setCategoryId(bike.getCategoryId());
                noti.setRequestProductId(requestProductEntity.getId());
                noti.setType(MainConstants.STATUS_BIKE + "-" + bike.getId());
                noti.setDescription(entry.getKey().toString());
                notis.add(noti);
            } else {
                noti = prepareNotification(MainConstants.NOTI_NEW, bike.getCategoryByCategoryId().getId(),
                        supplyProductRepository.findByItemId(bike.getId()).getId(), requestProductEntity.getId());
                noti.setDescription(entry.getKey().toString());

                notis.add(noti);
            }
        }
        if (notis.size() > 0)
            requestNotificationRepository.saveAll(notis);
    }


    private void addRequestNotificationFromAccessory(TreeMap<Double, AccessoryEntity> treeMap, RequestProductEntity requestProductEntity) {
        List<RequestNotificationEntity> notis = new ArrayList<>();

        for (Map.Entry<Double, AccessoryEntity> entry : treeMap.entrySet()) {
            AccessoryEntity accessory = entry.getValue();
            RequestNotificationEntity noti = new RequestNotificationEntity();
            if (accessory.getUrl() != null) {
                noti.setStatus(MainConstants.NOTI_NEW);
                noti.setCategoryId(accessory.getCategoryId());
                noti.setRequestProductId(requestProductEntity.getId());
                noti.setType(MainConstants.STATUS_ACCESSORY + "-" + accessory.getId());
                noti.setDescription(entry.getKey().toString());
                notis.add(noti);
            } else {
                noti = prepareNotification(MainConstants.NOTI_NEW, accessory.getCategoryByCategoryId().getId(),
                        supplyProductRepository.findByItemId(accessory.getId()).getId(), requestProductEntity.getId());
                noti.setDescription(entry.getKey().toString());
                notis.add(noti);
            }
        }
        requestNotificationRepository.saveAll(notis);
    }

    //search after
    //create/update request
    public void searchAfterActionRequest(RequestProductEntity requestProductEntity, Map<String, Object> body) {
        String type = body.get("type").toString();
        if (type.equals(MainConstants.STATUS_BIKE)) {
            TreeMap<Double, BikeEntity> treeMap = new TreeMap<>();

            //find all AVAILABLE bikes
            List<BikeEntity> bikes = bikeRepository.findAllBikesWhichSupplyPostStillPublic(MainConstants.SUPPLY_POST_PUBLIC);
            List<BikeEntity> bikesCrawl = bikeRepository.findAllByUrlIsNotNull();
            for (BikeEntity item : bikes) {
                double point = compareBikeEntityAndRequest(body, item);
                if (point >= MainConstants.COMPARISON_POINT) {
                    treeMap.put(point, item);
                }
            }
            for (BikeEntity item : bikesCrawl) {
                double point = compareBikeEntityAndRequest(body, item);
                if (point >= (MainConstants.COMPARISON_POINT + 10)) {
                    treeMap.put(point, item);
                }
            }

            addRequestNotificationFromBike(treeMap, requestProductEntity);
        } else if (type.equals(MainConstants.STATUS_ACCESSORY)) {
            TreeMap<Double, AccessoryEntity> map = new TreeMap<>();

            List<AccessoryEntity> accessories = accessoryRepository.findAllAccessoryWhichSupplyPostStillPublic(MainConstants.SUPPLY_POST_PUBLIC);
            List<AccessoryEntity> accessoriesCrawl = accessoryRepository.findAllByUrlIsNotNull();

            for (AccessoryEntity item : accessories) {
                double point = compareAccessoryEntityAndRequest(body, item);
                if (point >= MainConstants.COMPARISON_POINT) {
                    map.put(point, item);
                }
            }
            for (AccessoryEntity item : accessoriesCrawl) {
                double point = compareAccessoryEntityAndRequest(body, item);
                if (point >= MainConstants.COMPARISON_POINT + 10) {
                    System.out.println("point crawl " + point);
                    map.put(point, item);
                }
            }
            addRequestNotificationFromAccessory(map, requestProductEntity);
        }
    }

    //search after
    //create/update supply post
    public void searchRequestAfterActionSupplyPost(SupplyProductEntity supplyProduct) {
        List<RequestProductEntity> requests = requestProductRepository.findAllByStatus(MainConstants.REQUEST_FIND);

        List<RequestNotificationEntity> notis = new ArrayList<>();
        String desc = "";
        double point;
        for (RequestProductEntity item : requests) {
            desc = item.getDescription();
            point = 0;
            if (supplyProduct.getTypeItem().equals(MainConstants.STATUS_BIKE)) {
                BikeEntity bike = bikeRepository.findById((int) supplyProduct.getItemId());
                point = compareBikeEntityAndRequest(new JSONObject(desc).toMap(), bike);
            }
            if (supplyProduct.getTypeItem().equals(MainConstants.STATUS_ACCESSORY)) {
                AccessoryEntity accessory = accessoryRepository.findById((int) supplyProduct.getItemId());
                point = compareAccessoryEntityAndRequest(new JSONObject(desc).toMap(), accessory);
            }
            if (point >= MainConstants.COMPARISON_POINT) {
                RequestNotificationEntity noti = prepareNotification(MainConstants.NOTI_NEW,
                        supplyProduct.getCategoryId(), supplyProduct.getId(), item.getId());
                noti.setDescription(point + "");
                notis.add(noti);
            }
        }
        requestNotificationRepository.saveAll(notis);
    }


    private RequestNotificationEntity prepareNotification(String status, Integer cateId, Integer supplyProductId, Integer requesProductId) {
        RequestNotificationEntity noti = new RequestNotificationEntity();
        noti.setStatus(status);
        noti.setCategoryId(cateId);
        noti.setSupplyProductId(supplyProductId);
        noti.setRequestProductId(requesProductId);
        noti.setType(MainConstants.STATUS_SUPPLY_POST);
        return noti;
    }

}
