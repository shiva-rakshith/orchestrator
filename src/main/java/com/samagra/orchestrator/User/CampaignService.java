package com.samagra.orchestrator.User;

import com.inversoft.rest.ClientResponse;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.Application;
import io.fusionauth.domain.api.ApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CampaignService {

    @Autowired
    public FusionAuthClient client;

    static FusionAuthClient staticClient;

    @Autowired
    public void setStaticClient(FusionAuthClient client) {
        CampaignService.staticClient = client;
    }

    /**
     * Retrieve Campaign Params From its Identifier
     * @param campaignID - Campaign Identifier
     * @return Application
     * @throws Exception Error Exception, in failure in Network request.
     */
    public static Application getCampaignFromID(String campaignID) throws Exception {
        ClientResponse<ApplicationResponse, Void> applicationResponse = staticClient.retrieveApplication(UUID.fromString(campaignID));
        if(applicationResponse.wasSuccessful() ) {
            Application application = applicationResponse.successResponse.application;
            Map<String,Object> campaignData = new HashMap<>();
            ArrayList<String> transformers = new ArrayList<>();
            transformers.add(0, "Broadcast::SMS_1"); //SMS_1 refers to the template ID.
            transformers.add(1, "FORM::FORM_ID_1"); //Form_ID_1 refers to first step ODK Form
            transformers.add(2, "FORM::FORM_ID_2"); //Form_ID_2 refers to second step ODK Form
            //If it contains only Broadcast ---> SMS based campaign
            //If only FORM::Form_ID --> ODK Based campaign
            campaignData.put("transformers", transformers);
            application.data = campaignData;
            return application;
        }else if (applicationResponse.exception != null) {
            Exception exception = applicationResponse.exception;
            throw  exception;
        }
        return null;
    }
}