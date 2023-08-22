package com.tr.exe.kit;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.Objects;

/**
 * @Author: TR
 * @Date: 2023/8/11
 */
public class DiskKit {

    /**
     * 获取 windows 系统第一个磁盘序列号
     */
    public static String getFirstDiskSerial() {
        try {
            File[] roots = File.listRoots();
            if (Objects.nonNull(roots) && roots.length > 0) {
                FileStore store = Files.getFileStore(roots[0].toPath());
                return store.getAttribute("volume:vsn").toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
