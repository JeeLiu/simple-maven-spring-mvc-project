package com.sheldontalk.www.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;

/**
 * Created by SheldonChen on 14-11-1.
 */
public class FileUtil {
    protected static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String saveFile(HttpServletRequest request, MultipartFile file) {
        String fileName = null;
        // 判断文件是否为空
        if (!file.isEmpty()) {
            try {
                // 文件保存路径
                Date now = new Date(System.currentTimeMillis());
                String filePath = request.getSession().getServletContext().getRealPath("/")
                        + File.separator + "upload" + File.separator + now.getTime() + ".jpg";
                logger.debug(filePath);
                file.transferTo(new File(filePath));
                fileName = now.getTime() + ".jpg";
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return fileName;
    }
}
