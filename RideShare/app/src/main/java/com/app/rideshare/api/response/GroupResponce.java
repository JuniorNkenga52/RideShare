package com.app.rideshare.api.response;

import com.app.rideshare.model.ChooseGroupModel;

/**
 * Created by rlogical-dev-48 on 11/9/2017.
 */

public class GroupResponce {

    // @SerializedName("status")
    private String status;
    //@SerializedName("message")
    private String message;
    //@SerializedName("result")
    private ChooseGroupModel result;

    public ChooseGroupModel getResult() {
        return result;
    }

    public void setResult(ChooseGroupModel result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
