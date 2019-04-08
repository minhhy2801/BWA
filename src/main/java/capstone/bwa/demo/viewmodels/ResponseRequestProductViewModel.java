package capstone.bwa.demo.viewmodels;

import capstone.bwa.demo.entities.RequestProductEntity;
import com.google.gson.Gson;

import java.lang.reflect.Field;

public class ResponseRequestProductViewModel {

    private Integer creatorId;
    private String createdTime;
    private String editedTime;
    private String title;
    private RequestBikeAccessoryViewModel description;
    private String status;

    //sử dung reflection trong java
    public static ResponseRequestProductViewModel toThis(RequestProductEntity entity) {
        ResponseRequestProductViewModel viewModel = new ResponseRequestProductViewModel();

        Class entityType = entity.getClass();

        Class viewmodelType = ResponseRequestProductViewModel.class;
        Field[] viewmodelFields = viewmodelType.getDeclaredFields();

        for (Field viewmodelField : viewmodelFields) {
            String viewmodelFieldName = viewmodelField.getName();

            if (!viewmodelFieldName.equalsIgnoreCase("description")) {
                try {
                    Field entityFieldNeedToGet = entityType.getDeclaredField(viewmodelFieldName);
                    if (entityFieldNeedToGet != null) {

                        viewmodelField.setAccessible(true);
                        entityFieldNeedToGet.setAccessible(true);

                        Object value = entityFieldNeedToGet.get(entity);
                        viewmodelField.set(viewModel, value);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
//        viewModel.setCreatorId( entity.getCreatorId() );
//        viewModel.setCreatedTime( entity.getCreatedTime() );
//        viewModel.setEditedTime( entity.getEditedTime() );
//        viewModel.setTitle( entity.getTitle() );
//        viewModel.setStatus( entity.getStatus() );

        Gson gson = new Gson();
        viewModel.setDescription(gson.fromJson(entity.getDescription(), RequestBikeAccessoryViewModel.class));

        return viewModel;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getEditedTime() {
        return editedTime;
    }

    public void setEditedTime(String editedTime) {
        this.editedTime = editedTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RequestBikeAccessoryViewModel getDescription() {
        return description;
    }

    public void setDescription(RequestBikeAccessoryViewModel description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}
