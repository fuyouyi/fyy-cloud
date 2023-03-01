package com.fyy.common.tools.utils;

import cn.hutool.core.util.StrUtil;
import com.fyy.common.tools.exception.RenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * MultipartFile工具类
 *
 * @author hzy
 * @since 2021/2/20
 */
@Slf4j
public class MultipartFileUtils {
    /**
     * 校验文件大小
     *
     * @param file
     * @param fileSize 文件大小。单位为MB。
     */
    public static void checkFileSize(MultipartFile file, int fileSize) {
        checkNull(file);
        if (file.getSize() > fileSize * 1024 * 1024) {
            throw new RenException("文件大小超出最大" + fileSize + "MB限制");
        }
    }

    /**
     * 检查上传文件是否为空
     *
     * @param file
     */
    public static void checkNull(MultipartFile file) {
        if (null == file || file.isEmpty()) {
            throw new RenException("上传文件不能为空");
        }
    }

    /**
     * 校验文件格式
     *
     * @param file
     */
    public static void checkFileType(MultipartFile file, String... fileTypes) {
        checkNull(file);
        if (null == fileTypes || fileTypes.length < 1) {
            throw new RenException("文件格式不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String fileType = StrUtil.sub(originalFilename, originalFilename.lastIndexOf(".") + 1, originalFilename.length());
        boolean isContain = false;
        for (String type : fileTypes) {
            if (type.equalsIgnoreCase(fileType)) {
                isContain = true;
                break;
            }
        }
        if (!isContain) {
            throw new RenException("文件格式不正确");
        }
    }
}
