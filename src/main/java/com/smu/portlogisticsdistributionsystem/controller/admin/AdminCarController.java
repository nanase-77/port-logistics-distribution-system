package com.smu.portlogisticsdistributionsystem.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.CarDTO;
import com.smu.portlogisticsdistributionsystem.service.CarService;
import com.smu.portlogisticsdistributionsystem.util.MinioUtil;
import com.smu.portlogisticsdistributionsystem.vo.CarVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "管理员车辆管理")
@RestController
@RequestMapping("/api/admin/car")
@CrossOrigin
public class AdminCarController {
    @Autowired
    CarService carService;

    @Autowired
    MinioUtil minioUtil;

    @GetMapping("/select")
    @ApiOperation("查询所有车辆")
    public Result<Page<CarVO>> select(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        Page<CarVO> page = carService.select(pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("添加车辆")
    @PostMapping("/add")
    public Result add(@RequestBody CarDTO carDTO) {
        carService.add(carDTO);
        return Result.success();
    }

    @ApiOperation("删除车辆")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        carService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改车辆")
    @PutMapping("/update")
    public Result update(@RequestBody CarDTO carDTO) {
        carService.update(carDTO);
        return Result.success();
    }

    @ApiOperation("上传车辆图片")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String objectName = minioUtil.uploadFile(file);
            String fileUrl = minioUtil.getFileUrl(objectName);
            return Result.success(fileUrl);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}