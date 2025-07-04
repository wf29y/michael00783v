package com.leon.datalink.web.controller;

import com.leon.datalink.core.backup.Backup;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.web.backup.BackupService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * @ClassName RulesController
 * @Description 备份管理
 * @Author Leon
 * @Date 2022年7月22日15:22:32
 * @Version V1.0
 **/
@RestController
@RequestMapping("/api/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    /**
     * 上传备份文件
     *
     * @param jarFile
     * @throws IOException
     */
    @PostMapping(value = "/upload", consumes = "multipart/*", headers = "Content-Type=multipart/form-data")
    public void uploadJar(@RequestParam("file") MultipartFile jarFile) throws IOException, KvStorageException {
        if (jarFile == null || jarFile.getOriginalFilename() == null) {
            return;
        }
        backupService.upload(jarFile.getOriginalFilename(), jarFile.getBytes());
    }

    /**
     * 下载备份文件
     */
    @GetMapping(value = "/download")
    public void download(@RequestParam("backupName") String backupName, HttpServletResponse response) throws IOException, KvStorageException {
        response.setHeader("Content-Disposition", "attachment;filename=" + backupName);
        response.setContentType("application/octet-stream");
        OutputStream out = response.getOutputStream();
        out.write(backupService.download(backupName));
        out.flush();
    }

    /**
     * 恢复备份文件
     */
    @GetMapping(value = "/recover")
    public void recover(@RequestParam("backupName") String backupName) throws KvStorageException {
        backupService.recover(backupName);
    }


    /**
     * 创建备份
     *
     * @throws KvStorageException
     */
    @PostMapping("/create")
    public void createBackup() throws Exception {
        backupService.add(new Backup());
    }

    /**
     * 查询备份
     *
     * @param backup
     */
    @PostMapping("/list")
    public Object listBackup(@RequestBody Backup backup) {
        return backupService.list(backup);
    }

    /**
     * 移除备份
     *
     * @param backup
     * @throws Exception
     */
    @PostMapping("/remove")
    public void removeBackup(@RequestBody Backup backup) throws Exception {
        backupService.remove(backup);
    }


}

