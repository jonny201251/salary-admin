package com.hthyaq.salaryadmin.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadFileBatchModifyService {
    boolean completeUpload(MultipartFile[] files, String type, String path,String comment);
}
