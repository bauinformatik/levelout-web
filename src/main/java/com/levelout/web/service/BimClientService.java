package com.levelout.web.service;

import com.levelout.web.model.UploadRequest;
import org.bimserver.client.BimServerClient;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by akiajk on 8/1/2017.
 */
@Service
public class BimClientService {
	@Autowired
	BimServerClient bimServerClient;

    @Async
    public void updateForPid(UploadRequest uploadRequest, MultipartFile bimFile) {
        try {
            bimServerClient.checkin(
                    uploadRequest.getPoid(), uploadRequest.getComment(), uploadRequest.getOid(), bimFile.getSize(),
                    bimFile.getName(), bimFile.getInputStream(), (title, progress) -> {
                        //ToDo: Save the status of the check-in/upload Percentage
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UserException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadNew(MultipartFile bimFile) {
    }

    public void deleteForPid(long pid) {
    }
}
