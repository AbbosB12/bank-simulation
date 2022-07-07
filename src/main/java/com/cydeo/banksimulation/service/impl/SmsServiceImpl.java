package com.cydeo.banksimulation.service.impl;

import com.cydeo.banksimulation.dto.SmsRequestDTO;
import com.cydeo.banksimulation.service.SmsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@ConditionalOnProperty(value = "smsservice.enable", havingValue = "true", matchIfMissing = false)
public class SmsServiceImpl implements SmsService{
    @Override
    public String sendSms(SmsRequestDTO smsRequestDTO) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            URI uri = new URI("http://localhost:8081/v1/sms");
            return restTemplate.postForObject(uri, smsRequestDTO,String.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "nok";
    }
}
