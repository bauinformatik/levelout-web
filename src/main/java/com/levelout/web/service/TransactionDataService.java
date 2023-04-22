package com.levelout.web.service;

import com.levelout.web.model.TransactionDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class TransactionDataService {
    public static final String TRANSACTION_DATA = "transactionData";
    @Autowired
    private HttpSession httpSession;

    public TransactionDataModel getTransactionData() {
        return (TransactionDataModel) httpSession.getAttribute(TRANSACTION_DATA);
    }

    public TransactionDataModel setTransactionData(Long projectId) {
        if(projectId==null || projectId<=0)
            throw new RuntimeException("Invalid ProjectId");

        TransactionDataModel transactionData = new TransactionDataModel();
        transactionData.setProjectId(projectId);
        httpSession.setAttribute(TRANSACTION_DATA, transactionData);
        return transactionData;
    }

    public Long getProjectId() {
        TransactionDataModel transactionData = (TransactionDataModel) httpSession.getAttribute(TRANSACTION_DATA);
        return transactionData==null?0:transactionData.getProjectId();
    }
}
